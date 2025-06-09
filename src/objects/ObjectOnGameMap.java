package objects;

import java.io.Serializable;
import maps.Map;
import other.Plasable;

public class ObjectOnGameMap implements Plasable, Serializable{
    private static final long serialVersionUID = 2L;

    protected int x;
    protected int y;
    protected ObjectsDisplay display;

    public void place(Map map){
        map.placeCharacter(x, y, this);
    }

    public String getDisplay() {
        return display.getSymbol();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
