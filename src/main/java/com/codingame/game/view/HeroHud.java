package com.codingame.game.view;

import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.*;
import vindinium.Hero;

public class HeroHud {
    private final String fontFamily = "Trebuchet MS";
    private Group _container;
    private Hero _hero;
    private Text _messageText;
    private Text _goldText;
    private Text _healthText;
    public HeroHud(Hero hero, GraphicEntityModule graphicEntityModule, Player player, int x, int y, int width){
        _container = graphicEntityModule.createGroup().setZIndex(3).setX(x).setY(y);
        _hero = hero;
        _container.add(graphicEntityModule.createText(player.getNicknameToken()).setFontFamily(fontFamily).setX(width/2).setY(0).setAnchorX(0.5).setZIndex(1).setFontSize(30).setFillColor(player.getColorToken()));
        _container.add(graphicEntityModule.createSprite().setX(50).setY(10).setZIndex(1).setImage(player.getAvatarToken()).setAnchorX(0.5).setBaseHeight(100).setBaseWidth(100));
        _container.add(_messageText = graphicEntityModule.createText("").setX(300).setY(10).setFillColor(0xc0c0c0).setFontSize(20).setAnchorX(0.5));
        _container.add(_goldText = graphicEntityModule.createText("Gold: " + "0").setX(300).setY(45).setFontSize(20).setFillColor(0xffff00).setAnchorX(0.5));
        _container.add(_healthText = graphicEntityModule.createText("Health: " + "100").setX(300).setY(80).setFontSize(20).setFillColor(0xff1a53).setAnchorX(0.5));
        _container.add(graphicEntityModule.createSprite().setImage("winner_parchment.png").setZIndex(-2).setBaseWidth(width-10).setAnchorX(0));
    }

    public void OnRound(String message){
        if(message.length() > 10) message = message.substring(0, 10);
        _messageText.setText(message);
        _goldText.setText("Gold: " + _hero.gold);
        _healthText.setText("Health: " + _hero.life);
    }
}
