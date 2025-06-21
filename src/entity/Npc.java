package entity;

import other.TimeManager;
import other.Vaitable;
import terrains.TimeObject;

public class Npc implements Vaitable {
    private volatile int endTime = 0;

    public void goToTimeObject(TimeObject timeObject, int time) {
        timeObject.addVacation(this);
        setEndTime(time + TimeManager.getCurrentTime());
        waitUntillEndTime();
        timeObject.removeVacation(this);
    }

    public int getEndTime() {
        return endTime;
    }

    public synchronized void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public synchronized void waitUntillEndTime() {
        while (TimeManager.getCurrentTime() < endTime) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
