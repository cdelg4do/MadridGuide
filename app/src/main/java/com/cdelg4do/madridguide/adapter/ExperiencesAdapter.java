package com.cdelg4do.madridguide.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.model.Experience;
import com.cdelg4do.madridguide.model.Experiences;
import com.cdelg4do.madridguide.view.ActivityRowViewHolder;
import com.cdelg4do.madridguide.view.OnElementClickedListener;

/**
 * This class is the adapter to represent Experience objects in a RecyclerView.
 */
public class ExperiencesAdapter extends RecyclerView.Adapter<ActivityRowViewHolder> {

    private Experiences experiences;
    private LayoutInflater inflater;
    private OnElementClickedListener<Experience> listener;


    public ExperiencesAdapter(Experiences experiences, Context context) {

        this.experiences = experiences;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnElementClickedListener(@NonNull final OnElementClickedListener<Experience> listener) {
        this.listener = listener;
    }

    // Called to set the total count of items to show in the RecyclerView
    @Override
    public int getItemCount() {
        return (int) experiences.size();
    }

    // Called when creating the rows/cells of the RecyclerView (creates a new holder for each)
    @Override
    public ActivityRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_activity, parent, false);
        return new ActivityRowViewHolder(view);
    }

    // Called when there is need to populate with data a recycled row/cell
    @Override
    public void onBindViewHolder(ActivityRowViewHolder holder, final int position) {

        final Experience experience = experiences.get(position);
        holder.syncViewFromModel(experience);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (ExperiencesAdapter.this.listener != null)
                    listener.onElementClicked(experience, position);
            }
        });
    }
}
