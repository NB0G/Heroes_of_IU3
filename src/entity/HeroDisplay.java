package entity;
public enum HeroDisplay {
    OURHERO("🏇"),
    ENEMYHERO("🤺");

    private String symbol;

    HeroDisplay (String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
