package other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GameSaver {
    public static void saveGame(Game game, String fileName, String player){
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
}
