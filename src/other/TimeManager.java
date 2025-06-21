package other;

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
}
