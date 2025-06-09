package objects.types;

import objects.ObjectOnGameMap;
import objects.ObjectsDisplay;

public class Nothing extends ObjectOnGameMap{
    public Nothing(int x, int y){
        this.x = x;
        this.y = y;
        display = ObjectsDisplay.NOTHING;
    }
}
