package com.codingame.game;

import com.codingame.game.view.ViewController;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import vindinium.Board;
import vindinium.Config;
import vindinium.Tile;

import java.util.List;

public class BloodView implements IView {
    private Board _board;
    private GraphicEntityModule _entityModule;
    private String[] bloods;
    private Group _boardGroup;
    private int[][] hits;
    private int[][] bloodType;
    private Sprite[][] sprites;

    public BloodView(Board board, GraphicEntityModule entityModule, Group boardGroup) {
        _entityModule = entityModule;
        _boardGroup = boardGroup;
        _board = board;
        hits = new int[board.size][board.size];
        sprites = new Sprite[board.size][board.size];

        bloods = entityModule.createSpriteSheetLoader()
                .setSourceImage("blood.png")
                .setName("blood")
                .setWidth(32)
                .setHeight(32)
                .setImageCount(4 * 7)
                .setImagesPerRow(7)
                .setOrigCol(0)
                .setOrigRow(0)
                .load();

        bloodType = new int[board.size][board.size];
        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                bloodType[x][y] = Config.random.nextInt(4);
            }
        }
    }

    @Override
    public void onRound() {
        List<Tile> fightLocations = ViewController.fightLocations;
        for (Tile tile : fightLocations) {
            if (hits[tile.x][tile.y] == 6) continue; // no more sprites
            hits[tile.x][tile.y]++;
            sprites[tile.x][tile.y] = _entityModule.createSprite().setImage(bloods[1 + 7 * bloodType[tile.x][tile.y]])
                    .setZIndex(1)
                    .setAnchor(0.5)
                    .setBaseWidth(ViewController.CELL_SIZE)
                    .setBaseHeight(ViewController.CELL_SIZE)
                    .setX(ViewConstants.getCellPos(tile.x) + ViewController.CELL_SIZE - 4, Curve.NONE)
                    .setY(ViewConstants.getCellPos(tile.y) + ViewController.CELL_SIZE - 4, Curve.NONE);
            _boardGroup.add(sprites[tile.x][tile.y]);
        }
    }

    @Override
    public Group getView() {
        return null;
    }
}
