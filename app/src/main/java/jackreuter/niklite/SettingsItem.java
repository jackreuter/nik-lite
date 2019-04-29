package jackreuter.niklite;

public class SettingsItem {

    String name;
    int globalColorInt;
    int rgbColorInt;
    int temperature;
    int lightDuration;
    int darkDuration;
    float shapeSize;
    int shapeIndex;
    boolean kelvinMode;
    boolean lockMode;
    boolean strobeMode;

    public SettingsItem(String n,
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

    public int getGlobalColorInt() {
        return globalColorInt;
    }

    public int getRgbColorInt() {
        return rgbColorInt;
    }

    public int getTemperature() {
        return  temperature;
    }

    public int getLightDuration() {
        return lightDuration;
    }

    public int getDarkDuration() {
        return  darkDuration;
    }

    public float getShapeSize() {
        return shapeSize;
    }

    public int getShapeIndex() { return shapeIndex; }

    public boolean getKelvinMode() {
        return kelvinMode;
    }

    public boolean getLockMode() {
        return lockMode;
    }

    public boolean getStrobeMode() {
        return strobeMode;
    }
}
