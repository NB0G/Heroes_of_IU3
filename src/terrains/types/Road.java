package terrains.types;

import terrains.Terrain;
import terrains.TerrainDisplay;

public class Road extends Terrain{
    public Road(){
        type = "road";
        moveCost = 1;
        display = TerrainDisplay.ROAD;
    }
}
