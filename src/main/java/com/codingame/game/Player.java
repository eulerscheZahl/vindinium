package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import vindinium.Hero;

public class Player extends AbstractMultiplayerPlayer {
    private int _expected = 1;
    @Override
    public int getExpectedOutputLines() {
        return _expected;
    }

    public void setDeactivated(){
        _expected = 0;
    }

    public Hero hero;
}
