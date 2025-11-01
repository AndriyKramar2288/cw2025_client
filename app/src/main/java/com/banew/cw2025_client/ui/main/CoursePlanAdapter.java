package com.banew.cw2025_client.ui.main;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto;
import com.banew.cw2025_client.R;

import java.util.List;
import java.util.stream.Collectors;

public class CoursePlanAdapter extends RecyclerView.Adapter<CoursePlanAdapter.ViewHolder> {

    private List<CoursePlanBasicDto> courses;

    public CoursePlanAdapter(List<CoursePlanBasicDto> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_plan, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        var course = courses.get(position);
        holder.courseName.setText(course.name());
        holder.courseAuthor.setText("by " + course.author().username());
        holder.courseDescription.setText(course.description());
        holder.courseTopics.setText(
                "Topics: " + course.topics().stream()
                        .map(CoursePlanBasicDto.TopicBasicDto::name)
                        .collect(Collectors.joining(", "))
        );
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<CoursePlanBasicDto> list) {
        courses = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseAuthor, courseDescription, courseTopics;
        ViewHolder(View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseName);
            courseAuthor = itemView.findViewById(R.id.courseAuthor);
            courseDescription = itemView.findViewById(R.id.courseDescription);
            courseTopics = itemView.findViewById(R.id.courseTopics);
        }
    }
}
