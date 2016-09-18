package com.github.crazymax.crossfitreader.enums;

public enum CardScanTypeEnum {
    AUTH(0),
    ASSOCIATE(1),
    REMOVE(2);
    
    private final int id;
    
    private CardScanTypeEnum(final int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
}
