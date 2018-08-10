package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import vindinium.Hero;

import java.util.ArrayList;
import java.util.List;

public class GoldCounterView implements IView{

    private List<Hero> _heroes;
    private int _fullHeight;
    private int _xPos = ViewConstants.BarRight-52;
    private ArrayList<Rectangle> _rectangles = new ArrayList<>();
    public GoldCounterView(List<Hero> heroes, GraphicEntityModule entityModule){
        _heroes = heroes;
        _fullHeight = entityModule.getWorld().getHeight()-150;
        for(int i = 0; i < 4; i++){
            _rectangles.add(createRectangle(entityModule, i).setFillColor(_heroes.get(i).player.getColorToken()).setZIndex(8-i));
        }

        //overlay to make darker
        entityModule.createRectangle().setWidth(45).setZIndex(300).setX(_xPos).setY(90).setHeight(_fullHeight).setFillColor(0x000000).setAlpha(0.1);
    }

    private Rectangle createRectangle(GraphicEntityModule entityModule, int num){
        return entityModule.createRectangle().setX(_xPos).setWidth(45).setY(90).setHeight(_fullHeight/4*num+_fullHeight/4);
    }

    @Override
    public void onRound() {
        int sumGold = findGold(4);
        for(int i = 0; i < 4; i++){
            if(sumGold==0) _rectangles.get(i).setHeight(_fullHeight/4*i+_fullHeight/4);
            else {
                float currentSum = findGold(i+1);
                float degree = currentSum/(float)sumGold;
                _rectangles.get(i).setHeight((int)(degree*_fullHeight));
            }
        }
    }

    private int findGold(int num){
        if(num > 4) num = 4;
        int sum = 0;
        for(int i = 0; i < num; i++){
            sum += _heroes.get(i).gold;
        }

        return sum;
    }
}
