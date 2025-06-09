package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import other.Game;

public class GameTest {
    @Test
    public void testPlayerWins() {
        InputStream originalSystemIn = System.in;
        String data = "5 4 5 5 4 5 5 5 4 5 5 4 5 5 4 5 5 5 -1 -1 -1 -1 -1 -1 4 5 5 4 5 -1 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        Game game = new Game();
        // game.setActions(new int[]{5, 4, 5, 5, 4, 5, 5, 5, 4, 5, 5, 4, 5, 5, 4, 5, 5, 5, -1, -1, -1, -1, -1, -1, 4, 5, 5, 4, 5, -1, -1});
        int winner = game.start();
        assertEquals(1, winner);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testEnemyWins() {
        InputStream originalSystemIn = System.in;
        String data = "5 4 5 5 4 5 5 5 4 5 5 4 5 5 4 5 5 5 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        Game game = new Game();
        // game.setActions(new int[]{5, 4, 5, 5, 4, 5, 5, 5, 4, 5, 5, 4, 5, 5, 4, 5, 5, 5});
        int winner = game.start();
        assertEquals(2, winner);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testDraw() {
        InputStream originalSystemIn = System.in;
        String data = "5 4 5 5 4 5 5 5 4 5 5 4 5 5 4 5 5 5 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 4 5 5 4 5 -1 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        Game game = new Game();
        // game.setActions(new int[]{5, 4, 5, 5, 4, 5, 5, 5, 4, 5, 5, 4, 5, 5, 4, 5, 5, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 5, 5, 4, 5, -1, -1});
        int winner = game.start();
        assertEquals(0, winner);
        System.setIn(originalSystemIn);
    }
}
