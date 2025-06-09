package terrains.types;

import terrains.Terrain;
import terrains.TerrainDisplay;

public class EnemyCastle extends Terrain{
    public EnemyCastle(){
        type = "enemyCastle";
        moveCost = 2;
        display = TerrainDisplay.ENEMYCASTLE;
    }
}
