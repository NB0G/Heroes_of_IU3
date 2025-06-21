package terrains.types;

import entity.Character;
import entity.Hero;
import gameactors.Player;
import java.util.Scanner;
import other.TimeManager;
import other.Vaitable;
import terrains.TerrainDisplay;
import terrains.TimeObject;

public class Hotel extends TimeObject{
    private volatile int hpBonus = 20;
    private final int priceOneDay = 5;
    private final int priceThreeDays = 12;

    public Hotel(){
        type = "hotel";
        moveCost = 0;
        display = TerrainDisplay.HOTEL;
        maxVacations = 5;
    }

    public void setHpBonus(int hpBonus){
        this.hpBonus = hpBonus;
    }

    @Override
    public synchronized void takeBonus(Vaitable vacationer) {
        if (vacationer instanceof Hero) {
            Hero hero = (Hero) vacationer;
            for (Character unit : hero.getUnits()) {
                unit.setHp(unit.getHp() + hpBonus);
            }
        }
    }

    private void showWelcome() {
        System.out.println("Добро пожаловать в отель!");
    }

    private void showFreePlaces() {
        int free = maxVacations - getVacations().size();
        System.out.println("Свободных мест: " + free);
    }

    private void showOccupiedPlaces() {
        if (getVacations().size() > 0) {
            System.out.println("Занятые места:");
            for (Vaitable v : getVacations()) {
                int left = (v.getEndTime() - TimeManager.getCurrentTime()) / 20;
                System.out.println("Освободится через: " + (left > 0 ? left : 0) + " дней");
            }
        }
    }

    private void showMenu() {
        System.out.println("Выберите действие:");
        System.out.println("1 — Отдых на 1 день (+15 hp, " + priceOneDay + " монет)\n2 — Отдых на 3 дня (+30 hp, " + priceThreeDays + " монет)\n3 — Выйти");
    }

    public boolean processRest(Player player, Vaitable vacationer, int days, int bonus, int price, boolean skip) {
        if (player.getCoins() < price) {
            System.out.println("Недостаточно монет для отдыха! Требуется: " + price);
            return false;
        } else {
            player.setCoins(player.getCoins() - price);
        }
        vacationer.setEndTime(TimeManager.getCurrentTime() + days * 20);
        addVacation(vacationer);
        System.out.println("Ожидание " + days + " дней...");
        vacationer.waitUntillEndTime(skip);
        hpBonus = bonus;
        takeBonus(vacationer);
        removeVacation(vacationer);
        System.out.println("Ваш герой и его отряд отдохнули! +" + bonus + " к здоровью");
        return true;
    }

    private void processWait(Vaitable vacationer) {
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
                processRest(player, vacationer, 1, 15, priceOneDay, false);
                break;
            } else if (inp == 2) {
                processRest(player, vacationer, 3, 30, priceThreeDays, false);
                break;
            } else if (inp == 3) {
                System.out.println("Вы покинули отель.");
                break;
            } else {
                System.out.println("Некорректный ввод.");
            }
        }
    }
}
