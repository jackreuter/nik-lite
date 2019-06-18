package com.cottagestudios.lighttool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MainActivity extends FragmentActivity implements MenuFragment.MenuListener, FileListFragment.OnListFragmentInteractionListener {

    ImageView colorView;
    FrameLayout colorFrame;
    Button menuButton;
    VerticalSeekBar brightnessBar;

    FrameLayout menuFrame;
    MenuFragment menuFragment;
    FileListFragment fileListFragment;
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
    int lightDuration;
    int darkDuration;
    float shapeSize;

    float maxX;
    float maxY;
    Display mdisp;
    Point mdispSize;

    boolean kelvinMode, lockMode, strobeMode, isDark, menuEnabled, fileListEnabled;

    int [] shapeIDs;
    int currentShapeIndex;

    public static final int FULLSCREEN_INDEX = 0;
    public static final int CIRCLE_INDEX = 1;
    public static final int STAR_INDEX = 2;
    public static final int HEART_INDEX = 3;
    public static final int PLUS_INDEX = 4;
    public static final int MIN_SHAPE_HEIGHT = 100;
    public static final int MIN_SHAPE_WIDTH = 100;

    private ScaleGestureDetector scaleDetector;
    private int brightness;

    /** save state in case screen is rotated */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt("globalColorInt", globalColorInt);
        savedInstanceState.putInt("rgbColorInt", rgbColorInt);
        savedInstanceState.putInt("temperature", temperature);
        savedInstanceState.putInt("lightDuration", lightDuration);
        savedInstanceState.putInt("darkDuration", darkDuration);
        savedInstanceState.putFloat("shapeSize", shapeSize);
        savedInstanceState.putInt("shapeIndex", currentShapeIndex);

        savedInstanceState.putBoolean("kelvinMode", kelvinMode);
        savedInstanceState.putBoolean("lockMode", lockMode);
        savedInstanceState.putBoolean("strobeMode", strobeMode);

        savedInstanceState.putBoolean("menuEnabled", menuEnabled);
        savedInstanceState.putBoolean("fileListEnabled", fileListEnabled);

        //declare values before saving the state
        super.onSaveInstanceState(savedInstanceState);
    }

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

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        colorView = findViewById(R.id.colorView);
        colorFrame = findViewById(R.id.colorFrame);
        menuButton = findViewById(R.id.menuButton);
        menuButton.setBackgroundResource(R.drawable.ic_baseline_menu_24px);
        menuFrame = findViewById(R.id.menuFrame);

        brightnessBar = findViewById(R.id.brightnessSeekBar);
        try {
            brightness = Settings.System.getInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        brightnessBar.setProgress(brightness);

        fragmentManager = getSupportFragmentManager();
        setUIEnabled(false);
        mdisp = getWindowManager().getDefaultDisplay();
        mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;

        // set up menu frame
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) menuFrame.getLayoutParams();
        params.setMargins((int) maxX/10, (int) maxY/10, (int) maxX/10, (int) maxY/10);
        menuFrame.setLayoutParams(params);

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

        //check for saved instance state e.g. if screen rotated
        if (savedInstanceState != null){
            globalColorInt = savedInstanceState.getInt("globalColorInt");
            rgbColorInt = savedInstanceState.getInt("rgbColorInt");
            temperature = savedInstanceState.getInt("temperature");
            lightDuration = savedInstanceState.getInt("lightDuration");
            darkDuration = savedInstanceState.getInt("darkDuration");
            shapeSize = savedInstanceState.getFloat("shapeSize");
            currentShapeIndex = savedInstanceState.getInt("shapeIndex");

            kelvinMode = savedInstanceState.getBoolean("kelvinMode");
            lockMode = savedInstanceState.getBoolean("lockMode");
            strobeMode = savedInstanceState.getBoolean("strobeMode");

            menuEnabled = savedInstanceState.getBoolean("menuEnabled");
            fileListEnabled = savedInstanceState.getBoolean("fileListEnabled");
        } else {
            globalColorInt = Color.WHITE; //default color WHITE
            rgbColorInt = Color.WHITE; //default color WHITE
            temperature = 1500; //default temperature 1500K
            lightDuration = 500; //default strobe .5 second light
            darkDuration = 1000; //default strobe 1 second dark

            //default shape size smaller of height or width, minus buffer
            shapeSize = maxX;
            if (maxY < maxX) { shapeSize = maxY; }
            shapeSize = shapeSize - BUFFER_SIZE;
            currentShapeIndex = 0; //default to circle

            kelvinMode = false;
            lockMode = false;
            strobeMode = false;

            menuEnabled = false;
            fileListEnabled = false;
        }

        isDark = false;
        shapeIDs = new int[]{
                R.drawable.square_selected,
                R.drawable.circle_24px,
                R.drawable.star_24px,
                R.drawable.heart__24px,
                R.drawable.plus_24px,
        };

        if (currentShapeIndex > 0) {
            colorView.setBackgroundColor(Color.BLACK);
            colorView.setImageResource(shapeIDs[currentShapeIndex]);
        }
        colorBackground();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }
    }

    /** Check whether this app has android write settings permission */
    private boolean hasWriteSettingsPermission(Context context)
    {
        boolean ret;
        // Get the result from below code.
        ret = Settings.System.canWrite(context);
        return ret;
    }

    /** Start can modify system settings panel to let user change the write settings permission */
    private void changeWriteSettingsPermission(Context context)
    {
        Toast.makeText(this, "Light panel needs permission to change screen brightness", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            killMenuFragment();
        }

        if (fileListEnabled) {
            killFileListFragment();
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

        if (!lockMode && event.getPointerCount() == 1) {
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
        if (currentShapeIndex > 0) {
            colorView.setColorFilter(globalColorInt);
        } else {
            colorFrame.setBackgroundColor(globalColorInt);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (currentShapeIndex > 0) {
                float tmpSize = shapeSize * detector.getScaleFactor();

                // Don't let the object get too small or too large.
                if (tmpSize <= MIN_SHAPE_HEIGHT) {
                    tmpSize = MIN_SHAPE_WIDTH;
                }

                if (tmpSize >= 15 * maxX) {
                    tmpSize = 15 * maxX;
                }

                if (tmpSize >= 15 * maxY) {
                    tmpSize = 15 * maxY;
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
                currentShapeIndex,
                strobeMode,
                lightDuration,
                darkDuration,
                Color.red(rgbColorInt),
                Color.green(rgbColorInt),
                Color.blue(rgbColorInt),
                temperature
        );
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menuFrame, menuFragment, "menuFragment");
        fragmentTransaction.commit();
        menuEnabled = true;
    }

    /** go back to settings menu from file list menu */
    public void onClickBack(View view) {
        setUIEnabled(false);
        menuFragment = MenuFragment.newInstance(
                kelvinMode,
                lockMode,
                currentShapeIndex,
                strobeMode,
                lightDuration,
                darkDuration,
                Color.red(rgbColorInt),
                Color.green(rgbColorInt),
                Color.blue(rgbColorInt),
                temperature
        );
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menuFrame, menuFragment, "menuFragment");
        fragmentTransaction.commit();
        menuEnabled = true;
        fileListEnabled = false;
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
                    yLim = BUFFER_SIZE;
                    xLim = (yLim - b) / m;
                } else {
                    xLim =BUFFER_SIZE;
                    yLim = m * xLim + b;
                }
            } else {
                //second quadrant
                angle = radToDegrees(Math.atan((X - midX) / (midY - Y)));

                //figure out endpoint of line through center
                if (b > maxY) {
                    yLim = BUFFER_SIZE;
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
                    xLim = BUFFER_SIZE;
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
        return new float[]{angle, saturation, 1};
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
    public void onOpenButtonClick () {
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        ArrayList<String> filenames = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            filenames.add(entry.getKey());
        }
        Collections.sort(filenames);
        fileListEnabled = true;
        menuEnabled = false;
        fileListFragment = FileListFragment.newInstance(filenames);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menuFrame, fileListFragment, "fileListFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveButtonClick() {
        // get save_prompt.xml view
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        @SuppressLint("InflateParams") View savePromptView = li.inflate(R.layout.save_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set save_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(savePromptView);

        final EditText userInput = savePromptView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                //.setCancelable(false)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // data validation occurs in CustomListener class
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        // custom listener to validate user input
        class CustomListener implements View.OnClickListener {
            private final Dialog dialog;
            private CustomListener(Dialog dialog) {
                this.dialog = dialog;
            }
            @Override
            public void onClick(View v) {
                String input = userInput.getText().toString();

                // user input validation
                // filename cannot be blank
                if (input.equals("")) {
                    Toast.makeText(MainActivity.this, "Must enter a name", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    Map<String, ?> allEntries = sharedPref.getAll();
                    boolean duplicateFound = false;
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        if (entry.getKey().equals(input)) {
                            duplicateFound = true;
                            break;
                        }
                    }

                    // filename must be unique
                    if (duplicateFound) {
                        Toast.makeText(MainActivity.this, "A preset with that name already exists", Toast.LENGTH_SHORT).show();

                    } else {
                        SettingsItem currentSettings = new SettingsItem(
                                input,
                                globalColorInt,
                                rgbColorInt,
                                temperature,
                                lightDuration,
                                darkDuration,
                                shapeSize,
                                currentShapeIndex,
                                kelvinMode,
                                true,
                                strobeMode
                        );
                        SharedPreferences.Editor editor = sharedPref.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(currentSettings);
                        editor.putString(input, json);
                        editor.apply();
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Preset saved", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        Button confirmSaveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        confirmSaveButton.setOnClickListener(new CustomListener(alertDialog));
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
    public void onFullscreenButtonClick() {
        currentShapeIndex = FULLSCREEN_INDEX;
        colorView.setVisibility(View.INVISIBLE);
        colorBackground();
    }

    @Override
    public void onCircleButtonClick() {
        currentShapeIndex = CIRCLE_INDEX;
        colorView.setVisibility(View.VISIBLE);
        colorFrame.setBackgroundColor(Color.BLACK);
        colorView.setImageResource(shapeIDs[currentShapeIndex]);
        colorBackground();
    }

    @Override
    public void onStarButtonClick() {
        currentShapeIndex = STAR_INDEX;
        colorView.setVisibility(View.VISIBLE);
        colorFrame.setBackgroundColor(Color.BLACK);
        colorView.setImageResource(shapeIDs[currentShapeIndex]);
        colorBackground();
    }

    @Override
    public void onHeartButtonClick() {
        currentShapeIndex = HEART_INDEX;
        colorView.setVisibility(View.VISIBLE);
        colorFrame.setBackgroundColor(Color.BLACK);
        colorView.setImageResource(shapeIDs[currentShapeIndex]);
        colorBackground();
    }

    @Override
    public void onPlusButtonClick() {
        currentShapeIndex = PLUS_INDEX;
        colorView.setVisibility(View.VISIBLE);
        colorFrame.setBackgroundColor(Color.BLACK);
        colorView.setImageResource(shapeIDs[currentShapeIndex]);
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
        rgbColorInt = Color.rgb(seekBarValue, Color.green(rgbColorInt), Color.blue(rgbColorInt));
        globalColorInt = rgbColorInt;
        colorBackground();
    }

    @Override
    public void onGreenSeekBarChanged(int seekBarValue) {
        rgbColorInt = Color.rgb(Color.red(rgbColorInt), seekBarValue, Color.blue(rgbColorInt));
        globalColorInt = rgbColorInt;
        colorBackground();
    }

    @Override
    public void onBlueSeekBarChanged(int seekBarValue) {
        rgbColorInt = Color.rgb(Color.red(rgbColorInt), Color.green(rgbColorInt), seekBarValue);
        globalColorInt = rgbColorInt;
        colorBackground();
    }

    @Override
    public void onKelvinSeekBarChanged(int seekBarValue) {
        temperature = seekBarValue;
        globalColorInt = getColorIntFromTemperature();
        colorBackground();
    }

    @Override
    public void onHugeMistakeMade() {
        rgbColorInt = Color.WHITE;
        globalColorInt = rgbColorInt;
        colorBackground();
    }

    /** delete the selected save file */
    @Override
    public void onDeleteButtonClicked(final String item) {
        // get delete_prompt.xml view
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        @SuppressLint("InflateParams") View deletePromptView = li.inflate(R.layout.delete_prompt, null);
        TextView deletePromptText = deletePromptView.findViewById(R.id.textView1);
        deletePromptText.setText(String.format(getString(R.string.delete), item));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set save_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(deletePromptView);

        // set dialog message
        alertDialogBuilder
                //.setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove(item);
                                editor.apply();
                                killFileListFragment();
                                onOpenButtonClick();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /** open selected save file */
    @Override
    public void onItemClicked(String item) {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        boolean strobeWasRunning = strobeMode;

        Gson gson = new Gson();
        String json = sharedPref.getString(item, "");
        SettingsItem settings = gson.fromJson(json, SettingsItem.class);
        globalColorInt = settings.getGlobalColorInt();
        rgbColorInt = settings.getRgbColorInt();
        temperature = settings.getTemperature();
        lightDuration = settings.getLightDuration();
        darkDuration = settings.getDarkDuration();
        shapeSize = settings.getShapeSize();
        currentShapeIndex = settings.getShapeIndex();
        kelvinMode = settings.getKelvinMode();
        lockMode = settings.getLockMode();
        strobeMode = settings.getStrobeMode();

        if (strobeMode) {
            startStrobe();
        } else {
            if (strobeWasRunning) {
                stopStrobe();
            }
        }

        colorView.requestLayout();
        colorView.getLayoutParams().height = (int) (shapeSize);
        colorView.getLayoutParams().width = (int) (shapeSize);

        if (currentShapeIndex > 0) {
            colorFrame.setBackgroundColor(Color.BLACK);
            colorView.setImageResource(shapeIDs[currentShapeIndex]);
            colorView.setVisibility(View.VISIBLE);
        } else {
            colorView.setVisibility(View.INVISIBLE);
        }

        colorBackground();
        killFileListFragment();
    }

    /** define strobe light method */
    Runnable runStrobe = new Runnable() {
        @Override
        public void run() {
            try {
                if (isDark) {
                    colorBackground();
                } else {
                    if (currentShapeIndex > 0) {
                        colorView.setColorFilter(Color.BLACK);
                    } else {
                        colorFrame.setBackgroundColor(Color.BLACK);
                    }
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

    /** called when activity starts */
    @Override
    protected void onStart() {
        super.onStart();
        if (strobeMode) {
            startStrobe();
        }
    }

    /** called when activity stops */
    @Override
    protected void onStop() {
        super.onStop();
        if (strobeMode) {
            stopStrobe();
        }
    }

    /** start strobe */
    public void startStrobe() {
        strobeHandler = new Handler();
        runStrobe.run();
    }

    /** stop strobe */
    public void stopStrobe() {
        strobeHandler.removeCallbacks(runStrobe);
    }

    /** close menu fragment */
    public void killMenuFragment() {
        menuEnabled = false;
        if (menuFragment == null) {
            menuFragment = (MenuFragment) fragmentManager.findFragmentByTag("menuFragment");
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
    }

    /** close file list fragment */
    public void killFileListFragment() {
        fileListEnabled = false;
        if (fileListFragment == null) {
            fileListFragment = (FileListFragment) fragmentManager.findFragmentByTag("fileListFragment");
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fileListFragment);
        fragmentTransaction.commit();
    }
}