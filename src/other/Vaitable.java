package other;

public interface Vaitable {
    int endTime = 0;

    int getEndTime();
    void setEndTime(int endTime);

    void waitUntillEndTime(boolean skip);
}
