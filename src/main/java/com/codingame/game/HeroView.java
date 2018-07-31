package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import vindinium.Hero;

public class HeroView implements IView{
    public Hero _model;
    public HeroView(Hero model, GraphicEntityModule entityManager){
        _model = model;
    }

    @Override
    public void OnRound() {

    }

    @Override
    public Group GetView() {
        return null;
    }
}
