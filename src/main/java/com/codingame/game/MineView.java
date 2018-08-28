package com.codingame.game;

import com.codingame.gameengine.module.entities.Entity;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import modules.TooltipModule;
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

    public MineView(Mine model, GraphicEntityModule entityManager, TooltipModule tooltipModule) {
        _model = model;
        this.tooltipModule = tooltipModule;
        Tile tile = _model.tile;

        _mineGroup = entityManager.createGroup().setZIndex(tile.y);
        ViewController.moveEntity(_mineGroup, tile, -4, -4);

        Group goblinGroup = entityManager.createGroup();
        _mineGroup.add(goblinGroup);

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
        addMineTooltip(_model, _mineGroup);
    }


    private void addMineTooltip(Mine unit, Entity entity){
        Map<String, Object> params = new HashMap<>();
        params.put("Type", "Mine");

        //TODO: load parameters the viewer needs for the general tooltip contents.
        tooltipModule.registerEntity(entity, params);

        if(unit.owner != null)
            tooltipModule.updateExtraTooltipText(entity, "Owner: " + unit.owner.player.getNicknameToken());
    }

    private void updateMineOwners(Mine unit, Entity entity){
        if(this._model.owner != null)
            tooltipModule.updateExtraTooltipText(entity, "Owner: " + unit.owner.player.getNicknameToken());
    }

    @Override
    public void onRound() {
        if(_model.owner != _previousOwner){
            updateMineOwners(_model, _mineGroup);
            _previousOwner = _model.owner;
            _mineSprite.setImage(TileFactory.getInstance().mines[_model.owner == null ? 4 : _model.owner.player.getIndex()]);
        }
    }
    public Group getView() {
        return _mineGroup;
    }
}
