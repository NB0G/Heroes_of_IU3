package terrains.types;

import terrains.Terrain;
import terrains.TerrainDisplay;

public class EnemyTerritory extends Terrain{
    public EnemyTerritory(){
        type = "enemyTerritory";
        moveCost = 2;
        display = TerrainDisplay.ENEMYTERRITORY;
    }
}
