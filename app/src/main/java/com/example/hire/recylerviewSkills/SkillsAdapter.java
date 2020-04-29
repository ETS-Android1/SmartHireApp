package com.example.hire.recylerviewSkills;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire.R;

import java.util.List;

public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.SkillsHolder> {

    private List<Skills> skills;

    @NonNull
    @Override
    public SkillsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_design,parent,false);
        return new SkillsAdapter.SkillsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillsHolder holder, int position) {
        Skills currentSkills = skills.get(position);
        holder.textViewHolderSkillsPosition.setText(currentSkills.toString());
        holder.textViewHolderSkillsSkills.setText(currentSkills);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SkillsHolder extends RecyclerView.ViewHolder{

        TextView textViewHolderSkillsPosition, textViewHolderSkillsSkills,textViewHolderSkillsLevel;
        Button buttonHolderSkillsDelete;

        public SkillsHolder(@NonNull View itemView) {
            super(itemView);

            textViewHolderSkillsPosition = itemView.findViewById(R.id.textViewHolderSkillsPosition);
            textViewHolderSkillsSkills = itemView.findViewById(R.id.textViewHolderSkillsSkills);
            textViewHolderSkillsLevel = itemView.findViewById(R.id.textViewHolderSkillsLevel);

        }
    }
}


