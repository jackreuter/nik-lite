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
    MenuListener activityCallback;

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
                                           int blueValue
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


        final ToggleButton kelvinButton = (ToggleButton) view.findViewById(R.id.kelvinButton);
        kelvinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kelvinButtonClicked(v);
            }
        });

        final ToggleButton lockButton = (ToggleButton) view.findViewById(R.id.lockButton);
        lockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lockButtonClicked(v);
            }
        });

        final ToggleButton shapeButton = (ToggleButton) view.findViewById(R.id.shapeButton);
        shapeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shapeButtonClicked(v);
            }
        });

        final ToggleButton strobeButton = (ToggleButton) view.findViewById(R.id.strobeButton);
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
        final TextView lightDurationTextView = (TextView) view.findViewById(R.id.lightDurationTextView);
        lightDurationTextView.setText(String.format("LIGHT: %.1fs", lightDuration/1000.0));
        SeekBar lightDurationSeekBar = (SeekBar) view.findViewById(R.id.lightDurationSeekBar);
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

        final TextView darkDurationTextView = (TextView) view.findViewById(R.id.darkDurationTextView);
        darkDurationTextView.setText(String.format("DARK:  %.1fs", darkDuration/1000.0));
        SeekBar darkDurationSeekBar = (SeekBar) view.findViewById(R.id.darkDurationSeekBar);
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

        /** set up VerticalSeekBar's for RGB selection */
        final TextView colorNameTextView = (TextView) view.findViewById(R.id.colorNameTextView);
        String colorName = getColorName();
        colorNameTextView.setText(colorName);

        final TextView redValueTextView = (TextView) view.findViewById(R.id.redValueTextView);
        redValueTextView.setText("R: " + redValue);
        VerticalSeekBar redValueSeekBar = (VerticalSeekBar) view.findViewById(R.id.redSeekBar);
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

        final TextView greenValueTextView = (TextView) view.findViewById(R.id.greenValueTextView);
        greenValueTextView.setText("G: " + greenValue);
        VerticalSeekBar greenValueSeekBar = (VerticalSeekBar) view.findViewById(R.id.greenSeekBar);
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

        final TextView blueValueTextView = (TextView) view.findViewById(R.id.blueValueTextView);
        blueValueTextView.setText("B: " + blueValue);
        VerticalSeekBar blueValueSeekBar = (VerticalSeekBar) view.findViewById(R.id.blueSeekBar);
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

        return view;

    }

    /** check if color has name, if so print to textView */
    public String getColorName() {
        String colorName = "";
        if (redValue <= 5) {
            if (greenValue <= 5) {
                if (blueValue <= 5) {
                    colorName = "BLACK";
                } else if (blueValue >= 125 && blueValue <= 130) {

                } else if (blueValue >= 250) {
                    colorName = "BLUE";
                } else {

                }

            } else if (greenValue >= 125 && greenValue <= 130) {
                if (blueValue <= 5) {

                } else if (blueValue >= 125 && blueValue <= 130) {

                } else if (blueValue >= 250) {
                    colorName = "AZURE";
                } else {

                }

            } else if (greenValue >= 250) {
                if (blueValue <= 5) {
                    colorName = "GREEN";
                } else if (blueValue >= 125 && blueValue <= 130) {
                    colorName = "SPRING GREEN";
                } else if (blueValue >= 250) {
                    colorName = "CYAN";
                } else {

                }

            } else {

            }
        } else if (redValue >= 125 && redValue <= 130) {
            if (greenValue <= 5) {
                if (blueValue <= 5) {

                } else if (blueValue >= 125 && blueValue <= 130) {

                } else if (blueValue >= 250) {
                    colorName = "VIOLET";
                } else {

                }

            } else if (greenValue >= 125 && greenValue <= 130) {
                if (blueValue <= 5) {

                } else if (blueValue >= 125 && blueValue <= 130) {

                } else if (blueValue >= 250) {

                } else {

                }

            } else if (greenValue >= 250) {
                if (blueValue <= 5) {
                    colorName = "CHARTREUSE";
                } else if (blueValue >= 125 && blueValue <= 130) {

                } else if (blueValue >= 250) {

                } else {

                }

            } else {

            }

        } else if (redValue >= 250) {

            if (greenValue <= 5) {
                if (blueValue <= 5) {
                    colorName = "RED";
                } else if (blueValue >= 125 && blueValue <= 130) {
                    colorName = "ROSE";
                } else if (blueValue >= 250) {
                    colorName = "MAGENTA";
                } else {

                }

            } else if (greenValue >= 125 && greenValue <= 130) {
                if (blueValue <= 5) {
                    colorName = "ORANGE";
                } else if (blueValue >= 125 && blueValue <= 130) {

                } else if (blueValue >= 250) {

                } else {

                }

            } else if (greenValue >= 250) {
                if (blueValue <= 5) {
                    colorName = "YELLOW";
                } else if (blueValue >= 125 && blueValue <= 130) {

                } else if (blueValue >= 250) {
                    colorName = "WHITE";
                } else {

                }

            } else {

            }

        } else {

        }
        return colorName;
    }

    /** prevents menu from being closed when background of menu is clicked */
    public void backgroundViewClicked(View view) { }

    /** toggle kelvin mode */
    public void kelvinButtonClicked(View view) {
        kelvinMode = !kelvinMode;
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
