package terrains;

import gameactors.Player;
import java.util.concurrent.CopyOnWriteArrayList;
import other.Vaitable;

public abstract class TimeObject extends Terrain {
    private CopyOnWriteArrayList<Vaitable> vacations = new CopyOnWriteArrayList<>();
    protected int maxVacations;

    public synchronized void addVacation(Vaitable vacationer) {
        if (vacations.size() < maxVacations) {
            vacations.add(vacationer);
        }
    }

    public void removeVacation(Vaitable vacationer) {
        vacations.remove(vacationer);
    }

    public void waitEmptyVacations(Vaitable vacationer) {
        while (vacations.size() >= maxVacations) {
            try {
                Thread.sleep(100); // Wait for 100 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                break; // Exit if interrupted
            }
        }
    }

    public synchronized void takeBonus(Vaitable vacationer){
    }

    public synchronized void getInterface(Player player, Vaitable vacationer){
    }

    protected synchronized CopyOnWriteArrayList<Vaitable> getVacations() {
        return vacations;
    }
}
