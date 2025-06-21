package terrains.types;

import entity.Hero;
import gameactors.Player;
import java.util.Scanner;
import other.TimeManager;
import other.Vaitable;
import terrains.TerrainDisplay;
import terrains.TimeObject;

public class BarberShop extends TimeObject{
    private final int priceSimple = 5;
    private final int priceCool = 25;

    public BarberShop(){
        type = "barber_shop";
        moveCost = 0;
        display = TerrainDisplay.BARBER_SHOP;
        maxVacations = 2;
    }

    @Override
    public synchronized void takeBonus(Vaitable vacationer) {
        if (vacationer instanceof Hero) {
            Hero hero = (Hero) vacationer;
            hero.setStrizka(true);
        }
    }

    public void showWelcome() {
        System.out.println("Добро пожаловать в барбершоп!");
    }

    public void showFreePlaces() {
        int free = maxVacations - getVacations().size();
        System.out.println("Свободных мест: " + free);
    }

    public void showOccupiedPlaces() {
        if (getVacations().size() > 0) {
            System.out.println("Занятые места:");
            for (Vaitable v : getVacations()) {
                int left = (v.getEndTime() - TimeManager.getCurrentTime());
                System.out.println("Освободится через: " + (left > 0 ? left : 0) + " минут");
            }
        }
    }

    public void showMenu() {
        System.out.println("Выберите действие:");
        System.out.println("1 — обычная стрижка (" + priceSimple + " монет)\n2 — Крутая стрижка (быстрый выйгрыш, " + priceCool + " монет)\n3 — Выйти");
    }

    public void processRest(Player player, Vaitable vacationer, int days, int price) {
        if (player.getCoins() < price) {
            System.out.println("Недостаточно монет для отдыха! Требуется: " + price);
            return;
        } else {
            player.setCoins(player.getCoins() - price);
        }
        vacationer.setEndTime(TimeManager.getCurrentTime() + days);
        addVacation(vacationer);
        System.out.println("Ожидание " + days + " минут...");
        vacationer.waitUntillEndTime();
        if (price == priceCool) {
            takeBonus(vacationer);
            System.out.println("Ваш герой получил бонус!");
        }
        removeVacation(vacationer);
        System.out.println("Ваш герой пострижен!");
    }

    public void processWait(Vaitable vacationer) {
        waitEmptyVacations(vacationer);
    }

    @Override
    public synchronized void getInterface(Player player, Vaitable vacationer) {
        Scanner scanner = new Scanner(System.in);
        showWelcome();
        while (true) {
            int free = maxVacations - getVacations().size();
            showFreePlaces();
            showOccupiedPlaces();
            if (free == 0) {
                System.out.println("Нет свободных мест. 1 — подождать, 2 — выйти");
                int inp = scanner.nextInt();
                if (inp == 1) {
                    processWait(vacationer);
                    free = maxVacations - getVacations().size();
                    showFreePlaces();
                    showOccupiedPlaces();
                } else {
                    break;
                }
            }
            showMenu();
            int inp = scanner.nextInt();
            if (inp == 1) {
                processRest(player, vacationer, 30, priceSimple);
                break;
            } else if (inp == 2) {
                processRest(player, vacationer, 70, priceCool);
                break;
            } else if (inp == 3) {
                System.out.println("Вы покинули барбершоп.");
                break;
            } else {
                System.out.println("Некорректный ввод.");
            }
        }
    }
}
