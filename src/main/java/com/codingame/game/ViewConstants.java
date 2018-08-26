package com.codingame.game;

import com.codingame.game.view.ViewController;

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
}
