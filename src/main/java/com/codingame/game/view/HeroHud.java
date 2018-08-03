package com.codingame.game.view;

import com.codingame.game.Player;
import com.codingame.game.ViewConstants;
import com.codingame.gameengine.module.entities.*;
import vindinium.Hero;

public class HeroHud {

    private Group _container;
    private Hero _hero;
    private Text _messageText;
    private Text _goldText;
    private Text _healthText;
    public HeroHud(Hero hero, GraphicEntityModule graphicEntityModule, Player player, int x, int y, int width){
        _container = graphicEntityModule.createGroup().setZIndex(3).setX(x).setY(y);
        _hero = hero;
        _container.add(graphicEntityModule.createText(ViewConstants.cropString(player.getNicknameToken(), 14)).setFontFamily(ViewConstants.FontFamily).setX(120).setY(113/2).setAnchor(0.5).setZIndex(1).setFontSize(30).setFillColor(ViewConstants.getPlayerColor(player)));
        _container.add(_messageText = graphicEntityModule.createText("").setX(120).setY(113/2 + 25).setFontSize(20).setAnchorX(0.5));
        _container.add(_goldText = graphicEntityModule.createText("0").setX(195).setY(25).setFontSize(25).setAnchorX(1).setAnchorY(0.5));
        _container.add(graphicEntityModule.createSprite().setImage("coin.png").setX(205).setY(25).setAnchorY(0.5));
        _container.add(_healthText = graphicEntityModule.createText("Health: " + "100").setX(300).setY(80).setFontSize(20).setFillColor(0xff1a53).setAnchorX(0.5));
        _container.add(graphicEntityModule.createSprite().setImage("winner_parchment.png").setZIndex(-2).setAnchorX(0).setX(0));

        Group playerImageGroup = graphicEntityModule.createGroup().setX(250);
        _container.add(playerImageGroup);
        playerImageGroup.add(graphicEntityModule.createSprite().setImage("winner_parchment.png").setZIndex(-2).setBaseWidth(113).setTint(0xc0b9b4));
       // playerImageGroup.add(graphicEntityModule.createRectangle().setWidth(75).setX(113/2).setY(113/2).setHeight(75).setLineWidth(0).setFillColor(0x000000));
        Sprite mask = graphicEntityModule.createSprite().setImage("playerfilter.png").setAnchor(0.5).setBaseHeight(90).setBaseWidth(90).setX(113/2).setY(113/2).setZIndex(10);
        playerImageGroup.add(mask);
        playerImageGroup.add(graphicEntityModule.createSprite().setZIndex(1).setX(113/2).setY(113/2).setImage(player.getAvatarToken()).setMask(mask).setAnchor(0.5).setBaseHeight(100).setBaseWidth(100));
    }

    public void OnRound(String message){
        if(message.length() > 11) message = message.substring(0, 10);
        _messageText.setText(message);
        _goldText.setText(_hero.gold+"");
        _healthText.setText("Health: " + _hero.life);
    }
}
