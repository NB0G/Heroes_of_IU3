package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.TestInstance;

import entity.Hero;
import gameactors.Player;
import terrains.types.Hotel;

public class HotelTest {
    @Test
    public void testTakeBonus(){
        Hero hero = new Hero(10, 10, 1);
        Hotel hotel = new Hotel();
        hotel.setHpBonus(10);
        int startHp = hero.getUnits().get(0).getHp();
        hotel.takeBonus(hero);
        int endHp = hero.getUnits().get(0).getHp();
        assertEquals(startHp + 10, endHp);
    }

    @Test
    public void testProcessRestFailed(){
        Player player = new Player();
        player.setCoins(0);
        Hero hero = new Hero(10, 10, 1);
        Hotel hotel = new Hotel();
        assertEquals(false, hotel.processRest(player, hero, 1, 10, 10, true));
    }

    @Test
    public void testProcessRestAccepted(){
        Player player = new Player();
        player.setCoins(20);
        Hero hero = new Hero(10, 10, 1);
        hero.setEndTime(0);
        Hotel hotel = new Hotel();

        assertEquals(true, hotel.processRest(player, hero, 1, 10, 10, true));
    }
}
