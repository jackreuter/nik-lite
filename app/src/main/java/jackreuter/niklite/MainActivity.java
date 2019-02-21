package jackreuter.niklite;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    View colorView;
    TextView textView;
    int colorInt;
    int redValue;
    int greenValue;
    int blueValue;
    float maxX;
    float maxY;
    Display mdisp;
    Point mdispSize;
    int bufferSize;

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

        mdisp = getWindowManager().getDefaultDisplay();
        mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;

        //distance from edge of screen to get color value of 0
        bufferSize = 5;

        redValue = 255;
        greenValue = 255;
        blueValue = 255;
        colorView.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float X = event.getX();
        float Y = event.getY();
        float midX = maxX / 2;
        float midY = maxY / 2;
        float angle;
        float xLim;
        float yLim;

        //find equation for line through point and center
        float m = (Y - midY)/(X - midX);
        float b = Y - (m * X);

        //find angle from center
        if (Y < midY) {
            if (X < midX) {
                //first quadrant
                angle = 360 - radToDegrees(Math.atan((midX - X) / (midY - Y)));

                //figure out endpoint of line through center
                if (b < 0) {
                    yLim = 0;
                    xLim = (yLim - b) / m;
                } else {
                    xLim = 0;
                    yLim = m * xLim + b;
                }
            } else {
                //second quadrant
                angle = radToDegrees(Math.atan((X - midX) / (midY - Y)));

                //figure out endpoint of line through center
                if (b > maxY) {
                    yLim = 0;
                    xLim = (yLim - b) / m;
                } else {
                    xLim = maxX;
                    yLim = m * xLim + b;
                }

            }
        } else {
            if (X < midX) {
                //third quadrant
                angle = 180 + radToDegrees(Math.atan((midX - X) / (Y - midY)));

                //figure out endpoint of line through center
                if (b > maxY) {
                    yLim = maxY;
                    xLim = (yLim - b) / m;
                } else {
                    xLim = 0;
                    yLim = m * xLim + b;
                }

            } else {
                //fourth quadrant
                angle = 180 - radToDegrees(Math.atan((midX - X) / (midY - Y)));

                //figure out endpoint of line through center
                if (b < 0) {
                    yLim = maxY;
                    xLim = (yLim - b) / m;
                } else {
                    xLim = maxX;
                    yLim = m * xLim + b;
                }

            }
        }

        float saturation = getSaturationValue(midX, midY, X, Y, xLim, yLim);
        float[] hsvArray = {angle, saturation, 1};
        colorInt = Color.HSVToColor(hsvArray);
        colorView.setBackgroundColor(colorInt);
        textView.setText("R: "+Color.red(colorInt) +
                "\nG: "+Color.green(colorInt) +
                "\nB: "+Color.blue(colorInt));

        return true;
    }

    //convert angle from radians to degrees
    public float radToDegrees(double a) {
        return (float) (a * 180 / Math.PI);
    }

    //distance between two points
    public float getDistance(float x1, float y1, float x2, float y2) {
        return (float) (Math.hypot(x1-x2, y1-y2));
    }

    //convert distance from center to ratio between 0 and 1
    public float getSaturationValue(float x1, float y1,
                                    float x2, float y2,
                                    float x3, float y3) {
        return getDistance(x1, y1, x2, y2) / getDistance(x1, y1, x3, y3);
    }

}