package entity;
import java.util.ArrayList;
import java.util.Random;

import entity.types.Cavalryman;
import entity.types.Crossbowman;
import entity.types.Paladin;
import entity.types.Spearman;
import entity.types.Swordsman;
import java.io.Serializable;
import maps.Map;
import other.Plasable;
import other.TimeManager;
import other.Vaitable;

public class Hero implements Plasable, Vaitable, Serializable{
    Random random = new Random();
    private int speed = 0;
    private ArrayList<Character> units = new ArrayList<Character>();
    private int horse = 0;
    private int wings = 0;
    private int position[] = new int[2];
    private int team;
    private int stepsLeft;
    private volatile boolean strizka = false;
    private volatile int endTime = 0;

    public int getEndTime() {
        return endTime;
    }

    public synchronized void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public synchronized void waitUntillEndTime(boolean skip) {
        if (skip){
            return;
        }
        while (TimeManager.getCurrentTime() < endTime) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Hero(int speed, int seed, int team){
        random.setSeed(seed);
        this.speed = speed;
        this.team = team;
        for(int i = 0; i < random.nextInt(10) + 4; i++){
            switch (random.nextInt(5)) {
                case 0:
                    units.add(new Spearman(team));
                    break;
                case 1:
                    units.add(new Crossbowman(team));
                    break;
                case 2:
                    units.add(new Swordsman(team));
                    break;
                case 3:
                    units.add(new Cavalryman(team));
                    break;
                case 4:
                    units.add(new Paladin(team));
                    break;
                default:
                    break;
            }
        }
    }

    public boolean isStrizka() {
        return strizka;
    }

    public void setStrizka(boolean strizka) {
        this.strizka = strizka;
    }

    public int getStepsLeft() {
        return stepsLeft;
    }

    public void setStepsLeft(int stepsLeft) {
        this.stepsLeft = stepsLeft;
    }

    public int getTeam() {
        return team;
    }

    public void place(Map map){
        map.placeCharacter(position[1], position[0], this);
    }

    public String getDisplay(){
        if(team == 1){
            return HeroDisplay.OURHERO.getSymbol();
        } else {
            return HeroDisplay.ENEMYHERO.getSymbol();
        }
    }

    public int getWings() {
        return wings;
    }

    public void setWings() {
        wings = 1;
    }

    public void setHorse() {
        horse = 1;
        speed *= 2;
    }

    public int getHorse() {
        return horse;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public int getSpeed() {
        return speed;
    }

    public int[] getPosition() {
        return position;
    }

    public ArrayList<Character> getUnits() {
        return units;
    }

    public void addUnit(Character unit){
        units.add(unit);
    }

    public void deleteUnit(Character unit){
        units.remove(unit);
    }

    public void move(int dx, int dy){
        position[0] += dy;
        position[1] += dx;
    }
}
