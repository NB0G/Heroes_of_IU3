package entity.types;
import entity.Character;
import entity.UnitsDisplay;

public class Swordsman extends Character{
    //мечник
    public Swordsman(int team){
        setLvl(3);
        setHp(10);
        setDamage(3);
        setDamageDistance(1);
        setSpeed(8);
        setTeam(team);
        if(team == 1) {
            display = UnitsDisplay.OURTIER3;
        } else if(team == 2){
            display = UnitsDisplay.ENEMYTIER3;
        }
    }
}
