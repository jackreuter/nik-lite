package jackreuter.niklite;

import android.app.Activity;
import android.content.Context;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;


public class MenuFragment extends Fragment {

    boolean kelvinMode;
    boolean lockMode;
    boolean shapeMode;
    boolean strobeMode;
    int lightDuration;
    int darkDuration;
    int redValue;
    int greenValue;
    int blueValue;
    int temperature;
    MenuListener activityCallback;

    ToggleButton kelvinButton;
    ToggleButton lockButton;
    ToggleButton shapeButton;
    ToggleButton strobeButton;
    TextView lightDurationTextView;
    TextView darkDurationTextView;
    SeekBar lightDurationSeekBar;
    SeekBar darkDurationSeekBar;
    TextView colorNameTextView;
    TextView redValueTextView;
    TextView greenValueTextView;
    TextView blueValueTextView;
    TextView kelvinTextView;
    VerticalSeekBar redValueSeekBar;
    VerticalSeekBar greenValueSeekBar;
    VerticalSeekBar blueValueSeekBar;
    VerticalSeekBar kelvinSeekBar;

    public interface MenuListener {
        public void onKelvinButtonClick();
        public void onLockButtonClick();
        public void onShapeButtonClick();
        public void onStrobeButtonClick();
        public void onClose();
        public void onLightDurationSeekBarChanged(int seekBarValue);
        public void onDarkDurationSeekBarChanged(int seekBarValue);
        public void onRedSeekBarChanged(int seekBarValue);
        public void onGreenSeekBarChanged(int seekBarValue);
        public void onBlueSeekBarChanged(int seekBarValue);
        public void onKelvinSeekBarChanged(int seekBarValue);
    }

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(boolean kelvin,
                                           boolean lock,
                                           boolean shape,
                                           boolean strobe,
                                           int lightDuration,
                                           int darkDuration,
                                           int redValue,
                                           int greenValue,
                                           int blueValue,
                                           int temperature

    ) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("kelvin", kelvin);
        bundle.putBoolean("lock", lock);
        bundle.putBoolean("shape", shape);
        bundle.putBoolean("strobe", strobe);
        bundle.putInt("lightDuration", lightDuration);
        bundle.putInt("darkDuration", darkDuration);
        bundle.putInt("redValue", redValue);
        bundle.putInt("greenValue", greenValue);
        bundle.putInt("blueValue", blueValue);
        bundle.putInt("temperature", temperature);

        MenuFragment fragment = new MenuFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            kelvinMode = bundle.getBoolean("kelvin");
            lockMode = bundle.getBoolean("lock");
            shapeMode = bundle.getBoolean("shape");
            strobeMode = bundle.getBoolean("strobe");
            lightDuration = bundle.getInt("lightDuration");
            darkDuration = bundle.getInt("darkDuration");
            redValue = bundle.getInt("redValue");
            greenValue = bundle.getInt("greenValue");
            blueValue = bundle.getInt("blueValue");
            temperature = bundle.getInt("temperature");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_menu, container, false);
        readBundle(getArguments());

        final View backgroundView = view.findViewById(R.id.menuBackground);
        backgroundView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backgroundViewClicked(v);
            }
        });

        kelvinButton = (ToggleButton) view.findViewById(R.id.kelvinButton);
        kelvinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kelvinButtonClicked(v);
            }
        });

        lockButton = (ToggleButton) view.findViewById(R.id.lockButton);
        lockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lockButtonClicked(v);
            }
        });

        shapeButton = (ToggleButton) view.findViewById(R.id.shapeButton);
        shapeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shapeButtonClicked(v);
            }
        });

        strobeButton = (ToggleButton) view.findViewById(R.id.strobeButton);
        strobeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                strobeButtonClicked(v);
            }
        });

        kelvinButton.setChecked(kelvinMode);
        lockButton.setChecked(lockMode);
        shapeButton.setChecked(shapeMode);
        strobeButton.setChecked(strobeMode);

        /** set up SeekBar's for strobe settings */
        lightDurationTextView = (TextView) view.findViewById(R.id.lightDurationTextView);
        lightDurationTextView.setText(String.format("LIGHT: %.1fs", lightDuration/1000.0));
        lightDurationSeekBar = (SeekBar) view.findViewById(R.id.lightDurationSeekBar);
        lightDurationSeekBar.setProgress(lightDuration);
        // perform seek bar change listener event used for getting the progress value
        lightDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                lightDurationTextView.setText(String.format("LIGHT: %.1fs", sliderValue/1000.0));
                lightDurationSeekBarChanged(sliderValue);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        darkDurationTextView = (TextView) view.findViewById(R.id.darkDurationTextView);
        darkDurationTextView.setText(String.format("DARK:  %.1fs", darkDuration/1000.0));
        darkDurationSeekBar = (SeekBar) view.findViewById(R.id.darkDurationSeekBar);
        darkDurationSeekBar.setProgress(darkDuration);
        // perform seek bar change listener event used for getting the slider value
        darkDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                darkDurationTextView.setText(String.format("DARK:  %.1fs", sliderValue/1000.0));
                darkDurationSeekBarChanged(sliderValue);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        colorNameTextView = (TextView) view.findViewById(R.id.colorNameTextView);
        String colorName = getColorName();
        colorNameTextView.setText(colorName);

        /** set up VerticalSeekBar's for RGB selection */
        redValueTextView = (TextView) view.findViewById(R.id.redValueTextView);
        redValueTextView.setText("R: " + redValue);
        redValueSeekBar = (VerticalSeekBar) view.findViewById(R.id.redSeekBar);
        redValueSeekBar.setProgress(redValue);
        // perform seek bar change listener event used for getting the progress value
        redValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                redValue = value;
                redValueTextView.setText("R: " + sliderValue);
                redSeekBarChanged(sliderValue);
                String colorName = getColorName();
                colorNameTextView.setText(colorName);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        greenValueTextView = (TextView) view.findViewById(R.id.greenValueTextView);
        greenValueTextView.setText("G: " + greenValue);
        greenValueSeekBar = (VerticalSeekBar) view.findViewById(R.id.greenSeekBar);
        greenValueSeekBar.setProgress(greenValue);
        // perform seek bar change listener event used for getting the progress value
        greenValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                greenValue = value;
                greenValueTextView.setText("G: " + sliderValue);
                greenSeekBarChanged(sliderValue);
                String colorName = getColorName();
                colorNameTextView.setText(colorName);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        blueValueTextView = (TextView) view.findViewById(R.id.blueValueTextView);
        blueValueTextView.setText("B: " + blueValue);
        blueValueSeekBar = (VerticalSeekBar) view.findViewById(R.id.blueSeekBar);
        blueValueSeekBar.setProgress(blueValue);
        // perform seek bar change listener event used for getting the progress value
        blueValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                blueValue = value;
                blueValueTextView.setText("B: " + sliderValue);
                blueSeekBarChanged(sliderValue);
                String colorName = getColorName();
                colorNameTextView.setText(colorName);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        /** set up text view and seek bar for kelvin mode */
        kelvinTextView = (TextView) view.findViewById(R.id.kelvinTextView);
        kelvinTextView.setText(temperature+"K");
        kelvinSeekBar = (VerticalSeekBar) view.findViewById(R.id.kelvinSeekBar);
        kelvinSeekBar.setProgress(temperature);
        // perform seek bar change listener event used for getting the progress value
        kelvinSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                temperature = value;
                kelvinTextView.setText(sliderValue+"K");
                kelvinSeekBarChanged(sliderValue);
                String colorName = getColorName();
                colorNameTextView.setText(colorName);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        toggleKelvinModeUI(kelvinMode);
        return view;

    }

    /** enable or disable UI depending on value of kelvin mode */
    public void toggleKelvinModeUI(Boolean iskelvinModeEnabled) {
        int kelvinVisibility;
        int rgbVisibility;
        if (iskelvinModeEnabled) {
            kelvinVisibility = TextView.VISIBLE;
            rgbVisibility = TextView.INVISIBLE;
        } else {
            kelvinVisibility = TextView.INVISIBLE;
            rgbVisibility = TextView.VISIBLE;
        }

        kelvinTextView.setEnabled(iskelvinModeEnabled);
        kelvinSeekBar.setEnabled(iskelvinModeEnabled);
        redValueTextView.setEnabled(!iskelvinModeEnabled);
        greenValueTextView.setEnabled(!iskelvinModeEnabled);
        blueValueTextView.setEnabled(!iskelvinModeEnabled);
        redValueSeekBar.setEnabled(!iskelvinModeEnabled);
        greenValueSeekBar.setEnabled(!iskelvinModeEnabled);
        blueValueSeekBar.setEnabled(!iskelvinModeEnabled);

        kelvinTextView.setVisibility(kelvinVisibility);
        kelvinSeekBar.setVisibility(kelvinVisibility);
        redValueTextView.setVisibility(rgbVisibility);
        greenValueTextView.setVisibility(rgbVisibility);
        blueValueTextView.setVisibility(rgbVisibility);
        redValueSeekBar.setVisibility(rgbVisibility);
        greenValueSeekBar.setVisibility(rgbVisibility);
        blueValueSeekBar.setVisibility(rgbVisibility);
    }

    /** check if color has name, if so print to textView */
    public String getColorName() {
        String colorName = "";
        if (kelvinMode) {
            if (1000 <= temperature && temperature <= 2000) {
                colorName = "CANDLE FLAME";
            } else if (2500 <= temperature && temperature <= 3000) {
                colorName = "DOMESTIC LIGHTING";
            } else if (3000 <= temperature && temperature <= 4000) {
                colorName = "EARLY MORNING/EVENING";
            } else if (4000 <= temperature && temperature <= 5000) {
                colorName = "FLUORESCENT";
            } else if (5000 <= temperature && temperature <= 5500) {
                colorName = "FLASH";
            } else if (5500 <= temperature && temperature <= 6500) {
                colorName = "AVERAGE DAYLIGHT";
            } else if (6000 <= temperature && temperature <= 7000) {
                colorName = "NOON SUNLIGHT";
            } else if (6500 <= temperature && temperature <= 8000) {
                colorName = "SHADE";
            } else if (10000 <= temperature && temperature <= 15000) {
                colorName = "BLUE SKY";
            } else {

            }
        } else {
            int buffer = 10; 
            if (redValue <= buffer) {
                if (greenValue <= buffer) {
                    if (blueValue <= buffer) {
                        colorName = "BLACK";
                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {

                    } else if (blueValue >= 255 - buffer) {
                        colorName = "BLUE";
                    } else {

                    }

                } else if (greenValue >= 127 - buffer/2 && greenValue <= 127 + buffer/2) {
                    if (blueValue <= buffer) {

                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {

                    } else if (blueValue >= 255 - buffer) {
                        colorName = "AZURE";
                    } else {

                    }

                } else if (greenValue >= 255 - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "GREEN";
                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {
                        colorName = "SPRING GREEN";
                    } else if (blueValue >= 255 - buffer) {
                        colorName = "CYAN";
                    } else {

                    }

                } else {

                }
            } else if (redValue >= 127 - buffer/2 && redValue <= 127 + buffer/2) {
                if (greenValue <= buffer) {
                    if (blueValue <= buffer) {

                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {

                    } else if (blueValue >= 255 - buffer) {
                        colorName = "VIOLET";
                    } else {

                    }

                } else if (greenValue >= 127 - buffer/2 && greenValue <= 127 + buffer/2) {
                    if (blueValue <= buffer) {

                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {

                    } else if (blueValue >= 255 - buffer) {

                    } else {

                    }

                } else if (greenValue >= 255 - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "CHARTREUSE";
                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {

                    } else if (blueValue >= 255 - buffer) {

                    } else {

                    }

                } else {

                }

            } else if (redValue >= 255 - buffer) {

                if (greenValue <= buffer) {
                    if (blueValue <= buffer) {
                        colorName = "RED";
                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {
                        colorName = "ROSE";
                    } else if (blueValue >= 255 - buffer) {
                        colorName = "MAGENTA";
                    } else {

                    }

                } else if (greenValue >= 127 - buffer/2 && greenValue <= 127 + buffer/2) {
                    if (blueValue <= buffer) {
                        colorName = "ORANGE";
                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {

                    } else if (blueValue >= 255 - buffer) {

                    } else {

                    }

                } else if (greenValue >= 255 - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "YELLOW";
                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {

                    } else if (blueValue >= 255 - buffer) {
                        colorName = "WHITE";
                    } else {

                    }

                } else {

                }

            } else {

            }
        }
        return colorName;
    }

    /** prevents menu from being closed when background of menu is clicked */
    public void backgroundViewClicked(View view) { }

    /** toggle kelvin mode */
    public void kelvinButtonClicked(View view) {
        kelvinMode = !kelvinMode;
        toggleKelvinModeUI(kelvinMode);
        String color = getColorName();
        colorNameTextView.setText(color);
        final Button kelvinButton = (Button) view.findViewById(R.id.kelvinButton);
        activityCallback.onKelvinButtonClick();
    }

    /**  toggle lock mode */
    public void lockButtonClicked(View view) {
        lockMode = !lockMode;
        final Button lockButton = (Button) view.findViewById(R.id.lockButton);
        activityCallback.onLockButtonClick();
    }

    /** toggle shape mode */
    public void shapeButtonClicked(View view) {
        shapeMode = !shapeMode;
        final Button shapeButton = (Button) view.findViewById(R.id.shapeButton);
        activityCallback.onShapeButtonClick();
    }

    /** toggle strobe mode */
    public void strobeButtonClicked(View view) {
        strobeMode = !strobeMode;
        final Button strobeButton = (Button) view.findViewById(R.id.strobeButton);
        activityCallback.onStrobeButtonClick();
    }

    /** update light duration value for strobe setting */
    public void lightDurationSeekBarChanged(int value) {
        activityCallback.onLightDurationSeekBarChanged(value);
    }

    /** update dark duration value for strobe setting */
    public void darkDurationSeekBarChanged(int value) {
        activityCallback.onDarkDurationSeekBarChanged(value);
    }

    /** update red value */
    public void redSeekBarChanged(int value) {
        activityCallback.onRedSeekBarChanged(value);
    }

    /** update green value */
    public void greenSeekBarChanged(int value) {
        activityCallback.onGreenSeekBarChanged(value);
    }

    /** update blue value */
    public void blueSeekBarChanged(int value) {
        activityCallback.onBlueSeekBarChanged(value);
    }

    /** update temperature value */
    public void kelvinSeekBarChanged(int value) {
        activityCallback.onKelvinSeekBarChanged(value);
    }

    /** close menu fragment */
    public void close() { activityCallback.onClose(); }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (MenuListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MenuListener");
        }
    }

}
