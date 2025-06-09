package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.Before;

import entity.Hero;
import entity.types.Spearman;
import entity.types.Swordsman;

public class HeroTest {
    private Hero hero;

    @Before
    public void init() {
        System.out.println("init");
        hero = new Hero(10, 10, 1);
        hero.setPosition(new int[] {0, 0});
    }

    @Test
    public void testMove() {
        int[] startPosition = hero.getPosition().clone();
        hero.move(1, 1);
        int[] endPosition = hero.getPosition();
        assertEquals(startPosition[0] + 1, endPosition[0]);
        assertEquals(startPosition[1] + 1, endPosition[1]);
    }

    @Test
    public void testDeleteUnit() {
        Swordsman unit = new Swordsman(1);
        hero.addUnit(unit);
        int startUnitCount = hero.getUnits().size();
        hero.deleteUnit(unit);
        int endUnitCount = hero.getUnits().size();
        assertEquals(startUnitCount - 1, endUnitCount);
    }

    @Test
    public void testAddUnit() {
        int startUnitCount = hero.getUnits().size();
        hero.addUnit(new Spearman(1));
        int endUnitCount = hero.getUnits().size();
        assertEquals(startUnitCount + 1, endUnitCount);
    }

    @Test
    public void testSetHorse() {
        int startHorse = hero.getHorse();
        int startSpeed = hero.getSpeed();
        assertEquals(0, startHorse);
        hero.setHorse();
        int endHorse = hero.getHorse();
        int endSpeed = hero.getSpeed();
        assertEquals(1, endHorse);
        assertEquals(startSpeed * 2, endSpeed);
    }

    @Test
    public void testAddWings() {
        int startWings = hero.getWings();
        assertEquals(startWings, 0);
        hero.setWings();
        int endWings = hero.getWings();
        assertEquals(endWings, 1);
    }

    @Test
    public void testHeroMove() {
        int x = hero.getPosition()[1];
        int y = hero.getPosition()[0];

        int dx = 1;
        int dy = 1;

        hero.move(dx, dy);

        int x2 = hero.getPosition()[1];
        int y2 = hero.getPosition()[0];

        assertEquals(x + dx, x2);
        assertEquals(y + dy, y2);
    }
}
