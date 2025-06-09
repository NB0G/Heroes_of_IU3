package terrains.types;

import terrains.Terrain;
import terrains.TerrainDisplay;

public class Rock extends Terrain{
    public Rock(){
        type = "rock";
        moveCost = 999999;
        display = TerrainDisplay.ROCK;
    }
}
