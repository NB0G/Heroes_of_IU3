import entity.Character;
import entity.Hero;
import gameactors.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import other.TimeManager;
import other.Vaitable;
import terrains.TerrainDisplay;
import terrains.types.Hotel;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package test;




class HotelTest {

    private Hotel hotel;
    private TestHero hero;
    private Player player;

    // Minimal stub for Character
    static class TestCharacter extends Character {
        private int hp;

        public TestCharacter(int hp) {
            this.hp = hp;
        }

        @Override
        public int getHp() {
            return hp;
        }

        @Override
        public void setHp(int hp) {
            this.hp = hp;
        }
    }

    // Minimal stub for Hero
    static class TestHero extends Hero implements Vaitable {
        private List<Character> units = new ArrayList<>();
        private int endTime = 0;

        public TestHero() {
            units.add(new TestCharacter(50));
            units.add(new TestCharacter(60));
        }

        @Override
        public List<Character> getUnits() {
            return units;
        }

        @Override
        public int getEndTime() {
            return endTime;
        }

        @Override
        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        @Override
        public void waitUntillEndTime() {
            // Do nothing for test
        }
    }

    // Minimal stub for Player
    static class TestPlayer extends Player {
        private int coins;

        public TestPlayer(int coins) {
            this.coins = coins;
        }

        @Override
        public int getCoins() {
            return coins;
        }

        @Override
        public void setCoins(int coins) {
            this.coins = coins;
        }
    }

    // Minimal stub for TimeManager
    static class TestTimeManager extends TimeManager {
        private static int currentTime = 100;

        public static void setCurrentTime(int t) {
            currentTime = t;
        }

        public static int getCurrentTime() {
            return currentTime;
        }
    }

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hero = new TestHero();
        player = new TestPlayer(100);
        // Patch TimeManager if needed
    }

    @Test
    void testTakeBonusIncreasesHp() {
        int hpBefore0 = hero.getUnits().get(0).getHp();
        int hpBefore1 = hero.getUnits().get(1).getHp();
        hotel.takeBonus(hero);
        assertEquals(hpBefore0 + 20, hero.getUnits().get(0).getHp());
        assertEquals(hpBefore1 + 20, hero.getUnits().get(1).getHp());
    }

    @Test
    void testProcessRestOneDay() {
        int coinsBefore = player.getCoins();
        int hpBefore = hero.getUnits().get(0).getHp();
        // Use reflection to access private method
        try {
            var method = Hotel.class.getDeclaredMethod("processRest", Player.class, Vaitable.class, int.class, int.class, int.class);
            method.setAccessible(true);
            method.invoke(hotel, player, hero, 1, 15, 5);
        } catch (Exception e) {
            fail(e);
        }
        assertEquals(coinsBefore - 5, player.getCoins());
        assertEquals(hpBefore + 15, hero.getUnits().get(0).getHp());
    }

    @Test
    void testProcessRestThreeDays() {
        int coinsBefore = player.getCoins();
        int hpBefore = hero.getUnits().get(0).getHp();
        try {
            var method = Hotel.class.getDeclaredMethod("processRest", Player.class, Vaitable.class, int.class, int.class, int.class);
            method.setAccessible(true);
            method.invoke(hotel, player, hero, 3, 30, 12);
        } catch (Exception e) {
            fail(e);
        }
        assertEquals(coinsBefore - 12, player.getCoins());
        assertEquals(hpBefore + 30, hero.getUnits().get(0).getHp());
    }

    @Test
    void testProcessRestNotEnoughCoins() {
        player.setCoins(3);
        int coinsBefore = player.getCoins();
        int hpBefore = hero.getUnits().get(0).getHp();
        try {
            var method = Hotel.class.getDeclaredMethod("processRest", Player.class, Vaitable.class, int.class, int.class, int.class);
            method.setAccessible(true);
            method.invoke(hotel, player, hero, 1, 15, 5);
        } catch (Exception e) {
            fail(e);
        }
        // Coins and hp should not change
        assertEquals(coinsBefore, player.getCoins());
        assertEquals(hpBefore, hero.getUnits().get(0).getHp());
    }

    @Test
    void testHotelConstructorSetsFields() {
        assertEquals("hotel", hotel.type);
        assertEquals(0, hotel.moveCost);
        assertEquals(TerrainDisplay.HOTEL, hotel.display);
        assertEquals(5, hotel.maxVacations);
    }
}