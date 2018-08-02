package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Text;

public class TurnCounterView implements IView {

    private Group _group;
    private Text _turnCounter;
    private int _round;
    public TurnCounterView(GraphicEntityModule entityModule){
        _group = entityModule.createGroup().setX(entityModule.getWorld().getHeight()+100).setY(30).setZIndex(100);
        _group.add(entityModule.createSprite().setImage("winner_parchment.png").setZIndex(-2));
        _group.add(entityModule.createText("Turn").setFillColor(0x000000).setFontSize(33).setX(120).setY(10).setFontFamily(ViewConstants.FontFamily).setAnchorX(0.5));
        _group.add(_turnCounter = entityModule.createText(getTurnCounter()).setFontSize(30).setFontFamily(ViewConstants.FontFamily).setY(60).setX(120).setAnchorX(0.5));
    }

    @Override
    public void onRound() {
        _round++;
        _turnCounter.setText(getTurnCounter());
    }

    private String getTurnCounter(){
        String total = (ViewConstants.MAX_ROUNDS/4)+"";
        String current = (_round/4)+"";
        return current + " / " + total;
    }

    @Override
    public Group getView() {
        return _group;
    }
}
