package terrains.types;

import entity.Character;
import entity.Hero;
import gameactors.Player;
import java.util.Scanner;
import other.TimeManager;
import other.Vaitable;
import terrains.TerrainDisplay;
import terrains.TimeObject;

public class Cafe extends TimeObject{
    private volatile int bonus = 20;
    private final int priceSnack = 5;
    private final int priceLunch = 15;

    public Cafe(){
        type = "cafe";
        moveCost = 0;
        display = TerrainDisplay.CAFE;
        maxVacations = 12;
    }

    public synchronized void takeBonus(Vaitable vacationer) {
        if (vacationer instanceof Hero) {
            Hero hero = (Hero) vacationer;
            for (Character unit : hero.getUnits()) {
                unit.setSpeed(unit.getSpeed() + bonus);
            }
        }
    }

    private void showWelcome() {
        System.out.println("Добро пожаловать в кафе!");
    }

    private void showFreePlaces() {
        int free = maxVacations - getVacations().size();
        System.out.println("Свободных мест: " + free);
    }

    private void showOccupiedPlaces() {
        if (getVacations().size() > 0) {
            System.out.println("Занятые места:");
            for (Vaitable v : getVacations()) {
                int left = (v.getEndTime() - TimeManager.getCurrentTime());
                System.out.println("Освободится через: " + (left > 0 ? left : 0) + " минут");
            }
        }
    }

    private void showMenu() {
        System.out.println("Выберите действие:");
        System.out.println("1 — Легкий перекус (+20 к перемещению, " + priceSnack + " монет)\n2 — Плотный обед (+100 к перемещению, " + priceLunch + " монет)\n3 — Выйти");
    }

    private void processRest(Player player, Vaitable vacationer, int bonusLocal, int price) {
        if (player.getCoins() < price) {
            System.out.println("Недостаточно монет для заказа! Требуется: " + price);
            return;
        } else {
            player.setCoins(player.getCoins() - price);
        }
        vacationer.setEndTime(TimeManager.getCurrentTime() + 20); // 1 день
        addVacation(vacationer);
        System.out.println("Ожидайте");
        vacationer.waitUntillEndTime();
        bonus = bonusLocal;
        takeBonus(vacationer);
        removeVacation(vacationer);
        System.out.println("Ваш герой и его отряд поели! +" + bonus + " к перемещению");
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
                processRest(player, vacationer, 20, priceSnack);
                break;
            } else if (inp == 2) {
                processRest(player, vacationer, 100, priceLunch);
                break;
            } else if (inp == 3) {
                System.out.println("Вы покинули кафе.");
                break;
            } else {
                System.out.println("Некорректный ввод.");
            }
        }
    }
}
