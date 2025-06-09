package other;
import entity.Character;
import entity.Hero;
import entity.types.Cavalryman;
import entity.types.Crossbowman;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import maps.BattleMap;
import objects.types.Nothing;

public class Battle implements Serializable{
    private transient Scanner scanner = new Scanner(System.in);
    protected static final int[][] directions = {
        {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}
    };
    private Hero player;
    private Hero enemy;
    private BattleMap map;
    private Castle castle = new Castle();
    private int attackerTeam;
    private int coinsAttacker = 0;
    private int coinsDefender = 0;

    public Battle(Hero attacker, Hero defender, int attackerTeam, int seed){
        if(attackerTeam == 1){
            player = attacker;
            enemy = defender;
        } else if(attackerTeam == 2){
            player = defender;
            enemy = attacker;
        }
        this.attackerTeam = attackerTeam;
        map = new BattleMap(seed, player, enemy);
    }

    public int start(){
        map.print();

        while(true){
            turn(attackerTeam);
            if(player.getUnits().size() == 0){
                return 2;
            }
            if(enemy.getUnits().size() == 0){
                return 1;
            }

            turn(3 - attackerTeam);
            if(player.getUnits().size() == 0){
                return 2;
            }
            if(enemy.getUnits().size() == 0){
                return 1;
            }
        }
    }

    public int getPlayerInput(){
        return scanner.nextInt();
    }

    public int getAIInput(){
        return -1;
    }

    public int getDirection(int team){
        int direction = -1;
        if(team == 1){
            direction = getPlayerInput();
        } else if(team == 2){
            direction = getAIInput();
        }
        /*
        1 2 3
        8 * 4
        7 6 5
        */
        return direction;
    }

    public Hero getOurHero(int team){
        Hero hero = player;
        if(team == 2){
            hero = enemy;
        }
        return hero;
    }

    public int calculateNewSteps(int direction, int steps, int number, int[] unitPos, int team){
        int dx = directions[direction - 1][0], dy = directions[direction - 1][1];
        int newSteps = -1;
        if(direction % 2 == 1 && steps >= 1 + number % 2){
            newSteps = steps - (1 + number % 2) * map.getTerrain(unitPos[1] + dx, unitPos[0] + dy, team);
            if (newSteps >= 0){
                number++;
            }
        } else if (direction % 2 != 1) {
            newSteps = steps - map.getTerrain(unitPos[1] + dx, unitPos[0] + dy, team);
        }
        return newSteps;
    }

    public void go(int[] unitPos, int index, int dx, int dy, Hero hero, Character unit){
        map.placeCharacter(unitPos[1], unitPos[0], new Nothing(unitPos[1], unitPos[0]));
        hero.getUnits().get(index).move(dx, dy);
        map.placeCharacter(unitPos[1], unitPos[0], hero.getUnits().get(index));
    }

    public Character getOponent(int team, int dx, int dy, int[] unitPos){
        Character oponent = new Character();

        int x = unitPos[1] + dx;
        int y = unitPos[0] + dy;

        if(team == 1){
            for(Character j : enemy.getUnits()){
                if(j.getPosition()[1] == x && j.getPosition()[0] == y){
                    oponent = j;
                }
            }
        } else if(team == 2){
            for(Character j : player.getUnits()){
                if(j.getPosition()[1] == x && j.getPosition()[0] == y){
                    oponent = j;
                }
            }
        }

        return oponent;
    }

    public void deleteOponentUnit(int team, Character oponent){
        if(team == 1){
            enemy.deleteUnit(oponent);
        } else if(team == 2){
            player.deleteUnit(oponent);
        }
    }

    public void turn(int team){
        ArrayList<Character> unitsToKill = new ArrayList<Character>();
        Hero hero = getOurHero(team);
        for(int i = 0; i < hero.getUnits().size(); i++){
            int steps = hero.getUnits().get(i).getSpeed() * 10;
            int number = 0, dx = 0, dy = 0, direction = -1;
            while(steps > 0){                
                Character unit = hero.getUnits().get(i);
                int unitPos[] = hero.getUnits().get(i).getPosition();

                turnInerface(1, team, new int[] {steps, unitPos[1], unitPos[0]}, map);

                direction = getDirection(team);
                if(direction == -1){
                    break;
                }
                dx = directions[direction - 1][0];
                dy = directions[direction - 1][1];

                if(unitPos[0] + dy >= 0 && unitPos[0] + dy <= map.getSizeY() && unitPos[1] + dx >= 0 && unitPos[1] + dx <= map.getSizeX()){
                    int newSteps = calculateNewSteps(direction, steps, number, unitPos, team);

                    if(newSteps >= 0){
                        Plasable itemToGo = map.getCharacterXY(unitPos[1] + dx, unitPos[0] + dy);

                        if(itemToGo instanceof Nothing){
                            steps = newSteps;
                            go(unitPos, i, dx, dy, hero, unit);
                        } else if(itemToGo instanceof Character && ((Character)itemToGo).getTeam() != unit.getTeam()){                            
                            Character oponent = getOponent(team, dx, dy, unitPos);

                            int winner = attack(unit, oponent, team);

                            if(winner == 1){
                                coinsAttacker += castle.getReward(oponent.getLvl() + 1);
                                deleteOponentUnit(team, oponent);
                                steps = newSteps;
                                go(unitPos, i, dx, dy, hero, unit);
                            } else if (winner == 2){
                                coinsDefender += castle.getReward(oponent.getLvl() + 1);
                                unitsToKill.add(unit);
                                map.placeCharacter(unitPos[1], unitPos[0], new Nothing(unitPos[1], unitPos[0]));
                                newSteps = 0;
                                steps = 0;
                            }
                        }
                    }
                }

                hero.getUnits().get(i).setStepsLeft(steps);
                
                turnInerface(2, team, new int[] {}, map);

                if(checkBreakCondition(steps, unitPos, team) == 1){
                    break;
                }
            }
        }
        killUnits(unitsToKill);
        if(checkBattleFinal(team) == 1){
            return;
        }
    }

    public int checkBattleFinal(int team){
        if(player.getUnits().size() == 0 || enemy.getUnits().size() == 0){
            turnInerface(3, team, new int[] {player.getUnits().size(), enemy.getUnits().size()}, map);
            return 1;
        }
        return 0;
    }

    public void killUnits(ArrayList<Character> unitsToKill){
        for(Character i : unitsToKill){
            player.deleteUnit(i);
        }
    }

    public int checkBreakCondition(int steps, int[] unitPos, int team){
        if(steps >= 0){
            int minPointsToTurn = map.getMinSteps(unitPos[1], unitPos[0], team);
            if(minPointsToTurn > steps){
                return 1;
            }
        }
        return 0;
    }

    public void turnInerface(int stage, int team, int[] parametres, BattleMap map){
        if(team == 2){
            return;
        }
        switch (stage) {
            case 1:
                System.out.print("У вашего юнита осталось ");
                System.out.print(parametres[0]);
                System.out.print(" шагов и он находится в точке ");
                System.out.print(parametres[1]);
                System.out.print(" ");
                System.out.println(parametres[2]);
                break;
            case 2:
                map.print();
                break;
            case 3:
                if(parametres[0] == 0){
                    System.out.println("Компьютер победил!");
                } else if(parametres[1] == 0){
                    System.out.println("Игрок победил!");
                }
                break;
            default:
                break;
        }
    }

    public int getCoinsAttacker() {
        return coinsAttacker;
    }

    public int getCoinsDefender() {
        return coinsDefender;
    }

    public int attack(Character attacker, Character defender, int teamAttacker){
        int stackCount = CountConnectedCells(map.getCharacters(), attacker.getPosition()[1], attacker.getPosition()[0], map.getCharacterXY(attacker.getPosition()[1], attacker.getPosition()[0]));
        int stackEnemyCount = CountConnectedCells(map.getCharacters(), defender.getPosition()[1], defender.getPosition()[0], map.getCharacterXY(defender.getPosition()[1], defender.getPosition()[0]));

        int crossbowmanCount = CountDigitsInRadius(map.getCharacters(), attacker.getPosition()[1], attacker.getPosition()[0], new Crossbowman(1).getDamageDistance(), new Crossbowman(1));
        int cavalrymanCount = CountDigitsInRadius(map.getCharacters(), attacker.getPosition()[1], attacker.getPosition()[0], new Cavalryman(1).getDamageDistance(), new Cavalryman(1));
        int crossbowmanEnemyCount = CountDigitsInRadius(map.getCharacters(), defender.getPosition()[1], defender.getPosition()[0], new Crossbowman(2).getDamageDistance(), new Crossbowman(2));
        int cavalrymanEnemyCount = CountDigitsInRadius(map.getCharacters(), defender.getPosition()[1], defender.getPosition()[0], new Cavalryman(2).getDamageDistance(), new Cavalryman(2));

        int hpAttaker = attacker.getHp() * stackCount;
        int damageAttacker = attacker.getDamage() * stackCount + crossbowmanCount * new Crossbowman(0).getDamage() + cavalrymanCount * new Cavalryman(0).getDamage();

        int hpDefender = defender.getHp() * stackEnemyCount;
        int damageDefender = defender.getDamage() * stackEnemyCount + crossbowmanEnemyCount * new Crossbowman(0).getDamage() + cavalrymanEnemyCount * new Cavalryman(0).getDamage();

        int winner = 0;

        while(true){
            hpDefender -= damageAttacker;
            if(hpDefender <= 0){
                winner = 1;
                defender.setHp(0);
                break;
            }
            hpAttaker -= damageDefender;
            if(hpAttaker <= 0){
                winner = 2;
                attacker.setHp(0);
                break;
            }
        }

        return winner;
    }

    public static int CountDigitsInRadius(Plasable[][] array, int x, int y, int radius, Plasable target) {
        int rows = array.length;
        int cols = array[0].length;
        int count = 0;

        for (int i = Math.max(0, y - radius); i <= Math.min(rows - 1, y + radius); i++) {
            for (int j = Math.max(0, x - radius); j <= Math.min(cols - 1, x + radius); j++) {
                if (array[i][j].getClass().equals(target.getClass()) && ((Character)array[i][j]).getTeam() == ((Character)target).getTeam()) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int DFS(Plasable[][] matrix, int x, int y, Plasable targetNumber, boolean[][] visited) {
        // Проверяем границы массива и посещённость ячейки
        if (y < 0 || y >= matrix.length || x < 0 || x >= matrix[0].length || visited[y][x]) {
            return 0;
        }
        if(!matrix[y][x].getClass().equals(targetNumber.getClass()) || ((Character)matrix[y][x]).getTeam() != ((Character)targetNumber).getTeam()){
            return 0;
        }

        visited[y][x] = true; // Отмечаем ячейку как посещённую
        int count = 1; // Считаем текущую ячейку

        // Обходим все 8 направлений
        for (int[] dir : directions) {
            count += DFS(matrix, x + dir[0], y + dir[1], targetNumber, visited);
        }

        return count;
    }

    public static int CountConnectedCells(Plasable[][] matrix, int x, int y, Plasable targetNumber) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }

        boolean[][] visited = new boolean[matrix.length][matrix[0].length];

        int answer = DFS(matrix, x, y, targetNumber, visited);
        // Обходим все 8 направлений
        for (int[] dir : directions) {
            answer += DFS(matrix, x + dir[0], y + dir[1], targetNumber, visited);
        }

        return answer;
    }
}

/*
5 4 5 5 4 5 5 5 4 5 5 4 5 5 4 5 5 -1 4
5 4 5 5 4 5

убить кавалериста при помощи арбалетчика
умереть от копейщиков

следующий ход
потом 3й убить всех
*/