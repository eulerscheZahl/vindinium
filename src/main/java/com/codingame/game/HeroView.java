package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import vindinium.Hero;
import com.codingame.game.view.TileFactory;
import com.codingame.game.view.ViewController;
import vindinium.Tile;

public class HeroView implements IView{
    public Hero _model;
    private Sprite _sprite;
    private Group _group;
    private GraphicEntityModule _entityManager;
    private Tile _lastTile;
    private int _lastDir = -1;

    public HeroView(Hero model, GraphicEntityModule entityManager){
        _model = model;
        _entityManager = entityManager;
        _group = entityManager.createGroup()
                .setX(ViewController.CELL_SIZE * (model.tile.x + 1) - 4)
                .setY(ViewController.CELL_SIZE * (model.tile.y + 1) - 4).setZIndex(10);

        _sprite = entityManager.createSprite()
                .setImage(TileFactory.getInstance().heroes[model.player.getIndex() * 9])
                .setBaseHeight(ViewConstants.SPRITE_SIZE)
                .setBaseWidth(ViewConstants.SPRITE_SIZE)
                .setAlpha(1.0)
                .setZIndex(-1);
        _group.add(_sprite);
    }

    @Override
    public void onRound() {
        if(_lastDir != _model.lastDir) {
            _lastDir = _model.lastDir;
            if (_model.wasDead)
                _sprite.setImage(TileFactory.getInstance().heroes[4 * 9 + _model.lastDir]);
            else
                _sprite.setImage(TileFactory.getInstance().heroes[_model.player.getIndex() * 9 + _model.lastDir]);
            _entityManager.commitEntityState(0, _sprite);
        }

        if(_model.tile != _lastTile) {
            _lastTile = _model.tile;
            _group.setX(ViewController.CELL_SIZE * (_model.tile.x + 1) - 4)
                    .setY(ViewController.CELL_SIZE * (_model.tile.y + 1) - 4);
        }
    }

    @Override
    public Group getView() {
        return _group;
    }
}
