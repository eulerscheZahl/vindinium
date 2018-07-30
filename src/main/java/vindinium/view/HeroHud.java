package vindinium.view;

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
        _container = graphicEntityModule.createGroup().setX(width/2+x).setY(y);
        _hero = hero;
        _container.add(graphicEntityModule.createText(player.getNicknameToken()).setFontFamily(fontFamily).setX(5).setY(0).setAnchorX(0.5).setZIndex(1).setFontSize(30).setFillColor(player.getColorToken()));
        _container.add(graphicEntityModule.createSprite().setX(5).setY(50).setZIndex(1).setImage(player.getAvatarToken()).setAnchorX(0.5).setBaseHeight(175).setBaseWidth(175));
        _container.add(_messageText = graphicEntityModule.createText("").setX(5).setY(300).setFillColor(0xc0c0c0).setFontSize(20).setAnchorX(0.5));
        _container.add(_goldText = graphicEntityModule.createText("Gold: " + "0").setX(5).setY(350).setFontSize(20).setFillColor(0xffff00).setAnchorX(0.5));
        _container.add(_healthText = graphicEntityModule.createText("Health: " + "1000").setX(5).setY(400).setFontSize(20).setFillColor(0xff1a53).setAnchorX(0.5));

        // _container.add(_healthBar = graphicEntityModule.createLine().setX(0).);

    }

    public void OnRound(String message){
        _messageText.setText(message);
        _goldText.setText("Gold: " + _hero.gold);
        _healthText.setText("Health: " + _hero.life);
     //   _healthBar.setScaleX(Math.min(1,Math.max(0,_hero.life) / (double) _hero.maxLife));
    }
}
