import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import other.Vaitable;
import terrains.TimeObject;
import gameactors.Player;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

package test;




class TimeObjectTest {

    private static class TestTimeObject extends TimeObject {
        public TestTimeObject(int maxVacations) {
            this.maxVacations = maxVacations;
        }
    }

    private TestTimeObject timeObject;
    private Vaitable vacationer1;
    private Vaitable vacationer2;

    @BeforeEach
    void setUp() {
        timeObject = new TestTimeObject(1);
        timeObject.init();
        vacationer1 = mock(Vaitable.class);
        vacationer2 = mock(Vaitable.class);
    }

    @Test
    void testInitClearsVacations() {
        timeObject.addVacation(vacationer1);
        assertEquals(1, timeObject.getVacations().size());
        timeObject.init();
        assertEquals(0, timeObject.getVacations().size());
    }

    @Test
    void testAddVacationWithinLimit() {
        timeObject.addVacation(vacationer1);
        assertTrue(timeObject.getVacations().contains(vacationer1));
    }

    @Test
    void testAddVacationExceedsLimit() {
        timeObject.addVacation(vacationer1);
        timeObject.addVacation(vacationer2);
        assertFalse(timeObject.getVacations().contains(vacationer2));
        assertEquals(1, timeObject.getVacations().size());
    }

    @Test
    void testRemoveVacation() {
        timeObject.addVacation(vacationer1);
        timeObject.removeVacation(vacationer1);
        assertFalse(timeObject.getVacations().contains(vacationer1));
    }

    @Test
    void testWaitEmptyVacationsReturnsWhenBelowLimit() {
        timeObject.addVacation(vacationer1);
        Thread t = new Thread(() -> timeObject.waitEmptyVacations(vacationer2));
        t.start();
        // Remove vacationer after a short delay
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {}
        timeObject.removeVacation(vacationer1);
        try {
            t.join(500);
        } catch (InterruptedException ignored) {}
        assertFalse(t.isAlive());
    }

    @Test
    void testTakeBonusAndGetInterfaceAreCallable() {
        Player player = mock(Player.class);
        // Should not throw
        timeObject.takeBonus(vacationer1);
        timeObject.getInterface(player, vacationer1);
    }

    @Test
    void testGetVacationsReturnsCorrectList() {
        timeObject.addVacation(vacationer1);
        CopyOnWriteArrayList<Vaitable> vacations = timeObject.getVacations();
        assertEquals(1, vacations.size());
        assertTrue(vacations.contains(vacationer1));
    }
}