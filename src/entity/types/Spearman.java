package entity.types;
import entity.Character;
import entity.UnitsDisplay;

public class Spearman extends Character{
    //копейщик
    public Spearman(int team){
        setLvl(1);
        setHp(5);
        setDamage(1);
        setDamageDistance(1);
        setSpeed(5);
        setTeam(team);
        if(team == 1) {
            display = UnitsDisplay.OURTIER1;
        } else if(team == 2){
            display = UnitsDisplay.ENEMYTIER1;
        }
    }
}
