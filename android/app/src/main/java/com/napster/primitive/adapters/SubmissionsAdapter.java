package com.napster.primitive.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.napster.primitive.R;
import com.napster.primitive.pojo.Submission;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by napster on 05/01/17.
 */

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.ViewHolder>{

    ArrayList<Submission> submissions;

    public SubmissionsAdapter() {
        submissions = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Submission submission = submissions.get(position);
        holder.render(submission);
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    public void setSubmissions(ArrayList<Submission> submissions) {
        this.submissions.clear();
        this.submissions.addAll(submissions);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSubmission;
        TextView txtTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            imgSubmission = (ImageView) itemView.findViewById(R.id.imgSubmission);
        }

        public void render(Submission submission) {

            if(submission.isProcessed() && submission.getProcessedUri() != null && !submission.getProcessedUri().isEmpty()) {
                Picasso
                        .with(itemView.getContext())
                        .load(submission.getProcessedUri())
                        .into(imgSubmission);
            } else if(submission.getFileKey() != null && !submission.getFileKey().isEmpty()){
                String url = "https://s3.amazonaws.com/prim-out/" + submission.getFileKey();
                Picasso
                        .with(itemView.getContext())
                        .load(url)
                        .into(imgSubmission);
            } else {
                Picasso
                        .with(itemView.getContext())
                        .load(submission.getOriginalUri())
                        .resize(500, 0)
                        .into(imgSubmission);
            }
        }
    }
}
