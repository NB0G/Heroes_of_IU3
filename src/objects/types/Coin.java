package objects.types;

import objects.ObjectOnGameMap;
import objects.ObjectsDisplay;

public class Coin extends ObjectOnGameMap{
    public Coin(int x, int y){
        this.x = x;
        this.y = y;
        display = ObjectsDisplay.COIN;
    }
}
