package maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import objects.types.*;
import other.Plasable;
import terrains.Terrain;
import terrains.types.*;
import terrains.types.Void;

public class MapProcess {
    private static final transient Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // GameMap map = new GameMap(23);
        // map.print();
        // saveMap(map, "fileName3", "player");
        // System.out.println("Map saved");
        // GameMap loadedMap = loadMap("fileName2", "player");
        // loadedMap.print();

        // printAvailableMaps();

        GameMap map = createMapFromConsole();
        map.print();
        saveMap(map, "consoleMap", "player2");
        printAvailableMaps();
    }

    // public static void printAvailableMaps(){
    //     System.out.println("Доступные карты:");
    //     String path = "saves/maps/";
    //     String[] players = new java.io.File(path).list();
    //     for (String player : players) {
    //         String newPath = path + player + "/";
    //         String[] files = new java.io.File(newPath).list();
    //         System.out.println("Игрок: " + player);
    //         for (String file : files) {
    //             System.out.println("Название катры: ");
    //             System.out.println(file.substring(0, file.length() - 4));
    //             loadMap(file.substring(0, file.length() - 4), player).print();
    //         }
    //     }
    // }

    public static void printAvailableMaps() {
        System.out.println("Доступные карты:");
        String path = "saves/maps/";
        File mapsDir = new File(path);
        String[] players = mapsDir.list();
        if (players == null) {
            System.out.println("Нет доступных карт или директория не найдена.");
            return;
        }
        for (String player : players) {
            String newPath = path + player + "/";
            File playerDir = new File(newPath);
            String[] files = playerDir.list();
            if (player.equals(".DS_Store")) {
                continue;
            }
            if (files == null || files.length == 0) {
                System.out.println("Игрок: " + player + " — нет сохранённых карт.");
                continue;
            }
            System.out.println("Игрок: " + player);
            for (String file : files) {
                if (!file.endsWith(".ser")) continue;
                try {
                    System.out.println("Название карты: ");
                    System.out.println(file.substring(0, file.length() - 4));
                    GameMap map = loadMap(file.substring(0, file.length() - 4), player);
                    if (map != null) {
                        map.print();
                    } else {
                        System.out.println("Ошибка загрузки карты: " + file);
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при обработке файла " + file + ": " + e.getMessage());
                }
            }
        }
    }

    public static GameMap loadMap(String fileName, String player){
        String path = "saves/maps/" + player + "/" + fileName + ".ser";
        GameMap map = null;
        
        try (FileInputStream inputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            map = (GameMap) objectInputStream.readObject();
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке карты: " + e.getMessage());
            e.printStackTrace();
        }

        return map;
    }

    public static void saveMap(GameMap map, String fileName, String player){
        String path = "saves/maps/" + player + "/";

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        path += fileName + ".ser";
        
        try (FileOutputStream outputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GameMap createMapFromSeed(){
        int seed;
        printMessage("enter_seed");
        seed = scanner.nextInt();
        return new GameMap(seed);
    }

    public static GameMap createMapFromConsole() {
        // Ввод размеров карты
        int sizeX = 0, sizeY = 0;
        while (true) {
            printMessage("enter_map_size");
            sizeX = scanner.nextInt();
            sizeY = scanner.nextInt();
            if (sizeX > 0 && sizeY > 0) {
                break;
            } else {
                printMessage("invalid_map_size");
            }
        }

        Terrain[][] terrainMap = new Terrain[sizeY][sizeX];
        Plasable[][] characterMap = new Plasable[sizeY][sizeX];

        // Инициализация карты по умолчанию
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                terrainMap[y][x] = new Void();
                characterMap[y][x] = new Nothing(x, y);
            }
        }

        printMessage("map_creation_start");

        scanner.nextLine();

        while (true) {
            printMessage("enter_cell_data");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("done")) {
                if (!checkCastles(terrainMap)) {
                    printMessage("missing_castles");
                    continue;
                }
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length != 3) {
                printMessage("invalid_cell_format");
                continue;
            }

            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                String type = parts[2];

                if (x < 0 || x >= sizeX || y < 0 || y >= sizeY) {
                    printMessage("cell_out_of_bounds");
                    continue;
                }

                switch (type) {
                    case "OurCastle" -> terrainMap[y][x] = new OurCastle();
                    case "EnemyCastle" -> terrainMap[y][x] = new EnemyCastle();
                    case "Road" -> terrainMap[y][x] = new Road();
                    case "OurTerritory" -> terrainMap[y][x] = new OurTerritory();
                    case "EnemyTerritory" -> terrainMap[y][x] = new EnemyTerritory();
                    case "Rock" -> terrainMap[y][x] = new Rock();
                    // case "RoadBorder" -> terrainMap[y][x] = new RoadBorder();
                    case "Coin" -> characterMap[y][x] = new Coin(x, y);
                    case "Wings" -> characterMap[y][x] = new Wings(x, y);
                    default -> {
                        printMessage("unknown_type");
                        continue;
                    }
                }

                printMessage("cell_updated", x, y);
            } catch (NumberFormatException e) {
                printMessage("coordinate_error");
            }
        }

        return new GameMap(terrainMap, characterMap);
    }

    private static boolean checkCastles(Terrain[][] terrainMap) {
        boolean ourCastle = false;
        boolean enemyCastle = false;

        for (Terrain[] row : terrainMap) {
            for (Terrain cell : row) {
                if (cell instanceof OurCastle) {
                    ourCastle = true;
                } else if (cell instanceof EnemyCastle) {
                    enemyCastle = true;
                }
            }
        }

        return ourCastle && enemyCastle;
    }

    private static void printMessage(String messageKey, Object... args) {
        switch (messageKey) {
            case "missing_castles" -> System.out.println("На катре должен быть расположен хотя бы один замок каждого типа.");
            case "enter_seed" -> System.out.println("Введите сид для создания карты: ");
            case "enter_map_size" -> System.out.println("Введите размеры карты (ширина и высота): ");
            case "invalid_map_size" -> System.out.println("Размеры карты должны быть положительными числами. Попробуйте снова.");
            case "input_error" -> System.out.println("Ошибка ввода. Убедитесь, что вы вводите два целых числа.");
            case "map_creation_start" -> {
                System.out.println("Создание карты. По умолчанию все ячейки - Void и Nothing.");
                System.out.println("Введите координаты и тип объекта/террейна для изменения.");
                System.out.println("Формат ввода: x y type (например, 2 3 OurCastle). Введите 'done' для завершения.");
            }
            case "enter_cell_data" -> System.out.print("Введите данные: ");
            case "invalid_cell_format" -> System.out.println("Неверный формат ввода. Попробуйте снова.");
            case "cell_out_of_bounds" -> System.out.println("Координаты выходят за пределы карты. Попробуйте снова.");
            case "unknown_type" -> System.out.println("Неизвестный тип. Попробуйте снова.");
            case "cell_updated" -> System.out.printf("Ячейка (%d, %d) успешно обновлена.%n", args[0], args[1]);
            case "coordinate_error" -> System.out.println("Ошибка ввода координат. Попробуйте снова.");
            default -> System.out.println("Неизвестное сообщение.");
        }
    }
}