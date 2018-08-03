package vindinium;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public Tile[][] tiles;
    public int size;
    public List<Mine> mines = new ArrayList<>();
    public List<Hero> heroes = new ArrayList<>();
                                //N   E   S   W   NW  SW  NE  SE  P
    private static int[] dirX = { 0,  1,  0, -1,  -1, -1,  1,  1, 0 };
    private static int[] dirY = { -1, 0,  1,  0,  -1,  1, -1,  1, 0 };

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


    public ArrayList<Tile> neighbors(Tile t) {
        ArrayList<Tile> result = new ArrayList<>();
        for (int dir = 0; dir < 4; dir++) {
            int x = t.x + dirX[dir];
            int y = t.y + dirY[dir];
            if (IsOnBoard(x, y) && tiles[x][y].type == Tile.Type.Air) result.add(tiles[x][y]);
        }

        return result;
    }

    public ArrayList<Tile> fullNeighbors(Tile t) {
        ArrayList<Tile> result = new ArrayList<>();
        for (int dir = 0; dir < 4; dir++) {
            int x = t.x + dirX[dir];
            int y = t.y + dirY[dir];
            if (IsOnBoard(x, y)) result.add(tiles[x][y]);
            else result.add(null);
        }

        return result;
    }

    public void initMines() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (tiles[x][y].type == Tile.Type.Mine) {
                    tiles[x][y].mine = new Mine(tiles[x][y]);
                    mines.add(tiles[x][y].mine);
                }
            }
        }
    }

    public Tile neighbors9(Tile t, int dir) {
        if (t == null) return null;
        int x = t.x + dirX[dir];
        int y = t.y + dirY[dir];
        if(IsOnBoard(x, y)) return tiles[x][y];
        return null;
    }

    public boolean IsOnBoard(int x, int y){
        return x >= 0 && x < size && y >= 0 && y < size;
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

    public Hero getLeader(){
        Hero leader = null;
        int mostGold = 0;
        for(Hero hero : heroes){
            if(hero.gold==mostGold){
                leader=null;
            }else if(hero.gold>mostGold){
                mostGold = hero.gold;
                leader = hero;
            }
        }

        return leader;
    }
}
