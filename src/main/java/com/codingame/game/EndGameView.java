package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import vindinium.Hero;

import java.util.ArrayList;
import java.util.List;

public class EndGameView {
    public EndGameView(GraphicEntityModule entityModule, List<Hero> heroes){
        int xCenter = entityModule.getWorld().getWidth()/2;
        int yCenter = entityModule.getWorld().getHeight()/2;
        Group group = entityModule.createGroup().setX(xCenter).setY(yCenter).setZIndex(1000000000);
        entityModule.createRectangle().setWidth(xCenter*2).setHeight(yCenter*2).setZIndex(1000000).setAlpha(0.8).setFillColor(0x000000).setLineWidth(0);

        group.add(entityModule.createText("VINDINIUM").setFontFamily(ViewConstants.ArialFont).setFontSize(100).setFillColor(0xafafaf).setY(-yCenter+100).setAnchor(0.5));
        int c = 0;
        int prev = -1;
        int pos = 1;
        for(Hero hero : heroes){
            if(prev != hero.gold) c = heroes.indexOf(hero)+1;
            prev = hero.gold;
            createHeroGroup(c, hero, group, entityModule, pos++);
        }
    }

    private void createHeroGroup(int position, Hero hero, Group group, GraphicEntityModule entityModule, int pos){
        String ending = "ST";
        if(position==2) ending = "ND";
        if(position==3) ending = "RD";
        if(position==4) ending = "TH";
        int yPos = pos*200-500;

        Group playerGroup = entityModule.createGroup().setY(yPos);
        group.add(playerGroup);

        playerGroup.add(entityModule.createRectangle().setWidth(150).setHeight(150).setFillColor(0x000000).setX(-300));
        playerGroup.add(entityModule.createSprite().setImage(hero.player.getAvatarToken()).setBaseWidth(150).setBaseHeight(150).setX(-300));
        playerGroup.add(entityModule.createRectangle().setWidth(150).setHeight(150).setFillColor(0xaaaaaa).setAlpha(0.2).setX(-150));

        playerGroup.add(entityModule.createText(""+position).setX(-90).setAnchor(0.5).setY(75).setFontSize(75).setFontFamily(ViewConstants.ArialFont).setFillColor(ViewConstants.getEndScreenColor(hero.player)));
        playerGroup.add(entityModule.createText(ending).setX(-45).setY(75-50/2).setAnchor(0.5).setFontSize(25).setFontFamily(ViewConstants.ArialFont).setFillColor(ViewConstants.getEndScreenColor(hero.player)));

        playerGroup.add(entityModule.createText(hero.player.getNicknameToken()).setX(50).setY(10).setFontSize(50).setFontFamily(ViewConstants.ArialFont).setFillColor(ViewConstants.getEndScreenColor(hero.player)));
        playerGroup.add(entityModule.createText(hero.gold+ " gold").setX(50).setY(150-10).setAnchorY(1).setFontSize(40).setFontFamily(ViewConstants.ArialFont).setFillColor(0xffffff));
    }
}
