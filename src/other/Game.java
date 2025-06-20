package other;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gameactors.Enemy;
import gameactors.Player;
import java.io.Serializable;

import java.util.Calendar;
import java.util.Scanner;
import maps.GameMap;
import maps.MapProcess;
import objects.types.Wings;
import terrains.Terrain;
import terrains.types.Void;

public class Game implements Serializable{
    private static final Logger logger = LogManager.getLogger(Game.class);
    private transient Scanner scanner = new Scanner(System.in);
    private GameMap gameMap;
    private Player player;
    private Enemy enemy;
    private String playerNamePlusPass;

    public Game(String playerNamePlusPass){
        logger.info("Игра создана");

        scanner = new Scanner(System.in);
        this.playerNamePlusPass = playerNamePlusPass;

        printMessage("set_map");
        setGameMap();
        printMessage("loaded_map");
        gameMap.print();


        player = new Player(gameMap);
        enemy = new Enemy(gameMap);
        player.setEnemy(enemy);
        enemy.setEnemy(player);

        gameMap.placeCoins(40);
        gameMap.placeWings(2);
        gameMap.placeObjectXY(gameMap.getSizeX() - 1, 0, new Wings(gameMap.getSizeX() - 1, 0), new Terrain[] {new Void()});
    }

    public Game(){
        scanner = new Scanner(System.in);

        gameMap = new GameMap(20);
        gameMap.print();

        player = new Player(gameMap);
        enemy = new Enemy(gameMap);
        player.setEnemy(enemy);
        enemy.setEnemy(player);

        gameMap.placeCoins(40);
        gameMap.placeWings(2);
        gameMap.placeObjectXY(gameMap.getSizeX() - 1, 0, new Wings(gameMap.getSizeX() - 1, 0), new Terrain[] {new Void()});

        scanner.nextLine();
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
        } else {
            printMessage("error_input");
            setGameMap();
        }

        saveMap();
    }

    private void saveMap() {
        printMessage("save_map_or_not");
        int saveOrNot = scanner.nextInt();
        if (saveOrNot == 1) {
            printMessage("save_map");
            String saveName = scanner.next();
            MapProcess.saveMap(gameMap, saveName, playerNamePlusPass);
            logger.info("Карта сохранена под именем: {}", saveName);
        } else {
            logger.info("Сохранение карты отменено");
        }
    }

    private void userSave(){
        printMessage("save_game_or_not");
        int saveOrNot = scanner.nextInt();
        if (saveOrNot == 1) {
            printMessage("save_game");
            String saveName = scanner.next();
            GameSaver.saveGame(this, saveName, playerNamePlusPass);
            logger.info("Игра сохранена под именем: {}", saveName);
        } else {
            logger.info("Сохранение игры отменено");
        }
    }

    public int start(){
        logger.info("Игра началась");
        gameMap.print();
        player.initScanner();
        
        while (true) {
            printMessage("player_turn");
            player.turn();
            gameMap.print();
            printMessage("enemy_turn");
            enemy.turn();
            gameMap.print();

            int playerInCastle = player.getTurnsInCastle();
            int enemyInCastle = enemy.getTurnsInCastle();
            if(playerInCastle == 1 && enemyInCastle == 0){
                logger.info("Игра завершилась, победил игрок");
                return 1;
            } else if(playerInCastle == 0 && enemyInCastle == 1){
                logger.info("Игра завершилась, победил компьютер");
                return 2;
            } else if(playerInCastle == 1 && enemyInCastle == 1){
                logger.info("Игра завершилась, ничья");
                return 0;
            }

            userSave();
            GameSaver.saveGame(this, "Autosave " + Calendar.getInstance().getTime().toString(), playerNamePlusPass);
        }
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
