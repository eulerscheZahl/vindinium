package modules;

import java.util.ArrayList;
import java.util.List;

import com.codingame.game.Player;
import com.codingame.gameengine.core.Module;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

public class FXModule implements Module {

    MultiplayerGameManager<Player> gameManager;

    public int entityId;

    @Inject
    public FXModule(MultiplayerGameManager<Player> gameManager) {
        this.gameManager = gameManager;
        gameManager.registerModule(this);
    }

    @Override
    public void onGameInit() {
        gameManager.setViewGlobalData("fx", entityId);
    }

    @Override
    public void onAfterGameTurn() {

    }

    @Override
    public void onAfterOnEnd() {

    }

}