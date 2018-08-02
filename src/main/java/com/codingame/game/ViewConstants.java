package com.codingame.game;

public class ViewConstants {
    public static int SPRITE_SIZE = 32;
    public static int MAX_ROUNDS = 800; //TODO: set 800
    public static final String FontFamily = "Trebuchet MS";
    public static final int FrameLeft = (int)(15*1.2);
    public static final int FrameRight = (int)(1155*1.2);
    public static final int BarRight = (int)(1233*1.2);

    public static int getPlayerColor(Player player){
        if(player.getIndex()==3) return 0x766000;
        return player.getColorToken();
    }
}
