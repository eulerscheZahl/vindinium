package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import vindinium.Hero;
import vindinium.Mine;
import vindinium.Tile;
import com.codingame.game.view.TileFactory;
import com.codingame.game.view.ViewController;


public class MineView implements IView
{
    public Mine _model;
    private Hero _previousOwner;
    private Group _mineGroup;
    private Sprite _mineSprite;

    public MineView(Mine model, GraphicEntityModule entityManager){
        _model = model;
        Tile tile = _model.tile;

        _mineGroup = entityManager.createGroup().setX(ViewController.CELL_SIZE * (tile.x + 1) - 4)
                .setY(ViewController.CELL_SIZE * (tile.y + 1) - 4);

        Group goblinGroup = entityManager.createGroup();
        _mineGroup .add(goblinGroup);

        Sprite goblin = entityManager.createSprite()
                .setImage(TileFactory.getInstance().goblins[4])
                .setBaseHeight(ViewConstants.SPRITE_SIZE)
                .setBaseWidth(ViewConstants.SPRITE_SIZE)
                .setAlpha(1.0)
                .setY(-16)
                .setZIndex(-1);
        goblinGroup.add(goblin);

        _mineSprite = entityManager.createSprite()
                .setImage(TileFactory.getInstance().mines[4])
                .setBaseHeight(ViewConstants.SPRITE_SIZE)
                .setBaseWidth(ViewConstants.SPRITE_SIZE)
                .setAlpha(1.0)
                .setZIndex(-1);

        _mineGroup.add(_mineSprite);
    }

    @Override
    public void onRound() {
        if(_model.owner != _previousOwner){
            _previousOwner = _model.owner;
            _mineSprite.setImage(TileFactory.getInstance().mines[_model.owner == null ? 4 : _model.owner.player.getIndex()]);
        }
    }
    public Group getView() {
        return _mineGroup;
    }
}
