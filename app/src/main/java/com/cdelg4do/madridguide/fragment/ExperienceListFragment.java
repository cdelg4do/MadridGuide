package com.cdelg4do.madridguide.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.adapter.ExperiencesAdapter;
import com.cdelg4do.madridguide.model.Experience;
import com.cdelg4do.madridguide.model.Experiences;
import com.cdelg4do.madridguide.view.OnElementClickedListener;


/**
 * This fragment is used to represent a collection of Experience model objects.
 */
public class ExperienceListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExperiencesAdapter adapter;
    private Experiences experiences;
    private OnElementClickedListener<Experience> listener;


    // Required empty public constructor
    public ExperienceListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_experience_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.experiences_recycler_view);
        LinearLayoutManager layoutMgr = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutMgr);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutMgr.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return rootView;
    }


    public void setOnElementClickedListener(OnElementClickedListener<Experience> listener) {
        this.listener = listener;
    }


    public Experiences getExperiences() {
        return experiences;
    }


    // Set the experiences that will be shown by this fragment,
    // and then update the fragment UI (by setting an adapter for the fragment's RecyclerView)
    public void setExperiencesAndUpdateFragmentUI(Experiences experiences) {

        this.experiences = experiences;
        updateUI();
    }


    // Auxiliary methods:

    // Specifies the adapter to show the data on the RecyclerView
    // Also, sets the adapter's listener to take action when the user selects a row/cell
    private void updateUI() {

        adapter = new ExperiencesAdapter(getExperiences(), getActivity());
        recyclerView.setAdapter(adapter);

        // Add an anonymous OnElementClickedListener to the adapter,
        // that calls this fragment's listener when a row/cell is clicked
        adapter.setOnElementClickedListener(new OnElementClickedListener<Experience>() {

            @Override
            public void onElementClicked(Experience experience, int position) {

                if (listener != null)
                    listener.onElementClicked(experience,position);
            }
        });
    }
}
