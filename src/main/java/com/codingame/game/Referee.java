package com.codingame.game;
import com.codingame.gameengine.core.AbstractPlayer;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.core.Tooltip;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;
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
    //@Inject
    //private TooltipModule tooltipModule;
    private HeroHud[] HeroHuds = new HeroHud[4];
    private Board board;
    private ViewController view;

    @Override
    public void init() {
        gameManager.setMaxTurns(ViewConstants.MAX_ROUNDS);
        gameManager.setTurnMaxTime(50);

        Properties params = gameManager.getGameParameters();
        System.err.println("seed: " + getSeed(params));
        Config.random = new Random(getSeed(params));

        board = Config.generateMap(gameManager.getPlayers());
        System.err.print(board.print());

        board.initMines();
        initGridView();
    }

    private void initGridView() {
        view = new ViewController(graphicEntityModule, gameManager);//, tooltipModule);
        view.createGrid(board);

        int c = 0;
        int remainingSpace =(graphicEntityModule.getWorld().getWidth()-graphicEntityModule.getWorld().getHeight());
        int rightXPos = remainingSpace/2+graphicEntityModule.getWorld().getHeight();
        for (Player p : gameManager.getPlayers()) {
            view.setSpawn(p.hero.spawnPos, p.getIndex());
            int w = graphicEntityModule.getWorld().getWidth();
            int h = graphicEntityModule.getWorld().getHeight();
            int width = (w-ViewConstants.BarRight);

            HeroHuds[c] = new HeroHud(p.hero, graphicEntityModule, p,  ViewConstants.BarRight+(width-350)/2-10, c*125+20,width);
            c++;
        }
    }

    private void sendInputs(Player player, boolean initial) {
        System.err.println("input to player  " + player.getIndex());
        if (initial) {
            System.err.println(board.print().trim());
            System.err.println(player.getIndex());
        }
        System.err.println(board.boardState().trim());
        System.err.println("------------");

        if (initial) {
            player.sendInputLine(board.print().trim());
            player.sendInputLine(String.valueOf(player.getIndex()));
        }
        player.sendInputLine(board.boardState().trim());
    }

    @Override
    public void gameTurn(int turn) {
        Player player = gameManager.getPlayer(turn % 4);
        System.err.println("TURN: " + turn);

        String action = "WAIT";
        try {
            sendInputs(player, turn < gameManager.getPlayerCount());
                player.execute();
            if(player.getExpectedOutputLines()==1) {
                action = player.getOutputs().get(0).trim().toUpperCase();
            }
        } catch (Exception timeout) {
            if (player.getExpectedOutputLines() == 1) {
                player.setDeactivated();
                gameManager.addTooltip(new Tooltip(player.getIndex(), player.getNicknameToken() + " timeout"));
            }
        }

        Hero hero = player.hero;
        Tile target = hero.tile;
        String message = "";
        if (action.contains(" ")) {
            message = action.substring(action.indexOf(' ') + 1);
            action = action.substring(0, action.indexOf(' '));
        }

        if (action.equals("WAIT")) ;
        else if (action.equals("NORTH")) {
            if (target.y > 0) target = board.tiles[target.x][target.y - 1];
        } else if (action.equals("SOUTH")) {
            if (target.y + 1 < board.size) target = board.tiles[target.x][target.y + 1];
        } else if (action.equals("EAST")) {
            if (target.x + 1 < board.size) target = board.tiles[target.x + 1][target.y];
        } else if (action.equals("WEST")) {
            if (target.x > 0) target = board.tiles[target.x - 1][target.y];
        } else {
            try {
                if (!action.equals("MOVE")) throw new Exception();
                String[] parts = message.split(" ");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                message = "";
                for (int i = 2; i < parts.length; i++) message += parts[i] + " ";
                message = message.trim();
                target = board.tiles[x][y];
            } catch (Exception ex) {
                if (player.getExpectedOutputLines() == 1) {
                    player.setDeactivated();
                    gameManager.addTooltip(new Tooltip(player.getIndex(), player.getNicknameToken() + " invalid action: \"" + action + "\""));
                }
            }
        }

        hero.move(board, target);
        List<Tile> fightLocations = hero.fight(board);
        hero.finalize(board);
        player.setScore(hero.gold);
        HeroHuds[player.getIndex()].OnRound(message);
        view.onRound(fightLocations);
        hero.justRespawned = false;


        Hero leader = board.getLeader();
        for (HeroHud heroHud : HeroHuds) {
            if (heroHud._hero == leader) heroHud.setLeader(true);
            else heroHud.setLeader(false);
        }

        if (turn == ViewConstants.MAX_ROUNDS-1){
            ArrayList<Hero> heroes = new ArrayList<>();
            for(Hero h : board.heroes){
                heroes.add(h);
            }

            Comparator<Hero> c = (s1, s2) -> s1.gold < s2.gold? 1 : -1;
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
