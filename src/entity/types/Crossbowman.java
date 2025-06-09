package entity.types;
import entity.Character;
import entity.UnitsDisplay;

public class Crossbowman extends Character{
    //арбалетчик
    public Crossbowman(int team){
        setLvl(2);
        setHp(2);
        setDamage(3);
        setDamageDistance(3);
        setSpeed(5);
        setTeam(team);
        if(team == 1) {
            display = UnitsDisplay.OURTIER2;
        } else if(team == 2){
            display = UnitsDisplay.ENEMYTIER2;
        }
    }
}
