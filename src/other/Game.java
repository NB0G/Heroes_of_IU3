package other;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gameactors.Enemy;
import gameactors.Player;
import java.io.Serializable;
import java.sql.Time;

import java.util.Calendar;
import java.util.Scanner;
import maps.GameMap;
import maps.MapProcess;
import objects.types.Wings;
import terrains.Terrain;
import terrains.TimeObject;
import terrains.types.Void;

public class Game implements Serializable{
    private static final Logger logger = LogManager.getLogger(Game.class);
    private transient Scanner scanner = new Scanner(System.in);
    private GameMap gameMap;
    private Player player;
    private Enemy enemy;
    private String playerName;
    private int points = 0;
    private String mapNameGlobal;
    private String mapNameGlobalOwner;
    private String playerNameGlobal;
    private String mapOwner;
    private boolean isTest = false;
    private int startTimerTime = 0;
    private int needTurnsToWin = 2;

    public Game(String playerNameInput){
        logger.info("Игра создана");

        scanner = new Scanner(System.in);
        this.playerName = playerNameInput;

        printMessage("set_map");
        setGameMap();
        printMessage("loaded_map");
        gameMap.print();

        player = new Player(gameMap, playerNameInput);
        enemy = new Enemy(gameMap);
        player.setEnemy(enemy);
        enemy.setEnemy(player);

        gameMap.placeCoins(40);
        gameMap.placeWings(2);
        gameMap.placeHotel(1);
        gameMap.placeCafe(1);
        gameMap.placeBarberShop(2);
        gameMap.placeFuniculer(2);
        gameMap.placeObjectXY(gameMap.getSizeX() - 1, 0, new Wings(gameMap.getSizeX() - 1, 0), new Terrain[] {new Void()});
    }

    public Game(){
        isTest = true;

        scanner = new Scanner(System.in);

        gameMap = new GameMap(20);
        gameMap.print();

        player = new Player(gameMap, "Player");
        enemy = new Enemy(gameMap);
        player.setEnemy(enemy);
        enemy.setEnemy(player);

        gameMap.placeCoins(40);
        gameMap.placeWings(2);
        gameMap.placeObjectXY(gameMap.getSizeX() - 1, 0, new Wings(gameMap.getSizeX() - 1, 0), new Terrain[] {new Void()});

        // scanner.nextLine();
    }

    public void setNeedTurnsToWin(int needTurnsToWin) {
        this.needTurnsToWin = needTurnsToWin;
    }

    public void setStartTimerTime(int time) {
        startTimerTime = time;
    }

    private void setGameMap(){
        printMessage("load_or_create");
        int choice = scanner.nextInt();
        if (choice == 1) {
            MapProcess.printAvailableMaps();
            printMessage("load_map");
            String playerName = scanner.next();
            String mapName = scanner.next();
            gameMap = MapProcess.loadMap(mapName, playerName);
            mapOwner = playerName;
            mapNameGlobalOwner = mapName;
            if (gameMap == null) {
                logger.error("Ошибка при загрузке карты map:{}, player:{}", mapName, playerName);
                printMessage("error_load");
                setGameMap();
            } else {
                printMessage("load_ok");
            }
        } else if (choice == 2) {
            printMessage("map_gen_start");
            int genChoice = scanner.nextInt();
            if (genChoice == 1) {
                printMessage("enter_seed");
                int seed = scanner.nextInt();
                gameMap = new GameMap(seed);
            } else if (genChoice == 2) {
                gameMap = MapProcess.createMapFromConsole();
            } else {
                printMessage("error_input");
                setGameMap();
            }
            mapOwner = playerName;
        } else {
            printMessage("error_input");
            setGameMap();
        }

        saveMap();
    }

    private void saveMap() {
        printMessage("save_map");
        String saveName = scanner.next();
        MapProcess.saveMap(gameMap, saveName, playerName);
        playerNameGlobal = playerName;
        mapNameGlobal = saveName;
        logger.info("Карта сохранена под именем: {}", saveName);
    }

    private void userSave(){
        printMessage("save_game_or_not");
        int saveOrNot = scanner.nextInt();
        if (saveOrNot == 1) {
            printMessage("save_game");
            String saveName = scanner.next();
            GameSaver.saveGame(this, saveName, playerName);
            logger.info("Игра сохранена под именем: {}", saveName);
        } else {
            logger.info("Сохранение игры отменено");
        }
    }

    public void initTimeObjects() {
        for (int x = 0; x < gameMap.getSizeX(); x++) {
            for (int y = 0; y < gameMap.getSizeY(); y++) {
                Terrain obj = gameMap.getXY(x, y);
                if (obj instanceof TimeObject timeObject) {
                    timeObject.init();
                }
            }
        }
        TimeManager.setCurrentTime(startTimerTime);
        TimeManager.startTimer();
        TimeManager.createAndStartNpcs(15, gameMap, 1, 20, 1, 20);
        logger.info("Временные объекты и NPC инициализированы");
    }

    public int start(){
        scanner = new Scanner(System.in);
        initTimeObjects();
        initTimeObjects();
        logger.info("Игра началась");
        gameMap.print();
        player.initScanner();

        while (true) {
            printMessage("player_turn");
            points += player.turn();
            gameMap.print();
            printMessage("enemy_turn");
            enemy.turn();
            gameMap.print();

            if (player.getStrizka()){
                needTurnsToWin = 1;
            }

            int playerInCastle = player.getTurnsInCastle();
            int enemyInCastle = enemy.getTurnsInCastle();
            if(playerInCastle == needTurnsToWin && enemyInCastle == 0){
                logger.info("Игра завершилась, победил игрок");
                return 1;
            } else if(playerInCastle == 0 && enemyInCastle == 2){
                logger.info("Игра завершилась, победил компьютер");
                return 2;
            } else if(playerInCastle == needTurnsToWin && enemyInCastle == 2 && needTurnsToWin == 2){
                logger.info("Игра завершилась, ничья");
                return 0;
            }

            if (!isTest) {
                userSave();
                GameSaver.saveGame(this, "Autosave " + Calendar.getInstance().getTime().toString(), playerName);
            }
        }
    }

    public int getPoints(){
        return points;
    }

    public String getMapName(){
        return mapNameGlobal;
    }

    public String getMapNameOwner(){
        return mapNameGlobalOwner;
    }

    public String getPlayerName(){
        return playerNameGlobal;
    }

    public String getMapOwner(){
        return mapOwner;
    }

    private static void printMessage(String messageKey, Object... args) {
        switch (messageKey) {
            case "player_turn" -> System.out.println("Ваш ход");
            case "enemy_turn" -> System.out.println("Ход компьютера");
            case "set_map" -> System.out.println("Выбор карты");
            case "load_or_create" -> System.out.println("Загрузить (1) или создать (2) карту?");
            case "load_map" -> {
                System.out.println("Загрузить карту");
                System.out.println("Введите имя игрока и карты:");
            }
            case "error_load" -> System.out.println("Ошибка при загрузке карты. Попробуйте снова.");
            case "load_ok" -> System.out.println("Карта загружена успешно.");
            case "map_gen_start" -> System.out.println("Создание карты. Генерация по сиду (1) или ручная (2)?");
            case "enter_seed" -> System.out.println("Введите сид карты");
            case "error_input" -> System.out.println("Неверный ввод. Попробуйте снова.");
            case "loaded_map" -> System.out.println("Карта, которая будет использоваться:");
            case "save_game" -> System.out.println("Введите имя для сохранения игры:");
            case "save_game_or_not" -> System.out.println("Сохранить игру? (1 - да, 2 - нет)");
            case "save_map" -> System.out.println("Введите имя для сохранения карты:");
            case "save_map_or_not" -> System.out.println("Сохранить карту? (1 - да, 2 - нет)");
            default -> System.out.println("Неизвестное сообщение.");
        }
    }
}
