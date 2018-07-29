package vindinium;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.List;

public class Tile {
    public enum Type {
        Air, Wall, Tavern, Mine
    }

    public int x, y;
    public Type type;
    public Mine mine = null;

    public Tile(Type type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        switch (type) {
            case Tavern:
                return "T";
            case Wall:
                return "#";
            case Mine:
                return "M";
            default:
                return ".";
        }
    }

    public double distance(Tile t) {
        int dx = this.x - t.x;
        int dy = this.y - t.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
