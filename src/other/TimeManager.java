package other;

import entity.Npc;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import maps.GameMap;
import terrains.TimeObject;
import terrains.types.Hotel;

public class TimeManager {
    private static volatile int currentTime = 0;

    public static int getCurrentTime() {
        return currentTime;
    }

    public static synchronized void setCurrentTime(int time) {
        currentTime = time;
    }

    public static void startTimer() {
        Thread timerThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
                setCurrentTime(getCurrentTime() + 1);
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    // Поиск всех TimeObject на карте
    public static List<TimeObject> findAllTimeObjects(GameMap map) {
        List<TimeObject> result = new ArrayList<>();
        for (int y = 0; y < map.getSizeY(); y++) {
            for (int x = 0; x < map.getSizeX(); x++) {
                if (map.getXY(x, y) instanceof TimeObject to) {
                    result.add(to);
                }
            }
        }
        return result;
    }

    // Запуск всех NPC как демон-потоки
    public static void startAllNpc(Npc[] npcs, GameMap map, int minTime, int maxTime, int minWait, int maxWait) {
        List<TimeObject> timeObjects = findAllTimeObjects(map);
        for (Npc npc : npcs) {
            Thread t = new Thread(() -> runNpcBehavior(npc, timeObjects, minTime, maxTime, minWait, maxWait));
            t.setDaemon(true);
            t.start();
        }
    }

    // Создание массива NPC и запуск их поведения
    public static void createAndStartNpcs(int npcCount, GameMap map, int minTime, int maxTime, int minWait, int maxWait) {
        Npc[] npcs = new Npc[npcCount];
        for (int i = 0; i < npcCount; i++) {
            npcs[i] = new Npc();
        }
        startAllNpc(npcs, map, minTime, maxTime, minWait, maxWait);
    }

    // Поведение одного NPC: периодически занимает случайный TimeObject на случайное время
    private static void runNpcBehavior(Npc npc, List<TimeObject> timeObjects, int minTime, int maxTime, int minWait, int maxWait) {
        Random random = new Random();
        while (true) {
            TimeObject to = timeObjects.get(random.nextInt(timeObjects.size()));
            int time = minTime + random.nextInt(maxTime - minTime + 1);
            if (to instanceof Hotel) {
                time *= 20;
            }
            npc.goToTimeObject(to, time);
            int waitTime = minWait + random.nextInt(maxWait - minWait + 1);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
