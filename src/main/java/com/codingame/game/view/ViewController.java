package com.codingame.game.view;

import com.codingame.game.*;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.Entity;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import modules.TooltipModule;
import vindinium.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ViewController {
    private GraphicEntityModule entityManager;
    private MultiplayerGameManager<Player> gameManager;
    private TooltipModule tooltipModule;
    public static int CELL_SIZE = 24;
    private Board board;
    public Group boardGroup;
    private ArrayList<IView> _views = new ArrayList<>();
    private ArrayList<HeroView> _heroes = new ArrayList<>();
    public static List<Tile> fightLocations;

    public ViewController(GraphicEntityModule entityManager, MultiplayerGameManager<Player> gameManager, TooltipModule tooltipModule) {
        this.entityManager = entityManager;
        this.gameManager = gameManager;
        this.tooltipModule = tooltipModule;

        TileFactory.getInstance().init(entityManager);
    }

    public void createGrid(Board board) {
        entityManager.createSprite().setImage("Frame.png").setZIndex(2).setAnchor(0).setScale(1.2);
        entityManager.createRectangle().setFillColor(0x5c8ab8).setZIndex(-100000).setLineWidth(0).setWidth(1920).setHeight(1080);

        this.board = board;
        double terrainRandomnessFactor = 0.7 + 0.2 * Math.random();
        double terrainMineTavernFactor = 1.4 + 0.8 * Math.random();

        List<Tile> allTiles = new ArrayList<>();
        for (int y = 0; y < board.size; y++) {
            for (int x = 0; x < board.size; x++) {
                allTiles.add(board.tiles[x][y]);
            }
        }
        List<Tile> taverns = allTiles.stream().filter(tile -> tile.type == Tile.Type.Tavern).collect(Collectors.toList());
        List<Tile> mines = allTiles.stream().filter(tile -> tile.type == Tile.Type.Mine).collect(Collectors.toList());

        double[] noise = generatePerlinNoise(board.size, board.size, 4, 0.05, 0.3);
        String[][] tileTypes = new String[board.size][board.size];
        for (int i = 0; i < allTiles.size(); i++) {
            Tile t = allTiles.get(i);
            double mineFactor = 0, tavernFactor = 0;
            for (Tile tavern : taverns) {
                tavernFactor += smoothstep(6, 0, t.distance(tavern));
            }
            for (Tile mine : mines) {
                mineFactor += smoothstep(9, 1, t.distance(mine));
            }
            tavernFactor /= taverns.size();
            mineFactor /= mines.size();

            double value = noise[i] * terrainRandomnessFactor + terrainMineTavernFactor * (mineFactor - tavernFactor);
            tileTypes[t.x][t.y] = value < 0.3 ? "earth" : value > 0.8 - 0.1 * Math.random() ? "rock" : "plain";
        }

        List<Tile> waterTiles = connected(allTiles.stream()
                        .filter(t -> indexIsWaterEnoughSurroundedInitial(t))
                        .collect(Collectors.toList()),
                new ArrayList<Tile>(),
                t -> indexIsWaterEnoughSurroundedInitial(t));
        final List<Tile> initialWater = waterTiles.stream().collect(Collectors.toList());
        waterTiles = waterTiles.stream().filter(t -> indexIsWaterEnoughSurroundedExpand(t, initialWater)).collect(Collectors.toList());
        final List<Tile> finalWater = waterTiles.stream().collect(Collectors.toList());
        int xPos = (entityManager.getWorld().getWidth() - entityManager.getWorld().getHeight()) / 2;
        boardGroup = this.entityManager.createBufferedGroup().setScale(1080.0 / (CELL_SIZE * (board.size + 2))).setX((ViewConstants.FrameRight - ViewConstants.FrameLeft - 1080) / 2 + ViewConstants.FrameLeft);
        for (int y = -1; y <= board.size; y++) {
            for (int x = -1; x <= board.size; x++) {
                Group group = this.entityManager.createGroup();
                boardGroup.add(group);
                group.setX(CELL_SIZE * (x + 1))
                        .setY(CELL_SIZE * (y + 1));

                boolean outOfGrid = x == -1 || x == board.size || y == -1 || y == board.size;
                boolean isWater = outOfGrid || waterTiles.contains(board.tiles[x][y]);

                String name = compassRoseTileName(groundForPosition(x, y, tileTypes),
                        "water",
                        tile -> tile != null && !finalWater.contains(tile),
                        s -> TileFactory.getInstance().tiles.containsKey(s),
                        outOfGrid ? null : board.tiles[x][y]);
                if (outOfGrid) {
                    String dir = "";
                    int[] dx = {0, 0, 1, -1, 1, -1, 1, -1};
                    int[] dy = {1, -1, 0, 0, 1, 1, -1, -1};
                    String[] orientation = {"s", "n", "e", "w", "se", "sw", "ne", "nw"};
                    for (int i = 0; i < 8; i++) {
                        int x_ = x + dx[i];
                        int y_ = y + dy[i];
                        if (board.IsOnBoard(x_, y_) && !waterTiles.contains(board.tiles[x_][y_])) {
                            dir += orientation[i];
                            name = "water_" + tileTypes[x_][y_];
                        }
                        if (i >= 3 && !dir.equals("")) break;
                    }

                    if (!dir.equals(""))
                        name += "_" + dir;
                }

                if (!name.contains("water")) {
                    String current = tileTypes[x][y];
                    String north = y > 0 ? tileTypes[x][y - 1] : current;
                    String east = x < board.size - 1 ? tileTypes[x + 1][y] : current;
                    String south = y < board.size - 1 ? tileTypes[x][y + 1] : current;
                    String west = x > 0 ? tileTypes[x - 1][y] : current;
                    String dir = "";
                    String other = "";
                    if (hirarchyLess(current, north)) {
                        dir += "n";
                        other = north;
                    }
                    if (hirarchyLess(current, south)) {
                        dir += "s";
                        other = south;
                    }
                    if (hirarchyLess(current, east)) {
                        dir += "e";
                        other = east;
                    }
                    if (hirarchyLess(current, west)) {
                        dir += "w";
                        other = west;
                    }
                    if (!other.equals("")) {
                        String tmp = name + "_" + other + "_" + dir;
                        if (TileFactory.getInstance().tiles.containsKey(tmp)) name = tmp;
                    }
                }
                if (name.equals("plain")) {
                    double center = 2 * (0.5 - Math.abs(noise[x + y * board.size] - 0.5));
                    if (Math.random() < 0.2 * center) {
                        String[] choices = {
                                "plain_grass1",
                                "plain_grass2",
                                "plain_flower",
                                "plain_grass3"
                        };
                        name = choices[(int) (Math.random() * Math.random() * 4)];
                    }
                }
                name = TileFactory.getInstance().tiles.get(name);

                Sprite background = entityManager.createSprite()
                        .setImage(name)
                        .setBaseHeight(CELL_SIZE)
                        .setBaseWidth(CELL_SIZE)
                        .setAlpha(1.0)
                        .setZIndex(-1);
                group.add(background);

                if (!outOfGrid && !isWater) {
                    if (board.tiles[x][y].type == Tile.Type.Wall) {
                        String[] cand = new String[]{TileFactory.getInstance().tree};
                        if (tileTypes[x][y].equals("rock")) cand = TileFactory.getInstance().rockStuff;
                        if (tileTypes[x][y].equals("earth")) cand = TileFactory.getInstance().earthStuff;
                        String obst = cand[Config.random.nextInt(cand.length)];
                        Sprite obstacle = entityManager.createSprite()
                                .setImage(obst)
                                .setBaseHeight(CELL_SIZE)
                                .setBaseWidth(CELL_SIZE)
                                .setAlpha(1.0)
                                .setZIndex(-1);
                        group.add(obstacle);
                    }
                    if (board.tiles[x][y].type == Tile.Type.Tavern) {
                        Sprite tav = entityManager.createSprite()
                                .setImage("beer2.png")
                                .setBaseHeight(CELL_SIZE)
                                .setBaseWidth(CELL_SIZE)
                                .setAlpha(1.0)
                                .setZIndex(-1);
                        group.add(tav);
                    }
                }
            }
        }


        for (Hero hero : board.heroes) {
            HeroView view = new HeroView(hero, entityManager);
            _heroes.add(view);
            _views.add(view);
          //  createTooltip(view._model, view.getView());
            boardGroup.add(view.getView());
        }

        for (Mine mine : board.mines) {
            MineView view = new MineView(mine, entityManager);
            _views.add(view);
            boardGroup.add(view.getView());
        }

        _views.add(new GoldCounterView(board.heroes, entityManager));
        _views.add(new BloodView(board, entityManager, boardGroup));

        //TOO MUCH DATA :sob:
        _views.add(new FootstepsView(board.heroes, entityManager, boardGroup));
    }

    public void onRound(List<Tile> fightLocations) {
        ViewController.fightLocations = fightLocations;
        for(IView view : _views){
            view.onRound();
        }

        for(HeroView view : _heroes){
       //     updateTooltip(view._model, view.getView());
        }
    }

    public void setSpawn(Tile tile, int index) {
        Group group = this.entityManager.createGroup().setZIndex(-2);
        group.setX(CELL_SIZE * (tile.x + 1) - 4)
                .setY(CELL_SIZE * (tile.y + 1) - 4).setZIndex(9);
        Sprite spawn = entityManager.createSprite()
                .setImage(TileFactory.getInstance().spawns[index])
                .setBaseHeight(CELL_SIZE)
                .setBaseWidth(CELL_SIZE)
                .setAlpha(1.0);
        group.add(spawn);
        boardGroup.add(group);
    }

    private boolean hirarchyLess(String s1, String s2) {
        HashMap<String, Integer> hirarchy = new HashMap<>();
        hirarchy.put(null, 0);
        hirarchy.put("water", 0);
        hirarchy.put("earth", 1);
        hirarchy.put("rock", 2);
        hirarchy.put("plain", 3);
        return hirarchy.get(s1) < hirarchy.get(s2);
    }

    private String compassRoseTileName(String primaryName, String secondaryName, Predicate<Tile> isPrimary, Predicate<String> isValidName, Tile tile) {
        String p = primaryName;
        String s = secondaryName;

        boolean P = isPrimary.test(board.neighbors9(tile, 8));
        boolean N = isPrimary.test(board.neighbors9(tile, 0));
        boolean S = isPrimary.test(board.neighbors9(tile, 2));
        boolean W = isPrimary.test(board.neighbors9(tile, 3));
        boolean E = isPrimary.test(board.neighbors9(tile, 1));
        boolean NW = isPrimary.test(board.neighbors9(tile, 4));
        boolean SW = isPrimary.test(board.neighbors9(tile, 5));
        boolean NE = isPrimary.test(board.neighbors9(tile, 6));
        boolean SE = isPrimary.test(board.neighbors9(tile, 7));

        int nb = (N ? 1 : 0) + (NE ? 1 : 0) + (E ? 1 : 0) + (SE ? 1 : 0) + (S ? 1 : 0) + (SW ? 1 : 0) + (W ? 1 : 0) + (NW ? 1 : 0);
        String name = "";

        if (P) {
            return p;
        } else {
            if (nb == 0) return s;
            if (N && S || W && E) return s;

            if (SE && nb == 1) name = s + "_" + p + "_se";
            if (isValidName.test(name)) return name;
            if (SW && nb == 1) name = s + "_" + p + "_sw";
            if (isValidName.test(name)) return name;
            if (NE && nb == 1) name = s + "_" + p + "_ne";
            if (isValidName.test(name)) return name;
            if (NW && nb == 1) name = s + "_" + p + "_nw";
            if (isValidName.test(name)) return name;

            if (N && W) name = p + "_" + s + "_se";
            if (isValidName.test(name)) return name;
            if (S && W) name = p + "_" + s + "_ne";
            if (isValidName.test(name)) return name;
            if (N && E) name = p + "_" + s + "_sw";
            if (isValidName.test(name)) return name;
            if (S && E) name = p + "_" + s + "_nw";
            if (isValidName.test(name)) return name;

            if (E && !W) name = s + "_" + p + "_e";
            if (isValidName.test(name)) return name;
            if (W && !E) name = s + "_" + p + "_w";
            if (isValidName.test(name)) return name;
            if (N && !S) name = s + "_" + p + "_n";
            if (isValidName.test(name)) return name;
            if (S && !N) name = s + "_" + p + "_s";
            if (isValidName.test(name)) return name;

            return s;
        }
    }

    private void createTooltip(Hero unit, Entity entity){
        Map<String, Object> params = new HashMap<>();
        params.put("Owner", unit.player.getNicknameToken());

        //TODO: load parameters the viewer needs for the general tooltip contents.
        tooltipModule.registerEntity(entity, params);

        updateTooltip(unit, entity);
    }

    private void updateTooltip(Hero unit, Entity entity){
        tooltipModule.updateExtraTooltipText(entity, "x: " + unit.tile.x +
                "\ny: " + unit.tile.y);
    }

    private String groundForPosition(int x, int y, String[][] tileTypes) {
        if (x == -1) x++;
        if (y == -1) y++;
        if (x == board.size) x--;
        if (y == board.size) y--;
        return tileTypes[x][y];
    }

    private List<Tile> connected(List<Tile> positions, List<Tile> explored, Predicate<Tile> canReach) {
        List<Tile> news = new ArrayList<>();
        positions = positions.stream().filter(p -> canReach.test(p)).collect(Collectors.toList());
        explored.addAll(positions);
        for (Tile pos : positions) {
            for (Tile n : board.fullNeighbors(pos)) {
                if (!canReach.test(n)) continue;
                if (explored.contains(n)) continue;
                if (positions.contains(n)) continue;
                news.add(n);
            }
        }
        if (news.size() > 0) {
            return connected(news, explored, canReach);
        }
        return explored;
    }

    private boolean tileIsWall(Tile t) {
        return t == null || t.type == Tile.Type.Wall;
    }

    private boolean indexIsWaterEnoughSurroundedInitial(Tile t) {
        return t != null && t.type == Tile.Type.Wall && board.fullNeighbors(t).stream().filter(tile -> tileIsWall(tile)).count() >= 3;
    }

    private boolean indexIsWaterEnoughSurroundedExpand(Tile t, List<Tile> water) {
        return t != null && t.type == Tile.Type.Wall && board.fullNeighbors(t).stream().filter(tile -> water.contains(tile)).count() >= 2;
    }


    double smoothstep(double min, double max, double value) {
        double x = Math.max(0, Math.min(1, (value - min) / (max - min)));
        return x * x * (3 - 2 * x);
    }

    private double[] generatePerlinNoise(int width, int height, int octaveCount, double amplitude, double persistence) {
        double[] whiteNoise = generateWhiteNoise(width, height);

        double[][] smoothNoiseList = new double[octaveCount][];
        for (int i = 0; i < octaveCount; i++) {
            smoothNoiseList[i] = generateSmoothNoise(i, whiteNoise, width, height);
        }
        double[] perlinNoise = new double[width * height];
        double totalAmplitude = 0;
        // blend noise together
        for (int i = octaveCount - 1; i >= 0; i--) {
            amplitude *= persistence;
            totalAmplitude += amplitude;

            for (int j = 0; j < perlinNoise.length; j++) {
                perlinNoise[j] += smoothNoiseList[i][j] * amplitude;
            }
        }
        // normalization
        for (int i = 0; i < perlinNoise.length; i++) {
            perlinNoise[i] /= totalAmplitude;
        }

        return perlinNoise;
    }

    double[] generateSmoothNoise(int octave, double[] whiteNoise, int width, int height) {
        double[] noise = new double[width * height];
        int samplePeriod = (int) Math.pow(2, octave);
        double sampleFrequency = 1.0 / samplePeriod;
        int noiseIndex = 0;
        for (int y = 0; y < height; ++y) {
            int sampleY0 = y / samplePeriod * samplePeriod;
            int sampleY1 = (sampleY0 + samplePeriod) % height;
            double vertBlend = (y - sampleY0) * sampleFrequency;
            for (int x = 0; x < width; x++) {
                int sampleX0 = x / samplePeriod * samplePeriod;
                int sampleX1 = (sampleX0 + samplePeriod) % width;
                double horizBlend = (x - sampleX0) * sampleFrequency;

                // blend top two corners
                double top = interpolate(whiteNoise[sampleY0 * width + sampleX0], whiteNoise[sampleY1 * width + sampleX0], vertBlend);
                // blend bottom two corners
                double bottom = interpolate(whiteNoise[sampleY0 * width + sampleX1], whiteNoise[sampleY1 * width + sampleX1], vertBlend);
                // final blend
                noise[noiseIndex] = interpolate(top, bottom, horizBlend);
                noiseIndex += 1;
            }
        }
        return noise;
    }

    double[] generateWhiteNoise(int width, int height) {
        double[] noise = new double[width * height];
        for (int i = 0; i < noise.length; ++i) {
            noise[i] = Config.random.nextDouble();
        }
        return noise;
    }

    double interpolate(double x0, double x1, double alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }
}