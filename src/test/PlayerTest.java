package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import gameactors.Enemy;
import gameactors.Player;
import maps.GameMap;
import objects.types.Wings;
import terrains.Terrain;
import terrains.types.EnemyCastle;
import terrains.types.EnemyTerritory;
import terrains.types.OurCastle;
import terrains.types.OurTerritory;
import terrains.types.Road;
import terrains.types.Void;
import entity.Hero;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import other.Castle;

public class PlayerTest {
    private Player player;
    private Enemy enemy;

    public void init(){
        // InputStream originalSystemIn = System.in;
        // String data = "23 16 -1";
        // InputStream is = new ByteArrayInputStream(data.getBytes());
        // System.setIn(is);

        GameMap gameMap = new GameMap(20);//11 20

        player = new Player(gameMap, "TestPlayer");
        enemy = new Enemy(gameMap);
        player.setEnemy(enemy);
        enemy.setEnemy(player);

        // player.setInputType(1);

        gameMap.placeCoins(40);
        gameMap.placeWings(2);
        gameMap.placeObjectXY(gameMap.getSizeX() - 1, 0, new Wings(gameMap.getSizeX() - 1, 0), new Terrain[] {new Void()});
    }

    @Test
    public void testTurnsInCastle() {
        InputStream originalSystemIn = System.in;
        String data = "23 16 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        init();

        // int[] inp = new int[] {23, 16};
        // player.addElements(inp);
        
        enemy.turn();
        enemy.turn();
        enemy.turn();
        player.getHeroes().get(0).setWings();
        player.turn();
        assertEquals(player.getTurnsInCastle(), 1);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testKillHero() {
        init();

        Hero hero = player.getHeroes().get(0);
        player.killHero(hero);
        assertFalse(player.getHeroes().contains(hero));
    }

    @Test
    public void testHeroMoveWithWings() {
        InputStream originalSystemIn = System.in;
        String data = "10 11 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        init();

        // int[] inp = new int[] {10, 11};
        // player.addElements(inp);

        player.getHeroes().get(0).setWings();
        int x = player.getHeroes().get(0).getPosition()[1];
        int y = player.getHeroes().get(0).getPosition()[0];

        player.turn();

        int x2 = player.getHeroes().get(0).getPosition()[1];
        int y2 = player.getHeroes().get(0).getPosition()[0];

        assertEquals(10, x2);
        assertEquals(11, y2);

        assertNotEquals(x, x2);
        assertNotEquals(y, y2);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testAddHeroes() {
        init();

        int startHeroCount = player.getHeroes().size();
        player.addHero();
        int endHeroCount = player.getHeroes().size();
        assertEquals(startHeroCount + 1, endHeroCount);
    }

    @Test
    public void testBuyUnits(){
        InputStream originalSystemIn = System.in;
        String data = "-1 2 2 2 2 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        init();

        // int[] inp = new int[] {-1, 2, 2, 2, 2};
        // player.addElements(inp);

        int startUnits = player.getHeroes().get(0).getUnits().size();
        player.turn();
        int endUnits = player.getHeroes().get(0).getUnits().size();
        assertEquals(startUnits + 2, endUnits);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testSetCoins() {
        init();

        int startCoins = player.getCoins();
        player.setCoins(startCoins + 10);
        int endCoins = player.getCoins();
        assertEquals(startCoins + 10, endCoins);
    }

    @Test
    public void testBuildings(){
        InputStream originalSystemIn = System.in;
        String data = "-1 1 0 1 1 1 3 1 4 1 5 1 6 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        init();

        // int[] inp = new int[] {-1, 1, 0, 1, 1, 1, 3, 1, 4, 1, 5, 1, 6};
        // player.addElements(inp);

        Castle castle = player.getCastle();
        int[] buildingsPlaced = castle.getIsPlaced();
        int[] exepted = {0, 0, 1, 0, 0, 0, 0};
        assertTrue(Arrays.equals(buildingsPlaced, exepted));
        player.turn();
        int[] buildingsPlaced2 = castle.getIsPlaced();
        int[] exepted2 = {1, 1, 1, 1, 1, 1, 1};
        assertTrue(Arrays.equals(buildingsPlaced2, exepted2));
        System.setIn(originalSystemIn);
    }

    @Test
    public void testMoveCost(){
        InputStream originalSystemIn = System.in;
        String data = "8 2 -1 -1 6 -1 -1 2 -1 -1 5 -1 -1 4 -1 -1 5 5 -1 -1 6 -1 -1 5 5 -1 -1 4 4 4 5 5 4 5 5 4 5 -1 -1 4 4 4 4 4 4 4 -1 -1 4 -1 -1 8 8 8 8 8 8 8 -1 -1 6 6 6 6 6 -1 -1 6 -1 -1 2 2 2 2 2 -1 -1 5 5 4 5 5 4 -1 -1 4 -1 -1 6 -1 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        init();
        // int[] inp = new int[] {8, 2, -1, -1, 6, -1, -1, 2, -1, -1, 5, -1, -1, 4, -1, -1, 5, 5, -1, -1, 6, -1, -1, 5, 5, -1, -1, 4, 4, 4, 5, 5, 4, 5, 5, 4, 5, -1, -1, 4, 4, 4, 4, 4, 4, 4, -1, -1, 4, -1, -1, 8, 8, 8, 8, 8, 8, 8, -1, -1, 6, 6, 6, 6, 6, -1, -1, 6, -1, -1, 2, 2, 2, 2, 2, -1, -1, 5, 5, 4, 5, 5, 4, -1, -1, 4, -1, -1, 6};
        // player.addElements(inp);

        int startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        int endSteps = player.getHeroes().get(0).getStepsLeft();
        int exepted = 0;
        int test = startSteps - endSteps;
        assertEquals(exepted, test);

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new OurTerritory().getMoveCost();
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new OurCastle().getMoveCost();
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new Road().getMoveCost();
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new Road().getMoveCost();
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new Road().getMoveCost() * 3;
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new Void().getMoveCost();
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new Void().getMoveCost() * 3;
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        player.turn();
        player.turn();

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = 0;
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        player.turn();
        player.turn();
        player.turn();

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = 0;
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        player.turn();
        enemy.turn();
        enemy.turn();
        enemy.turn();
        enemy.turn();
        player.turn();

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new EnemyTerritory().getMoveCost() + 1;
        test = startSteps - endSteps;
        assertEquals(exepted, test);

        startSteps = player.getHeroes().get(0).getSpeed();
        player.turn();
        endSteps = player.getHeroes().get(0).getStepsLeft();
        exepted = new EnemyCastle().getMoveCost() + 1;
        test = startSteps - endSteps;
        assertEquals(exepted, test);
        System.setIn(originalSystemIn);
    }
}
