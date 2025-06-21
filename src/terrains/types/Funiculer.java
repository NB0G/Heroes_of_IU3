package terrains.types;

import entity.Hero;
import gameactors.Player;
import java.util.Scanner;
import objects.types.Nothing;
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

    public static int[] funiculer2 = {0, 0};

    public void setFuniculer2(int x, int y) {
        funiculer2[0] = 10;
        funiculer2[1] = 10;
    }

    public void move(Player player, Vaitable vacationer){
        if (vacationer instanceof Hero) {
            player.moveToCoords(funiculer2[0], funiculer2[1], (Hero) vacationer);
        }
    }

    public void checkActive(Player player){
        if (player.getObjectOnMap(funiculer2[0], funiculer2[1]) instanceof Nothing){
            isActive = true;
        } else {
            isActive = false;
        }
    }

    @Override
    public synchronized void getInterface(Player player, Vaitable vacationer) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать на фуникулер!");

        checkActive(player);
        if (!isActive) {
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

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
