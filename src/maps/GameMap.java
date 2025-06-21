package maps;
import java.io.Serializable;
import java.util.ArrayList;

import objects.ObjectOnGameMap;
import objects.types.Nothing;
import objects.types.Wings;
import other.Plasable;
import terrains.Terrain;
import terrains.types.BarberShop;
import terrains.types.Cafe;
import terrains.types.EnemyCastle;
import terrains.types.EnemyTerritory;
import terrains.types.Funiculer;
import terrains.types.Hotel;
import terrains.types.OurCastle;
import terrains.types.OurTerritory;
import terrains.types.Road;
import terrains.types.RoadBorder;
import terrains.types.Rock;
import terrains.types.Void;
import objects.types.Coin;

public class GameMap extends Map implements Serializable {
    private static final long serialVersionUID = 1L;

    private int posPlayer[] = new int[2], posPC[] = new int[2];
    private int xPlayer = 0;
    private int yPlayer = 0;
    private int xPC = 0;
    private int yPC = 0;
    private ArrayList<ObjectOnGameMap> objects = new ArrayList<ObjectOnGameMap>();

    // 0 - нет героя
    // 1 - герой игрока
    // 2 - герой компьютера
    // 3 - монетка

    /*
     ____
    |0  1|
    |    |
    |3__2|
     */

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

    public GameMap(int seed){
        random.setSeed(seed);
        //карта
        sizeX = random.nextInt(30) + 1;
        sizeY = random.nextInt(30) + 1;
        map = new Terrain[sizeY][sizeX];
        characters = new Plasable[sizeY][sizeX];
        for(int i = 0; i < sizeY; i++){
            for(int j = 0; j < sizeX; j++){
                map[i][j] = new Void();
                characters[i][j] = new Nothing(j, i);
            }
        }
        //замки
        int pos = random.nextInt(4);
        if(pos == 0){
            posPlayer[0] = 0;
            posPlayer[1] = 0;
            posPC[0] = sizeY - 1;
            posPC[1] = sizeX - 1;
        } else if(pos == 1){
            posPlayer[0] = 0;
            posPlayer[1] = sizeX - 1;
            posPC[0] = sizeY - 1;
            posPC[1] = 0;
        } else if(pos == 2){
            posPlayer[0] = sizeY - 1;
            posPlayer[1] = sizeX - 1;
            posPC[0] = 0;
            posPC[1] = 0;
        } else if(pos == 3){
            posPlayer[0] = sizeY - 1;
            posPlayer[1] = 0;
            posPC[0] = 0;
            posPC[1] = sizeX - 1;
        }
        map[posPlayer[0]][posPlayer[1]] = new OurCastle();
        map[posPC[0]][posPC[1]] = new EnemyCastle();
        xPlayer = posPlayer[1];
        yPlayer = posPlayer[0];
        xPC = posPC[1];
        yPC = posPC[0];
        //дорога
        drawLine(posPlayer[1], posPlayer[0], posPC[1], posPC[0]);
        markBorder(new Road(), RoadBorder.class);
        //области
        generateLines(posPlayer[1], posPlayer[0], 10, 1);
        markBorder(new OurTerritory(), Rock.class);
        markBorder(new EnemyTerritory(), Rock.class);
        for(int i = 0; i < sizeY; i++){
            for(int j = 0; j < sizeX; j++){
                if(map[i][j] instanceof RoadBorder){
                    map[i][j] = new Void();
                }
            }
        }
    }

    public GameMap(Terrain[][] terrainMap, Plasable[][] characterMap) {
        sizeY = terrainMap.length;
        sizeX = terrainMap[0].length;
        map = new Terrain[sizeY][sizeX];
        characters = new Plasable[sizeY][sizeX];
        
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                map[i][j] = terrainMap[i][j];
                characters[i][j] = characterMap[i][j];
            }
        }

        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (map[i][j] instanceof OurCastle) {
                    posPlayer[0] = i;
                    posPlayer[1] = j;
                    xPlayer = j;
                    yPlayer = i;
                } else if (map[i][j] instanceof EnemyCastle) {
                    posPC[0] = i;
                    posPC[1] = j;
                    xPC = j;
                    yPC = i;
                }
            }
        }
    }

    public int placeObjectXY(int x, int y, ObjectOnGameMap object, Terrain[] typesOfTerrain){
        for(Terrain i : typesOfTerrain){
            if(characters[y][x] instanceof Nothing && map[y][x].getClass().equals(i.getClass())){
                objects.add(object);
                characters[y][x] = object;
                return 1;
            }
        }
        return 0;
    }

    public void placeCoins(int number){
        while(number > 0){
            int x = random.nextInt(sizeX);
            int y = random.nextInt(sizeY);

            if(placeObjectXY(x, y, new Coin(x, y), new Terrain[] {new Void(), new Road(), new OurTerritory(), new EnemyTerritory()}) == 1){
                number--;
            }
        }
    }

    public void placeHotel(int number){
        while(number > 0){
            int x = random.nextInt(sizeX);
            int y = random.nextInt(sizeY);

            if(map[y][x].getClass().equals(Rock.class)){
                number--;
                map[y][x] = new Hotel();
            }
        }
    }

    public void placeCafe(int number){
        while(number > 0){
            int x = random.nextInt(sizeX);
            int y = random.nextInt(sizeY);

            if(map[y][x].getClass().equals(Void.class)){
                number--;
                map[y][x] = new Cafe();
            }
        }
    }

    public void placeBarberShop(int number){
        while(number > 0){
            int x = random.nextInt(sizeX);
            int y = random.nextInt(sizeY);

            if(map[y][x].getClass().equals(Road.class)){
                number--;
                map[y][x] = new BarberShop();
            }
        }
    }

    public void placeFuniculer(int number){
        while(number > 0){
            int x = random.nextInt(sizeX);
            int y = random.nextInt(sizeY);

            if(map[y][x].getClass().equals(Void.class)){
                number--;
                map[y][x] = new Funiculer();
            }
        }

        ArrayList<int[]> funiculerCoords = new ArrayList<>();
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                if (map[y][x] instanceof Funiculer) {
                    funiculerCoords.add(new int[]{x, y});
                }
            }
        }
        if (funiculerCoords.size() >= 2) {
            Funiculer funiculer1 = (Funiculer) map[funiculerCoords.get(0)[1]][funiculerCoords.get(0)[0]];
            Funiculer funiculer2 = (Funiculer) map[funiculerCoords.get(1)[1]][funiculerCoords.get(1)[0]];
            funiculer1.setFuniculer2(funiculerCoords.get(1)[0], funiculerCoords.get(1)[1]);
            funiculer2.setFuniculer2(funiculerCoords.get(0)[0], funiculerCoords.get(0)[1]);
        }
    }

    public void placeWings(int number){
        while(number > 0){
            int x = random.nextInt(sizeX);
            int y = random.nextInt(sizeY);

            if(placeObjectXY(x, y, new Wings(x, y), new Terrain[] {new Void()}) == 1){
                number--;
            }
        }
    }

    public int[] getPosPlayer(int player) {
        if(player == 1){
            return posPlayer;
        } else {
            return posPC;
        }
    }
    
    public int getXPlayer(int player) {
        if(player == 1){
            return xPlayer;
        } else {
            return xPC;
        }
    }

    public int getYPlayer(int player) {
        if(player == 1){
            return yPlayer;
        } else {
            return yPC;
        }
    }

    private void markBorder(Terrain areaIndex, Class<? extends Terrain> borderIndex){
        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[y].length; x++){
                if(map[y][x].getClass().equals(areaIndex.getClass())){
                    for(int dy = -1; dy <= 1; dy++){
                        for(int dx = -1; dx <= 1; dx++){
                            int nx = x + dx, ny = y + dy;
                            if(nx >= 0 && nx < map[0].length && ny >= 0 && ny < map.length && (map[ny][nx] instanceof Void || map[ny][nx] instanceof RoadBorder)){
                                try {
                                    map[ny][nx] = borderIndex.getDeclaredConstructor().newInstance();
                                } catch (Exception e) {
                                    System.out.println("Exeption");
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    for(int dy = -3; dy <= 3; dy++){
                        for(int dx = -3; dx <= 3; dx++){
                            int nx = x + dx, ny = y + dy;
                            if(nx >= 0 && nx < map[0].length && ny >= 0 && ny < map.length && map[ny][nx] instanceof RoadBorder){
                                try {
                                    map[ny][nx] = borderIndex.getDeclaredConstructor().newInstance();
                                } catch (Exception e) {
                                    System.out.println("Exeption");
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ObjectOnGameMap getTypeOfObjectXY(int x, int y){
        for(ObjectOnGameMap i : objects){
            if(i.getX() == x && i.getY() == y){
                return i;
            }
        }
        return null;
    }

    public void print(){
        for(int i = -1; i < sizeY; i++){
            if(i != -1){
                System.out.print(i % 10);
            } else {
                System.out.print(" ");
            }
            for(int j = 0; j < sizeX; j++){
                if(i == -1){
                    System.out.print(j % 10);
                    System.out.print(" ");
                    continue;
                }
                if ((characters[i][j] instanceof Nothing) == false){
                    System.out.print(characters[i][j].getDisplay());
                    continue;
                }
                System.out.print(map[i][j].getDisplay());
            }
            System.out.println();
        }
    }
}
