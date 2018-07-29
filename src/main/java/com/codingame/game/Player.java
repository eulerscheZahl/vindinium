package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import vindinium.Hero;

public class Player extends AbstractMultiplayerPlayer {
    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public Hero hero;
}
