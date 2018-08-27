package com.codingame.game.view;

import com.codingame.game.HeroView;
import com.codingame.game.Player;
import com.codingame.game.ViewConstants;
import com.codingame.gameengine.module.entities.*;
import vindinium.Hero;

public class HeroHud {

    public Group _container;
    public Hero _hero;
    private Text _messageText;
    private Text _goldText;
    private Sprite _medal;
    private GraphicEntityModule _graphicEntityModule;
    public HeroHud(Hero hero, GraphicEntityModule graphicEntityModule, Player player, int x, int y, int width){
        _graphicEntityModule = graphicEntityModule;
        _hero = hero;

        _container = graphicEntityModule.createGroup().setZIndex(3).setX(x).setY(y);
        _container.add(graphicEntityModule.createText(ViewConstants.cropString(player.getNicknameToken(), 14)).setFontFamily(ViewConstants.FontFamily).setX(120).setY(113/2).setAnchor(0.5).setZIndex(1).setFontSize(30).setFillColor(ViewConstants.getPlayerColor(player)));
        _container.add(_messageText = graphicEntityModule.createText("").setX(15).setY(113/2 + 25).setFontSize(20).setAnchorX(0));
        _container.add(_goldText = graphicEntityModule.createText("0").setX(195).setY(25).setFontSize(25).setAnchorX(1).setAnchorY(0.5));
        _container.add(graphicEntityModule.createSprite().setImage("coin.png").setX(205).setY(25).setAnchorY(0.5));
        _container.add(graphicEntityModule.createSprite().setImage("winner_parchment.png").setZIndex(-2).setAnchorX(0).setX(0));
        _container.add(graphicEntityModule.createSprite().setImage("player"+(player.getIndex()+1)+".png").setX(15).setY(25).setAnchorY(0.5));
        _container.add(_medal = graphicEntityModule.createSprite().setImage("award.png").setX(55).setScale(1.5).setY(25).setAnchorY(0.5).setAlpha(0));


        Group playerImageGroup = graphicEntityModule.createGroup().setX(250);
        _container.add(playerImageGroup);
        playerImageGroup.add(graphicEntityModule.createSprite().setImage("winner_parchment.png").setZIndex(-2).setBaseWidth(113).setTint(0xc0b9b4));
        Sprite mask = graphicEntityModule.createSprite().setImage("playerfilter.png").setAnchor(0.5).setBaseHeight(90).setBaseWidth(90).setX(113/2).setY(113/2).setZIndex(10);
        playerImageGroup.add(mask);
        playerImageGroup.add(graphicEntityModule.createSprite().setZIndex(1).setX(113/2).setY(113/2).setImage(player.getAvatarToken()).setMask(mask).setAnchor(0.5).setBaseHeight(100).setBaseWidth(100));
    }

    public void OnRound(String message){
        if(!ViewConstants.cropString(message, 10).equals(_messageText.getText()))
            _messageText.setText(ViewConstants.cropString(message, 10));

        if(!_goldText.getText().equals(_hero.gold+""))
            _goldText.setText(_hero.gold+"");
    }

    public void setLeader(boolean leading){
        if(leading)_medal.setAlpha(1);
        else _medal.setAlpha(0);
        _hero.leading = leading;
    }
}