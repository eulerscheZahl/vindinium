package com.codingame.game;

import com.codingame.gameengine.module.entities.Entity;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import modules.TooltipModule;
import vindinium.Board;
import vindinium.Hero;
import vindinium.Mine;
import vindinium.Tile;
import com.codingame.game.view.TileFactory;
import com.codingame.game.view.ViewController;

import javax.tools.Tool;
import java.util.HashMap;
import java.util.Map;


public class MineView implements IView
{
    public Mine _model;
    private TooltipModule tooltipModule;
    private Hero _previousOwner;
    private Group _mineGroup;
    private Sprite _mineSprite;
    private Sprite goblin;
    private Board _board;
    private boolean wasDefending;
    private Group goblinGroup;


    public MineView(Mine model, GraphicEntityModule entityManager, TooltipModule tooltipModule, Board board) {
        _board = board;
        _model = model;
        this.tooltipModule = tooltipModule;
        Tile tile = _model.tile;

        _mineGroup = entityManager.createGroup().setZIndex(tile.y);
        ViewController.moveEntity(_mineGroup, tile, -4, -4);

        goblinGroup = entityManager.createGroup().setZIndex(-2);
        _mineGroup.add(goblinGroup);

        goblin = entityManager.createSprite()
                .setImage(TileFactory.getInstance().goblins[4])
                .setBaseHeight(ViewConstants.SPRITE_SIZE)
                .setBaseWidth(ViewConstants.SPRITE_SIZE)
                .setAlpha(1.0)
                .setY(-16);
        goblinGroup.add(goblin);

        _mineSprite = entityManager.createSprite()
                .setImage(TileFactory.getInstance().mines[4])
                .setBaseHeight(ViewConstants.SPRITE_SIZE)
                .setBaseWidth(ViewConstants.SPRITE_SIZE)
                .setAlpha(1.0)
                .setZIndex(-1);

        _mineGroup.add(_mineSprite);
        addMineTooltip(_model, _mineGroup);
    }


    private void addMineTooltip(Mine unit, Entity entity){
        Map<String, Object> params = new HashMap<>();
        params.put("Type", "Mine");
        params.put("X", unit.tile.x+"");
        params.put("Y", unit.tile.y+"");
        tooltipModule.registerEntity(entity, params);

        updateMineOwners(unit, entity);
    }

    private void updateMineOwners(Mine unit, Entity entity){
        if(this._model.owner != null)
            tooltipModule.updateExtraTooltipText(entity, "Owner: " + unit.owner.player.getNicknameToken());
        else
            tooltipModule.updateExtraTooltipText(entity, "Owner: none");
    }

    @Override
    public void onRound() {
        boolean shouldDefend = shouldDefend();
        if(shouldDefend != wasDefending){
            wasDefending = shouldDefend;
            if(shouldDefend){
                goblinGroup.setZIndex(1).setY(3);
            }else{
                goblinGroup.setZIndex(-2).setY(0);
            }
        }
        if(_model.owner != _previousOwner){
            updateMineOwners(_model, _mineGroup);
            _previousOwner = _model.owner;
            _mineSprite.setImage(TileFactory.getInstance().mines[_model.owner == null ? 4 : _model.owner.player.getIndex()]);
            goblin.setImage(TileFactory.getInstance().goblins[_model.owner == null ? 4 : _model.owner.player.getIndex()]);
        }
    }

    private boolean shouldDefend() {
        for(Hero hero : _board.heroes){
            if(_model.tile.distance(hero.tile) == 1 && hero != _model.owner){
                return true;
            }
        }

        return false;
    }

    public Group getView() {
        return _mineGroup;
    }
}
