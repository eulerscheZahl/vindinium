package com.codingame.game.view;

import com.codingame.game.*;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.*;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import modules.FXModule;
import vindinium.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ViewController {
    private GraphicEntityModule entityManager;
    private MultiplayerGameManager<Player> gameManager;
    private TooltipModule tooltipModule;
    private FXModule fxModule;
    public static int CELL_SIZE = 24;
    private Board board;
    public Group boardGroup;
    private ArrayList<IView> _views = new ArrayList<>();
    public ArrayList<HeroView> _heroes = new ArrayList<>();
    public static List<Tile> fightLocations;

    public ViewController(GraphicEntityModule entityManager, MultiplayerGameManager<Player> gameManager, TooltipModule tooltipModule, FXModule fxModule) {
        this.entityManager = entityManager;
        this.gameManager = gameManager;
        this.tooltipModule = tooltipModule;
        this.fxModule = fxModule;

        TileFactory.getInstance().init(entityManager);
    }

    private enum TileType {
        WATER, EARTH, ROCK, PLAIN
    }

    private class Point {
        public int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void createGrid(Board board) {
        entityManager.createSprite().setImage("Frame.png").setZIndex(2).setAnchor(0).setScale(1.2);
        entityManager.createRectangle().setFillColor(0x5c8ab8).setZIndex(-100000).setLineWidth(0).setWidth(1920).setHeight(1080);

        this.board = board;
        double terrainRandomnessFactor = 0.7 + 0.2 * Config.random.nextDouble();
        double terrainMineTavernFactor = 1.4 + 0.8 * Config.random.nextDouble();

        List<Tile> allTiles = new ArrayList<>();
        for (int y = 0; y < board.size; y++) {
            for (int x = 0; x < board.size; x++) {
                allTiles.add(board.tiles[x][y]);
            }
        }
        List<Tile> taverns = allTiles.stream().filter(tile -> tile.type == Tile.Type.Tavern).collect(Collectors.toList());
        List<Tile> mines = allTiles.stream().filter(tile -> tile.type == Tile.Type.Mine).collect(Collectors.toList());

        double[] noise = generatePerlinNoise(board.size, board.size, 4, 0.05, 0.3);
        TileType[][] tileTypes = new TileType[board.size + 2][board.size + 2];
        for (int x = 0; x < board.size + 2; x++) {
            for (int y = 0; y < board.size + 2; y++) {
                tileTypes[x][y] = TileType.WATER;
            }
        }
        for (int i = 0; i < board.size * board.size; i++) {
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
            tileTypes[t.x + 1][t.y + 1] = value < 0.3 ? TileType.EARTH : value > 0.8 - 0.1 * Config.random.nextDouble() ? TileType.ROCK : TileType.PLAIN;
        }

        List<Tile> waterTiles = connected(allTiles.stream()
                        .filter(t -> indexIsWaterEnoughSurroundedInitial(t))
                        .collect(Collectors.toList()),
                new ArrayList<Tile>(),
                t -> indexIsWaterEnoughSurroundedInitial(t));
        int initialSize;
        do {
            initialSize = waterTiles.size();
            final List<Tile> initialWater = waterTiles.stream().collect(Collectors.toList());
            waterTiles = waterTiles.stream().filter(t -> indexIsWaterEnoughSurroundedExpand(t, initialWater)).collect(Collectors.toList());
        } while (waterTiles.size() != initialSize);

        for (Tile water : waterTiles) {
            tileTypes[water.x + 1][water.y + 1] = TileType.WATER;
        }

        boardGroup = this.entityManager.createGroup()
                .setScale(1080.0 / (CELL_SIZE * (board.size + 2)))
                .setX((ViewConstants.FrameRight - ViewConstants.FrameLeft - 1080) / 2 + ViewConstants.FrameLeft);

        ArrayList<ArrayList<Point>> regions = findRegions(tileTypes, board.size + 2);
        boolean[][] filled = new boolean[board.size + 2][board.size + 2];

        boardGroup = this.entityManager.createGroup()
                .setScale(1080.0 / (CELL_SIZE * (board.size + 2)))
                .setX((ViewConstants.FrameRight - ViewConstants.FrameLeft - 1080) / 2 + ViewConstants.FrameLeft);
        BufferedGroup bufferedGroup = this.entityManager.createBufferedGroup().setZIndex(-10);
        boardGroup.add(bufferedGroup);
        Group innerGroup = this.entityManager.createGroup();
        bufferedGroup.add(innerGroup);
        Group debuggroup = ViewConstants.createGrid(entityManager, board.size).setZIndex(-1).setVisible(false);
        boardGroup.add(debuggroup);
        fxModule.entityId = debuggroup.getId();

        while (regions.size() > 0) {
            ArrayList<Point> take = takeRegion(regions, filled, tileTypes, board.size + 2);
            regions.remove(take);
            for (Point p : take) {
                int x = p.x - 1;
                int y = p.y - 1;
                filled[p.x][p.y] = true;

                String name = smoothEdge(p, tileTypes, filled, board.size + 2);
                if (!TileFactory.getInstance().tiles.containsKey(name))
                    name = tileTypes[p.x][p.y].toString().toLowerCase();

                if (name.equals("plain")) {
                    double center = 2 * (0.5 - Math.abs(noise[x + y * board.size] - 0.5));
                    if (Config.random.nextDouble() < 0.2 * center) {
                        String[] choices = {
                                "plain_grass1",
                                "plain_grass2",
                                "plain_flower",
                                "plain_grass3"
                        };
                        name = choices[(int) (Config.random.nextDouble() * Config.random.nextDouble() * 4)];
                    }
                }
                name = TileFactory.getInstance().tiles.get(name);

                Sprite background = entityManager.createSprite()
                        .setImage(name)
                        .setX(p.x * CELL_SIZE)
                        .setY(p.y * CELL_SIZE)
                        .setZIndex(-1);
                innerGroup.add(background);
                if (x >= 0 && x < board.size && y >= 0 && y < board.size) {
                    Rectangle tooltipRect = entityManager.createRectangle().setX(p.x * CELL_SIZE).setY(p.y * CELL_SIZE).setWidth(CELL_SIZE).setHeight(CELL_SIZE).setAlpha(0);
                    boardGroup.add(tooltipRect);
                    tooltipModule.setTooltipText(tooltipRect, "X: " + x + "\nY: " + y);
                }
            }
        }
        for (int x = 0; x < board.size + 2; x++) {
            for (int y = 0; y < board.size + 2; y++) {
                if (tileTypes[x][y] != TileType.WATER) {
                    if (board.tiles[x - 1][y - 1].type == Tile.Type.Wall) {
                        String[] cand = new String[]{TileFactory.getInstance().tree};
                        if (tileTypes[x][y] == TileType.ROCK) cand = TileFactory.getInstance().rockStuff;
                        if (tileTypes[x][y] == TileType.EARTH) cand = TileFactory.getInstance().earthStuff;
                        String obst = cand[Config.random.nextInt(cand.length)];
                        Sprite obstacle = entityManager.createSprite()
                                .setImage(obst).setZIndex(y);
                        moveEntity(obstacle, board.tiles[x - 1][y - 1], 0, 0);
                        innerGroup.add(obstacle);
                    }
                    if (board.tiles[x - 1][y - 1].type == Tile.Type.Tavern) {
                        Sprite tav = entityManager.createSprite()
                                .setImage("beer2.png").setZIndex(y);
                        moveEntity(tav, board.tiles[x - 1][y - 1], 0, -4);
                        addCellTooltip(tav, "Tavern");
                        boardGroup.add(tav);
                    }
                }
            }
        }


        for (Hero hero : board.heroes) {
            HeroView view = new HeroView(hero, entityManager);
            _heroes.add(view);
            _views.add(view);
            setTooltip(view._model, view._sprite);
            boardGroup.add(view.getView());
        }

        for (Mine mine : board.mines) {
            MineView view = new MineView(mine, entityManager, tooltipModule, board);
            _views.add(view);
            boardGroup.add(view.getView());
        }

        _views.add(new GoldCounterView(board.heroes, entityManager));
        _views.add(new BloodView(board, entityManager, innerGroup));

        _views.add(new FootstepsView(board.heroes, entityManager, boardGroup));
    }

    private String smoothEdge(Point p, TileType[][] tileTypes, boolean[][] filled, int size) {
        TileType current = tileTypes[p.x][p.y];
        String result = current.toString();
        String direction = "";
        TileType other = current;
        if (p.y > 0 && current != tileTypes[p.x][p.y - 1] && !filled[p.x][p.y - 1]) {
            direction += "N";
            other = tileTypes[p.x][p.y - 1];
        }
        if (p.y < size - 1 && current != tileTypes[p.x][p.y + 1] && !filled[p.x][p.y + 1]) {
            direction += "S";
            other = tileTypes[p.x][p.y + 1];
        }
        if (p.x > 0 && current != tileTypes[p.x - 1][p.y] && !filled[p.x - 1][p.y]) {
            other = tileTypes[p.x - 1][p.y];
            direction += "W";
        }
        if (p.x < size - 1 && current != tileTypes[p.x + 1][p.y] && !filled[p.x + 1][p.y]) {
            other = tileTypes[p.x + 1][p.y];
            direction += "E";
        }
        if (direction.length() > 1) {
            current = other;
            other = tileTypes[p.x][p.y];
            if (direction.equals("NE")) direction = "SW";
            else if (direction.equals("NW")) direction = "SE";
            else if (direction.equals("SE")) direction = "NW";
            else if (direction.equals("SW")) direction = "NE";
        }
        if (other == current) {
            if (p.x > 0 && p.y > 0 && current != tileTypes[p.x - 1][p.y - 1] && !filled[p.x - 1][p.y - 1]) {
                direction += "NW";
                other = tileTypes[p.x - 1][p.y - 1];
            }
            if (p.x < size - 1 && p.y > 0 && current != tileTypes[p.x + 1][p.y - 1] && !filled[p.x + 1][p.y - 1]) {
                direction += "NE";
                other = tileTypes[p.x + 1][p.y - 1];
            }
            if (p.x > 0 && p.y < size - 1 && current != tileTypes[p.x - 1][p.y + 1] && !filled[p.x - 1][p.y + 1]) {
                direction += "SW";
                other = tileTypes[p.x - 1][p.y + 1];
            }
            if (p.x < size - 1 && p.y < size - 1 && current != tileTypes[p.x + 1][p.y + 1] && !filled[p.x + 1][p.y + 1]) {
                direction += "SE";
                other = tileTypes[p.x + 1][p.y + 1];
            }
        }

        if (current != other) result = current + "_" + other + "_" + direction;
        return result.toLowerCase();
    }

    private ArrayList<Point> takeRegion(ArrayList<ArrayList<Point>> regions, boolean[][] filled, TileType[][] types, int size) {
        for (ArrayList<Point> region : regions) {
            if (types[region.get(0).x][region.get(0).y] == TileType.WATER) return region;
        }

        ArrayList<Point> best = null;
        double score = 1e9;
        for (ArrayList<Point> region : regions) {
            double tmp = regionScore(region, filled, types, size);
            if (tmp == 0) return region;
            if (tmp < score) {
                best = region;
                score = regionScore(region, filled, types, size);
            }
        }

        for (TileType type : new TileType[]{TileType.ROCK, TileType.EARTH}) {
            for (ArrayList<Point> region : regions) {
                if (types[region.get(0).x][region.get(0).y] == type) return region;
            }
        }
        return best;
    }

    private double regionScore(ArrayList<Point> region, boolean[][] filled, TileType[][] types, int size) {
        double errors = 0;
        for (Point p : region) {
            String name = smoothEdge(p, types, filled, size);
            if (!TileFactory.getInstance().tiles.containsKey(name)) errors++;
        }

        return errors * errors / region.size();
    }

    private ArrayList<ArrayList<Point>> findRegions(TileType[][] tileTypes, int size) {
        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};
        ArrayList<ArrayList<Point>> result = new ArrayList<>();
        boolean[][] visited = new boolean[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (visited[x][y]) continue;
                ArrayList<Point> region = new ArrayList<>();
                Stack<Point> stack = new Stack<>();
                stack.push(new Point(x, y));
                visited[x][y] = true;
                while (stack.size() > 0) {
                    Point p = stack.pop();
                    region.add(p);
                    for (int dir = 0; dir < 4; dir++) {
                        Point q = new Point(p.x + dx[dir], p.y + dy[dir]);
                        if (q.x < 0 || q.x >= size || q.y < 0 || q.y >= size || visited[q.x][q.y]) continue;
                        if (tileTypes[p.x][p.y] != tileTypes[q.x][q.y]) continue;
                        visited[q.x][q.y] = true;
                        stack.push(q);
                    }
                }
                result.add(region);
            }
        }
        return result;
    }

    public void addCellTooltip(Entity entity, String type) {
        tooltipModule.setTooltipText(entity, "Type: " + type);
    }

    public void onRound(List<Tile> fightLocations, int round) {
        ViewController.fightLocations = fightLocations;
        for (IView view : _views) {
            view.onRound(round);
        }

        for (HeroView view : _heroes) {
            setTooltip(view._model, view._sprite);
        }
    }

    public void setSpawn(Tile tile, int index) {
        Group group = this.entityManager.createGroup().setZIndex(-1);
        moveEntity(group, tile, 0, -4);
        Sprite spawn = entityManager.createSprite()
                .setImage(TileFactory.getInstance().spawns[index])
                .setBaseHeight(CELL_SIZE)
                .setBaseWidth(CELL_SIZE)
                .setAlpha(1.0);
        group.add(spawn);
        boardGroup.add(group);
    }

    public static void moveEntity(Entity entity, Tile tile, int dx, int dy) {
        // tile offset because there is a frame of water around the board
        entity.setX((tile.x + 1) * CELL_SIZE + dx)
                .setY((tile.y + 1) * CELL_SIZE + dy);
    }

    private void setTooltip(Hero unit, Entity entity) {
        tooltipModule.setTooltipText(entity, "Type: Hero\nOwner: " + unit.player.getIndex() + "\nLife: " + unit.life);
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