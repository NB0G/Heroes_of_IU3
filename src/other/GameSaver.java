package other;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GameSaver {
    public static void saveGame(Game game, String fileName, String player){
        game.setStartTimerTime(TimeManager.getCurrentTime());

        String path = "saves/games/" + player + "/";

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        path += fileName + ".ser";
        
        try (FileOutputStream outputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(game);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Game loadGame(String fileName, String player){
        String path = "saves/games/" + player + "/" + fileName + ".ser";
        Game game = null;
        
        try (FileInputStream inputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                game = (Game) objectInputStream.readObject();
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке игры: " + e.getMessage());
            e.printStackTrace();
        }

        return game;
    }

    public static void printAvailableGames(String playerName){
        System.out.println("Доступные сохранения:");
        String path = "saves/games/";
        String[] players = new java.io.File(path).list();
        
        for (String player : players) {
            if (player.equals(playerName)){
                String newPath = path + playerName + "/";
                String[] files = new java.io.File(newPath).list();
                for (String file : files) {
                    System.out.println(file.substring(0, file.length() - 4));
                }
            }
        }
    }

    public static void enhanceMapPoints(String mapOwner, String mapName){
        String csvPath = "saves/votes.csv";
        List<String[]> rows = new ArrayList<>();
        boolean found = false;
        try {
            File file = new File(csvPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (FileReader fileReader = new FileReader(csvPath);
                 CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
                List<String[]> allRows = csvReader.readAll();
                for (String[] parts : allRows) {
                    if (parts.length == 3 && parts[0].equals(mapOwner) && parts[1].equals(mapName)) {
                        int votes = Integer.parseInt(parts[2]);
                        votes++;
                        parts[2] = String.valueOf(votes);
                        found = true;
                    }
                    rows.add(parts);
                }
            }
            if (!found) {
                rows.add(new String[]{mapOwner, mapName, "1"});
            }
            try (FileWriter fileWriter = new FileWriter(csvPath, false);
                 CSVWriter csvWriter = new CSVWriter(fileWriter,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)) {
                for (String[] row : rows) {
                    csvWriter.writeNext(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getAmplifier(String playerName){
        String csvPath = "saves/votes.csv";
        int amp = 1;
        try (FileReader fileReader = new FileReader(csvPath);
            CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            List<String[]> allRows = csvReader.readAll();
            for (String[] parts : allRows) {
                if (parts.length == 3 && parts[0].equals(playerName)) {
                    int votes = Integer.parseInt(parts[2]);
                    if (votes >= 2) amp++;
                }
            }
        } catch (Exception e) {
        }
        return amp;
    }
}
