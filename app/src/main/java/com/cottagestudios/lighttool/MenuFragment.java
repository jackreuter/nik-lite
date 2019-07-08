package com.cottagestudios.lighttool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MenuFragment extends Fragment {

    boolean kelvinMode;
    boolean lockMode;
    int shapeIndex;
    boolean strobeMode;
    int lightDuration;
    int darkDuration;
    int redValue;
    int greenValue;
    int blueValue;
    int temperature;
    MenuListener activityCallback;

    Button openButton, saveButton;
    ToggleButton kelvinButton, lockButton, strobeButton;
    Button fullscreenButton, circleButton, starButton, heartButton, plusButton;
    ImageView fullscreenSelectedImageView, circleSelectedImageView, starSelectedImageView, heartSelectedImageView, plusSelectedImageView;
    TextView lightDurationTextView, darkDurationTextView;
    SeekBar lightDurationSeekBar, darkDurationSeekBar;
    TextView colorNameTextView, redValueTextView, greenValueTextView, blueValueTextView, kelvinTextView;
    VerticalSeekBar redValueSeekBar, greenValueSeekBar, blueValueSeekBar, kelvinSeekBar;

    final double MILLISECONDS_PER_SECOND = 1000.0;
    final int CANDLE_FLAME_LOWER = 1000;
    final int CANDLE_FLAME_UPPER = 2000;
    final int DOMESTIC_LIGHTING_LOWER = 2500;
    final int DOMESTIC_LIGHTING_UPPER = 3000;
    final int EARLY_MORNING_EVENING_LOWER = 3000;
    final int EARLY_MORNING_EVENING_UPPER = 4000;
    final int FLUORESCENT_LOWER = 4000;
    final int FLUORESCENT_UPPER = 5000;
    final int FLASH_LOWER = 5000;
    final int FLASH_UPPER = 5500;
    final int AVERAGE_DAYLIGHT_LOWER = 5500;
    final int AVERAGE_DAYLIGHT_UPPER = 6500;
    final int NOON_SUNLIGHT_LOWER = 6500;
    final int NOON_SUNLIGHT_UPPER = 7000;
    final int SHADE_LOWER = 7000;
    final int SHADE_UPPER = 8000;
    final int BLUE_SKY_LOWER = 10000;
    final int BLUE_SKY_UPPER = 15000;

    public interface MenuListener {
        void onSaveButtonClick();
        void onOpenButtonClick();
        void onKelvinButtonClick();
        void onLockButtonClick();
        void onFullscreenButtonClick();
        void onCircleButtonClick();
        void onStarButtonClick();
        void onHeartButtonClick();
        void onPlusButtonClick();
        void onStrobeButtonClick();
        void onLightDurationSeekBarChanged(int seekBarValue);
        void onDarkDurationSeekBarChanged(int seekBarValue);
        void onRedSeekBarChanged(int seekBarValue);
        void onGreenSeekBarChanged(int seekBarValue);
        void onBlueSeekBarChanged(int seekBarValue);
        void onKelvinSeekBarChanged(int seekBarValue);
        void onHugeMistakeMade();

    }

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(boolean kelvin,
                                           boolean lock,
                                           int shape,
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
        bundle.putInt("shapeIndex", shape);
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
            shapeIndex = bundle.getInt("shapeIndex");
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_menu, container, false);
        readBundle(getArguments());

        // Prevent menu from closing on background click
        final View backgroundView = view.findViewById(R.id.menuBackground);
        backgroundView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        openButton = view.findViewById(R.id.openButton);
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openButtonClicked(v);
            }
        });

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveButtonClicked(v);
            }
        });

        kelvinButton = view.findViewById(R.id.kelvinButton);
        kelvinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kelvinButtonClicked(v);
            }
        });

        lockButton = view.findViewById(R.id.lockButton);
        lockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lockButtonClicked(v);
            }
        });

        //create shape selection buttons and selection outline images
        fullscreenSelectedImageView = view.findViewById(R.id.fullScreenSelected);
        circleSelectedImageView = view.findViewById(R.id.circleSelected);
        starSelectedImageView = view.findViewById(R.id.starSelected);
        heartSelectedImageView = view.findViewById(R.id.heartSelected);
        plusSelectedImageView = view.findViewById(R.id.plusSelected);

        fullscreenButton = view.findViewById(R.id.fullScreenButton);
        circleButton = view.findViewById(R.id.circleButton);
        starButton = view.findViewById(R.id.starButton);
        heartButton = view.findViewById(R.id.heartButton);
        plusButton = view.findViewById(R.id.plusButton);

        setShapeSelectorImage();

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fullscreenButtonClicked(v);
            }
        });
        circleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                circleButtonClicked(v);
            }
        });
        starButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                starButtonClicked(v);
            }
        });
        heartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                heartButtonClicked(v);
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                plusButtonClicked(v);
            }
        });

        strobeButton = view.findViewById(R.id.strobeButton);
        strobeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                strobeButtonClicked(v);
            }
        });

        kelvinButton.setChecked(kelvinMode);
        lockButton.setChecked(lockMode);
        strobeButton.setChecked(strobeMode);

        // set up SeekBar's for strobe settings
        lightDurationTextView = view.findViewById(R.id.lightDurationTextView);
        lightDurationTextView.setText(String.format(getString(R.string.light), lightDuration/MILLISECONDS_PER_SECOND));
        lightDurationSeekBar = view.findViewById(R.id.lightDurationSeekBar);
        lightDurationSeekBar.setProgress(lightDuration);
        // perform seek bar change listener event used for getting the progress value
        lightDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                lightDurationTextView.setText(String.format(getString(R.string.light), value/MILLISECONDS_PER_SECOND));
                lightDurationSeekBarChanged(value);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        darkDurationTextView = view.findViewById(R.id.darkDurationTextView);
        darkDurationTextView.setText(String.format(getString(R.string.dark), darkDuration/MILLISECONDS_PER_SECOND));
        darkDurationSeekBar = view.findViewById(R.id.darkDurationSeekBar);
        darkDurationSeekBar.setProgress(darkDuration);
        // perform seek bar change listener event used for getting the slider value
        darkDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                darkDurationTextView.setText(String.format(getString(R.string.dark), value/MILLISECONDS_PER_SECOND));
                darkDurationSeekBarChanged(value);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        colorNameTextView = view.findViewById(R.id.colorNameTextView);
        String colorName = getColorName();
        colorNameTextView.setText(colorName);

        // set up VerticalSeekBar's for RGB selection
        redValueTextView = view.findViewById(R.id.redValueTextView);
        redValueTextView.setText(String.format(getString(R.string.r), redValue));
        redValueSeekBar = view.findViewById(R.id.redSeekBar);
        redValueSeekBar.setProgress(redValue);
        // perform seek bar change listener event used for getting the progress value
        redValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                redValue = value;
                redValueTextView.setText(String.format(getString(R.string.r), value));
                redSeekBarChanged(value);
                String colorName = getColorName();
                colorNameTextView.setText(colorName);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) {
                easterEggCheck();
            }
        });

        greenValueTextView = view.findViewById(R.id.greenValueTextView);
        greenValueTextView.setText(String.format(getString(R.string.g), greenValue));
        greenValueSeekBar = view.findViewById(R.id.greenSeekBar);
        greenValueSeekBar.setProgress(greenValue);
        // perform seek bar change listener event used for getting the progress value
        greenValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                greenValue = value;
                greenValueTextView.setText(String.format(getString(R.string.g), value));
                greenSeekBarChanged(value);
                String colorName = getColorName();
                colorNameTextView.setText(colorName);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) {
                easterEggCheck();
            }
        });

        blueValueTextView = view.findViewById(R.id.blueValueTextView);
        blueValueTextView.setText(String.format(getString(R.string.b), blueValue));
        blueValueSeekBar = view.findViewById(R.id.blueSeekBar);
        blueValueSeekBar.setProgress(blueValue);
        // perform seek bar change listener event used for getting the progress value
        blueValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                blueValue = value;
                blueValueTextView.setText(String.format(getString(R.string.b), value));
                blueSeekBarChanged(value);
                String colorName = getColorName();
                colorNameTextView.setText(colorName);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) {
                easterEggCheck();
            }
        });

        // set up text view and seek bar for kelvin mode
        kelvinTextView = view.findViewById(R.id.kelvinTextView);
        kelvinTextView.setText(String.format(getString(R.string.k), temperature));
        kelvinSeekBar = view.findViewById(R.id.kelvinSeekBar);
        kelvinSeekBar.setProgress(temperature);
        // perform seek bar change listener event used for getting the progress value
        kelvinSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                temperature = value;
                kelvinTextView.setText(String.format(getString(R.string.k), value));
                kelvinSeekBarChanged(value);
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

    /** check for 69 69 69 */
    public void easterEggCheck() {
        if (redValue == 69 && greenValue == 69 && blueValue == 69) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(getString(R.string.are_you_sure));

            // set dialog message
            alertDialogBuilder
                    //.setCancelable(false)
                    .setPositiveButton(getString(R.string.yes_im_sure),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton(getString(R.string.huge_mistake),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    redValueTextView.setText(String.format(getString(R.string.r), MainActivity.MAX_SATURATION));
                                    greenValueTextView.setText(String.format(getString(R.string.g), MainActivity.MAX_SATURATION));
                                    blueValueTextView.setText(String.format(getString(R.string.b), MainActivity.MAX_SATURATION));
                                    redValueSeekBar.setProgress(MainActivity.MAX_SATURATION);
                                    greenValueSeekBar.setProgress(MainActivity.MAX_SATURATION);
                                    blueValueSeekBar.setProgress(MainActivity.MAX_SATURATION);
                                    redValue = MainActivity.MAX_SATURATION;
                                    greenValue = MainActivity.MAX_SATURATION;
                                    blueValue = MainActivity.MAX_SATURATION;
                                    activityCallback.onHugeMistakeMade();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    /** check if color has name, if so print to textView */
    public String getColorName() {
        String colorName = "";
        if (kelvinMode) {
            if (CANDLE_FLAME_LOWER <= temperature && temperature <= CANDLE_FLAME_UPPER) {
                colorName = getString(R.string.candle_flame);
            } else if (DOMESTIC_LIGHTING_LOWER <= temperature && temperature <= DOMESTIC_LIGHTING_UPPER) {
                colorName = getString(R.string.domestic_lighting);
            } else if (EARLY_MORNING_EVENING_LOWER <= temperature && temperature <= EARLY_MORNING_EVENING_UPPER) {
                colorName = getString(R.string.early_morning_evening);
            } else if (FLUORESCENT_LOWER <= temperature && temperature <= FLUORESCENT_UPPER) {
                colorName = getString(R.string.fluorescent);
            } else if (FLASH_LOWER <= temperature && temperature <= FLASH_UPPER) {
                colorName = getString(R.string.flash);
            } else if (AVERAGE_DAYLIGHT_LOWER <= temperature && temperature <= AVERAGE_DAYLIGHT_UPPER) {
                colorName = getString(R.string.average_daylight);
            } else if (NOON_SUNLIGHT_LOWER <= temperature && temperature <= NOON_SUNLIGHT_UPPER) {
                colorName = getString(R.string.noon_sunlight);
            } else if (SHADE_LOWER <= temperature && temperature <= SHADE_UPPER) {
                colorName = getString(R.string.shade);
            } else if (BLUE_SKY_LOWER <= temperature && temperature <= BLUE_SKY_UPPER) {
                colorName = getString(R.string.blue_sky);
            }
        } else {
            int buffer = 10; 
            if (redValue <= buffer) {
                if (greenValue <= buffer) {
                    if (blueValue <= buffer) {
                        colorName = "BLACK";
                    } else if (blueValue >= MainActivity.MAX_SATURATION - buffer) {
                        colorName = "BLUE";
                    }

                } else if (greenValue >= MainActivity.MAX_SATURATION/2 - buffer/2 && greenValue <= MainActivity.MAX_SATURATION/2 + buffer/2) {
                    if (blueValue >= MainActivity.MAX_SATURATION - buffer) {
                        colorName = "AZURE";
                    }

                } else if (greenValue >= MainActivity.MAX_SATURATION - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "GREEN";
                    } else if (blueValue >= MainActivity.MAX_SATURATION/2 - buffer/2 && blueValue <= MainActivity.MAX_SATURATION/2 + buffer/2) {
                        colorName = "SPRING GREEN";
                    } else if (blueValue >= MainActivity.MAX_SATURATION - buffer) {
                        colorName = "CYAN";
                    }

                }
            } else if (redValue >= MainActivity.MAX_SATURATION/2 - buffer/2 && redValue <= MainActivity.MAX_SATURATION/2 + buffer/2) {
                if (greenValue <= buffer) {
                    if (blueValue >= MainActivity.MAX_SATURATION - buffer) {
                        colorName = "VIOLET";
                    }

                } else if (greenValue >= MainActivity.MAX_SATURATION - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "CHARTREUSE";
                    }
                }

            } else if (redValue >= MainActivity.MAX_SATURATION - buffer) {

                if (greenValue <= buffer) {
                    if (blueValue <= buffer) {
                        colorName = "RED";
                    } else if (blueValue >= MainActivity.MAX_SATURATION/2 - buffer/2 && blueValue <= MainActivity.MAX_SATURATION/2 + buffer/2) {
                        colorName = "ROSE";
                    } else if (blueValue >= MainActivity.MAX_SATURATION - buffer) {
                        colorName = "MAGENTA";
                    }

                } else if (greenValue >= MainActivity.MAX_SATURATION/2 - buffer/2 && greenValue <= MainActivity.MAX_SATURATION/2 + buffer/2) {
                    if (blueValue <= buffer) {
                        colorName = "ORANGE";
                    }

                } else if (greenValue >= MainActivity.MAX_SATURATION - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "YELLOW";
                    } else if (blueValue >= MainActivity.MAX_SATURATION - buffer) {
                        colorName = "WHITE";
                    }
                }
            }
        }
        return colorName;
    }

    /** open dialog menu to select user saved settings */
    public void openButtonClicked(View view) {
        activityCallback.onOpenButtonClick();
    }

    /** save current settings as preset, prompt user to enter name */
    public void saveButtonClicked(View view) {
        activityCallback.onSaveButtonClick();
    }

    /** toggle kelvin mode */
    public void kelvinButtonClicked(View view) {
        kelvinMode = !kelvinMode;
        toggleKelvinModeUI(kelvinMode);
        String color = getColorName();
        colorNameTextView.setText(color);
        activityCallback.onKelvinButtonClick();
    }

    /**  toggle lock mode */
    public void lockButtonClicked(View view) {
        lockMode = !lockMode;
        activityCallback.onLockButtonClick();
    }

    /** set fullscreen mode */
    public void fullscreenButtonClicked(View view) {
        shapeIndex = MainActivity.FULLSCREEN_INDEX;
        setShapeSelectorImage();
        activityCallback.onFullscreenButtonClick();
    }

    /** set circle mode */
    public void circleButtonClicked(View view) {
        shapeIndex = MainActivity.CIRCLE_INDEX;
        setShapeSelectorImage();
        activityCallback.onCircleButtonClick();
    }

    /** set star mode */
    public void starButtonClicked(View view) {
        shapeIndex = MainActivity.STAR_INDEX;
        setShapeSelectorImage();
        activityCallback.onStarButtonClick();
    }

    /** set heart mode */
    public void heartButtonClicked(View view) {
        shapeIndex = MainActivity.HEART_INDEX;
        setShapeSelectorImage();
        activityCallback.onHeartButtonClick();
    }

    /** set plus mode */
    public void plusButtonClicked(View view) {
        shapeIndex = MainActivity.PLUS_INDEX;
        setShapeSelectorImage();
        activityCallback.onPlusButtonClick();
    }

    /** set shape selector image */
    public void setShapeSelectorImage() {
        fullscreenSelectedImageView.setVisibility(View.INVISIBLE);
        circleSelectedImageView.setVisibility(View.INVISIBLE);
        starSelectedImageView.setVisibility(View.INVISIBLE);
        heartSelectedImageView.setVisibility(View.INVISIBLE);
        plusSelectedImageView.setVisibility(View.INVISIBLE);

        if (shapeIndex == MainActivity.FULLSCREEN_INDEX) { fullscreenSelectedImageView.setVisibility(View.VISIBLE); }
        if (shapeIndex == MainActivity.CIRCLE_INDEX) { circleSelectedImageView.setVisibility(View.VISIBLE); }
        if (shapeIndex == MainActivity.STAR_INDEX) { starSelectedImageView.setVisibility(View.VISIBLE); }
        if (shapeIndex == MainActivity.HEART_INDEX) { heartSelectedImageView.setVisibility(View.VISIBLE); }
        if (shapeIndex == MainActivity.PLUS_INDEX) { plusSelectedImageView.setVisibility(View.VISIBLE); }
    }

    /** toggle strobe mode */
    public void strobeButtonClicked(View view) {
        strobeMode = !strobeMode;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            activityCallback = (MenuListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MenuListener");
        }
    }

}
