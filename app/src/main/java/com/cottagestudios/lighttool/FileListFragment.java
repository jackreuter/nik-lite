package com.cottagestudios.lighttool;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FileListFragment extends Fragment {

    private ArrayList<String> filenames;
    private static String ARG_FILENAMES = "filenames";
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileListFragment() {
    }

    public static FileListFragment newInstance(ArrayList<String> filenames) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_FILENAMES, filenames);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            filenames = getArguments().getStringArrayList(ARG_FILENAMES);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filelist_list, container, false);
        RecyclerView fileListRecyclerView = view.findViewById(R.id.list);

        // Prevent menu from closing on background click
        final View backgroundView = view.findViewById(R.id.menuBackground);
        backgroundView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        // Set the adapter
        Context context = view.getContext();
        fileListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        fileListRecyclerView.setAdapter(new MyFileListRecyclerViewAdapter(filenames, mListener));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onItemClicked(String item);
        void onDeleteButtonClicked(String item);
    }
}
