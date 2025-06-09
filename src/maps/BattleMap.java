package maps;
import entity.Hero;
import objects.types.Nothing;
import other.Plasable;
import terrains.Terrain;
import terrains.types.Void;

public class BattleMap extends Map {
    /*
    0 - ничего
    // 1 - герой игрока
    // 2 - герой компьютера
    1 - юнит игрока 1 уровня
    2 - юнит игрока 2 уровня
    3 - юнит игрока 3 уровня
    4 - юнит игрока 4 уровня
    5 - юнит игрока 5 уровня
    6 - юнит компьютера 1 уровня
    7 - юнит компьютера 2 уровня
    8 - юнит компьютера 3 уровня
    9 - юнит компьютера 4 уровня
    10 - юнит компьютера 5 уровня
     */
    /*
     ____
    |0  1|
    |    |
    |3__2|
     */

    /*
    0 - ничего
    3 - дорога
    4 - территория игрока
    5 - территория компьютера
     */

    public BattleMap(int seed, Hero player, Hero enemy){
        random.setSeed(seed);
        //карта
        sizeX = random.nextInt(30 / 2) + 1;
        sizeY = random.nextInt(30 / 2) + 1;
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
        int posPlayer[] = new int[2], posPC[] = new int[2];
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
        placeUnits(player, posPlayer[1], posPlayer[0]);
        placeUnits(enemy, posPC[1], posPC[0]);
        //дорога
        drawLine(posPlayer[1], posPlayer[0], posPC[1], posPC[0]);
        //области
        generateLines(posPlayer[1], posPlayer[0], 25, 1);
    }

    public void placeUnits(Hero hero, int x, int y){
        int rows = characters.length;
        int cols = characters[0].length;
        
        int[][] distances = new int[rows][cols];
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                distances[r][c] = Math.abs(r - y) + Math.abs(c - x);
            }
        }

        characters[y][x] = hero.getUnits().get(0);
        int positionStart[] = {y, x};
        hero.getUnits().get(0).setPosition(positionStart);
        distances[y][x] = Integer.MAX_VALUE;

        for(int i = 1; i < hero.getUnits().size(); i++){
            int minDist = Integer.MAX_VALUE;
            int minRow = -1, minCol = -1;
            
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if ((r != y || c != x) && distances[r][c] < minDist) {
                        minDist = distances[r][c];
                        minRow = r;
                        minCol = c;
                    }
                }
            }
            
            if (minRow != -1 && minCol != -1) {
                characters[minRow][minCol] = hero.getUnits().get(i);
                int position[] = {minRow, minCol};
                hero.getUnits().get(i).setPosition(position);
                distances[minRow][minCol] = Integer.MAX_VALUE;
            }
        }
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

//💎🌱🪨🏰🏯🌳⛰️🌋🏇🤺🏕️⛺️🛖⛩️🛕