package com.aj.trackmate.models.game;

public enum GamePurchaseType {
    PHYSICAL("Physical"),
    DIGITAL("Digital"),
    NOT_DECIDED("Not Decided");

    private final String purchaseType;

    GamePurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getPurchaseType() {
        return this.purchaseType;
    }

    public static GamePurchaseType fromType(String name) {
        for (GamePurchaseType gamePurchaseType : values()) {
            if (gamePurchaseType.getPurchaseType().equalsIgnoreCase(name)) {
                return gamePurchaseType;
            }
        }
        throw new IllegalArgumentException("No enum constant with name: " + name);
    }
}
