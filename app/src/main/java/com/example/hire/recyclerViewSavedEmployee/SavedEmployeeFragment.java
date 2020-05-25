package com.example.hire.recyclerViewSavedEmployee;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hire.CustomItemAnimation;
import com.example.hire.Employee;
import com.example.hire.R;
import com.example.hire.databinding.ActivityFabForEmployeeListBinding;
import com.example.hire.databinding.ActivityRecylerViewBinding;
import com.example.hire.databinding.FragmentSavedEmployeeBinding;
import com.example.hire.recyclerview.MyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class SavedEmployeeFragment extends Fragment {

    private FragmentSavedEmployeeBinding binding;
    private MyAdapter myAdapter;
    private ArrayList<Employee> employees;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedEmployeeBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
        binding.include.textViewNoItem.setVisibility(TextView.INVISIBLE);

        if(checkConnectivity()){

            binding.progressBarSavedEmployeeFragment.setVisibility(ProgressBar.INVISIBLE);
            mStorage = FirebaseStorage.getInstance();

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    employees.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Employee employee = postSnapshot.getValue(Employee.class);
                        employee.setKey(postSnapshot.getKey());

                            employees.add(employee);

                    }

                    myAdapter.addToeEmployeesFull(employees);

                    myAdapter.notifyDataSetChanged();

                    if(myAdapter.getItemCount()==0){

                        binding.textViewNoSavedEmployee.setVisibility(TextView.VISIBLE);

                    }else{

                        binding.textViewNoSavedEmployee.setVisibility(TextView.INVISIBLE);

                    }

                    //progressCircle.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    //progressCircle.setVisibility(View.INVISIBLE);

                }
            });
        }else{
            Toast.makeText(getActivity(),"No Internet",Toast.LENGTH_LONG).show();
        }
    }

    public void setUpRecyclerView(){

        binding.include.recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        myAdapter = new MyAdapter(getActivity(),employees);

        binding.include.recyclerView.setAdapter(myAdapter);

        binding.include.recyclerView.setItemAnimator(new CustomItemAnimation());

        //myAdapter.setOnItemClickListener(this);
    }

    public boolean checkConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }else{
            return false;
        }
    }
}
