package com.github.crazymax.crossfitreader.enums;

public enum CardUidLayoutEnum {
    SCAN_CARD(0, "scanCard"),
    RESULT(1, "result");
    
    private final int id;
    private final String name;
    
    private CardUidLayoutEnum(final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public static CardUidLayoutEnum find(final int id) {
        for (CardUidLayoutEnum scope : values()) {
            if (scope.getId() == id) {
                return scope;
            }
        }
        return null;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}
