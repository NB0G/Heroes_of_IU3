package terrains.types;

import entity.Hero;
import gameactors.Player;
import java.util.Scanner;
import other.Vaitable;
import terrains.TerrainDisplay;
import terrains.TimeObject;

public class Funiculer extends TimeObject{
    private volatile boolean isActive = true;

    public Funiculer(){
        type = "funiculer";
        moveCost = 0;
        display = TerrainDisplay.FUNICULER;
        maxVacations = 0;
    }

    public int[] funiculer2 = {0, 0};

    public void setFuniculer2(int x, int y) {
        funiculer2[0] = x;
        funiculer2[1] = y;
    }

    public void move(Player player, Vaitable vacationer){
        if (vacationer instanceof Hero) {
            player.moveToCoords(funiculer2[0], funiculer2[1], (Hero) vacationer);
        }
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getIsActive() {
        return isActive;
    }

    // public void checkActive(Player player){
    //     if (player.getObjectOnMap(funiculer2[0], funiculer2[1]).getClass().equals(Hero.class)){
    //         isActive = false;
    //     } else {
    //         isActive = true;
    //     }
    // }

    @Override
    public synchronized void getInterface(Player player, Vaitable vacationer) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать на фуникулер!");


        if (!isActive) {
            System.out.println("Фуникулер временно не работает. Пожалуйста, подождите.");
            System.out.println("Нажмите любую клавишу для выхода.");
            scanner.nextLine();
            return;
        }

        boolean hasTicket = false;
        int ticketIndex = -1;
        for (int i = 0; i < player.getTickets().size(); i++) {
            if (player.getTickets().get(i).isValid()) {
                hasTicket = true;
                ticketIndex = i;
                break;
            }
        }
        if (!hasTicket) {
            System.out.println("У вас нет действующего билета на фуникулер. Купите билет в магазине (5 монет).");
            System.out.println("Нажмите любую клавишу для выхода.");
            scanner.nextLine();
            return;
        }

        System.out.println("Билет действителен! Перемещаем вас на другой фуникулер...");
        
        move(player, vacationer);

        System.out.println("Вы успешно перемещены на другой фуникулер");

        player.removeTicket(ticketIndex);
        System.out.println("Спасибо за поездку!");
    }
}
