package objects.types;

import objects.ObjectOnGameMap;
import objects.ObjectsDisplay;

public class Wings extends ObjectOnGameMap{
    public Wings(int x, int y){
        this.x = x;
        this.y = y;
        display = ObjectsDisplay.WINGS;
    }
}
