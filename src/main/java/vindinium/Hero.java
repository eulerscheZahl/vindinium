package vindinium;

import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import vindinium.view.TileFactory;
import vindinium.view.ViewController;

public class Hero {
    public Player player;
    public Tile tile;
    public Tile spawnPos;
    public int lastDir;
    public int life;
    public int gold;
    public int lastRespawn = -1;
    private Group group;
    private Sprite sprite;
    private static int SPRITE_SIZE = 32;

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
        group = entityManager.createGroup();
        group.setX((int) ((tile.x + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setY((int) ((tile.y + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setScale(ViewController.scaleSize / ViewController.CELL_SIZE);

        sprite = entityManager.createSprite()
                .setImage(TileFactory.getInstance().heroes[player.getIndex()*9])
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
        }
    }

    public void day() {
        if (life > dayLife) life -= dayLife;
    }

    public void respawn(int turn) {
        life = maxLife;
        tile = spawnPos;
        lastRespawn = turn;
    }

    public void fightMine(Tile target) {
        life -= mineLife;
        if (life > 0) {
            target.mine.conquer(this);
        }
    }

    public void defend() {
        life -= defendLife;
    }

    public void move(Tile target) {
        day();
        if (target.type == Tile.Type.Wall) return;

        if (target.type == Tile.Type.Tavern) drinkBeer();
        else if (target.type == Tile.Type.Mine) fightMine(target);
        else {
            if (tile.x < target.x) lastDir = 0;
            else if (tile.x > target.x) lastDir = 1;
            else if (tile.y < target.y) lastDir = 2;
            else if (tile.y > target.y) lastDir = 3;
            tile = target;

            group.setX((int) ((tile.x + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                    .setY((int) ((tile.y + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE));
        }
    }

    public String print() {
        return "HERO " + player.getIndex() + " " + tile.x + " " + tile.y + " " + life + " " + gold;
    }
}