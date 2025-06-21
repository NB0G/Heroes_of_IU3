import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.util.Scanner;

import other.Game;
import other.GameSaver;

public class Main {
    public static void main(String[] args) {
        String playerName = null;
        while (playerName == null) {
            playerName = login();
        }

        Game game = setGame(playerName);

        printMessage("game_started");

        int winner = game.start();
        if(winner == 1){
            printMessage("player_win");
        } else if(winner == 2){
            printMessage("pc_win");
        } else {
            printMessage("draw");
        }

        if (game.getPoints() > getMaxPoints(playerName)) {
            setMaxPoints(playerName, game.getPoints());
            setMapName(playerName, game.getMapName());
        }

        vote(playerName, game.getMapOwner(), game.getMapNameOwner());
    }

    public static void vote(String playerName, String mapOwner, String mapName){
        Scanner scanner = new Scanner(System.in);
        if(playerName.equals(mapOwner)){
            return;
        }
        printMessage("your_vote");

        int answer = scanner.nextInt();
        if (answer == 1){
            GameSaver.enhanceMapPoints(mapOwner, mapName);
        }
    }

    public static Game setGame(String playerName){
        Scanner scanner = new Scanner(System.in);

        Game game = null;

        printMessage("load_or_create_game");
        int input = scanner.nextInt();
        scanner.nextLine();
        if(input == 1){
            game = new Game(playerName);
        } else if (input == 2){
            GameSaver.printAvailableGames(playerName);

            String fileName = scanner.nextLine();
            game = GameSaver.loadGame(fileName, playerName);
        }

        return game;
    }

    public static String login(){
        Scanner scanner = new Scanner(System.in);
        List<String[]> data = readCSV();
        while (true) {
            printMessage("logging");
            String player = scanner.next();
            String password = scanner.next();
            boolean found = false;
            for (String[] i : data){
                if(i[0].equals(player) && i[1].equals(password)){
                    printMessage("logged_in", player);
                    return player;
                }
            }
            printMessage("login_failed");
            System.out.println("Попробовать еще раз (1) или создать нового пользователя (2)?");
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 2) {
                return register();
            }
        }
    }

    public static String register(){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Введите имя пользователя:");
            String playerName = scanner.nextLine();

            List<String> existingNames = getAllPlayerNames();
            if (existingNames.contains(playerName)) {
                System.out.println("Пользователь с таким именем уже существует. Введите другое имя:");
                continue;
            }

            printMessage("registering", playerName);
            String password = scanner.nextLine();
            writeCSV(playerName, password);
            return playerName;
        }
    }

    public static List<String> getAllPlayerNames() {
        List<String> names = new java.util.ArrayList<>();
        List<String[]> data = readCSV();
        if (data != null && !data.isEmpty()) {
            for (int i = 1; i < data.size(); i++) { // skip header
                String[] row = data.get(i);
                if (row.length > 0) {
                    names.add(row[0]);
                }
            }
        }
        return names;
    }

    public static void writeCSV(String playerName, String password){
        try {
            File dir = new File("saves");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File("saves/players.csv");
            boolean fileExists = file.exists();

            try (FileWriter fileWriter = new FileWriter(file, true);
                 CSVWriter csvWriter = new CSVWriter(fileWriter,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)) {
                if (!fileExists) {
                    csvWriter.writeNext(new String[]{"name", "password"});
                }
                // fileWriter.write(System.lineSeparator());
                csvWriter.writeNext(new String[]{playerName, password});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readCSV(){
        List<String[]> data = null;
        try {
            FileReader fileReader = new FileReader("saves/players.csv");
            CSVReader csvReader = new CSVReaderBuilder(fileReader).build();
            data = csvReader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return data;
    }

    public static int getMaxPoints(String playerName) {
        List<String[]> data = readRecordsCSV();
        if (data != null) {
            for (String[] row : data) {
                if (row.length >= 3 && row[0].equals(playerName)) {
                    try {
                        return Integer.parseInt(row[2]);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    public static void setMaxPoints(String playerName, int newValue) {
        List<String[]> data = readRecordsCSV();
        boolean found = false;
        if (data == null) data = new ArrayList<>();
        for (String[] row : data) {
            if (row.length >= 3 && row[0].equals(playerName)) {
                row[2] = String.valueOf(newValue);
                found = true;
                break;
            }
        }
        if (!found) {
            data.add(new String[]{playerName, "", String.valueOf(newValue)});
        }
        writeRecordsCSV(data);
    }

    public static void setMapName(String playerName, String name) {
        List<String[]> data = readRecordsCSV();
        boolean found = false;
        if (data == null) data = new ArrayList<>();
        for (String[] row : data) {
            if (row.length >= 3 && row[0].equals(playerName)) {
                row[1] = name;
                found = true;
                break;
            }
        }
        if (!found) {
            data.add(new String[]{playerName, name, "0"});
        }
        writeRecordsCSV(data);
    }

    public static List<String[]> readRecordsCSV() {
        List<String[]> data = null;
        try {
            File dir = new File("saves");
            if (!dir.exists()) dir.mkdirs();
            File file = new File("saves/records.csv");
            if (!file.exists()) return new ArrayList<>();
            FileReader fileReader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(fileReader).build();
            data = csvReader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeRecordsCSV(List<String[]> data) {
        try {
            File dir = new File("saves");
            if (!dir.exists()) dir.mkdirs();
            File file = new File("saves/records.csv");
            try (FileWriter fileWriter = new FileWriter(file, false);
                 CSVWriter csvWriter = new CSVWriter(fileWriter,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)) {
                for (String[] row : data) {
                    csvWriter.writeNext(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printAllRecords() {
        List<String[]> data = readRecordsCSV();
        System.out.println("Рекорды игроков:");
        if (data == null || data.isEmpty()) {
            System.out.println("Нет рекордов.");
            return;
        }
        for (String[] row : data) {
            if (row.length >= 3) {
                System.out.printf("Игрок: %s | Карта: %s | Рекорд: %s\n", row[0], row[1], row[2]);
            }
        }
    }

    private static void printMessage(String messageKey, Object... args) {
        switch (messageKey) {
            case "player_win" -> System.out.println("Победа!");
            case "pc_win" -> System.out.println("Поражение!");
            case "draw" -> System.out.println("Ничья!");
            case "load_or_create_game" -> System.out.println("Введите (1), чтобы создать новую игру или (2), чтобы загрузить игру");
            case "logging" -> System.out.println("Введите имя пользователя и пароль");
            case "logged_in" -> System.out.printf("Выполнен вход как %s\n", args[0]);
            case "login_failed" -> System.out.println("Ошибка входа");
            case "question" -> System.out.println("1 -  Да, 2 - Нет");
            case "registering" -> System.out.printf("Регистрация нового пользователя %s, введите пароль: ", args[0]);
            case "register_failed" -> System.out.println("Регистрация не удалась");
            case "game_started" -> System.out.println("Игра началась!");
            case "your_vote" -> System.out.println("Голосуйте за карту (1) понравилось, (2) нет");
            default -> System.out.println("Неизвестное сообщение.");
        }
    }
}

/*
// добавить регистрацию нового пользователя
// добавить сохранения пользователя
//протестировать создание загрузку и сохранение карты
протестировать вход игрока в систему
//протестировать сохранения
//добавить счетчик рекордов
//сделать имена уникальными
//добавить цсв файл для оцененных карт и по нему вычислять множитель

//доп задание
//можно загружать карты других игроков и ставить оценки
//если у карты более двух положительных оценок от игроков то в каждой новой игре пользователь получает в сколько-то раз больше монет
//если положительно проголосовали за несколько карт то множитель умножится на количество карт
//за себя голосовать нельзя

//что за игрок null (это тесты), убрать


 */


//это был InputData
// package other;

// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.Serializable;
// import java.util.ArrayList;
// import java.util.Scanner;

// public class InputData implements Serializable{
//     private transient Scanner scanner = new Scanner(System.in);
//     private ArrayList<Integer> input = new ArrayList<Integer>();
//     private int inputType = 0;

//     public InputData() {
//         scanner = new Scanner(System.in);
//     }

//     public void setInputType(int inputType){
//         this.inputType = inputType % 2;
//     }

//     public int getNextInt(){
//         int out = -1;
//         if(inputType == 0){
//             out = scanner.nextInt();
//         } else if(input.size() > 0){
//             out = input.get(0);
//             input.removeFirst();
//         }
//         return out;
//     }

//     public void addElements(int[] elements){
//         for (int i : elements){
//             input.add(i);
//         }
//     }

//     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//         in.defaultReadObject();  // стандартная десериализация
//         this.scanner = new Scanner(System.in);  // восстанавливаем Scanner
//     }
// }
