package vindinium;

import com.codingame.game.Player;

import java.util.*;

public class Config {
    public static Random random;
    private static int size;
    private static int wallPercent;
    private static int minePercent;

    public static Board generateMap(List<Player> players, Properties params) {
        size = 10 + 2 * random.nextInt(3); // TODO: back to random.nextInt(10)
        wallPercent = 10 + random.nextInt(32);
        minePercent = 3 + random.nextInt(7);

        try {
            if (params.containsKey("size")) size = Integer.parseInt(params.getProperty("size"));
            if (params.containsKey("wallPercent")) wallPercent = Integer.parseInt(params.getProperty("wallPercent"));
            if (params.containsKey("minePercent")) minePercent = Integer.parseInt(params.getProperty("minePercent"));
            if (size < 10) size = 10;
            if (size > 30) size = 30;
            if (wallPercent < 10) wallPercent = 10;
            if (wallPercent > 41) wallPercent = 41;
            if (minePercent < 3) minePercent = 3;
            if (minePercent > 9) minePercent = 9;
        } catch (Exception ex) {
            // keep default values, if the user set unparsable input
        }

        while (true) {
            Board board = generateBoard();
            Tile spawnPos = generateSpawnPos(board);
            if (spawnPos == null) continue;
            fillBoard(board, spawnPos);
            if (board.countMines() == 0) continue;
            if (!placeTaverns(board, spawnPos)) continue;
            while (board.shrink()) size = board.size;

            players.get(0).hero = new Hero(players.get(0), spawnPos);
            players.get(1).hero = new Hero(players.get(1), board.tiles[spawnPos.x][size - spawnPos.y - 1]);
            players.get(2).hero = new Hero(players.get(2), board.tiles[size - spawnPos.x - 1][spawnPos.y]);
            players.get(3).hero = new Hero(players.get(3), board.tiles[size - spawnPos.x - 1][size - spawnPos.y - 1]);
            board.heroes.add(players.get(0).hero);
            board.heroes.add(players.get(1).hero);
            board.heroes.add(players.get(2).hero);
            board.heroes.add(players.get(3).hero);

            params.setProperty("size", String.valueOf(size));
            params.setProperty("wallPercent", String.valueOf(wallPercent));
            params.setProperty("minePercent", String.valueOf(minePercent));
            if (params.containsKey("players") && params.getProperty("players").equals("2")) {
                for (int y = size/2; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        board.tiles[x][y].type = Tile.Type.Wall;
                    }
                }
                board.heroes.remove(3);
                board.heroes.remove(2);
                board.heroes.remove(1);
                players.get(1).hero = new Hero(players.get(1), board.tiles[size - spawnPos.x - 1][spawnPos.y]);
                board.heroes.add(players.get(1).hero);
            }
            return board;
        }
    }

    private static Board generateBoard() {
        Tile[][] grid = new Tile[size][size];
        for (int x = 0; x < size / 2; x++) {
            for (int y = 0; y < size / 2; y++) {
                int rnd = random.nextInt(100);
                if (rnd < minePercent) grid[x][y] = new Tile(Tile.Type.Mine, x, y);
                else if (rnd < minePercent + wallPercent) grid[x][y] = new Tile(Tile.Type.Wall, x, y);
                else grid[x][y] = new Tile(Tile.Type.Air, x, y);

                grid[size - x - 1][y] = new Tile(grid[x][y].type, size - x - 1, y);
                grid[size - x - 1][size - y - 1] = new Tile(grid[x][y].type, size - x - 1, size - y - 1);
                grid[x][size - y - 1] = new Tile(grid[x][y].type, x, size - y - 1);
            }
        }

        return new Board(grid, size);
    }

    private static Tile generateSpawnPos(Board board) {
        Tile spawn = null;
        int loop = 0;
        do {
            spawn = board.tiles[random.nextInt(size / 2 - 2)][random.nextInt(size / 2 - 2)];
        } while (!validateSpawnPos(board, spawn) && ++loop < 100);
        if (loop == 100) return null;
        return spawn;
    }

    public static int[][] findDistance(Board board, Tile start) {
        int[][] dist = new int[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                dist[x][y] = -1;
            }
        }
        dist[start.x][start.y] = 0;

        Queue<Tile> queue = new LinkedList<>();
        queue.add(start);
        while (queue.size() > 0) {
            Tile t = queue.poll();
            for (Tile n : board.neighbors(t)) {
                if (dist[n.x][n.y] >= 0) continue;
                dist[n.x][n.y] = dist[t.x][t.y] + 1;
                queue.add(n);
            }
        }
        return dist;
    }

    private static boolean[][] findReachable(Board board, Tile spawn) {
        boolean[][] visited = new boolean[size][size];
        int[][] dist = findDistance(board, spawn);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                visited[x][y] = dist[x][y] >= 0;
            }
        }
        return visited;
    }

    private static boolean validateSpawnPos(Board board, Tile spawn) {
        if (spawn.type != Tile.Type.Air) return false;
        boolean[][] reachable = findReachable(board, spawn);
        return reachable[size - spawn.x - 1][spawn.y] && reachable[spawn.x][size - spawn.y - 1];
    }

    private static boolean neighborReachable(Board board, boolean[][] reachable, Tile tile) {
        for (Tile t : board.neighbors(tile)) {
            if (reachable[t.x][t.y]) return true;
        }
        return false;
    }

    private static void fillBoard(Board board, Tile spawn) {
        boolean[][] reachable = findReachable(board, spawn);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (reachable[x][y]) continue;
                Tile t = board.tiles[x][y];
                if (t.type == Tile.Type.Mine) {
                    if (!neighborReachable(board, reachable, t)) t.type = Tile.Type.Wall;
                } else t.type = Tile.Type.Wall;
            }
        }
    }

    private static int reachableMines(Board board, Tile spawn) {
        int result = 0;
        boolean[][] reachable = findReachable(board, spawn);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Tile t = board.tiles[x][y];
                if (t.type == Tile.Type.Mine && neighborReachable(board, reachable, t)) result++;
            }
        }

        return result;
    }

    private static boolean placeTaverns(Board board, Tile spawn) {
        boolean[][] reachable = findReachable(board, spawn);
        int mines = reachableMines(board, spawn);

        ArrayList<Tile> reachableFields = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (reachable[x][y]) reachableFields.add(board.tiles[x][y]);
            }
        }
        int reachableSize = reachableFields.size();

        while (reachableFields.size() > 0) {
            Tile t = reachableFields.get(random.nextInt(reachableFields.size()));
            reachableFields.remove(t);

            t.type = Tile.Type.Tavern;
            board.tiles[size - t.x - 1][t.y].type = Tile.Type.Tavern;
            board.tiles[size - t.x - 1][size - t.y - 1].type = Tile.Type.Tavern;
            board.tiles[t.x][size - t.y - 1].type = Tile.Type.Tavern;

            boolean[][] reachable2 = findReachable(board, spawn);
            int mines2 = reachableMines(board, spawn);

            ArrayList<Tile> reachableFields2 = new ArrayList<>();
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    if (reachable2[x][y]) reachableFields2.add(board.tiles[x][y]);
                }
            }
            int reachableSize2 = reachableFields2.size();

            if (reachableSize2 == reachableSize - 4 && mines == mines2) return true;

            t.type = Tile.Type.Air;
            board.tiles[size - t.x - 1][t.y].type = Tile.Type.Air;
            board.tiles[size - t.x - 1][size - t.y - 1].type = Tile.Type.Air;
            board.tiles[t.x][size - t.y - 1].type = Tile.Type.Air;
        }
        return false;
    }

}
