package other;

import terrains.TimeObject;

public interface Vaitable {
    int endTime = 0;

    int getEndTime();
    void setEndTime(int endTime);

    void waitUntillEndTime(TimeObject timeObject);
}
