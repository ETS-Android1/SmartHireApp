package com.example.hire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hire.databinding.ActivityRecylerViewBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecylerViewActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    MyAdapter myAdapter;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private ArrayList<Employee> employees;
    private ValueEventListener mDBListener;

    private ActivityRecylerViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecylerViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //progressCircle = findViewById(R.id.progress_circle);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this)); // it will create recyclerview in linear layout

        employees = new ArrayList<>();

        myAdapter = new MyAdapter(RecylerViewActivity.this, employees);

        binding.recyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(RecylerViewActivity.this);

        mStorage = FirebaseStorage.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");



        //myAdapter = new MyAdapter(this,getMyList());

        //recyclerView.setAdapter(myAdapter);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employees.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Employee employee = postSnapshot.getValue(Employee.class);
                    employee.setKey(postSnapshot.getKey());
                    employees.add(employee);
                }

                myAdapter.notifyDataSetChanged();

                //progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(RecylerViewActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                //progressCircle.setVisibility(View.INVISIBLE);

            }
        });


    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
        Employee selectedItem = employees.get(position);
        String selectedKey = selectedItem.getKey();
        String name = selectedItem.getmName();
        String employeePosition = selectedItem.getmPosition();
        String employeePhoneNum = selectedItem.getmPhoneNumber();
        String employeeEmail = selectedItem.getmEmail();
        int employeeAge = selectedItem.getmAge();
        String employeeSkills = selectedItem.getmSkills();
        String employeeEducation = selectedItem.getmEducation();

        String profilePhoto = selectedItem.getmImageUrl();
        Intent intent = new Intent(RecylerViewActivity.this,EmployeeProfile.class);
        intent.putExtra("EMPLOYEE_NAME",name);
        intent.putExtra("EMPLOYEE_POSITION",employeePosition);
        intent.putExtra("EMPLOYEE_PHONE",employeePhoneNum);
        intent.putExtra("EMPLOYEE_EMAIL",employeeEmail);
        intent.putExtra("EMPLOYEE_AGE",employeeAge);
        intent.putExtra("EMPLOYEE_SKILLS",employeeSkills);
        intent.putExtra("EMPLOYEE_EDUCATION",employeeEducation);
        intent.putExtra("EMPLOYEE_PHOTO",profilePhoto);
        startActivity(intent);


    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RecylerViewActivity.this, "Employee deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
