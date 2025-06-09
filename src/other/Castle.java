package other;
import entity.Character;
import entity.Hero;
import entity.types.Cavalryman;
import entity.types.Crossbowman;
import entity.types.Paladin;
import entity.types.Spearman;
import entity.types.Swordsman;
import gameactors.Player;
import java.io.Serializable;

public class Castle implements Serializable{
    private String[] names = {"Таверна", "Конюшня", "Сторожевой пост", "Башня арбалетчиков", "Оружейная", "Арена", "Собор"};
    private String[] unitNames = {"", "", "Копейщик", "Арбалетчик", "Мечник", "Кавалерист", "Паладин"};
    private int[] isPlaced = {0, 0, 1, 0, 0, 0, 0};
    private int[] cost = {2, 2, 1, 2, 2, 3, 3};
    private int[] unitCost = {0, 0, 1, 1, 1, 2, 2};
    private int[] reward = {0, 0, 2, 2, 2, 3, 3};

    public int[] getIsPlaced(){
        return isPlaced;
    }

    public int getReward(int index) {
        return reward[index];
    }

    public int isTavern(){
        if(isPlaced[0] == 1){
            return 1;
        }
        return 0;
    }

    public int isStable(){
        if(isPlaced[1] == 1){
            return 1;
        }
        return 0;
    }

    public void printAvailableBuildings(){
        System.out.println("Вы можете купить:");
        for(int i = 0; i < 7; i++){
            if(isPlaced[i] == 0){
                System.out.print("(");
                System.out.print(i);
                System.out.print(") ");
                System.out.print(names[i]);
                System.out.print(" цена: ");
                System.out.println(cost[i]);
            }
        }
    }

    public void buy(Player player, int buy){
        if(buy < 0 || buy >= cost.length){
            return;
        }
        if(player.getCoins() >= cost[buy] && isPlaced[buy] == 0){
            isPlaced[buy] = 1;
            player.setCoins(player.getCoins() - cost[buy]);
        }
    }

    public int canBuy(int index){
        return isPlaced[index];
    }

    public void printAvailableUnits(){
        System.out.println("Вы можете купить:");
        for(int i = 2; i < 7; i++){
            if(isPlaced[i] == 1){
                System.out.print("(");
                System.out.print(i);
                System.out.print(") ");
                System.out.print(unitNames[i]);
                System.out.print(" цена: ");
                System.out.println(unitCost[i]);
            }
        }
    }

    public void unit(Player player, Hero hero, int buy){
        if(player.getCoins() >= unitCost[buy]){
            player.setCoins(player.getCoins() - cost[buy]);
            Character newUnit = new Character();

            if(buy == 2){
                newUnit = new Spearman(hero.getTeam());
            } else if(buy == 3){
                newUnit = new Crossbowman(hero.getTeam());
            } else if(buy == 4){
                newUnit = new Swordsman(hero.getTeam());
            } else if(buy == 5){
                newUnit = new Cavalryman(hero.getTeam());
            } else if(buy == 6){
                newUnit = new Paladin(hero.getTeam());
            }

            hero.addUnit(newUnit);
        }
    }
}
