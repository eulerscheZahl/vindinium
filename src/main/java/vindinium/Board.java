package vindinium;

import com.codingame.gameengine.module.entities.GraphicEntityModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    public Tile[][] tiles;
    public int size;
    public List<Hero> heroes = new ArrayList<>();

    public Board(Tile[][] tiles, int size) {
        this.tiles = tiles;
        this.size = size;
    }

    public void transferMines(Hero from, Hero to) {
        for (Mine m : mines) {
            if (m.owner == from) m.conquer(to);
        }
    }

    public int countMines(Hero owner) {
        int result = 0;
        for (Mine m : mines) {
            if (m.owner == owner) result++;
        }
        return result;
    }

    public int countMines() {
        int result = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (tiles[x][y].type == Tile.Type.Mine) result++;
            }
        }
        return result;
    }

    private static int[] dx = {0, 1, 0, -1};
    private static int[] dy = {1, 0, -1, 0};

    public ArrayList<Tile> neighbors(Tile t) {
        ArrayList<Tile> result = new ArrayList<>();
        for (int dir = 0; dir < 4; dir++) {
            int x = t.x + dx[dir];
            int y = t.y + dy[dir];
            if (x >= 0 && x < size && y >= 0 && y < size && tiles[x][y].type == Tile.Type.Air) result.add(tiles[x][y]);
        }

        return result;
    }

    public ArrayList<Tile> fullNeighbors(Tile t) {
        ArrayList<Tile> result = new ArrayList<>();
        for (int dir = 0; dir < 4; dir++) {
            int x = t.x + dx[dir];
            int y = t.y + dy[dir];
            if (x >= 0 && x < size && y >= 0 && y < size) result.add(tiles[x][y]);
            else result.add(null);
        }

        return result;
    }

    private List<Mine> mines = new ArrayList<>();
    public void initMines(GraphicEntityModule entityManager) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (tiles[x][y].type == Tile.Type.Mine) {
                    tiles[x][y].mine = new Mine(tiles[x][y], entityManager);
                    mines.add(tiles[x][y].mine);
                }
            }
        }
    }

    public void initHeroes(GraphicEntityModule entityManager) {
        for (Hero h : heroes) {
            h.initUI(entityManager);
        }
    }

    public Tile neighbors9(Tile t, int dir) {
        if (t == null) return null;
        int[] dx = {0, 0, 0, -1, 1, -1, -1, 1, 1};
        int[] dy = {0, -1, 1, 0, 0, -1, 1, -1, 1};

        int x = t.x + dx[dir];
        int y = t.y + dy[dir];
        if (x < 0 || x >= size || y < 0 || y >= size) return null;
        return tiles[x][y];
    }

    public String print() {
        StringBuilder result = new StringBuilder();
        result.append(size + "\n");
        for(int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                result.append(tiles[x][y].toString());
            }
            result.append("\n");
        }
        return result.toString();
    }

    public String boardState() {
       StringBuilder result = new StringBuilder();
        result.append((heroes.size() + mines.size()) + "\n");
        for (Hero hero : heroes) {
            result.append(hero.print() + "\n");
        }
        for (Mine mine : mines) {
            result.append(mine.print() + "\n");
        }
        return result.toString();
    }
}
