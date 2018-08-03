import java.util.Random;
import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        System.err.println("in bot");
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        // read board
        int size = scanner.nextInt();
        System.err.println("size: " + size);
        System.err.println("discard: "+ scanner.nextLine());
        char[][] grid = new char[size][size];
        for (int y = 0; y < size; y++) {
            String line = scanner.nextLine();
            System.err.println("grid: " + line);
            for (int x = 0; x < size; x++) {
                grid[x][y] = line.charAt(x);
            }
        }
        int myID = scanner.nextInt();
        System.err.println("id: " + myID);

        while (true) {
            int entityCount = scanner.nextInt();
            System.err.println("entityCount: " + entityCount);
            for (int entity = 0; entity < entityCount; entity++) {
                String type = scanner.next();
                int id = scanner.nextInt();
                int x = scanner.nextInt();
                int y = scanner.nextInt();
                int life = scanner.nextInt();
                int gold = scanner.nextInt();
                System.err.println(type + " " + x + " " + y + " " + id + " " + life + " " + gold);
            }

            String[] choices = {"NORTH", "EAST", "SOUTH", "WEST"};
            System.out.println(choices[random.nextInt(choices.length)] + " DO THIS!");
        }
    }
}
