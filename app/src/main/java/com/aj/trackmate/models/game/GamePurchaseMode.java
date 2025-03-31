package com.aj.trackmate.models.game;

public enum GamePurchaseMode {
    PURCHASE("Purchase"),
    SUBSCRIPTION("Subscription"),
    NOT_YET("Not Yet");

    private final String purchaseMode;

    GamePurchaseMode(String purchaseMode) {
        this.purchaseMode = purchaseMode;
    }

    public String getPurchaseMode() {
        return this.purchaseMode;
    }

    public static GamePurchaseMode fromMode(String name) {
        for (GamePurchaseMode gamePurchaseMode : values()) {
            if (gamePurchaseMode.getPurchaseMode().equalsIgnoreCase(name)) {
                return gamePurchaseMode;
            }
        }
        throw new IllegalArgumentException("No enum constant with name: " + name);
    }
}
