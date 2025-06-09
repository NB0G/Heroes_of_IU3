package entity.types;
import entity.Character;
import entity.UnitsDisplay;

public class Cavalryman extends Character{
    //кавалерист
    public Cavalryman(int team){
        setLvl(4);
        setHp(7);
        setDamage(4);
        setDamageDistance(2);
        setSpeed(12);
        setTeam(team);
        if(team == 1) {
            display = UnitsDisplay.OURTIER4;
        } else if(team == 2){
            display = UnitsDisplay.ENEMYTIER4;
        }
    }
}
