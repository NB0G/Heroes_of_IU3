package terrains;

import java.io.Serializable;

public class Terrain implements Serializable {
    private static final long serialVersionUID = 3L;

    protected int moveCost;
    protected String type;
    protected TerrainDisplay display;

    public Terrain(){}

    public String getDisplay() {
        return display.getSymbol();
    }

    public String getType() {
        return type;
    }

    public int getMoveCost() {
        return moveCost;
    }
}
