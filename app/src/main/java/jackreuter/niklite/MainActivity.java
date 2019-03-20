package jackreuter.niklite;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Fragment;

public class MainActivity extends Activity implements MenuFragment.MenuListener {

    View colorView;
    TextView textView;
    Button menuButton;
    FrameLayout menuFrame;

    MenuFragment menuFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    final int FADE_TIME = 3000;
    final int BUFFER_SIZE = 20;
    final int TEMPERATURE_MAX = 15000;
    final int TEMPERATURE_MIN = 1000;

    int colorInt;
    int redValue;
    int greenValue;
    int blueValue;
    float maxX;
    float maxY;
    float shapeSize;
    Display mdisp;
    Point mdispSize;

    boolean kelvinMode;
    boolean lockMode;
    boolean shapeMode;
    boolean strobeMode;
    boolean menuEnabled;

    private ScaleGestureDetector scaleDetector;

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
        textView = (TextView) findViewById(R.id.textView);
        menuButton = (Button) findViewById(R.id.menuButton);
        menuFrame = (FrameLayout) findViewById(R.id.menuFrame);

        fragmentManager = getFragmentManager();

        setUIEnabled(false);
        kelvinMode = false;
        lockMode = false;
        strobeMode = false;
        menuEnabled = false;

        mdisp = getWindowManager().getDefaultDisplay();
        mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;
        shapeSize = maxX;
        if (maxY < maxX) { shapeSize = maxY; }
        shapeSize = shapeSize - BUFFER_SIZE;

        redValue = 255;
        greenValue = 255;
        blueValue = 255;
        colorView.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));

        scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    /** enables or disables textView and menuButton */
    public void setUIEnabled(Boolean bool) {
        menuButton.setEnabled(bool);
        if (bool) {
            menuButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            menuButton.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
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
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    setUIEnabled(false);
                }
            }, FADE_TIME);
        } else {
            setUIEnabled(true);
        }

        float X = event.getX();
        float Y = event.getY();

        if (lockMode) {}
        else {
            if (kelvinMode) {
                //kelvin mode
                float temperature = getTemperatureFromY(Y);
                redValue = getRedValueFromTemperature(temperature/100);
                greenValue = getGreenValueFromTemperature(temperature/100);
                blueValue = getBlueValueFromTemperature(temperature/100);
                colorInt = Color.rgb(redValue, greenValue, blueValue);

                //if shape mode enable create the desired shape
                if (shapeMode) {
                    drawShape(R.drawable.circle);
                } else {
                    colorView.setBackgroundColor(colorInt);
                }

                textView.setText((int) temperature + "K\n" +
                        "R: " + Color.red(colorInt) +
                        "\nG: " + Color.green(colorInt) +
                        "\nB: " + Color.blue(colorInt));
            } else {
                //color wheel mode
                float[] hsv = getHSVfromXY(X, Y);
                colorInt = Color.HSVToColor(hsv);

                //if shape mode enabled create the desired shape
                if (shapeMode) {
                    drawShape(R.drawable.circle);
                } else {
                    colorView.setBackgroundColor(colorInt);
                }
                textView.setText("R: " + Color.red(colorInt) +
                        "\nG: " + Color.green(colorInt) +
                        "\nB: " + Color.blue(colorInt));
            }
        }
        return true;
    }

    /** draws shape to colorView view */
    public void drawShape(int shapeId) {
        Drawable colorShape = ResourcesCompat.getDrawable(getResources(), shapeId, null);
        if (colorShape instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) colorShape;
            shapeDrawable.getPaint().setColor(colorInt);
        } else if (colorShape instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) colorShape;
            gradientDrawable.setColor(colorInt);
        } else if (colorShape instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) colorShape;
            colorDrawable.setColor(colorInt);
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
        menuFragment = MenuFragment.newInstance(kelvinMode, lockMode, shapeMode, strobeMode);
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

    /** calculate temperature based on y value*/
    public float getTemperatureFromY(float yVal) {
        return ((yVal / maxY) * TEMPERATURE_MAX) + TEMPERATURE_MIN;
    }

    /** calculate red value based on temperature from
     * http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/ */
    public int getRedValueFromTemperature(float tVal) {
        if (tVal <= 66) {
            return 255;
        } else {
            float red = tVal - 60;
            red = (float) (329.698727446 * (Math.pow(red,-0.1332047592)));
            if (red < 0) { red = 0; }
            if (red > 255) { red = 255; }
            return (int) red;
        }
    }

    /** calculate green value based on temperature from
     * http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/ */
    public int getGreenValueFromTemperature(float tVal) {
        float green;
        if (tVal <= 66) {
            green = tVal;
            green = (float) (99.4708025861 * Math.log(green) - 161.1195681661);
        } else {
            green = tVal - 60;
            green = (float) (288.1221695283 * Math.pow(green, -0.0755148492));
        }
        if (green < 0) { green = 0; }
        if (green > 255) { green = 255; }
        return (int) green;
    }

    /** calculate blue value based on temperature from
     * http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/ */
    public int getBlueValueFromTemperature(float tVal) {
        if (tVal >= 66) {
            return 255;
        } else if (tVal <= 19) {
            return 0;
        } else {
            float blue = tVal - 10;
            blue = (float) (138.5177312231 * Math.log(blue) - 305.0447927307);
            if (blue < 0) { blue = 0; }
            if (blue > 255) { blue = 255; }
            return (int) blue;
        }
    }

    @Override
    public void onKelvinButtonClick() {
        kelvinMode = !kelvinMode;

        /**
        //kill menu fragment
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
         */
    }

    @Override
    public void onLockButtonClick() {
        lockMode = !lockMode;

        /**
        //kill menu fragment
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
         */
    }

    @Override
    public void onShapeButtonClick() {
        shapeMode = !shapeMode;

        //circle shape
        if (shapeMode) {
            drawShape(R.drawable.circle);
            colorView.requestLayout();
            colorView.getLayoutParams().height = (int) (shapeSize);
            colorView.getLayoutParams().width = (int) (shapeSize);

        } else {
            colorView.requestLayout();
            colorView.getLayoutParams().height = (int) (maxY);
            colorView.getLayoutParams().width = (int) (maxX);
            colorView.setBackgroundColor(colorInt);
        }

        /**
        //kill menu fragment
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
         */
    }

    @Override
    public void onStrobeButtonClick() {
        strobeMode = !strobeMode;
    }

    @Override
    public void onClose() {
        //kill menu fragment
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
    }
}