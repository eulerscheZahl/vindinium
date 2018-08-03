import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        // Adds as many player as you need to test your game
        gameRunner.addAgent(Agent1.class);
        //gameRunner.addAgent(Agent1.class);
        gameRunner.addAgent(Agent1.class);
        gameRunner.addAgent(Agent1.class);

        // Another way to add a player
     //   gameRunner.addAgent("mono /Users/erikkvanli/Projects/LOCM/Locm/Vindinum/bin/Debug/Vindinum.exe");
       // gameRunner.addAgent("mono /Users/erikkvanli/Projects/LOCM/Locm/Vindinum/bin/Debug/Vindinum.exe");
       // gameRunner.addAgent("mono /Users/erikkvanli/Projects/LOCM/Locm/Vindinum/bin/Debug/Vindinum.exe");
       // gameRunner.addAgent("mono /Users/erikkvanli/Projects/LOCM/Locm/Vindinum/bin/Debug/Vindinum.exe");
        gameRunner.start();
    }
}
