package terrains;
public enum TerrainDisplay {
    VOID("🌳"),
    OURCASTLE("🏰"),
    ENEMYCASTLE("🏯"),
    ROAD("🌱"),
    OURTERRITORY("⛺️"),
    ENEMYTERRITORY("🛕"),
    ROCK("🪨 "),
    ROADBORDER("");

    private String symbol;

    TerrainDisplay (String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
