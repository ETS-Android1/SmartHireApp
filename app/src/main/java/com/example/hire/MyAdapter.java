package com.example.hire;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    Context context;
    ArrayList<Employee> employees;
    private OnItemClickListener mListener;

    public MyAdapter(Context context, ArrayList<Employee> employees) {
        this.context = context;
        this.employees = employees;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_design,viewGroup,false); // this line inflate the cardView
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        Employee employeeCurrent = employees.get(i);
        myHolder.mName.setText(employeeCurrent.getmName()); // here i is the position
        myHolder.mPosition.setText(employeeCurrent.getmPosition());
        myHolder.textViewRecruitedDate.setText(employeeCurrent.getRecruitedDate());
        Picasso.get()
                .load(employeeCurrent.getmImageUrl())
                .fit()
                .centerCrop()
                .into(myHolder.mImageView);
        Log.d("IMAGE: ", "onBindViewHolder:  "+ employeeCurrent.getmImageUrl());
        //myHolder.mImageView.setImageResource(employees.get(i).getImg()); //here we use image resource because we will use images in our resource folder which is drawable

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        ImageView mImageView;
        TextView mName, mPosition,textViewRecruitedDate;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.mImageView = itemView.findViewById(R.id.imageViewPerson);
            this.mName = itemView.findViewById(R.id.textViewName);
            this.mPosition = itemView.findViewById(R.id.textViewPosition);
            this.textViewRecruitedDate = itemView.findViewById(R.id.textViewDateRecruited);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem phoneCall = menu.add(Menu.NONE, 1, 1, "Call");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            phoneCall.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}
