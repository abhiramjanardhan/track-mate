package com.aj.trackmate.models.game;

public enum DownloadableContentType {
    STORY("Story"),
    MULTI_PLAYER("Multi Player"),
    COSMETICS("Cosmetics"),
    OTHER("Other");

    private final String dlcType;

    DownloadableContentType(String dlcType) {
        this.dlcType = dlcType;
    }

    public String getDLCType() {
        return this.dlcType;
    }

    public static DownloadableContentType fromType(String name) {
        for (DownloadableContentType downloadableContentType : values()) {
            if (downloadableContentType.getDLCType().equalsIgnoreCase(name)) {
                return downloadableContentType;
            }
        }
        throw new IllegalArgumentException("No enum constant with name: " + name);
    }
}
