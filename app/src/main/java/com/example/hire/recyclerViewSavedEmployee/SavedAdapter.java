package com.example.hire.recyclerViewSavedEmployee;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire.R;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedHolder> {

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SavedHolder extends RecyclerView.ViewHolder{
        ImageView mImageView, verifiedLogo, bookmarkedLogo;
        TextView mName, mPosition,textViewRecruitedDate;

        public SavedHolder (@NonNull View itemView) {
            super(itemView);

            this.mImageView = itemView.findViewById(R.id.imageViewPerson);
            this.mName = itemView.findViewById(R.id.textViewName);
            this.mPosition = itemView.findViewById(R.id.textViewPosition);
            this.textViewRecruitedDate = itemView.findViewById(R.id.textViewDateRecruited);
            this.verifiedLogo = itemView.findViewById(R.id.imageViewVerifiedCardView);
            this.bookmarkedLogo = itemView.findViewById(R.id.imageViewBookmarkCardView);

        }

    }
}
