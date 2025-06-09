package terrains.types;

import terrains.Terrain;
import terrains.TerrainDisplay;

public class RoadBorder extends Terrain{
    public RoadBorder(){
        type = "rock";
        moveCost = 999999;
        display = TerrainDisplay.ROADBORDER;
    }
}
