package vindinium;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import vindinium.view.TileFactory;
import vindinium.view.ViewController;

public class Mine {
    public Tile tile;
    public Group mineGroup;
    public Group goblinGroup;
    private Sprite mineSprite;
    public Hero owner;
    private static int SPRITE_SIZE = 32;

    public Mine(Tile tile, GraphicEntityModule entityManager) {
        this.tile = tile;
        goblinGroup = entityManager.createGroup();
        goblinGroup.setX((int) ((tile.x + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setY((int) ((tile.y + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setScale(ViewController.scaleSize / ViewController.CELL_SIZE);

        mineGroup = entityManager.createGroup();
        mineGroup.setX((int) ((tile.x + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setY((int) ((tile.y + 1) * ViewController.scaleSize - 4 * ViewController.scaleSize / ViewController.CELL_SIZE))
                .setScale(ViewController.scaleSize / ViewController.CELL_SIZE);

        Sprite goblin = entityManager.createSprite()
                .setImage(TileFactory.getInstance().goblins[4])
                .setBaseHeight(SPRITE_SIZE)
                .setBaseWidth(SPRITE_SIZE)
                .setAlpha(1.0)
                .setY(-16)
                .setZIndex(-1);
        goblinGroup.add(goblin);
        mineSprite = entityManager.createSprite()
                .setImage(TileFactory.getInstance().mines[4])
                .setBaseHeight(SPRITE_SIZE)
                .setBaseWidth(SPRITE_SIZE)
                .setAlpha(1.0)
                .setZIndex(-1);
        mineGroup.add(mineSprite);
    }

    public String print() {
        return "MINE " + (owner == null ? -1 : owner.player.getIndex()) + " " + tile.x + " " + tile.y + " -1 -1";
    }

    public void conquer(Hero hero) {
        owner = hero;
        mineSprite.setImage(TileFactory.getInstance().mines[hero == null ? 4 : hero.player.getIndex()]);
    }
}