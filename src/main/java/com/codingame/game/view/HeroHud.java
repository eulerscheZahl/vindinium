package com.codingame.game.view;

import com.codingame.game.HeroView;
import com.codingame.game.Player;
import com.codingame.game.ViewConstants;
import com.codingame.gameengine.module.entities.*;
import org.apache.commons.lang3.StringUtils;
import vindinium.Board;
import vindinium.Hero;

public class HeroHud {

    public Group _container;
    public Hero _hero;
    private Text _messageText;
    private Text _goldText;
    private Sprite _medal;
    private Text _mineCounter;
    private GraphicEntityModule _graphicEntityModule;
    public HeroHud(Hero hero, GraphicEntityModule graphicEntityModule, Player player, int x, int y, int width){
        _graphicEntityModule = graphicEntityModule;
        _hero = hero;
//50 -150
        _container = graphicEntityModule.createGroup().setZIndex(3).setX(x).setY(y);
        _container.add(graphicEntityModule.createSprite().setImage("playerframe.png").setZIndex(-2).setAnchorX(0).setX(0));


        int messageY = 143;
        int goldY = 77;
        int mineY = 110;
        int iconX = 165;
        int textX = 210;

      //  _container.add(graphicEntityModule.createSprite().setImage("messagebobble.png").setBaseWidth(23).setBaseHeight(26).setX(iconX).setY(messageY).setAnchor(0.5));
        _container.add(_messageText = graphicEntityModule.createText("").setX(iconX-15).setY(messageY-10).setFontSize(25).setAnchorY(0).setAnchorX(0));

        _container.add(graphicEntityModule.createSprite().setImage("coin.png").setX(iconX).setY(goldY).setAnchor(0.5));
        _container.add(_goldText = graphicEntityModule.createText("0").setX(textX).setY(goldY).setFontSize(25).setAnchorX(0).setAnchorY(0.5));

        _container.add(graphicEntityModule.createSprite().setImage(TileFactory.getInstance().mines[player.getIndex()]).setX(iconX).setY(mineY).setAnchor(0.5));
        _container.add(_mineCounter = graphicEntityModule.createText("0").setX(textX).setY(mineY).setFontSize(25).setAnchorX(0).setAnchorY(0.5));

        int toplineY = 32;
        _container.add(graphicEntityModule.createSprite().setImage("player"+(player.getIndex()+1)+".png").setX(27).setY(toplineY).setAnchorY(0.5).setZIndex(0));
        _container.add(_medal = graphicEntityModule.createSprite().setImage("award.png").setX(300).setScale(1.5).setY(toplineY).setAnchorY(0.5).setAlpha(0));
        _container.add(graphicEntityModule.createText(ViewConstants.cropString(player.getNicknameToken(), 14)).setFontFamily(ViewConstants.FontFamily).setX(width/2).setY(toplineY).setAnchor(0.5).setZIndex(1).setFontSize(30).setFillColor(ViewConstants.getPlayerColor(player)));


       // Group playerImageGroup = graphicEntityModule.createGroup().setX(250);
       // _container.add(playerImageGroup);
       // playerImageGroup.add(graphicEntityModule.createSprite().setImage("winner_parchment.png").setZIndex(-2).setBaseWidth(113).setTint(0xc0b9b4));
       // Sprite mask = graphicEntityModule.createSprite().setImage("playerfilter.png").setAnchor(0.5).setBaseHeight(90).setBaseWidth(90).setX(113/2).setY(113/2).setZIndex(10);
       // playerImageGroup.add(mask);
        _container.add(graphicEntityModule.createSprite().setZIndex(-10).setX(83).setY(115).setImage(player.getAvatarToken()).setAnchor(0.5).setBaseHeight(110).setBaseWidth(110));
    }

    public void OnRound(String message, Board board){
        String msg=ViewConstants.cropString(message, 7);
        //if(msg == null)  msg = "";
       // msg = StringUtils.rightPad(msg, 20);
       // msg =msg.substring(0,7) + "\n" + msg.substring(7, 14);
        if(!msg.equals(_messageText.getText()))
            _messageText.setText(msg);

        if(!_goldText.getText().equals(_hero.gold+""))
            _goldText.setText(_hero.gold+"");

        int mines = board.countMines(_hero);
        if(!_mineCounter.getText().equals(mines+""))
            _mineCounter.setText(mines+"");

    }

    public void setLeader(boolean leading){
        if(leading)_medal.setAlpha(1);
        else _medal.setAlpha(0);
        _hero.leading = leading;
    }
}