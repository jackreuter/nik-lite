package com.cottagestudios.lighttool;

public class SettingsItem {

    private String name;
    private int globalColorInt;
    private int rgbColorInt;
    private int temperature;
    private int lightDuration;
    private int darkDuration;
    private float shapeSize;
    private int shapeIndex;
    private boolean kelvinMode;
    private boolean lockMode;
    private boolean strobeMode;

    SettingsItem(String n,
                        int global,
                        int rgb,
                        int temp,
                        int light,
                        int dark,
                        float shapeFloat,
                        int shapeInt,
                        boolean kelvin,
                        boolean lock,
                        boolean strobe) {
        name = n;
        globalColorInt = global;
        rgbColorInt = rgb;
        temperature = temp;
        lightDuration = light;
        darkDuration = dark;
        shapeSize = shapeFloat;
        shapeIndex = shapeInt;
        kelvinMode = kelvin;
        lockMode = lock;
        strobeMode = strobe;
    }

    public String getName() {
        return name;
    }

    int getGlobalColorInt() {
        return globalColorInt;
    }

    int getRgbColorInt() {
        return rgbColorInt;
    }

    int getTemperature() {
        return  temperature;
    }

    int getLightDuration() {
        return lightDuration;
    }

    int getDarkDuration() {
        return  darkDuration;
    }

    float getShapeSize() {
        return shapeSize;
    }

    int getShapeIndex() { return shapeIndex; }

    boolean getKelvinMode() {
        return kelvinMode;
    }

    boolean getLockMode() {
        return lockMode;
    }

    boolean getStrobeMode() {
        return strobeMode;
    }
}
