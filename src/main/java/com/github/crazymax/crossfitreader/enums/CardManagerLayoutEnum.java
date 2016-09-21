package com.github.crazymax.crossfitreader.enums;

public enum CardManagerLayoutEnum {
    INTRO(0, "intro"),
    SEARCH_USERS(1, "searchUsers"),
    SELECT_USER(2, "selectUser"),
    SCAN_CARD(3, "scanCard");
    
    private final int id;
    private final String name;
    
    private CardManagerLayoutEnum(final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public static CardManagerLayoutEnum find(final int id) {
        for (CardManagerLayoutEnum scope : values()) {
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
