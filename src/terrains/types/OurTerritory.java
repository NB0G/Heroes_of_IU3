package terrains.types;

import terrains.Terrain;
import terrains.TerrainDisplay;

public class OurTerritory extends Terrain{
    public OurTerritory(){
        type = "ourTerritory";
        moveCost = 2;
        display = TerrainDisplay.OURTERRITORY;
    }
}
