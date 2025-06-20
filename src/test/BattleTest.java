package test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import entity.Hero;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import other.Battle;

public class BattleTest {
    private Battle battle;
    private Hero player;
    private Hero enemy;

    @Before
    public void init() {
        player = new Hero(10, 6, 1);
        enemy = new Hero(10, 6, 2);
    }

    @Test
    public void testPlayerWin(){
        InputStream originalSystemIn = System.in;
        String data = "3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 4 4 6 -1 -1 -1 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        battle = new Battle(player, enemy, 1, 24);

        int winner = battle.start();
        assertEquals(1, winner);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testEnemyWin(){
        InputStream originalSystemIn = System.in;
        String data = "-1 -1 -1 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 4 4 6 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        battle = new Battle(player, enemy, 1, 24);

        int winner = battle.start();
        assertEquals(1, winner);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testGetCoinsAttackerPlayerWins(){
        InputStream originalSystemIn = System.in;
        String data = "3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 4 4 6 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        battle = new Battle(player, enemy, 1, 24);

        battle.start();

        int coinsAttacker = battle.getCoinsAttacker();
        assertEquals(11, coinsAttacker);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testGetCoinsDefenderPlayerWins(){
        InputStream originalSystemIn = System.in;
        String data = "3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 4 4 6 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        battle = new Battle(player, enemy, 1, 24);

        battle.start();

        int coinsDefender = battle.getCoinsDefender();
        assertEquals(0, coinsDefender);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testGetCoinsAttackerEnemyWins(){
        InputStream originalSystemIn = System.in;
        String data = "-1 -1 -1 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 4 4 6 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        battle = new Battle(player, enemy, 1, 24);

        battle.start();

        int coinsAttacker = battle.getCoinsAttacker();
        assertEquals(11, coinsAttacker);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testGetCoinsDefenderEnemyWins(){
        InputStream originalSystemIn = System.in;
        String data = "-1 -1 -1 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 -1 3 3 3 3 3 3 3 3 4 4 6 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        battle = new Battle(player, enemy, 1, 24);

        battle.start();

        int coinsDefender = battle.getCoinsDefender();
        assertEquals(0, coinsDefender);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testBattleInit() {
        battle = new Battle(player, enemy, 1, 24);
        assertEquals(player, battle.getOurHero(1));
        assertEquals(enemy, battle.getOurHero(2));
        battle = new Battle(enemy, player, 1, 24);
        assertEquals(enemy, battle.getOurHero(1));
        assertEquals(player, battle.getOurHero(2));
    }

    @Test
    public void testKillUnitsInBattle() {
        InputStream originalSystemIn = System.in;
        String data = "-1 -1 -1 3 3 3 3 3 3 3 4 4 2 -1";
        InputStream is = new ByteArrayInputStream(data.getBytes());
        System.setIn(is);

        battle = new Battle(player, enemy, 1, 24);

        int startUnitCount = enemy.getUnits().size();
        battle.turn(1);
        int endUnitCount = enemy.getUnits().size();
        assertEquals(1, startUnitCount - endUnitCount);
        System.setIn(originalSystemIn);
    }

    @Test
    public void testKillUnits(){
        battle = new Battle(player, enemy, 1, 24);
        ArrayList kill = new ArrayList<>();
        kill.add(player.getUnits().get(0));
        int startUnitsNum = player.getUnits().size();
        battle.killUnits(kill);
        int endUnitsNum = player.getUnits().size();

        assertEquals(1, startUnitsNum - endUnitsNum);
    }
}