package gameactors;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import entity.Hero;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import maps.GameMap;
import objects.types.Coin;
import objects.types.Nothing;
import objects.types.Wings;
import other.Battle;
import other.Castle;
import other.GameSaver;
import other.Plasable;
import other.Ticket;
import other.Vaitable;
import terrains.Terrain;
import terrains.TimeObject;
import terrains.types.EnemyCastle;
import terrains.types.OurCastle;
import terrains.types.Rock;

public class Player implements Serializable{
    private static final Logger logger = LogManager.getLogger(Player.class);
    private transient Scanner scanner = new Scanner(System.in);
    protected ArrayList<Hero> heroes = new ArrayList<Hero>();
    protected int coins = 20;
    protected Castle castle = new Castle();
    protected Player enemy;
    protected int turnsInCastle = 0;
    protected int number = 0;
    protected GameMap map;
    private int points = 0;
    private boolean strizka = false;
    private CopyOnWriteArrayList<Ticket> tickets = new CopyOnWriteArrayList<>();
    protected static final int[][] directions = {
        {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}
    };

    protected int speedBoost = 3;

    public Player(){}

    public Player(GameMap map, String playerName){
        coins *= GameSaver.getAmplifier(playerName);
        heroes.add(new Hero(10 * speedBoost, 10, 1));
        heroes.get(0).setPosition(map.getPosPlayer(whoAmI()));
        map.placeCharacter(heroes.get(0).getPosition()[1], heroes.get(0).getPosition()[0], heroes.get(0));
        this.map = map;
    }

    // 5 4 5 5 4 5 5 5 4 5 5 4 5 5 4 5 5 5 4

    public Castle getCastle(){
        return castle;
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    public ArrayList<Hero> getHeroes() {
        return heroes;
    }

    public int whoAmI(){
        return 1;
    }

    public Hero findEnemyHero(int x, int y){
        ArrayList<Hero> enemyHeroes = enemy.getHeroes();
        for(int i = 0; i < enemyHeroes.size(); i++){
            int position[] = enemyHeroes.get(i).getPosition();
            if(position[1] == x && position[0] == y){
                return enemyHeroes.get(i);
            }
        }
        return null;
    }

    public boolean getStrizka() {
        return strizka;
    }

    public void setStrizka(boolean strizka) {
        this.strizka = strizka;
    }

    public int getTurnsInCastle() {
        return turnsInCastle;
    }

    public void setTurnsInCastle(int turnsInCastle) {
        this.turnsInCastle = turnsInCastle;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getCoins() {
        return coins;
    }

    public void addHero(){
        heroes.add(new Hero(10 * speedBoost, 10, whoAmI()));
        int pos[] = {map.getYPlayer(whoAmI()), map.getXPlayer(whoAmI())};
        heroes.get(heroes.size() - 1).setPosition(pos);
        map.placeCharacter(heroes.get(heroes.size() - 1).getPosition()[1], heroes.get(heroes.size() - 1).getPosition()[0], heroes.get(heroes.size() - 1));
        points += 10;
    }

    public void killHero(Hero hero){
        heroes.remove(hero);
        points -= 10;
    }

    public int getHeroDirection(Hero hero){
        return scanner.nextInt();
    }

    public int getBuying(int phase, Castle castle){
        return scanner.nextInt();
    }

    public int setHorses(int heroPos[], int heroNumber, int steps){
        if(heroPos[0] == map.getYPlayer(whoAmI()) && heroPos[1] == map.getXPlayer(whoAmI()) && heroes.get(heroNumber).getHorse() == 0 && castle.isStable() == 1){
            heroes.get(heroNumber).setHorse();
            steps *= 2;
            points += 7;
        }
        return steps;
    }

    public int calculateNewSteps(int direction, int[] heroPos, int steps){
        if(direction == -1){
            return steps;
        }
        int dx = directions[direction - 1][0], dy = directions[direction - 1][1];

        int newSteps = -1;
        if(direction % 2 == 1 && steps >= 1 + number % 2){
            newSteps = steps - (1 + number % 2) * map.getTerrain(heroPos[1] + dx, heroPos[0] + dy, whoAmI());
            if (newSteps >= 0){
                number++;
            }
        } else if (direction % 2 != 1) {
            newSteps = steps - map.getTerrain(heroPos[1] + dx, heroPos[0] + dy, whoAmI());
        }
        return newSteps;
    }

    public void processObjects(int x, int y, Hero hero){
        Plasable type = map.getTypeOfObjectXY(x, y);

        if(type instanceof Coin){
            coins++;
            points++;
        } else if(type instanceof Wings){
            hero.setWings();
            points += 6;
        }
    }

    public Plasable getObjectOnMap(int x, int y){
        return map.getCharacterXY(x, y);
    }

    public synchronized void moveToCoords(int x, int y, Hero hero){
        map.placeCharacter(hero.getPosition()[1], hero.getPosition()[0], new Nothing(hero.getPosition()[1], hero.getPosition()[0]));
        int dx = x - hero.getPosition()[1];
        int dy = y - hero.getPosition()[0];
        hero.move(dx, dy);
        map.placeCharacter(hero.getPosition()[1], hero.getPosition()[0], hero);
    }

    public void go(Terrain buildingToGo, Plasable itemToGo, int[] heroPos, int dx, int dy, int heroNumber){
        map.placeCharacter(heroPos[1], heroPos[0], new Nothing(heroPos[1], heroPos[0]));
        heroes.get(heroNumber).move(dx, dy);
        map.placeCharacter(heroPos[1], heroPos[0], heroes.get(heroNumber));
        if(itemToGo instanceof Coin || itemToGo instanceof Wings){
            processObjects(heroPos[1], heroPos[0], heroes.get(heroNumber));
        }

        if (this instanceof Enemy){
            return;
        }

        Terrain terrain = map.getXY(heroPos[1], heroPos[0]);
        if (terrain instanceof TimeObject) {
            ((TimeObject) terrain).getInterface(this, (Vaitable) heroes.get(heroNumber));
            if (heroes.get(heroNumber).isStrizka()) {
                strizka = true;
            }
        }
    }

    public int goAndAttack(int dx, int dy, int[] heroPos, int heroNumber, ArrayList<Hero> heroesToKill, int newSteps){
        turnInerface(2, new int []{});
        Hero enemyHero = findEnemyHero(heroPos[1] + dx, heroPos[0] + dy);
        Battle battle = new Battle(heroes.get(heroNumber), enemyHero, whoAmI(), 24);
        int winner = battle.start();

        this.coins += battle.getCoinsAttacker();
        enemy.setCoins(enemy.getCoins() + battle.getCoinsDefender());

        if(winner == whoAmI()){
            enemy.killHero(enemyHero);

            map.placeCharacter(heroPos[1], heroPos[0], new Nothing(heroPos[1], heroPos[0]));
            heroes.get(heroNumber).move(dx, dy);
            map.placeCharacter(heroPos[1], heroPos[0], heroes.get(heroNumber));
            points += 5;
        } else {
            heroesToKill.add(heroes.get(heroNumber));

            map.placeCharacter(heroPos[1], heroPos[0], new Nothing(heroPos[1], heroPos[0]));

            points -= 5;
            return 0;
        }
        return newSteps;
    }

    public int checkBreakCondition(int steps, int[] heroPos, int direction){
        if(steps >= 0){
            int minPointsToTurn = map.getMinSteps(heroPos[1], heroPos[0], whoAmI());
            if(minPointsToTurn > steps){
                return 1;
            }
        }

        if(direction == -1){
            return 1;
        }

        return 0;
    }

    public void killHeroes(ArrayList<Hero> heroesToKill){
        for(Hero i : heroesToKill){
            this.killHero(i);
        }
    }

    public void initScanner(){
        scanner = new Scanner(System.in);
    }

    public int turn(){
        points = 0;
        ArrayList<Hero> heroesToKill = new ArrayList<Hero>();
        for(int i = 0; i < heroes.size(); i++){
            int heroPos[] = heroes.get(i).getPosition();

            if(heroes.get(i).getWings() == 0){
                int steps = heroes.get(i).getSpeed();
                int dx = 0, dy = 0, direction;
                number = 0;

                turnInerface(1, new int []{steps, i});

                while(steps > 0){
                    direction = getHeroDirection(heroes.get(i));
                    if(direction != -1){
                        dx = directions[direction - 1][0];
                        dy = directions[direction - 1][1];
                    } else {
                        dx = 0;
                        dy = 0;
                    }
    
                    steps = setHorses(heroPos, i, steps);
    
                    if(heroPos[0] + dy >= 0 && heroPos[0] + dy <= map.getSizeY() && heroPos[1] + dx >= 0 && heroPos[1] + dx <= map.getSizeX()){
                        int newSteps = calculateNewSteps(direction, heroPos, steps);
        
                        if(newSteps >= 0 || direction == -1){
                            Plasable itemToGo = map.getCharacterXY(heroPos[1] + dx, heroPos[0] + dy);
                            Terrain buildingToGo = map.getXY(heroPos[1] + dx, heroPos[0] + dy);
    
                            steps = actionsAfterMove(heroes.get(i), itemToGo, buildingToGo, newSteps, dx, dy, heroPos, i, heroesToKill);
                        } else {
                            if(whoAmI() == 2){
                                break;
                            }
                        }
                    } else {
                        logger.warn("Попытка выхода за границы карты x:{} y:{}", heroPos[1] + dx, heroPos[0] + dy);
                    }
                    turnInerface(3, new int []{steps, i});
                    if(checkBreakCondition(steps, heroPos, direction) == 1){
                        break;
                    }
                }
                heroes.get(i).setStepsLeft(steps);
            } else if(heroes.get(i).getWings() == 1){
                turnInerface(4, new int []{i});

                int[] xy = getXYForWings();

                int dx = xy[0] - heroPos[1];
                int dy = xy[1] - heroPos[0];

                Plasable itemToGo = map.getCharacterXY(xy[0], xy[1]);
                Terrain buildingToGo = map.getXY(xy[0], xy[1]);

                actionsAfterMove(heroes.get(i), itemToGo, buildingToGo, 0, dx, dy, heroPos, i, heroesToKill);
                turnInerface(5, new int []{i});
            }
        }
        killHeroes(heroesToKill);
        checkTurnsInCastle();
        buying();
        return points;
    }

    public void checkTurnsInCastle(){
        for (Hero hero : heroes) {
            if (map.getXY(hero.getPosition()[1], hero.getPosition()[0]) instanceof EnemyCastle && hero.getTeam() == 1) {
                turnsInCastle += 1;
                break;
            } else if (map.getXY(hero.getPosition()[1], hero.getPosition()[0]) instanceof OurCastle && hero.getTeam() == 2) {
                turnsInCastle += 1;
                break;
            } else {
                turnsInCastle = 0;
            }
        }
    }

    public int actionsAfterMove(Hero hero, Plasable itemToGo, Terrain buildingToGo, int newSteps, int dx, int dy, int[] heroPos, int index, ArrayList<Hero> heroesToKill){
        int steps = 0;
        if(itemToGo instanceof Hero && ((Hero)itemToGo).getTeam() != hero.getTeam()){
            logger.info("Началась битва");
            steps = goAndAttack(dx, dy, heroPos, index, heroesToKill, newSteps);
        } else {
            steps = newSteps;
            go(buildingToGo, itemToGo, heroPos, dx, dy, index);
        }
        return steps;
    }

    public int[] getXYForWings(){
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        while(true){
            if (x < 0 || y < 0 || x >= map.getSizeX() || y >= map.getSizeY()) {
                logger.warn("Попытка выхода за границы карты при помощи крыльев x:{} y:{}", x, y);
                x = scanner.nextInt();
                y = scanner.nextInt();
            } else {
                break;
            }
        }
        if(map.getXY(x, y) instanceof Rock){
            logger.error("Попытка полететь на скалу");
        }
        return new int[] {x, y};
    }

    public void turnInerface(int stage, int parametres[]){
        if(whoAmI() != 1){
            return;
        }
        switch (stage) {
            case 1:
                System.out.print("У вашего героя осталось ");
                System.out.print(parametres[0]);
                System.out.print(" шагов и он находится в точке ");
                System.out.print(heroes.get(parametres[1]).getPosition()[1]);
                System.out.print(" ");
                System.out.println(heroes.get(parametres[1]).getPosition()[0]);
                System.out.print("Сейчас у вас ");
                System.out.print(coins);
                System.out.println(" монет");
                break;
            case 2:
                System.out.println("Началась битва!");
                break;
            case 3:
                map.print();
                System.out.print("У вашего героя осталось ");
                System.out.print(parametres[0]);
                System.out.print(" шагов и он находится в точке ");
                System.out.print(heroes.get(parametres[1]).getPosition()[1]);
                System.out.print(" ");
                System.out.println(heroes.get(parametres[1]).getPosition()[0]);
                System.out.print("Сейчас у вас ");
                System.out.print(coins);
                System.out.println(" монет");
                break;
            case 4:
                System.out.print("У вашего героя есть крылья ");
                System.out.print("и он находится в точке ");
                System.out.print(heroes.get(parametres[0]).getPosition()[1]);
                System.out.print(" ");
                System.out.println(heroes.get(parametres[0]).getPosition()[0]);
                System.out.print("Сейчас у вас ");
                System.out.print(coins);
                System.out.println(" монет\nВведите координаты перемещения x, y");
                break;
            case 5:
                map.print();
                System.out.print("Ваш герой находится в точке ");
                System.out.print(heroes.get(parametres[0]).getPosition()[1]);
                System.out.print(" ");
                System.out.println(heroes.get(parametres[0]).getPosition()[0]);
                System.out.print("Сейчас у вас ");
                System.out.print(coins);
                System.out.println(" монет");
                break;
            default:
                break;
        }
    }

    public void buying(){
        while(true){
            buyingInterface(1);
            int inp = getBuying(1, castle);

            if(inp == 1){
                buyingInterface(2);
                castle.buy(this, getBuying(2, castle));
            } else if(inp == 2){
                for(int i = 0; i < heroes.size(); i++){
                    int position[] = heroes.get(i).getPosition();
                    if(position[0] == map.getYPlayer(whoAmI()) && position[1] == map.getXPlayer(whoAmI())){
                        buyingInterface(3);
                        castle.unit(this, heroes.get(i), getBuying(3, castle));
                    }
                }
            } else if(inp == 3){
                int xPlayer = map.getXPlayer(whoAmI());
                int yPlayer = map.getYPlayer(whoAmI());

                if(coins >= 10 && map.getCharacterXY(xPlayer, yPlayer) instanceof Nothing){
                    if(castle.isTavern() == 1){
                        addHero();
                    }
                }
            } else if(inp == 4 || inp == -1){
                for(int i = 0; i < heroes.size(); i++){
                    int position[] = heroes.get(i).getPosition();
                    if(position[0] == map.getYPlayer(1) && position[1] == map.getXPlayer(1) && heroes.get(i).getHorse() == 0 && castle.isStable() == 1){
                        heroes.get(i).setHorse();
                    }
                }
                break;
            } else if(inp == 5) {
                if (coins >= 5) {
                    tickets.add(new Ticket());
                    coins -= 5;
                } else {
                    logger.error("Недостаточно монет для покупки билета на фуникулер");
                }
            } else {
                logger.error("Неверный ввод при закупке ввод:{}", inp);
            }
        }
    }

    public void buyingInterface(int stage){
        if(whoAmI() != 1){
            return;
        }
        switch (stage) {
            case 1:
                System.out.println("(1)Купить постройку (2)Купить юнита (3)Нанять героя (4)Выйти (5)Купить билет на фуникулер");
                break;
            case 2:
                castle.printAvailableBuildings();
                break;
            case 3:
                castle.printAvailableUnits();
                break;
            default:
                break;
        }
    }

    public CopyOnWriteArrayList<Ticket> getTickets() {
        return tickets;
    }
    public void removeTicket(int index) {
        tickets.remove(index);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();  // стандартная десериализация
        this.scanner = new Scanner(System.in);  // восстанавливаем Scanner
    }
}
