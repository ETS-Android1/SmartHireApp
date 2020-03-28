package com.example.hire;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    Context context;
    ArrayList<Employee> employees;

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

        myHolder.mName.setText(employees.get(i).getName()); // here i is the position
        myHolder.mPosition.setText(employees.get(i).getPosition());
        myHolder.mImageView.setImageResource(employees.get(i).getImg()); //here we use image resource because we will use images in our resource folder which is drawable

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }
}
