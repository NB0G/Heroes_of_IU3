package maps;
import java.io.Serializable;
import java.util.Random;
import other.Plasable;
import terrains.Terrain;
import terrains.types.EnemyCastle;
import terrains.types.EnemyTerritory;
import terrains.types.OurCastle;
import terrains.types.OurTerritory;
import terrains.types.Road;
import terrains.types.RoadBorder;
import terrains.types.Rock;
import terrains.types.Void;

/*
    0 - ничего
    1 - замок игрока
    2 - замок компьютера
    3 - дорога
    4 - территория игрока
    5 - территория компьютера
    6 - препятствие
    7 - препятствие (дороги)
 */

public class Map implements Serializable{
    private static final long serialVersionUID = 4L;

    Random random = new Random();
    protected int sizeX = 0, sizeY = 0;
    protected Terrain map[][];
    protected Plasable characters[][];

    public void print(){}

    public void placeCharacter(int x, int y, Plasable character){
        characters[y][x] = character;
    }

    public int getMinSteps(int x, int y, int player){
        int steps[] = new int[8];
        for(int i = 0; i < 8; i++){
            int dx = 0;
            int dy = 0;
            if(i >= 1 && i <= 3){
                dy = -1;
            } else if (i >= 5 && i <= 7){
                dy = 1;
            }
            if(i >= 3 && i <= 5){
                dx = 1;
            } else if (i == 7 || i == 8 || i == 1){
                dx = -1;
            }
            if(y + dy >= 0 && y + dy < sizeY && x + dx >= 0 && x + dx < sizeX){
                steps[i] = getTerrain(x + dx, y + dy, player);
            } else {
                steps[i] = 9999999;
            }

            
        }
        int minimum = steps[0];
        for(int i : steps){
            if(i < minimum){
                minimum = i;
            }
        }
        return minimum;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public Terrain getXY(int x, int y){
        if(x < 0 || x >= sizeX || y < 0 || y >= sizeY){
            return new Rock();
        }
        return map[y][x];
    }

    public Plasable[][] getCharacters() {
        return characters;
    }

    public Plasable getCharacterXY(int x, int y){
        if(x < 0 || x >= sizeX || y < 0 || y >= sizeY){
            return null;
        }
        return characters[y][x];
    }

    public int getTerrain(int x, int y, int player){
        if(x < 0 || x >= sizeX || y < 0 || y >= sizeY){
            return 9999999;
        }
        int cost = map[y][x].getMoveCost();
        if(player == 1 && (map[y][x] instanceof EnemyTerritory || map[y][x] instanceof EnemyCastle)){
            cost++;
        } else if(player == 2 && (map[y][x] instanceof OurTerritory || map[y][x] instanceof OurCastle)){
            cost++;
        }
        return cost;
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            // Отметить точку на сетке
            if(map[y1][x1] instanceof Void){
                map[y1][x1] = new Road();
            }

            // Если достигли конца линии, выходим
            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    protected void generateLines(int startX, int startY, int segments, int team) {
        int x = startX, y = startY;
        if((map[y][x] instanceof Void || map[y][x] instanceof RoadBorder) && (map[sizeY - y - 1][sizeX - x - 1] instanceof Void || map[sizeY - y - 1][sizeX - x - 1] instanceof RoadBorder)){
            if(team == 1){
                map[y][x] = new OurTerritory();
                map[sizeY - y - 1][sizeX - x - 1] = new EnemyTerritory();
            } else {
                map[y][x] = new EnemyTerritory();
                map[sizeY - y - 1][sizeX - x - 1] = new OurTerritory();
            }
        }

        for(int i = 0; i < segments; i++){
            int dir = random.nextInt(4);
            int length = random.nextInt(3); // Длина от 0 до 2

            for(int j = 0; j < length; j++){
                switch(dir){
                    case 0 -> y = Math.max(0, y - 1); // вверх
                    case 1 -> y = Math.min(map.length - 1, y + 1); // вниз
                    case 2 -> x = Math.max(0, x - 1); // влево
                    case 3 -> x = Math.min(map[0].length - 1, x + 1); // вправо
                }
                if((map[y][x] instanceof Void || map[y][x] instanceof RoadBorder) && (map[sizeY - y - 1][sizeX - x - 1] instanceof Void || map[sizeY - y - 1][sizeX - x - 1] instanceof RoadBorder)){
                    if(team == 1){
                        map[y][x] = new OurTerritory();
                        map[sizeY - y - 1][sizeX - x - 1] = new EnemyTerritory();
                    } else {
                        map[y][x] = new EnemyTerritory();
                        map[sizeY - y - 1][sizeX - x - 1] = new OurTerritory();
                    }
                }
            }
        }
    }
}
