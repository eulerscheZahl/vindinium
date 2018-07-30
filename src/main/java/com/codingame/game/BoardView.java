package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import vindinium.Board;

public class BoardView {
    private Board _board;
    private GraphicEntityModule _graphicEntityModule;

    public BoardView(Board board, GraphicEntityModule graphicEntityModule){

        _board = board;
        _graphicEntityModule = graphicEntityModule;
    }

    public void Initialize(){

    }
}
