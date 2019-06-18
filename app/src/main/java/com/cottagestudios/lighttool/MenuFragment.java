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

        final View backgroundView = view.findViewById(R.id.menuBackground);
        backgroundView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backgroundViewClicked(v);
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
        lightDurationTextView.setText(String.format(getString(R.string.light), lightDuration/1000.0));
        lightDurationSeekBar = view.findViewById(R.id.lightDurationSeekBar);
        lightDurationSeekBar.setProgress(lightDuration);
        // perform seek bar change listener event used for getting the progress value
        lightDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                lightDurationTextView.setText(String.format(getString(R.string.light), sliderValue/1000.0));
                lightDurationSeekBarChanged(sliderValue);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }

            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        darkDurationTextView = view.findViewById(R.id.darkDurationTextView);
        darkDurationTextView.setText(String.format(getString(R.string.dark), darkDuration/1000.0));
        darkDurationSeekBar = view.findViewById(R.id.darkDurationSeekBar);
        darkDurationSeekBar.setProgress(darkDuration);
        // perform seek bar change listener event used for getting the slider value
        darkDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                darkDurationTextView.setText(String.format(getString(R.string.dark), sliderValue/1000.0));
                darkDurationSeekBarChanged(sliderValue);
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
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                redValue = value;
                redValueTextView.setText(String.format(getString(R.string.r), sliderValue));
                redSeekBarChanged(sliderValue);
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
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                greenValue = value;
                greenValueTextView.setText(String.format(getString(R.string.g), sliderValue));
                greenSeekBarChanged(sliderValue);
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
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                blueValue = value;
                blueValueTextView.setText(String.format(getString(R.string.b), sliderValue));
                blueSeekBarChanged(sliderValue);
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
            int sliderValue = 0;

            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                sliderValue = value;
                temperature = value;
                kelvinTextView.setText(String.format(getString(R.string.k), sliderValue));
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
                                    redValueTextView.setText(String.format(getString(R.string.r), 255));
                                    greenValueTextView.setText(String.format(getString(R.string.g), 255));
                                    blueValueTextView.setText(String.format(getString(R.string.b), 255));
                                    redValueSeekBar.setProgress(255);
                                    greenValueSeekBar.setProgress(255);
                                    blueValueSeekBar.setProgress(255);
                                    redValue = 255;
                                    greenValue = 255;
                                    blueValue = 255;
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
            if (1000 <= temperature && temperature <= 2000) {
                colorName = getString(R.string.candle_flame);
            } else if (2500 <= temperature && temperature <= 3000) {
                colorName = getString(R.string.domestic_lighting);
            } else if (3000 <= temperature && temperature <= 4000) {
                colorName = getString(R.string.early_morning_evening);
            } else if (4000 <= temperature && temperature <= 5000) {
                colorName = getString(R.string.fluorescent);
            } else if (5000 <= temperature && temperature <= 5500) {
                colorName = getString(R.string.flash);
            } else if (5500 <= temperature && temperature <= 6500) {
                colorName = getString(R.string.average_daylight);
            } else if (6000 <= temperature && temperature <= 7000) {
                colorName = getString(R.string.noon_sunlight);
            } else if (6500 <= temperature && temperature <= 8000) {
                colorName = getString(R.string.shade);
            } else if (10000 <= temperature && temperature <= 15000) {
                colorName = getString(R.string.blue_sky);
            }
        } else {
            int buffer = 10; 
            if (redValue <= buffer) {
                if (greenValue <= buffer) {
                    if (blueValue <= buffer) {
                        colorName = "BLACK";
                    } else if (blueValue >= 255 - buffer) {
                        colorName = "BLUE";
                    }

                } else if (greenValue >= 127 - buffer/2 && greenValue <= 127 + buffer/2) {
                    if (blueValue >= 255 - buffer) {
                        colorName = "AZURE";
                    }

                } else if (greenValue >= 255 - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "GREEN";
                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {
                        colorName = "SPRING GREEN";
                    } else if (blueValue >= 255 - buffer) {
                        colorName = "CYAN";
                    }

                }
            } else if (redValue >= 127 - buffer/2 && redValue <= 127 + buffer/2) {
                if (greenValue <= buffer) {
                    if (blueValue >= 255 - buffer) {
                        colorName = "VIOLET";
                    }

                } else if (greenValue >= 255 - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "CHARTREUSE";
                    }
                }

            } else if (redValue >= 255 - buffer) {

                if (greenValue <= buffer) {
                    if (blueValue <= buffer) {
                        colorName = "RED";
                    } else if (blueValue >= 127 - buffer/2 && blueValue <= 127 + buffer/2) {
                        colorName = "ROSE";
                    } else if (blueValue >= 255 - buffer) {
                        colorName = "MAGENTA";
                    }

                } else if (greenValue >= 127 - buffer/2 && greenValue <= 127 + buffer/2) {
                    if (blueValue <= buffer) {
                        colorName = "ORANGE";
                    }

                } else if (greenValue >= 255 - buffer) {
                    if (blueValue <= buffer) {
                        colorName = "YELLOW";
                    } else if (blueValue >= 255 - buffer) {
                        colorName = "WHITE";
                    }
                }
            }
        }
        return colorName;
    }

    /** prevents menu from being closed when background of menu is clicked */
    public void backgroundViewClicked(View view) { }

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
