package terrains.types;

import terrains.Terrain;
import terrains.TerrainDisplay;

public class OurCastle extends Terrain{
    public OurCastle(){
        type = "ourCastle";
        moveCost = 2;
        display = TerrainDisplay.OURCASTLE;
    }
}
