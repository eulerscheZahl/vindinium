package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;
import vindinium.Hero;
import com.codingame.game.view.TileFactory;
import com.codingame.game.view.ViewController;
import vindinium.Tile;

public class HeroView implements IView {
    private int _healthBarHeight = 32;
    public Hero _model;
    public Sprite _sprite;
    private Group _group;
    private GraphicEntityModule _entityManager;
    private Tile _lastTile;
    private int _lastDir = -1;
    private Rectangle _healthBar;

    public HeroView(Hero model, GraphicEntityModule entityManager){
        _model = model;
        _entityManager = entityManager;
        _group = entityManager.createGroup().setZIndex(model.tile.y);
        ViewController.moveEntity(_group, _model.tile, -4, -4);

        _sprite = entityManager.createSprite()
                .setImage(TileFactory.getInstance().heroes[model.player.getIndex() * 9])
                .setBaseHeight(ViewConstants.SPRITE_SIZE)
                .setBaseWidth(ViewConstants.SPRITE_SIZE)
                .setAlpha(1.0)
                .setZIndex(-1);
        _group.add(_sprite);

        _group.add(entityManager.createRectangle().setWidth(_healthBarHeight).setHeight(3).setX(0).setY(-5).setFillColor(0xff0000));
        _group.add(_healthBar = entityManager.createRectangle().setWidth(_healthBarHeight).setHeight(3).setX(0).setY(-5).setFillColor(0x00ff00));
    }

    private boolean wasDead;
    private int _lastLife = -1;
    @Override
    public void onRound() {
        int leadingOffset = _model.leading ? 5 : 0;
        if(_model.justRespawned && !wasDead){
            wasDead = true;
            _sprite.setImage(TileFactory.getInstance().heroes[4 * 9 + _model.lastDir]);
            _entityManager.commitEntityState(0.8, _sprite, _group);
            ViewController.moveEntity(_group, _model.tile, -4, -4);
            _group.setZIndex(_model.tile.y);

            _entityManager.commitEntityState(1.0, _sprite, _group);
            _sprite.setImage(TileFactory.getInstance().heroes[_model.player.getIndex() * 9 + _model.lastDir + leadingOffset]);
        }
        else
        {
            if(_lastDir != _model.lastDir || wasDead) {
                wasDead = false;
                _lastDir = _model.lastDir;
                _sprite.setImage(TileFactory.getInstance().heroes[_model.player.getIndex() * 9 + _model.lastDir + leadingOffset]);
                _entityManager.commitEntityState(0, _sprite);
            }

            if(_model.tile != _lastTile) {
                _lastTile = _model.tile;
                ViewController.moveEntity(_group, _model.tile, -4, -4);
                _group.setZIndex(_model.tile.y);
            }
        }

        if(_lastLife != _model.life){
            _lastLife = _model.life;
            _healthBar.setWidth((int)(_healthBarHeight*_model.life/100.0));
        }
    }

    public Group getView() {
        return _group;
    }
}
