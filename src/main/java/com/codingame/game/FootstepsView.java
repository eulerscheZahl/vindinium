package com.codingame.game;

import com.codingame.game.view.ViewController;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import vindinium.Hero;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class FootstepsView implements IView{
    private List<Hero> _heroes;
    private List<Sprite> _footSteps = new ArrayList<Sprite>();
    private GraphicEntityModule _entityModule;
    private String[] footSteps;
    private Group _boardGroup;
    private int _round = -1;
    private ArrayList<Sprite>[] _steps = new ArrayList[16];

    public FootstepsView(List<Hero> heroes, GraphicEntityModule entityModule, Group boardGroup){
        _entityModule = entityModule;
        _boardGroup = boardGroup;
        _heroes = heroes;

        footSteps = entityModule.createSpriteSheetLoader()
                .setSourceImage("footprints.png")
                .setName("foot")
                .setWidth(24)
                .setHeight(24)
                .setImageCount(1*2)
                .setImagesPerRow(2)
                .setOrigCol(0)
                .setOrigRow(0)
                .load();

        for(int i = 0; i < _steps.length; i++){
            _steps[i] = new ArrayList<>();
        }
    }

    public Sprite getStep(){
        if(_footSteps.size()==0){
            Sprite steps = _entityModule.createSprite().setImage(footSteps[0])
                    .setZIndex(1)
                    .setAnchor(0.5)
                    .setBaseWidth(ViewController.CELL_SIZE)
                    .setBaseHeight(ViewController.CELL_SIZE);

            _boardGroup.add(steps);
            _footSteps.add(steps);
        }

        return _footSteps.remove(0);
    }

    @Override
    public void onRound() {
        _round++;

        //Add new
        ArrayList<Sprite> newSteps = new ArrayList<>();
        for(Hero hero : _heroes){
            if(_round % 4 != 0) continue;

            double dir = hero.lastDir*Math.PI/2.0;

            Sprite step = getStep();
            step.setX(ViewConstants.getCellPos(hero.tile.x) + ViewController.CELL_SIZE, Curve.NONE)
                    .setY(ViewConstants.getCellPos(hero.tile.y) + ViewController.CELL_SIZE, Curve.NONE)
                    .setAlpha(1, Curve.NONE)
                    .setRotation(dir, Curve.NONE);
            newSteps.add(step);
        }

        //Remove oldest
        for(Sprite step : _steps[0]){
            _footSteps.add(step);
            step.setAlpha(0);
        }

        _steps[0].clear();

        //Adjust by 1
        for(int i = 0; i < _steps.length-1; i++){
            _steps[i] = _steps[i+1];
        }

        _steps[_steps.length-1] = newSteps;
    }
}
