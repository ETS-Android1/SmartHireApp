package com.example.hire;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire.databinding.ActivityFabForEmployeeListBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements MyAdapter.OnItemClickListener {

    MyAdapter myAdapter;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private ArrayList<Employee> employees;
    private ValueEventListener mDBListener;

    private ActivityFabForEmployeeListBinding binding;

    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = ActivityFabForEmployeeListBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();

        //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right );

        binding.include.recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        //((AppCompatActivity)getActivity()).setSupportActionBar(binding.include.toolbarHomePage);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Hire");

        employees = new ArrayList<>();

        myAdapter = new MyAdapter( getActivity(),employees);

        binding.include.recyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(this);

        mStorage = FirebaseStorage.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        //NavHostFragment navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host);

        /*binding.fabAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_uploadFragment);
                //uploadResume();
            }
        });*/

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
                    binding.include.textViewNoItem.setVisibility(TextView.VISIBLE);
                }else{
                    binding.include.textViewNoItem.setVisibility(TextView.INVISIBLE);

                }

                //progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                //progressCircle.setVisibility(View.INVISIBLE);

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        binding.fabAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navController.navigate(R.id.action_homeFragment_to_uploadFragment);
                Intent intent = new Intent(getActivity(),HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void uploadResume(){

        Intent intent = new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
        Employee selectedItem = employees.get(position);
        String selectedKey = selectedItem.getKey();
        String name = selectedItem.getmName();
        String employeePosition = selectedItem.getmPosition();
        String employeePhoneNum = selectedItem.getmPhoneNumber();
        String employeeEmail = selectedItem.getmEmail();
        int employeeAge = selectedItem.getmAge();
        String employeeSkills = selectedItem.getmSkills();
        String employeeEducation = selectedItem.getmEducation();
        String employeeAddress = selectedItem.getmAddress();

        String profilePhoto = selectedItem.getmImageUrl();
        Bundle bundle = new Bundle();
        bundle.putString("EMPLOYEE_NAME",name);
        bundle.putString("EMPLOYEE_POSITION",employeePosition);
        bundle.putString("EMPLOYEE_PHONE",employeePhoneNum);
        bundle.putString("EMPLOYEE_EMAIL",employeeEmail);
        bundle.putInt("EMPLOYEE_AGE",employeeAge);
        bundle.putString("EMPLOYEE_SKILLS",employeeSkills);
        bundle.putString("EMPLOYEE_EDUCATION",employeeEducation);
        bundle.putString("EMPLOYEE_PHOTO",profilePhoto);
        bundle.putString("EMPLOYEE_ADDRESS",employeeAddress);
        navController.navigate(R.id.action_homeFragment_to_employeeProfile,bundle);
    }

    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {
        Employee selectedItem = employees.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
        final StorageReference imageResumeRef = mStorage.getReferenceFromUrl(selectedItem.getResumeImageUrl());

        Log.d("ERROR", "onDeleteClick: "+imageRef);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                imageResumeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabaseRef.child(selectedKey).removeValue();
                        Toast.makeText(getActivity(), "Employee deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.serach_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
