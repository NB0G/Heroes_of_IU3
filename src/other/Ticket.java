package other;

import java.io.Serializable;

public class Ticket implements Serializable {
    private volatile int endTime = 0;

    public Ticket() {
        endTime = TimeManager.getCurrentTime() + 600;
    }

    public boolean isValid() {
        return endTime >= TimeManager.getCurrentTime();
    }
}
