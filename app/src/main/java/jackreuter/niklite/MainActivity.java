package jackreuter.niklite;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;

public class MainActivity extends Activity implements MenuFragment.MenuListener {

    View colorView;
    Button menuButton;
    VerticalSeekBar brightnessBar;
    FrameLayout menuFrame;

    MenuFragment menuFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private Handler strobeHandler;
    private Handler uiHandler;

    final int FADE_TIME = 3000;
    final int BUFFER_SIZE = 20;
    final int TEMPERATURE_MAX = 15000;
    final int TEMPERATURE_MIN = 1000;

    int globalColorInt;
    int rgbColorInt;
    int temperature;
    float maxX;
    float maxY;
    float shapeSize;
    int lightDuration;
    int darkDuration;
    Display mdisp;
    Point mdispSize;

    boolean kelvinMode;
    boolean lockMode;
    boolean shapeMode;
    boolean strobeMode;
    boolean isDark;
    boolean menuEnabled;

    int [] shapeIDs;
    int currentShapeIndex;

    private ScaleGestureDetector scaleDetector;
    private int brightness;

    /** Set up fullscreen, get display size and create views */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_main);

        colorView = findViewById(R.id.colorView);
        menuButton = (Button) findViewById(R.id.menuButton);
        menuButton.setBackgroundResource(R.drawable.ic_baseline_menu_24px);
        menuFrame = (FrameLayout) findViewById(R.id.menuFrame);
        brightnessBar = (VerticalSeekBar) findViewById(R.id.brightnessSeekBar);
        try {
            brightness = Settings.System.getInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        brightnessBar.setProgress(brightness);

        fragmentManager = getFragmentManager();

        setUIEnabled(false);
        kelvinMode = false;
        lockMode = false;
        strobeMode = false;
        isDark = false;
        menuEnabled = false;

        shapeIDs = new int[]{
                R.drawable.circle,
                R.drawable.ring,
        };
        currentShapeIndex = 0; //default to circle

        mdisp = getWindowManager().getDefaultDisplay();
        mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;
        shapeSize = maxX;
        if (maxY < maxX) { shapeSize = maxY; }
        shapeSize = shapeSize - BUFFER_SIZE;

        lightDuration = 500; //default strobe .5 second light
        darkDuration = 1000; //default strobe 1 second dark

        rgbColorInt = Color.WHITE; //default color WHITE
        temperature = 1000; //default temperature 1000K
        colorView.setBackgroundColor(rgbColorInt);

        scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        uiHandler = new Handler();

        // perform seek bar change listener event used for getting the progress value
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                // Get app context object.
                Context context = getApplicationContext();

                // Check whether has the write settings permission or not.
                boolean settingsCanWrite = hasWriteSettingsPermission(context);

                // If do not have then open the Can modify system settings panel.
                if(!settingsCanWrite) {
                    changeWriteSettingsPermission(context);
                } else {
                    changeScreenBrightness(context, value);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                uiHandler.removeCallbacksAndMessages(null);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                uiHandler.postDelayed(new Runnable() {
                    public void run() {
                        setUIEnabled(false);
                    }
                }, FADE_TIME);
            }
        });
    }

    /** Check whether this app has android write settings permission */
    private boolean hasWriteSettingsPermission(Context context)
    {
        boolean ret = true;
        // Get the result from below code.
        ret = Settings.System.canWrite(context);
        return ret;
    }

    /** Start can modify system settings panel to let user change the write settings permission */
    private void changeWriteSettingsPermission(Context context)
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);
    }

    /** https://www.dev2qa.com/android-change-screen-brightness-programmatically-example/ */
    private void changeScreenBrightness(Context context, int screenBrightnessValue)
    {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);

        /*
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = screenBrightnessValue / 255f;
        window.setAttributes(layoutParams);
        */
    }

    /** enables or disables brightness slider and menu button */
    public void setUIEnabled(Boolean bool) {
        menuButton.setEnabled(bool);
        brightnessBar.setEnabled(bool);

        if (bool) {
            menuButton.setVisibility(View.VISIBLE);
            brightnessBar.setVisibility(View.VISIBLE);
        } else {
            menuButton.setVisibility(View.INVISIBLE);
            brightnessBar.setVisibility(View.INVISIBLE);
        }
    }

    /** change color based on touch position */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (menuEnabled) {
            menuFragment.close();
        }
        scaleDetector.onTouchEvent(event);

        //enable and disable UI
        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_UP) {
            setUIEnabled(true);
            uiHandler.removeCallbacksAndMessages(null);
            uiHandler.postDelayed(new Runnable() {
                public void run() {
                    setUIEnabled(false);
                }
            }, FADE_TIME);
        }

        if (eventAction == MotionEvent.ACTION_DOWN) {
            setUIEnabled(false);
        }

        float X = event.getX();
        float Y = event.getY();

        if (lockMode) {}
        else {
            if (kelvinMode) {
                //kelvin mode
                temperature = getTemperatureFromY(Y);
                globalColorInt = getColorIntFromTemperature();
                colorBackground();
            } else {
                //color wheel mode
                float[] hsv = getHSVfromXY(X, Y);
                rgbColorInt = Color.HSVToColor(hsv);
                globalColorInt = rgbColorInt;
                colorBackground();
            }
        }
        return true;
    }

    /** creates background from global color int value */
    public void colorBackground() {
        //if shape mode enabled create the desired shape
        if (shapeMode) {
            drawShape(shapeIDs[currentShapeIndex], globalColorInt);
        } else {
            colorView.setBackgroundColor(globalColorInt);
        }
    }

    /** draws shape to colorView view */
    public void drawShape(int shapeId, int color) {

        Drawable colorShape = ResourcesCompat.getDrawable(getResources(), shapeId, null);
        if (colorShape instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) colorShape;
            shapeDrawable.getPaint().setColor(color);
        } else if (colorShape instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) colorShape;
            gradientDrawable.setColor(color);
        } else if (colorShape instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) colorShape;
            colorDrawable.setColor(color);
        }
        colorView.setBackgroundDrawable(colorShape);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (shapeMode) {
                float tmpSize = shapeSize * detector.getScaleFactor();

                // Don't let the object get too small or too large.
                if (tmpSize <= 100) {
                    tmpSize = 100;
                }

                if (tmpSize >= maxX) {
                    tmpSize = maxX;
                }

                if (tmpSize >= maxY) {
                    tmpSize = maxY;
                }

                shapeSize = tmpSize;
                colorView.requestLayout();
                colorView.getLayoutParams().height = (int) (shapeSize);
                colorView.getLayoutParams().width = (int) (shapeSize);
            }
            return true;
        }
    }

    /** start menu fragment */
    public void onClickMenu(View view) {
        setUIEnabled(false);
        menuFragment = MenuFragment.newInstance(
                kelvinMode,
                lockMode,
                shapeMode,
                strobeMode,
                lightDuration,
                darkDuration,
                Color.red(rgbColorInt),
                Color.green(rgbColorInt),
                Color.blue(rgbColorInt),
                temperature
        );
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.menuFrame, menuFragment);
        fragmentTransaction.commit();
        menuEnabled = true;
    }

    /** colorWheel logic for non-kelvin mode */
    public float[] getHSVfromXY(float X, float Y) {
        float midX = maxX / 2;
        float midY = maxY / 2;
        float angle;
        float xLim;
        float yLim;

        //find equation for line through point and center
        float m = (Y - midY) / (X - midX);
        float b = Y - (m * X);

        //find angle from center
        if (Y < midY) {
            if (X < midX) {
                //first quadrant
                angle = 360 - radToDegrees(Math.atan((midX - X) / (midY - Y)));

                //figure out endpoint of line through center
                if (b < 0) {
                    yLim = 0 + BUFFER_SIZE;
                    xLim = (yLim - b) / m;
                } else {
                    xLim = 0 + BUFFER_SIZE;
                    yLim = m * xLim + b;
                }
            } else {
                //second quadrant
                angle = radToDegrees(Math.atan((X - midX) / (midY - Y)));

                //figure out endpoint of line through center
                if (b > maxY) {
                    yLim = 0 + BUFFER_SIZE;
                    xLim = (yLim - b) / m;
                } else {
                    xLim = maxX - BUFFER_SIZE;
                    yLim = m * xLim + b;
                }

            }
        } else {
            if (X < midX) {
                //third quadrant
                angle = 180 + radToDegrees(Math.atan((midX - X) / (Y - midY)));

                //figure out endpoint of line through center
                if (b > maxY) {
                    yLim = maxY - BUFFER_SIZE;
                    xLim = (yLim - b) / m;
                } else {
                    xLim = 0 + BUFFER_SIZE;
                    yLim = m * xLim + b;
                }

            } else {
                //fourth quadrant
                angle = 180 - radToDegrees(Math.atan((midX - X) / (midY - Y)));

                //figure out endpoint of line through center
                if (b < 0) {
                    yLim = maxY - BUFFER_SIZE;
                    xLim = (yLim - b) / m;
                } else {
                    xLim = maxX - BUFFER_SIZE;
                    yLim = m * xLim + b;
                }

            }
        }

        //get saturation based on distance from center, populate textView
        float saturation = getSaturationValue(midX, midY, X, Y, xLim, yLim);
        float[] hsvArray = {angle, saturation, 1};
        return hsvArray;
    }

    /** convert angle from radians to degrees */
    public float radToDegrees(double a) {
        return (float) (a * 180 / Math.PI);
    }

    /** get distance between two points */
    public float getDistance(float x1, float y1, float x2, float y2) {
        return (float) (Math.hypot(x1-x2, y1-y2));
    }

    /** convert distance from center to ratio between 0 and 1 based on distance from edge*/
    public float getSaturationValue(float x1, float y1,
                                    float x2, float y2,
                                    float x3, float y3) {
        //buffer around center and outside
        if (getDistance(x2, y2, x1, y1) < BUFFER_SIZE) {
            return 0;
        } else if (getDistance(x2, y2, x3, y3) < BUFFER_SIZE) {
            return 1;
        } else {
            return getDistance(x1, y1, x2, y2) / getDistance(x1, y1, x3, y3);
        }
    }

    /** converts kelvin temperature into color */
    public int getColorIntFromTemperature() {
        float tmp = (float) (temperature/100.0);
        int redValue = getRedValueFromTemperature(tmp);
        int greenValue = getGreenValueFromTemperature(tmp);
        int blueValue = getBlueValueFromTemperature(tmp);
        return Color.rgb(redValue, greenValue, blueValue);
    }

    /** calculate temperature based on y value*/
    public int getTemperatureFromY(float yVal) {
        if (yVal <= 5) {
            return TEMPERATURE_MAX;
        } else if (maxY - yVal <= 5) {
            return TEMPERATURE_MIN;
        } else {
            return (int) ((((maxY - yVal) / maxY) * (TEMPERATURE_MAX - TEMPERATURE_MIN)) + TEMPERATURE_MIN);
        }
    }

    /** calculate red value based on temperature from
     * http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
     * updated to http://www.zombieprototypes.com/?p=210*/
    public int getRedValueFromTemperature(float tVal) {
        if (tVal <= 66) {
            return 255;
        } else {
            //𝑎+𝑏𝑥+𝑐𝑙𝑛(𝑥)
            double a = 351.97690566805693;
            double b = 0.114206453784165;
            double c = -40.25366309332127;
            double x = tVal - 55;

            double red = a + (b * x) + (c * Math.log(x));
            if (red < 0) { red = 0; }
            if (red > 255) { red = 255; }
            return (int) red;
        }
    }

    /** calculate green value based on temperature from
     * http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
     * updated to http://www.zombieprototypes.com/?p=210 */
    public int getGreenValueFromTemperature(float tVal) {
        double green;
        if (tVal <= 66) {
            //𝑎+𝑏𝑥+𝑐𝑙𝑛(𝑥)
            double a = -155.25485562709179;
            double b = -0.44596950469579133;
            double c = 104.49216199393888;
            double x = tVal - 2;
            green = a + (b * x) + (c * Math.log(x));
        } else {
            //𝑎+𝑏𝑥+𝑐𝑙𝑛(𝑥)
            double a = 325.4494125711974;
            double b = 0.07943456536662342;
            double c = -28.0852963507957;
            double x = tVal - 50;
            green = a + (b * x) + (c * Math.log(x));
        }
        if (green < 0) { green = 0; }
        if (green > 255) { green = 255; }
        return (int) green;
    }

    /** calculate blue value based on temperature from
     * http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
     * updated to http://www.zombieprototypes.com/?p=210 */
    public int getBlueValueFromTemperature(float tVal) {
        if (tVal >= 66) {
            return 255;
        } else if (tVal <= 20) {
            return 0;
        } else {
            //𝑎+𝑏𝑥+𝑐𝑙𝑛(𝑥)
            double a = -254.76935184120902;
            double b = 0.8274096064007395;
            double c = 115.67994401066147;
            double x = tVal - 10;
            double blue = a + (b * x) + (c * Math.log(x));
            if (blue < 0) { blue = 0; }
            if (blue > 255) { blue = 255; }
            return (int) blue;
        }
    }

    @Override
    public void onKelvinButtonClick() {
        kelvinMode = !kelvinMode;
        if (kelvinMode) {
            globalColorInt = getColorIntFromTemperature();
        } else {
            globalColorInt = rgbColorInt;
        }
        colorBackground();
    }

    @Override
    public void onLockButtonClick() {
        lockMode = !lockMode;
    }

    @Override
    public void onShapeButtonClick() {
        shapeMode = !shapeMode;

        if (shapeMode) {
            colorView.requestLayout();
            colorView.getLayoutParams().height = (int) (shapeSize);
            colorView.getLayoutParams().width = (int) (shapeSize);

        } else {
            colorView.requestLayout();
            colorView.getLayoutParams().height = (int) (maxY);
            colorView.getLayoutParams().width = (int) (maxX);
        }
        colorBackground();

    }

    @Override
    public void onStrobeButtonClick() {
        strobeMode = !strobeMode;
        if (strobeMode) {
            strobeHandler = new Handler();
            startStrobe();
        } else {
            stopStrobe();
            if (isDark) {
                colorBackground();
            }
        }
    }

    @Override
    public void onLightDurationSeekBarChanged(int seekBarValue) {
        lightDuration = seekBarValue;
    }

    @Override
    public void onDarkDurationSeekBarChanged(int seekBarValue) {
        darkDuration = seekBarValue;
    }

    @Override
    public void onRedSeekBarChanged(int seekBarValue) {
        int redValue = seekBarValue;
        rgbColorInt = Color.rgb(redValue, Color.green(rgbColorInt), Color.blue(rgbColorInt));
        globalColorInt = rgbColorInt;
        colorBackground();
    }

    @Override
    public void onGreenSeekBarChanged(int seekBarValue) {
        int greenValue = seekBarValue;
        rgbColorInt = Color.rgb(Color.red(rgbColorInt), greenValue, Color.blue(rgbColorInt));
        globalColorInt = rgbColorInt;
        colorBackground();
    }

    @Override
    public void onBlueSeekBarChanged(int seekBarValue) {
        int blueValue = seekBarValue;
        rgbColorInt = Color.rgb(Color.red(rgbColorInt), Color.green(rgbColorInt), blueValue);
        globalColorInt = rgbColorInt;
        colorBackground();
    }

    @Override
    public void onKelvinSeekBarChanged(int seekBarValue) {
        temperature = seekBarValue;
        globalColorInt = getColorIntFromTemperature();
        colorBackground();
    }

    /** define strobe light method */
    Runnable runStrobe = new Runnable() {
        @Override
        public void run() {
            try {
                if (isDark) {
                    colorBackground();
                } else {
                    colorView.setBackgroundColor(Color.rgb(0, 0, 0));
                }
             } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                if (isDark) {
                    strobeHandler.postDelayed(runStrobe, lightDuration);
                } else {
                    strobeHandler.postDelayed(runStrobe, darkDuration);
                }
                isDark = !isDark;
            }
        }
    };

    /** start strobe */
    void startStrobe() {
        runStrobe.run();
    }

    /** stop strobe */
    void stopStrobe() {
        strobeHandler.removeCallbacks(runStrobe);
    }

    @Override
    public void onClose() {
        //kill menu fragment
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
    }
}