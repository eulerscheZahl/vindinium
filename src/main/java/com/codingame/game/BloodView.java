package com.codingame.game;

import java.util.List;

import com.codingame.game.view.ViewController;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;

import vindinium.Board;
import vindinium.Config;
import vindinium.Hero;
import vindinium.Tile;

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

		bloods = entityModule.createSpriteSheetSplitter().setSourceImage("blood.png").setName("blood").setWidth(32).setHeight(32).setImageCount(4 * 7).setImagesPerRow(7).setOrigCol(0).setOrigRow(0).split();

		bloodType = new int[board.size][board.size];
		for (int x = 0; x < board.size; x++) {
			for (int y = 0; y < board.size; y++) {
				bloodType[x][y] = Config.random.nextInt(4);
			}
		}
	}

	@Override
	public void onRound(int round) {
		List<Tile> fightLocations = ViewController.fightLocations;
		for (Tile tile : fightLocations) {
			if (hits[tile.x][tile.y] == 6)
				continue; // no more sprites
			hits[tile.x][tile.y]++;
			if (sprites[tile.x][tile.y] == null) {
				sprites[tile.x][tile.y] = _entityModule.createSprite();
				ViewController.moveEntity(sprites[tile.x][tile.y], tile, -4, -4);
				_boardGroup.add(sprites[tile.x][tile.y]);
			}
			sprites[tile.x][tile.y].setImage(bloods[hits[tile.x][tile.y] + 7 * bloodType[tile.x][tile.y]]);
		}

		for (Hero h : _board.heroes) {
			if (hits[h.tile.x][h.tile.y] > 0)
				h.lastBlood = round;
		}
	}
}
