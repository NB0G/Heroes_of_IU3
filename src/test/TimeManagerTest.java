import entity.Npc;
import other.TimeManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import terrains.TimeObject;
import terrains.types.Hotel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.mockito.Mockito.*;

package test;




class TimeManagerTest {

    // Helper to run the private method via reflection
    private void invokeRunNpcBehavior(Npc npc, List<TimeObject> timeObjects, int minTime, int maxTime, int minWait, int maxWait, int iterations) throws Exception {
        // Use a thread to stop after a few iterations
        AtomicBoolean stop = new AtomicBoolean(false);

        // Spy the npc to count calls
        Npc spyNpc = spy(npc);

        Thread t = new Thread(() -> {
            try {
                // Use reflection to access private method
                var m = TimeManager.class.getDeclaredMethod("runNpcBehavior", Npc.class, List.class, int.class, int.class, int.class, int.class);
                m.setAccessible(true);

                // Patch Thread.sleep to break after N iterations
                int[] count = {0};
                do {
                    m.invoke(null, spyNpc, timeObjects, minTime, maxTime, minWait, maxWait);
                    count[0]++;
                } while (count[0] < iterations && !Thread.currentThread().isInterrupted());
            } catch (Exception ignored) {}
            stop.set(true);
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(200); // Let it run a bit
        t.interrupt();
        t.join(500);
    }

    @Test
    @Timeout(2)
    void testRunNpcBehaviorCallsGoToTimeObject() throws Exception {
        Npc npc = mock(Npc.class);
        TimeObject to1 = mock(TimeObject.class);
        TimeObject to2 = mock(TimeObject.class);
        List<TimeObject> timeObjects = new ArrayList<>();
        timeObjects.add(to1);
        timeObjects.add(to2);

        // Run in a thread and interrupt after a short time
        Thread t = new Thread(() -> {
            try {
                var m = TimeManager.class.getDeclaredMethod("runNpcBehavior", Npc.class, List.class, int.class, int.class, int.class, int.class);
                m.setAccessible(true);
                m.invoke(null, npc, timeObjects, 1, 2, 1, 2);
            } catch (Exception ignored) {}
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(100);
        t.interrupt();
        t.join(200);

        verify(npc, atLeastOnce()).goToTimeObject(any(TimeObject.class), anyInt());
    }

    @Test
    @Timeout(2)
    void testRunNpcBehaviorHotelTimeMultiplier() throws Exception {
        Npc npc = mock(Npc.class);
        Hotel hotel = mock(Hotel.class);
        List<TimeObject> timeObjects = new ArrayList<>();
        timeObjects.add(hotel);

        Thread t = new Thread(() -> {
            try {
                var m = TimeManager.class.getDeclaredMethod("runNpcBehavior", Npc.class, List.class, int.class, int.class, int.class, int.class);
                m.setAccessible(true);
                m.invoke(null, npc, timeObjects, 2, 2, 1, 1);
            } catch (Exception ignored) {}
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(100);
        t.interrupt();
        t.join(200);

        // For hotel, time should be multiplied by 20
        verify(npc, atLeastOnce()).goToTimeObject(eq(hotel), eq(40));
    }

    @Test
    @Timeout(2)
    void testRunNpcBehaviorHandlesInterruptedException() throws Exception {
        Npc npc = mock(Npc.class);
        TimeObject to = mock(TimeObject.class);
        List<TimeObject> timeObjects = new ArrayList<>();
        timeObjects.add(to);

        Thread t = new Thread(() -> {
            try {
                var m = TimeManager.class.getDeclaredMethod("runNpcBehavior", Npc.class, List.class, int.class, int.class, int.class, int.class);
                m.setAccessible(true);
                m.invoke(null, npc, timeObjects, 1, 1, 1000, 1000); // Long sleep
            } catch (Exception ignored) {}
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(100);
        t.interrupt();
        t.join(200);

        // Should not throw, just exit
        verify(npc, atLeastOnce()).goToTimeObject(any(TimeObject.class), anyInt());
    }
}