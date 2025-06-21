package objects;
public enum ObjectsDisplay {
    COIN("💎"),
    WINGS("🪽 "),
    NOTHING("");
    
    private String symbol;

    ObjectsDisplay (String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
