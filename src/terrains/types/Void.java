package terrains.types;

import terrains.Terrain;
import terrains.TerrainDisplay;

public class Void extends Terrain{
    public Void(){
        type = "grass";
        moveCost = 4;
        display = TerrainDisplay.VOID;
    }
}
