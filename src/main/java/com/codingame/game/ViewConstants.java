package com.codingame.game;

import com.codingame.game.view.ViewController;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Line;
import com.codingame.gameengine.module.entities.SpriteAnimation;

public class ViewConstants {
    public static int SPRITE_SIZE = 32;
    public static int MAX_ROUNDS = 800;
    public static final String FontFamily = "Trebuchet MS";
    public static final String ArialFont = "Arial Black";

    public static final int FrameLeft = (int)(15*1.2);
    public static final int FrameRight = (int)(1155*1.2);
    public static final int BarRight = (int)(1233*1.2);

    public static int getPlayerColor(Player player){
        if(player.getIndex()==3) return 0x766000;
        return player.getColorToken();
    }

    public static int getCellPos(int pos){
        return (1 + ViewController.CELL_SIZE)*pos;
    }

    public static String cropString(String s, int length){
        if(s.length() > length) return s.substring(0, length);
        return s;
    }

    public static int getEndScreenColor(Player player){
        if(player.getIndex()==1) return 0x9999FF; //33AAFF
        if(player.getIndex()==3) return 0xEFFD5F;
        if(player.getIndex()==0) return 0xFF8080;
        else return 0x00C200;
    }

    public static SpriteAnimation createAnimation(GraphicEntityModule module, boolean loop, int duration, String... images){
        return module.createSpriteAnimation().setImages(images).setLoop(loop).setDuration(duration);
    }

    public static int getAngleFromLastDir(int dir){
      // if (tile.y < target.y) lastDir = 0;
      // else if (tile.x > target.x) lastDir = 1;
      // else if (tile.x < target.x) lastDir = 2;
      // else if (tile.y > target.y) lastDir = 3;
        if(dir == 0) return 270;
        if(dir == 1) return 0;
        if(dir == 2) return 180;
        return 90;
    }

    public static double getRadAngle(int angle){
        return Math.toRadians(angle);
    }

    public static Group createGrid(GraphicEntityModule module, int size){
        int cellWidth = ViewController.CELL_SIZE;
        int width = ViewController.CELL_SIZE*size;
        int height = ViewController.CELL_SIZE*size;
        Group group = module.createGroup();
        for(int i = 1; i < size+2; i++)
        {
            group.add(createLine(module, cellWidth, width+cellWidth+1, i*cellWidth, i*cellWidth));
        }
        for(int j = 1; j < size+2; j++)
        {
            group.add(createLine(module, j*cellWidth, j*cellWidth, cellWidth, height+cellWidth+1));
        }

        return group.setAlpha(0.3);
    }

    private static Line createLine(GraphicEntityModule module, int x0, int x1, int y0, int y1){
        return module.createLine().setX(x0).setY(y0).setX2(x1).setY2(y1).setFillColor(0x000000).setLineWidth(1);
    }
}
