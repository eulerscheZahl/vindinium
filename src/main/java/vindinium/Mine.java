package vindinium;

public class Mine {
    public Tile tile;
    public Hero owner;
    public boolean didFight;

    public Mine(Tile tile) {
        this.tile = tile;
    }

    public String print() {
        return "MINE " + (owner == null ? -1 : owner.player.getIndex()) + " " + tile.x + " " + tile.y + " -1 -1";
    }

    public void conquer(Hero hero) {
        owner = hero;
    }
}