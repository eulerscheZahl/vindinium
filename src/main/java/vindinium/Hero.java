package vindinium;

import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import vindinium.view.TileFactory;
import vindinium.view.ViewController;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    public Player player;
    public Tile tile;
    public Tile spawnPos;
    public int lastDir;
    public int life;
    public int gold;
    private boolean justRespawned = false;

    private Group group;
    private Sprite sprite;
    private static int SPRITE_SIZE = 32;
    private GraphicEntityModule entityManager;

    static final int maxLife = 100;
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

    public void initUI(GraphicEntityModule entityManager) {
        this.entityManager = entityManager;

        group = entityManager.createGroup();
        group.setX((int) ((tile.x + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setY((int) ((tile.y + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setScale(ViewController.scaleSize / ViewController.CELL_SIZE);

        sprite = entityManager.createSprite()
                .setImage(TileFactory.getInstance().heroes[player.getIndex() * 9])
                .setBaseHeight(SPRITE_SIZE)
                .setBaseWidth(SPRITE_SIZE)
                .setAlpha(1.0)
                .setZIndex(-1);
        group.add(sprite);
    }

    public void drinkBeer() {
        if (gold >= beerGold) {
            gold -= beerGold;
            life += beerLife;
            if (life > maxLife) life = maxLife;
        }
    }

    public void day() {
        if (life > dayLife) life -= dayLife;
    }

    public void respawn(Board board) {
        justRespawned = true;
        life = maxLife;
        tile = spawnPos;
        sprite.setImage(TileFactory.getInstance().heroes[4 * 9 + lastDir]);
        entityManager.commitEntityState(0, sprite);
        group.setX((int) ((tile.x + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setY((int) ((tile.y + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE));

        for (Hero h : board.heroes) {
            if (h == this || tile != h.tile) continue;
            board.transferMines(h, this);
            h.respawn(board);
        }
    }

    public void fightMine(Board board, Tile target) {
        if (target.mine.owner == this) return;
        life -= mineLife;
        if (life > 0) {
            target.mine.conquer(this);
        } else {
            board.transferMines(this, null);
            respawn(board);
        }
    }

    public void fight(Board board) {
        for (Hero h : board.heroes) {
            if (tile.distance(h.tile) != 1 || h.justRespawned) continue;
            h.defend();
            if (h.life <= 0) {
                board.transferMines(h, this);
                h.respawn(board);
            }
        }
    }

    public void defend() {
        life -= defendLife;
    }

    public void move(Board board, Tile target) {
        if (target.type == Tile.Type.Wall) {
            // reset sprite for respawn
            sprite.setImage(TileFactory.getInstance().heroes[player.getIndex() * 9 + lastDir]);
            entityManager.commitEntityState(0, sprite);
            return;
        }

        if (target.type == Tile.Type.Tavern) drinkBeer();
        else if (target.type == Tile.Type.Mine) fightMine(board, target);
        else {
            if (tile.y < target.y) lastDir = 0;
            else if (tile.x > target.x) lastDir = 1;
            else if (tile.x < target.x) lastDir = 2;
            else if (tile.y > target.y) lastDir = 3;
            tile = target;

            sprite.setImage(TileFactory.getInstance().heroes[player.getIndex() * 9 + lastDir]);
            entityManager.commitEntityState(0, sprite);
            group.setX((int) ((tile.x + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                    .setY((int) ((tile.y + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE));
        }
    }

    public void finalize(Board board) {
        justRespawned = false;
        day();
        gold += board.countMines(this);
    }

    public String print() {
        return "HERO " + player.getIndex() + " " + tile.x + " " + tile.y + " " + life + " " + gold;
    }
}