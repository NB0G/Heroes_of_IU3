import entity.Hero;
import gameactors.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import other.TimeManager;
import other.Vaitable;
import terrains.TerrainDisplay;
import terrains.types.BarberShop;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package test;




class BarberShopTest {

    private BarberShop barberShop;
    private TestHero hero;
    private Player player;

    // Minimal stub for Vaitable/Hero for testing
    static class TestHero extends Hero {
        private boolean strizka = false;
        private int endTime = 0;
        private boolean waited = false;

        @Override
        public void setStrizka(boolean value) {
            this.strizka = value;
        }

        @Override
        public boolean isStrizka() {
            return strizka;
        }

        @Override
        public void setEndTime(int time) {
            this.endTime = time;
        }

        @Override
        public int getEndTime() {
            return endTime;
        }

        @Override
        public void waitUntillEndTime() {
            waited = true;
        }
    }

    @BeforeEach
    void setUp() {
        barberShop = new BarberShop();
        hero = new TestHero();
        player = new Player("TestPlayer");
        player.setCoins(100);
        // Clear vacations if needed
        barberShop.getVacations().clear();
    }

    @Test
    void testConstructorSetsFields() {
        assertEquals("barber_shop", barberShop.type);
        assertEquals(0, barberShop.moveCost);
        assertEquals(TerrainDisplay.BARBER_SHOP, barberShop.display);
        assertEquals(2, barberShop.maxVacations);
    }

    @Test
    void testTakeBonusSetsStrizka() {
        hero.setStrizka(false);
        barberShop.takeBonus(hero);
        assertTrue(hero.isStrizka());
    }

    @Test
    void testProcessRestSimpleHaircutDeductsCoinsAndWaits() {
        int initialCoins = player.getCoins();
        barberShop.getVacations().clear();
        // Use reflection to call private method
        invokeProcessRest(barberShop, player, hero, 30, 5);
        assertEquals(initialCoins - 5, player.getCoins());
        assertTrue(hero.waited);
    }

    @Test
    void testProcessRestCoolHaircutGivesBonus() {
        hero.setStrizka(false);
        barberShop.getVacations().clear();
        invokeProcessRest(barberShop, player, hero, 70, 25);
        assertTrue(hero.isStrizka());
    }

    @Test
    void testProcessRestNotEnoughCoins() {
        player.setCoins(1);
        int initialCoins = player.getCoins();
        barberShop.getVacations().clear();
        invokeProcessRest(barberShop, player, hero, 30, 5);
        assertEquals(initialCoins, player.getCoins());
        assertFalse(hero.waited);
    }

    @Test
    void testMaxVacationsLimit() {
        // Fill up all vacation slots
        List<Vaitable> fakeList = barberShop.getVacations();
        fakeList.clear();
        fakeList.add(new TestHero());
        fakeList.add(new TestHero());
        assertEquals(0, barberShop.maxVacations - fakeList.size());
    }

    // Helper to invoke private processRest
    private void invokeProcessRest(BarberShop shop, Player player, Vaitable vacationer, int days, int price) {
        try {
            var m = BarberShop.class.getDeclaredMethod("processRest", Player.class, Vaitable.class, int.class, int.class);
            m.setAccessible(true);
            m.invoke(shop, player, vacationer, days, price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}