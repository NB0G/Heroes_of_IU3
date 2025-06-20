import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.util.Scanner;

import other.Game;
import other.GameSaver;

public class Main {
    public static void main(String[] args) {
        String playerNamePlusPass = null;
        while (playerNamePlusPass == null) {
            playerNamePlusPass = login();
        }

        Game game = setGame(playerNamePlusPass);

        printMessage("game_started");

        int winner = game.start();
        if(winner == 1){
            printMessage("player_win");
        } else if(winner == 2){
            printMessage("pc_win");
        } else {
            printMessage("draw");
        }
    }

    public static Game setGame(String playerNamePlusPass){
        Scanner scanner = new Scanner(System.in);

        Game game = null;

        printMessage("load_or_create_game");
        int input = scanner.nextInt();
        scanner.nextLine();
        if(input == 1){
            game = new Game(playerNamePlusPass);
        } else if (input == 2){
            GameSaver.printAvailableGames(playerNamePlusPass);

            String fileName = scanner.nextLine();
            game = GameSaver.loadGame(fileName, playerNamePlusPass);
        }

        return game;
    }

    public static String login(){
        Scanner scanner = new Scanner(System.in);

        List<String[]> data = readCSV();

        printMessage("logging");

        String player = scanner.next();
        String password = scanner.next();

        for (String[] i : data){
            if(i[0].equals(player) && i[1].equals(password)){
                printMessage("logged_in", player);
                return player + password;
            }
        }

        printMessage("login_failed");

        return register(player);
    }

    public static String register(String playerName){
        printMessage("question");
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        scanner.nextLine();

        if(input == 1){
            printMessage("registering", playerName);
            String password = scanner.nextLine();
            writeCSV(playerName, password);
            return playerName +  password;
        } else {
            printMessage("register_failed");
            return null;
        }
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

    private static void printMessage(String messageKey, Object... args) {
        switch (messageKey) {
            case "player_win" -> System.out.println("Победа!");
            case "pc_win" -> System.out.println("Поражение!");
            case "draw" -> System.out.println("Ничья!");
            case "load_or_create_game" -> System.out.println("Введите (1), чтобы создать новую игру или (2), чтобы загрузить игру");
            case "logging" -> System.out.println("Введите имя пользователя и пароль");
            case "logged_in" -> System.out.printf("Выполнен вход как %s\n", args[0]);
            case "login_failed" -> System.out.println("Ошибка входа. Вы хотите создать нового пользователя с таким именем?");
            case "question" -> System.out.println("1 -  Да, 2 - Нет");
            case "registering" -> System.out.printf("Регистрация нового пользователя %s, введите пароль: ", args[0]);
            case "register_failed" -> System.out.println("Регистрация не удалась");
            case "game_started" -> System.out.println("Игра началась!");
            default -> System.out.println("Неизвестное сообщение.");
        }
    }
}

/*
// добавить регистрацию нового пользователя
// добавить сохранения пользователя
протестировать создание загрузку и сохранение карты
протестировать вход игрока в систему
протестировать сохранения
добавить счетчик рекордов


доп задание
можно загружать сейвы других игроков и ставить оценки
если у карты более двух положительных оценок от игроков то в каждой новой игре пользователь получает в сколько-то раз больше монет
если положительно проголосовали за несколько карт то множитель умножется на количество карт
за себя голосовать нельзя

что за игрок null (это тесты), убрать


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
