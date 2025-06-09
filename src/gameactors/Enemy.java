package gameactors;
import java.util.Random;

import entity.Hero;
import maps.GameMap;
import other.Castle;
import terrains.Terrain;
import terrains.types.Rock;

public class Enemy extends Player {
    private int alreadyBuy = 0;
    private int counter = 0;
    private int heroCounter = 0;
    
    public Enemy(GameMap map){
        setCoins(6);
        heroes.add(new Hero(10, 10, 2));
        heroes.get(0).setPosition(map.getPosPlayer(whoAmI()));
        map.placeCharacter(heroes.get(0).getPosition()[1], heroes.get(0).getPosition()[0], heroes.get(0));
        this.map = map;
    }

    @Override
    public int whoAmI(){
        return 2;
    }

    @Override
    public int getHeroDirection(Hero hero){
        if(alreadyBuy == 1){
            int xHero = hero.getPosition()[1];
            int yHero = hero.getPosition()[0];

            for(int i = 0; i < 8; i++){
                Terrain itemToGo3 = map.getXY(xHero + directions[i][0], yHero + directions[i][1]);
                if(itemToGo3 instanceof Rock){
                    for(int j = i; j >= 0; j--){
                        Terrain itemToGo2 = map.getXY(xHero + directions[j][0], yHero + directions[j][1]);
                        if((itemToGo2 instanceof Rock) == false){
                            return j + 1;
                        }
                    }
                    for(int j = 7; j > i; j--){
                        Terrain itemToGo2 = map.getXY(xHero + directions[j][0], yHero + directions[j][1]);
                        if((itemToGo2 instanceof Rock) == false){
                            return j + 1;
                        }
                    }
                }
            }

            heroCounter += 1;
            if(heroCounter % 10 == 0){
                return -1;
            }
        }
        return -1;
    }

    @Override
    public int[] getXYForWings(){
        int x = map.getXPlayer(1);
        int y = map.getYPlayer(1);
        return new int[] {x, y};
    }
    
    @Override
    public int getBuying(int phase, Castle castle){
        Random random2 = new Random();
        random2.setSeed(1);
        if(alreadyBuy == 1){
            return 4;
        }
        if(counter == 5){
            counter = 6;
            alreadyBuy = -1;
        }
        if(coins < 3){
            alreadyBuy = 1;
        }

        if(phase == 1){
            if(alreadyBuy == 0){
                return 1;
            } else if(alreadyBuy == -1){
                return 2;
            } else if(alreadyBuy == 1){
                return 4;
            }
        } else if(phase == 2){
            counter += 1;
            return random2.nextInt(7);
        } else if(phase == 3){
            int number = random2.nextInt(5) + 2;
            while (castle.canBuy(number) != 1) {
                number = random2.nextInt(5) + 2;
            }
            return number;
        }
        return -1;
    }
}
