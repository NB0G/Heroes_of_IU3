package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import other.GameSaver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.Assert.*;

public class GameSaverTest {

    private final String savesDir = "saves/games/testPlayer/";
    private final String savesRoot = "saves/games/";
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() throws Exception {
        // Redirect System.out
        System.setOut(new PrintStream(outContent));
        // Create test directory and files
        new File(savesDir).mkdirs();
        new File(savesDir + "save1.ser").createNewFile();
        new File(savesDir + "save2.ser").createNewFile();
    }

    @After
    public void tearDown() throws Exception {
        // Restore System.out
        System.setOut(originalOut);
        // Clean up test files and directories
        Files.deleteIfExists(Paths.get(savesDir + "save1.ser"));
        Files.deleteIfExists(Paths.get(savesDir + "save2.ser"));
        Files.deleteIfExists(Paths.get(savesDir));
        // Remove testPlayer directory if empty
        File playerDir = new File(savesDir);
        if (playerDir.exists() && playerDir.isDirectory()) {
            playerDir.delete();
        }
        // Remove saves/games/ if empty
        File rootDir = new File(savesRoot);
        if (rootDir.exists() && rootDir.isDirectory() && rootDir.list().length == 0) {
            rootDir.delete();
        }
    }

    @Test
    public void testPrintAvailableGames_PrintsCorrectSaves() {
        GameSaver.printAvailableGames("testPlayer");
        String output = outContent.toString();
        assertTrue(output.contains("Доступные сохранения:"));
        assertTrue(output.contains("save1"));
        assertTrue(output.contains("save2"));
        assertFalse(output.contains(".ser"));
    }

    @Test
    public void testPrintAvailableGames_NoSaves() throws Exception {
        // Remove all saves
        Files.deleteIfExists(Paths.get(savesDir + "save1.ser"));
        Files.deleteIfExists(Paths.get(savesDir + "save2.ser"));
        outContent.reset();
        GameSaver.printAvailableGames("testPlayer");
        String output = outContent.toString();
        assertTrue(output.contains("Доступные сохранения:"));
        // Should not print any save names
        assertFalse(output.contains("save1"));
        assertFalse(output.contains("save2"));
    }

    @Test
    public void testPrintAvailableGames_PlayerNotExists() {
        outContent.reset();
        GameSaver.printAvailableGames("nonexistentPlayer");
        String output = outContent.toString();
        assertTrue(output.contains("Доступные сохранения:"));
        // Should not print any save names
        assertFalse(output.contains("save1"));
        assertFalse(output.contains("save2"));
    }
    @Test
    public void testEnhanceMapPoints_NewEntry_CompareVotes() throws Exception {
        String mapOwner = "owner1";
        String mapName = "mapA";
        // Get initial votes
        int initialVotes = 0;
        File votesFile = new File("saves/votes.csv");
        if (votesFile.exists()) {
            String content = new String(Files.readAllBytes(votesFile.toPath()));
            for (String line : content.split("\n")) {
                String[] parts = line.trim().split(",");
                if (parts.length == 3 && parts[0].equals(mapOwner) && parts[1].equals(mapName)) {
                    initialVotes = Integer.parseInt(parts[2]);
                }
            }
        }
        // Enhance points
        GameSaver.enhanceMapPoints(mapOwner, mapName);
        // Get new votes
        int newVotes = 0;
        String content = new String(Files.readAllBytes(votesFile.toPath()));
        for (String line : content.split("\n")) {
            String[] parts = line.trim().split(",");
            if (parts.length == 3 && parts[0].equals(mapOwner) && parts[1].equals(mapName)) {
                newVotes = Integer.parseInt(parts[2]);
            }
        }
        assertEquals(initialVotes + 1, newVotes);
    }

    @Test
    public void testEnhanceMapPoints_ExistingEntry_CompareVotes() throws Exception {
        String mapOwner = "owner2";
        String mapName = "mapB";
        // Ensure entry exists
        GameSaver.enhanceMapPoints(mapOwner, mapName);
        // Get current votes
        File votesFile = new File("saves/votes.csv");
        int initialVotes = 0;
        String content = new String(Files.readAllBytes(votesFile.toPath()));
        for (String line : content.split("\n")) {
            String[] parts = line.trim().split(",");
            if (parts.length == 3 && parts[0].equals(mapOwner) && parts[1].equals(mapName)) {
                initialVotes = Integer.parseInt(parts[2]);
            }
        }
        // Enhance points again
        GameSaver.enhanceMapPoints(mapOwner, mapName);
        // Get new votes
        int newVotes = 0;
        content = new String(Files.readAllBytes(votesFile.toPath()));
        for (String line : content.split("\n")) {
            String[] parts = line.trim().split(",");
            if (parts.length == 3 && parts[0].equals(mapOwner) && parts[1].equals(mapName)) {
                newVotes = Integer.parseInt(parts[2]);
            }
        }
        assertEquals(initialVotes + 1, newVotes);
    }

    @Test
    public void testGetAmplifier_Default() {
        int amp = GameSaver.getAmplifier("unknownPlayer");
        assertEquals(1, amp);
    }

    @Test
    public void testGetAmplifier_WithVotes() {
        String player = "votedPlayer";
        // Simulate 3 votes
        GameSaver.enhanceMapPoints(player, "mapX");
        GameSaver.enhanceMapPoints(player, "mapX");
        GameSaver.enhanceMapPoints(player, "mapX");
        int amp = GameSaver.getAmplifier(player);
        assertTrue(amp >= 2);
    }
}