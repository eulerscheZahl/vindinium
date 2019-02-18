package com.codingame.game.view;

import java.util.HashMap;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

public class TileFactory {

	@Inject
	GraphicEntityModule graphicEntityModule;

	private static TileFactory instance;

	private static String[] lowlands;

	public static TileFactory getInstance() {
		if (instance != null)
			return instance;

		instance = new TileFactory();
		return instance;
	}

	HashMap<String, String> tiles = new HashMap<>();
	String[] earthStuff;
	String[] rockStuff;
	String tree;
	String groundTexture;
	String topLeftCornerTexture;
	String bottomLeftCornerTexture;
	String bottomRightCornerTexture;
	String topRightCornerTexture;
	String topBorderTexture;
	String bottomBorderTexture;
	String leftBorderTexture;
	String rightBorderTexture;
	String[] possibleWallObjectsTexture;
	public String[] mines;
	public String[] goblins;
	public String[] heroes;
	String[] spawns;

	public void init(GraphicEntityModule graphicEntityModule) {
		this.graphicEntityModule = graphicEntityModule;

		mines = instance.graphicEntityModule.createSpriteSheetSplitter().setSourceImage("mines.png").setName("m").setWidth(34).setHeight(34).setImageCount(1 * 5).setImagesPerRow(1).setOrigCol(0).setOrigRow(0).split();

		goblins = instance.graphicEntityModule.createSpriteSheetSplitter().setSourceImage("goblins.png").setName("b").setWidth(34).setHeight(34).setImageCount(1 * 5).setImagesPerRow(1).setOrigCol(0).setOrigRow(0).split();

		heroes = instance.graphicEntityModule.createSpriteSheetSplitter().setSourceImage("heroes.png").setName("h").setWidth(34).setHeight(34).setImageCount(9 * 6).setImagesPerRow(9).setOrigCol(0).setOrigRow(0).split();
		spawns = new String[] { heroes[0 * 9 + 4], heroes[1 * 9 + 4], heroes[2 * 9 + 4], heroes[3 * 9 + 4]
		};

		lowlands = instance.graphicEntityModule.createSpriteSheetSplitter().setSourceImage("lowlands_24.png").setName("l").setWidth(24).setHeight(24).setImageCount(16 * 6).setImagesPerRow(16).setOrigCol(0).setOrigRow(0).split();

		Object[][] tilesConf = {
				// Primitive tiles
				{ "plain", 1, 1 }, { "water", 3, 2 }, { "earth", 4, 2 }, { "rock", 4, 3 }, { "empty", 3, 3 },

				// Water / Plain
				{ "water_plain_se", 0, 0 }, { "water_plain_s", 1, 0 }, { "water_plain_sw", 2, 0 }, { "water_plain_e", 0, 1 }, { "water_plain_w", 2, 1 }, { "water_plain_ne", 0, 2 }, { "water_plain_n", 1, 2 }, { "water_plain_nw", 2, 2 }, { "plain_water_se", 3, 0 }, { "plain_water_ne", 3, 1 }, { "plain_water_sw", 4, 0 }, { "plain_water_nw", 4, 1 },

				// Earth / Plain
				{ "earth_plain_se", 5, 0 }, { "earth_plain_s", 6, 0 }, { "earth_plain_sw", 7, 0 }, { "earth_plain_e", 5, 1 }, { "earth_plain_w", 7, 1 }, { "earth_plain_ne", 5, 2 }, { "earth_plain_n", 6, 2 }, { "earth_plain_nw", 7, 2 }, { "plain_earth_se", 8, 0 }, { "plain_earth_ne", 8, 1 }, { "plain_earth_sw", 9, 0 }, { "plain_earth_nw", 9, 1 },

				// Rock / Plain
				{ "rock_plain_se", 5, 3 }, { "rock_plain_s", 6, 3 }, { "rock_plain_sw", 7, 3 }, { "rock_plain_e", 5, 4 }, { "rock_plain_w", 7, 4 }, { "rock_plain_ne", 5, 5 }, { "rock_plain_n", 6, 5 }, { "rock_plain_nw", 7, 5 }, { "plain_rock_se", 8, 2 }, { "plain_rock_ne", 8, 3 }, { "plain_rock_sw", 9, 2 }, { "plain_rock_nw", 9, 3 },

				// Empty / Plain
				{ "empty_plain_se", 0, 3 }, { "empty_plain_s", 1, 3 }, { "empty_plain_sw", 2, 3 }, { "empty_plain_e", 0, 4 }, { "empty_plain_w", 2, 4 }, { "empty_plain_ne", 0, 5 }, { "empty_plain_n", 1, 5 }, { "empty_plain_nw", 2, 5 }, { "plain_empty_se", 3, 4 }, { "plain_empty_ne", 3, 5 }, { "plain_empty_sw", 4, 4 }, { "plain_empty_nw", 4, 5 },

				// Water / Earth
				{ "water_earth_se", 10, 2 }, { "water_earth_s", 11, 2 }, { "water_earth_sw", 12, 2 }, { "water_earth_e", 10, 3 }, { "water_earth_w", 12, 3 }, { "water_earth_ne", 10, 4 }, { "water_earth_n", 11, 4 }, { "water_earth_nw", 12, 4 }, { "earth_water_se", 8, 4 }, { "earth_water_ne", 8, 5 }, { "earth_water_sw", 9, 4 }, { "earth_water_nw", 9, 5 },

				// Water / Rock
				{ "water_rock_se", 13, 2 }, { "water_rock_s", 14, 2 }, { "water_rock_sw", 15, 2 }, { "water_rock_e", 13, 3 }, { "water_rock_w", 15, 3 }, { "water_rock_ne", 13, 4 }, { "water_rock_n", 14, 4 }, { "water_rock_nw", 15, 4 }, { "rock_water_se", 12, 0 }, { "rock_water_ne", 12, 1 }, { "rock_water_sw", 13, 0 }, { "rock_water_nw", 13, 1 },

				// Plain extras
				{ "plain_grass1", 10, 0 }, { "plain_grass2", 11, 0 }, { "plain_grass3", 11, 1 }, { "plain_flower", 10, 1 },

				// alias names
				{ "plain_earth_n", 6, 0 }, { "plain_earth_w", 5, 1 }, { "plain_earth_e", 7, 1 }, { "plain_earth_s", 6, 2 }, { "plain_rock_n", 6, 3 }, { "plain_rock_w", 5, 4 }, { "plain_rock_e", 7, 4 }, { "plain_rock_s", 6, 5 },
		};
		for (Object[] objects : tilesConf) {
			tiles.put((String) objects[0], lowlands[(int) objects[2] * 16 + (int) objects[1]]);
		}

		String[] farm = instance.graphicEntityModule.createSpriteSheetSplitter().setSourceImage("farming_fishing_24.png").setName("f").setWidth(24).setHeight(24).setImageCount(7 * 2).setImagesPerRow(7).setOrigCol(0).setOrigRow(0).split();
		earthStuff = new String[] { farm[0 * 7 + 0], // medium wood
				farm[0 * 7 + 1], // big wood
				farm[0 * 7 + 2], // seeds
				farm[0 * 7 + 3], // tools
				farm[0 * 7 + 4], // green weird seeds
				farm[0 * 7 + 5], // forge
				farm[0 * 7 + 6], // seed bag
				farm[1 * 7 + 0], // forge with hammer
				farm[1 * 7 + 1], // hammers
				farm[1 * 7 + 2], // tools 2
				farm[1 * 7 + 3], // cut tools 1
				farm[1 * 7 + 4], // cut tools 2
				farm[1 * 7 + 5], // cut tools 3
				farm[1 * 7 + 6] // cut tools 4
		};

		String[] rocks = instance.graphicEntityModule.createSpriteSheetSplitter().setSourceImage("stuff.png").setName("s").setWidth(24).setHeight(24).setImageCount(1 * 10).setImagesPerRow(1).setOrigCol(0).setOrigRow(0).split();
		rockStuff = new String[] { rocks[1 * 1 + 0], rocks[2 * 1 + 0], rocks[3 * 1 + 0]
		};

		String[] trees = instance.graphicEntityModule.createSpriteSheetSplitter().setSourceImage("tree.png").setName("t").setWidth(24).setHeight(24).setImageCount(1 * 1).setImagesPerRow(1).setOrigCol(0).setOrigRow(0).split();
		tree = trees[0 * 1 + 0];

		String[] ground = instance.graphicEntityModule.createSpriteSheetSplitter().setSourceImage("plowed_soil_24.png").setName("g").setWidth(24).setHeight(24).setImageCount(3 * 6).setImagesPerRow(3).setOrigCol(0).setOrigRow(0).split();
		groundTexture = ground[3 * 5 + 0];
		topLeftCornerTexture = ground[3 * 2 + 0];
		bottomLeftCornerTexture = ground[3 * 4 + 0];
		bottomRightCornerTexture = ground[3 * 4 + 2];
		topRightCornerTexture = ground[3 * 2 + 2];
		topBorderTexture = ground[3 * 2 + 1];
		bottomBorderTexture = ground[3 * 4 + 1];
		leftBorderTexture = ground[3 * 3 + 0];
		rightBorderTexture = ground[3 * 3 + 2];

		possibleWallObjectsTexture = new String[] { farm[0 * 7 + 0], farm[0 * 7 + 1], rocks[1 * 0 + 0], rocks[1 * 1 + 0], rocks[2 * 1 + 0], rocks[3 * 1 + 0] };
	}

}