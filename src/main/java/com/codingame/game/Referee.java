package com.codingame.game;
import com.codingame.gameengine.core.AbstractPlayer;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.core.Tooltip;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;
import modules.FXModule;
import modules.TooltipModule;
import vindinium.Board;
import vindinium.Config;
import vindinium.Hero;
import vindinium.Tile;
import com.codingame.game.view.HeroHud;
import com.codingame.game.view.ViewController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Referee extends AbstractReferee {
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    @Inject
    private TooltipModule tooltipModule;

    @Inject
    private FXModule fxModule;

    public static int MAX_ROUNDS = 200;
    private List<HeroHud> HeroHuds = new ArrayList<>();
    private Board board;
    private ViewController view;
    private int playerCount;

    @Override
    public void init() {
        Properties params = gameManager.getGameParameters();
        System.err.println("seed: " + getSeed(params));
        Config.random = new Random(getSeed(params));
        try {
            MAX_ROUNDS = Integer.parseInt(params.getProperty("turns"));
            if (MAX_ROUNDS < 10) MAX_ROUNDS = 10;
            if (MAX_ROUNDS > 200) MAX_ROUNDS = 200;
        } catch (Exception ex) {
            // keep the default number of 200, if no value specified
        }
        playerCount = gameManager.getPlayerCount();
        if (params.containsKey("players") && params.getProperty("players").equals("2")) playerCount = 2;

        gameManager.setMaxTurns(playerCount * MAX_ROUNDS);
        gameManager.setTurnMaxTime(50);
        gameManager.setFrameDuration(250);

        board = Config.generateMap(gameManager.getPlayers(), params);
        System.err.print(board.print());

        board.initMines();
        initGridView();
    }

    private void initGridView() {
        view = new ViewController(graphicEntityModule, gameManager, tooltipModule, fxModule);
        view.createGrid(board);

        int c = 0;
        for (Player p : gameManager.getPlayers()) {
            if (p.getIndex() >= playerCount) break;
            view.setSpawn(p.hero.spawnPos, p.getIndex());
            int w = graphicEntityModule.getWorld().getWidth();
            int width = (w - ViewConstants.BarRight);

            HeroHuds.add(new HeroHud(p.hero, graphicEntityModule, p, ViewConstants.BarRight + (width - 350) / 2 - 10, c * 125 + 20, width));
            c++;
        }
    }

    private void sendInputs(Player player, boolean initial) {
        //System.err.println("input to player  " + player.getIndex());
        //if (initial) {
        //    System.err.println(board.print().trim());
        //    System.err.println(player.getIndex());
        //}
        //System.err.println(board.boardState().trim());
        //System.err.println("------------");

        if (initial) {
            player.sendInputLine(board.print().trim());
            player.sendInputLine(String.valueOf(player.getIndex()));
        }
        player.sendInputLine(board.boardState().trim());
    }

    @Override
    public void gameTurn(int turn) {
        Player player = gameManager.getPlayer(turn % playerCount);
        //System.err.println("TURN: " + turn + ", player = " + player.getIndex());

        String action = "WAIT";
        try {
            if (player.getExpectedOutputLines() == 1) {
                sendInputs(player, turn < playerCount);
            }
            player.execute();
            if (player.getExpectedOutputLines() == 1) {
                action = player.getOutputs().get(0).trim().toUpperCase();
            }
        } catch (Exception timeout) {
            if (player.getExpectedOutputLines() == 1) {
                player.setDeactivated();
                gameManager.addTooltip(new Tooltip(player.getIndex(), player.getNicknameToken() + " timeout"));
                gameManager.addToGameSummary("[Error] " + player.getNicknameToken() + " didn't provide any output in time");
            }
        }

        Hero hero = player.hero;
        String message = "";
        if (action.contains(" ")) {
            message = action.substring(action.indexOf(' ') + 1);
            action = action.substring(0, action.indexOf(' '));
        }

        Tile target = new Tile(Tile.Type.Air, hero.tile.x, hero.tile.y);
        if (action.equals("WAIT")) ;
        else if (action.equals("NORTH")) {
            target.y--;
        } else if (action.equals("SOUTH")) {
            target.y++;
        } else if (action.equals("EAST")) {
            target.x++;
        } else if (action.equals("WEST")) {
            target.x--;
        } else {
            try {
                if (!action.equals("MOVE")) throw new Exception();
                String[] parts = message.split(" ");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                message = "";
                for (int i = 2; i < parts.length; i++) message += parts[i] + " ";
                message = message.trim();
                target.x = x;
                target.y = y;
            } catch (Exception ex) {
                if (player.getExpectedOutputLines() == 1) {
                    player.setDeactivated();
                    gameManager.addTooltip(new Tooltip(player.getIndex(), player.getNicknameToken() + " invalid action: \"" + action + "\""));
                    gameManager.addToGameSummary("[Error] " + player.getNicknameToken() + " performed an invalid action: " + action);
                }
            }
        }

        hero.move(board, target, gameManager);
        List<Tile> fightLocations = hero.fight(board, gameManager);
        hero.finalize(board);
        player.setScore(hero.gold);
        HeroHuds.get(player.getIndex()).OnRound(message);

        Hero leader = board.getLeader();
        for (HeroHud heroHud : HeroHuds) {
            if (heroHud._hero == leader) heroHud.setLeader(true);
            else heroHud.setLeader(false);
        }

        view.onRound(fightLocations);

        if (turn == MAX_ROUNDS * playerCount - 1) {
            ArrayList<Hero> heroes = new ArrayList<>();
            for (Hero h : board.heroes) {
                heroes.add(h);
            }

            Comparator<Hero> c = (s1, s2) -> s1.gold < s2.gold ? 1 : -1;
            heroes.sort(c);
            new EndGameView(graphicEntityModule, heroes);
        }
    }

    private Long getSeed(Properties params) {
        try {
            return Long.parseLong(params.getProperty("seed", "0"));
        } catch (NumberFormatException nfe) {
            return 0L;
        }
    }
}
