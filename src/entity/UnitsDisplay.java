package entity;
public enum UnitsDisplay {
    OURTIER1("🥢"),
    OURTIER2("🏹"),
    OURTIER3("🔪"),
    OURTIER4("🏇"),
    OURTIER5("🤺"),
    ENEMYTIER1("🥉"),
    ENEMYTIER2("🥈"),
    ENEMYTIER3("🥇 "),
    ENEMYTIER4("🏵️ "),
    ENEMYTIER5("🏆");

    private String symbol;

    UnitsDisplay (String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
