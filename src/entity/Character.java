package entity;

import java.io.Serializable;
import maps.Map;
import other.Plasable;

public class Character implements Plasable, Serializable{
    private int lvl = 0;
    private int hp = 0;
    private int damage = 0;
    private int speed = 0;
    private int damageDistance = 1;
    private int position[] = new int[2];
    private int team;
    protected UnitsDisplay display;
    protected int stepsLeft;

    public void setStepsLeft(int stepsLeft) {
        this.stepsLeft = stepsLeft;
    }

    public int getStepsLeft() {
        return stepsLeft;
    }

    public String getDisplay() {
        return display.getSymbol();
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getTeam() {
        return team;
    }
    
    public void place(Map map){
        map.placeCharacter(position[1], position[0], this);
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDamageDistance(int damageDistance) {
        this.damageDistance = damageDistance;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public int getLvl() {
        return lvl;
    }

    public int getHp() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }

    public int getDamageDistance() {
        return damageDistance;
    }

    public int getSpeed() {
        return speed;
    }

    public int[] getPosition() {
        return position;
    }

    public void move(int dx, int dy){
        position[0] += dy;
        position[1] += dx;
    }
}
