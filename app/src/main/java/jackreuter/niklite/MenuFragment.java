package jackreuter.niklite;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MenuFragment extends Fragment {

    boolean kelvinMode;
    boolean lockMode;
    boolean shapeMode;
    MenuListener activityCallback;

    public interface MenuListener {
        public void onKelvinButtonClick();
        public void onLockButtonClick();
        public void onShapeButtonClick();
        public void onCloseButtonClick();
    }

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(boolean kelvin, boolean lock, boolean shape) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("kelvin", kelvin);
        bundle.putBoolean("lock", lock);
        bundle.putBoolean("shape", shape);

        MenuFragment fragment = new MenuFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            kelvinMode = bundle.getBoolean("kelvin");
            lockMode = bundle.getBoolean("lock");
            shapeMode = bundle.getBoolean("shape");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_menu, container, false);
        readBundle(getArguments());

        final Button kelvinButton = (Button) view.findViewById(R.id.kelvinButton);
        kelvinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kelvinButtonClicked(v);
            }
        });

        final Button lockButton = (Button) view.findViewById(R.id.lockButton);
        lockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lockButtonClicked(v);
            }
        });

        final Button shapeButton = (Button) view.findViewById(R.id.shapeButton);
        shapeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shapeButtonClicked(v);
            }
        });

        final Button closeButton = (Button) view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeButtonClicked(v);
            }
        });

        if (kelvinMode) { kelvinButton.setText("COLOR WHEEL"); }
        else { kelvinButton.setText("KELVIN"); }

        if (lockMode) { lockButton.setText("UNLOCK"); }
        else { lockButton.setText("LOCK"); }

        if (shapeMode) { shapeButton.setText("FULL"); }
        else { shapeButton.setText("SHAPE"); }

        return view;

    }

    public void kelvinButtonClicked(View view) {
        kelvinMode = !kelvinMode;
        final Button kelvinButton = (Button) view.findViewById(R.id.kelvinButton);
        if (kelvinMode) { kelvinButton.setText("COLOR WHEEL"); }
        else { kelvinButton.setText("KELVIN"); }
        activityCallback.onKelvinButtonClick();
    }

    public void lockButtonClicked(View view) {
        lockMode = !lockMode;
        final Button lockButton = (Button) view.findViewById(R.id.lockButton);
        if (lockMode) { lockButton.setText("UNLOCK"); }
        else { lockButton.setText("LOCK"); }
        activityCallback.onLockButtonClick();
    }

    public void shapeButtonClicked(View view) {
        shapeMode = !shapeMode;
        final Button shapeButton = (Button) view.findViewById(R.id.shapeButton);
        if (shapeMode) { shapeButton.setText("FULL"); }
        else { shapeButton.setText("SHAPE"); }
        activityCallback.onShapeButtonClick();
    }

    public void closeButtonClicked(View view) {
        activityCallback.onCloseButtonClick();
    }

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
