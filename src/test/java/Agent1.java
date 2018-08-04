import java.util.Random;
import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        // read board
        int size = scanner.nextInt();
        scanner.nextLine();
        char[][] grid = new char[size][size];
        for (int y = 0; y < size; y++) {
            String line = scanner.nextLine();
            for (int x = 0; x < size; x++) {
                grid[x][y] = line.charAt(x);
            }
        }
        int myID = scanner.nextInt();
        int k = 0;
        int targetX = 0;
        int targetY = 0;
        while (true) {
            int entityCount = scanner.nextInt();
            for (int entity = 0; entity < entityCount; entity++) {
                String type = scanner.next();
                int id = scanner.nextInt();
                int x = scanner.nextInt();
                int y = scanner.nextInt();
                int life = scanner.nextInt();
                int gold = scanner.nextInt();
                if (type.equals("MINE") && id != myID) {
                    targetX = x;
                    targetY = y;
                }
            }

            String[] choices = {"NORTH", "EAST", "SOUTH", "WEST"};
            int[] arr = new int[0];
            int z = 0;
            // if(random.nextInt(100)==1) z = arr[1];
            System.out.println("MOVE " + targetX + " " + targetY + " DO " + k++);
        }
    }
}
