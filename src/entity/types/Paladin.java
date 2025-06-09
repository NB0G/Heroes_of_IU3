package entity.types;
import entity.Character;
import entity.UnitsDisplay;

public class Paladin extends Character{
    //копейщик
    public Paladin(int team){
        setLvl(5);
        setHp(15);
        setDamage(15);
        setDamageDistance(1);
        setSpeed(15);
        setTeam(team);
        if(team == 1) {
            display = UnitsDisplay.OURTIER5;
        } else if(team == 2){
            display = UnitsDisplay.ENEMYTIER5;
        }
    }
}
