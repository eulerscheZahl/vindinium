package vindinium;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.core.Tooltip;
import modules.TooltipModule;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    public Player player;
    public Tile tile;
    public Tile spawnPos;
    public int lastDir;
    public int life;
    public int gold;
    public boolean justRespawned = false;
    public boolean leading = false;
    public boolean didFight;

    public static final int maxLife = 100;
    static final int beerLife = 50;
    static final int beerGold = 2;
    static final int dayLife = 1;
    static final int mineLife = 20;
    static final int defendLife = 20;

    public Hero(Player player, Tile spawnPos) {
        this.player = player;
        this.spawnPos = spawnPos;
        this.tile = spawnPos;
        this.life = maxLife;
    }

    public void drinkBeer(MultiplayerGameManager<Player> gameManager) {
        if (gold >= beerGold) {
            gold -= beerGold;
            life += beerLife;
            if (life > maxLife) life = maxLife;
            gameManager.addToGameSummary(player.getNicknameToken() + " buys beer");
        } else {
            gameManager.addToGameSummary("[Warning] " + player.getNicknameToken() + ": not enough gold to buy beer");
        }
    }

    public void day() {
        if (life > dayLife) life -= dayLife;
    }

    public void respawn(Board board) {
        justRespawned = true;
        life = maxLife;
        tile = spawnPos;

        for (Hero h : board.heroes) {
            if (h == this || tile != h.tile || h.life <= 0) continue;
            board.transferMines(h, this);
            h.respawn(board);
        }
    }

    public void fightMine(Board board, Tile target, MultiplayerGameManager<Player> gameManager) {
        if (target.mine.owner == this) return;
        didFight = true;
        life -= mineLife;
        if (life > 0) {
            target.mine.conquer(this);
            gameManager.addToGameSummary(player.getNicknameToken() + " conquered a mine");
        } else {
            board.transferMines(this, null);
            respawn(board);
            gameManager.addToGameSummary(player.getNicknameToken() + " died while trying to conquer a mine");
        }
    }

    public List<Tile> fight(Board board, MultiplayerGameManager<Player> gameManager) {
        ArrayList<Tile> fightLocations = new ArrayList<>();
        for (Hero h : board.heroes) {
            if (tile.distance(h.tile) != 1 || this.justRespawned || h.justRespawned) continue;
            h.defend();
            fightLocations.add(h.tile);
            didFight = true;
            if (h.life <= 0) {
                board.transferMines(h, this);
                h.respawn(board);
                gameManager.addToGameSummary(player.getNicknameToken() + " kills " + h.player.getNicknameToken());
            } else {
                gameManager.addToGameSummary(player.getNicknameToken() + " attacks " + h.player.getNicknameToken());
            }
        }
        return fightLocations;
    }

    public void defend() {
        life -= defendLife;
    }

    public void move(Board board, Tile target, MultiplayerGameManager<Player> gameManager) {
        justRespawned = false;
        if (tile.distance(target) > 1) {
            target = findTarget(board, target);
        }

        if (tile.y < target.y) lastDir = 0;
        else if (tile.x > target.x) lastDir = 1;
        else if (tile.x < target.x) lastDir = 2;
        else if (tile.y > target.y) lastDir = 3;

        if (target.type == Tile.Type.Wall) {
            gameManager.addToGameSummary("[Warning] " + player.getNicknameToken() + " tried to walk into a wall");
            return;
        }

        if (target.type == Tile.Type.Tavern) {
            drinkBeer(gameManager);
        } else if (target.type == Tile.Type.Mine) {
            fightMine(board, target, gameManager);
        } else {
            final Tile t = target;
            if (board.heroes.stream().anyMatch(h -> h.tile == t)) {
                target = tile; // can't share position with other hero
            }
            tile = target;
        }
    }

    private Tile findTarget(Board board, Tile target) {
        int[][] distToHero = Config.findDistance(board, tile);
        Tile t = tile;
        double toTarget = Integer.MAX_VALUE;
        double toHero = Integer.MAX_VALUE;
        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                if (distToHero[x][y] == -1) continue;
                double d1 = board.tiles[x][y].distance(target);
                double d2 = board.tiles[x][y].distance(tile);
                if (d1 < toTarget || d1 == toTarget && d2 < toHero) {
                    t = board.tiles[x][y];
                    toTarget = d1;
                    toHero = d2;
                }
            }
        }

        int[][] distFromTarget = Config.findDistance(board, t);
        for (Tile next : board.neighbors(tile)) {
            if (distFromTarget[next.x][next.y] == distFromTarget[tile.x][tile.y] - 1) return next;
        }

        return tile;
    }

    public void finalize(Board board) {
        day();
        gold += board.countMines(this);
    }

    public String print() {
        return "HERO " + player.getIndex() + " " + tile.x + " " + tile.y + " " + life + " " + gold;
    }
}