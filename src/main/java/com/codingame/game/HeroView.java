package com.codingame.game;

import com.codingame.gameengine.module.entities.*;
import vindinium.Hero;
import com.codingame.game.view.TileFactory;
import com.codingame.game.view.ViewController;
import vindinium.Tile;

public class HeroView implements IView {
    private int _healthBarHeight = 32;
    public Hero _model;
    public Sprite _sprite;
    public Group _group;
    private GraphicEntityModule _entityManager;
    private Tile _lastTile;
    private int _lastDir = -1;
    private Rectangle _healthBar;
    private SpriteAnimation[] fightAnimation = new SpriteAnimation[4];
    private int counter = -1;

    public HeroView(Hero model, GraphicEntityModule entityManager){
        _model = model;
        _entityManager = entityManager;
        _group = entityManager.createGroup().setZIndex(model.tile.y);
        ViewController.moveEntity(_group, _model.tile, -5, -5);

        _sprite = entityManager.createSprite()
                .setImage(TileFactory.getInstance().heroes[model.player.getIndex() * 9])
                .setBaseHeight(ViewConstants.SPRITE_SIZE)
                .setBaseWidth(ViewConstants.SPRITE_SIZE)
                .setAlpha(1.0)
                .setZIndex(-1);
        _group.add(_sprite);

        _group.add(entityManager.createRectangle().setWidth(_healthBarHeight).setHeight(3).setX(0).setY(-5).setFillColor(0xff0000));
        _group.add(_healthBar = entityManager.createRectangle().setWidth(_healthBarHeight).setHeight(3).setX(0).setY(-5).setFillColor(0x00ff00));
        for (int i = 0; i < 4; i++) {
            fightAnimation[i] = ViewConstants.createAnimation(entityManager, false, 200, "blast.png", "Empty.png")
                    .setAnchor(0.5).setX(ViewController.CELL_SIZE / 2 + 2).setY(ViewController.CELL_SIZE / 2 + 2).setAlpha(0).setRotation(i * Math.PI / 2).setZIndex(9);
            _group.add(fightAnimation[i]);
        }
    }

    private boolean wasLeading;
    private boolean wasDead;
    private boolean hasDeadImg;
    private int _lastLife = -1;
    @Override
    public void onRound(int round) {
        counter++;
        int leadingOffset = _model.leading ? 5 : 0;
        if (_model.justRespawned && !wasDead) {
            wasDead = true;
            hasDeadImg = true;
            _sprite.setImage(TileFactory.getInstance().heroes[4 * 9 + _model.lastDir]);
            _entityManager.commitEntityState(0.0, _sprite);
            _entityManager.commitEntityState(0.8, _sprite, _group);
            ViewController.moveEntity(_group, _model.tile, -5, -5);
            _group.setZIndex(_model.tile.y);

            _model.justRespawned = false;
        } else {
            if (counter % 4 == _model.player.getIndex() && hasDeadImg) {
                hasDeadImg = false;
                _sprite.setImage(TileFactory.getInstance().heroes[_model.player.getIndex() * 9 + _model.lastDir + leadingOffset]);
            }
            wasDead = false;
            if (_lastDir != _model.lastDir || wasLeading != _model.leading) {
                _lastDir = _model.lastDir;
                wasLeading = _model.leading;
                _sprite.setImage(TileFactory.getInstance().heroes[_model.player.getIndex() * 9 + _model.lastDir + leadingOffset]);
                _entityManager.commitEntityState(0, _sprite);
            }

            for (Tile fight : _model.fightCells) {
                int index = 0;
                if (fight.x < _model.tile.x) index = 2;
                if (fight.y < _model.tile.y) index = 3;
                if (fight.y > _model.tile.y) index = 1;
                fightAnimation[index].reset();
                fightAnimation[index].setAlpha(1, Curve.NONE);
                fightAnimation[index].play();
                _entityManager.commitEntityState(0.8, fightAnimation[index]);
                fightAnimation[index].setAlpha(0, Curve.NONE);
            }
            _model.fightCells.clear();
            if (_model.tile != _lastTile) {
                _group.setX(ViewController.CELL_SIZE * (_model.tile.x + 1) - 4)
                        .setY(ViewController.CELL_SIZE * (_model.tile.y + 1) - 4)
                        .setZIndex(_model.tile.y);
                _entityManager.commitEntityState(0.8, _sprite, _group);
                ViewController.moveEntity(_group, _model.tile, -4, -4);
                _group.setZIndex(_model.tile.y);
            }

            if(_model.receivedDamage){
                _sprite.setTint(0xffffff, Curve.IMMEDIATE);
                _entityManager.commitEntityState(0.9, _sprite);
                _sprite.setTint(0x999999, Curve.LINEAR);
                _entityManager.commitEntityState(0.95, _sprite);
                _sprite.setTint(0xffffff, Curve.IMMEDIATE);
                _entityManager.commitEntityState(1, _sprite);
            }
        }

        if (_lastLife != _model.life) {
            _lastLife = _model.life;
            _healthBar.setWidth((int) (_healthBarHeight * _model.life / 100.0));
        }

        _lastTile = _model.tile;
        _model.receivedDamage = false;
    }

    public Group getView() {
        return _group;
    }
}
