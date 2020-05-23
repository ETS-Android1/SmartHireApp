package com.example.hire.recyclerview;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire.Employee;
import com.example.hire.R;
import com.example.hire.database.EmployeeEntity;
import com.example.hire.database.HomeFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> implements Filterable {

    Context context;
    private List<Employee> employees;
    private List<Employee> employeesFull;
    private OnItemClickListener mListener;

    public MyAdapter(Context context, List<Employee> employees) {
        this.context = context;
        this.employees = employees;
    }

    public void addToeEmployeesFull(List<Employee> employee){
        employeesFull = new ArrayList<>(employee);
    }

    public void setEmployees(List<Employee> employees){
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
        myHolder.textViewRecruitedDate.setText("Recruited on: "+employeeCurrent.getRecruitedDate());
        //myHolder.mImageView.setImageURI(Uri.parse(employeeCurrent.getmImageUrl()));
        if(employeeCurrent.getmImageUrl().equals("noProfile")){

            myHolder.mImageView.setImageResource(R.drawable.ic_person);

        }else{
            Picasso.get()
                    .load(employeeCurrent.getmImageUrl())
                    .fit()
                    .centerCrop()
                    .into(myHolder.mImageView);
        }

        ViewCompat.setTransitionName(myHolder.mImageView, i + "_image");


        //Log.d("IMAGE: ", "onBindViewHolder:  "+ employeeCurrent.getmImageUrl());
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
                //ViewCompat.setTransitionName(v.findViewById(R.id.imageViewPerson), position + "_image");
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(v,position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            //menu.setHeaderIcon(R.drawable.ai_logo);
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
                            mListener.onCallEmployeeClick(position);
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

    public void deleteItem(int position) {
        //employees.remove(position);
        //notifyDataSetChanged();
        mListener.onDeleteClick(position);
    }

    public interface OnItemClickListener {

        void onItemClick(View v,int position);

        void onCallEmployeeClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Employee> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(employeesFull);
            }else{
                String filterPatter = constraint.toString().toLowerCase().trim();

                for(Employee item: employeesFull){
                    if(item.getmName().toLowerCase().contains(filterPatter)){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            employees.clear();
            if(results.values!=null){
                employees.addAll((List)results.values);
            }

            Log.d("RESULT", ""+results.values);
            notifyDataSetChanged();
        }
    };

}
