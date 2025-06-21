import entity.Character;
import entity.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import other.Vaitable;
import terrains.types.Cafe;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package test;




class CafeTest {

    private Cafe cafe;

    @BeforeEach
    void setUp() {
        cafe = new Cafe();
    }

    static class TestCharacter extends Character {
        private int speed;

        public TestCharacter(int speed) {
            this.speed = speed;
        }

        @Override
        public int getSpeed() {
            return speed;
        }

        @Override
        public void setSpeed(int speed) {
            this.speed = speed;
        }
    }

    static class TestHero extends Hero implements Vaitable {
        private final List<Character> units = new ArrayList<>();

        public void addUnit(Character unit) {
            units.add(unit);
        }

        @Override
        public List<Character> getUnits() {
            return units;
        }

        // Stub methods for Vaitable
        private int endTime = 0;

        @Override
        public void setEndTime(int time) {
            endTime = time;
        }

        @Override
        public int getEndTime() {
            return endTime;
        }

        @Override
        public void waitUntillEndTime() {
        }
    }

    static class NotHero implements Vaitable {
        private int endTime = 0;

        @Override
        public void setEndTime(int time) {
            endTime = time;
        }

        @Override
        public int getEndTime() {
            return endTime;
        }

        @Override
        public void waitUntillEndTime() {
        }
    }

    @Test
    void takeBonus_shouldIncreaseSpeedOfAllUnits_whenVacationerIsHero() {
        TestHero hero = new TestHero();
        TestCharacter unit1 = new TestCharacter(10);
        TestCharacter unit2 = new TestCharacter(15);
        hero.addUnit(unit1);
        hero.addUnit(unit2);

        cafe.takeBonus(hero);

        assertEquals(30, unit1.getSpeed() + unit2.getSpeed()); // 10+20, 15+20
        assertEquals(30, unit1.getSpeed() + unit2.getSpeed());
        assertEquals(30, hero.getUnits().stream().mapToInt(Character::getSpeed).sum());
    }

    @Test
    void takeBonus_shouldNotThrow_whenVacationerIsNotHero() {
        NotHero notHero = new NotHero();
        assertDoesNotThrow(() -> cafe.takeBonus(notHero));
    }

    @Test
    void takeBonus_shouldNotChangeAnything_whenHeroHasNoUnits() {
        TestHero hero = new TestHero();
        // No units added
        cafe.takeBonus(hero);
        assertEquals(0, hero.getUnits().size());
    }
}