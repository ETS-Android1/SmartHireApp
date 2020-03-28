package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class RecylerViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyler_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // it will create recyclerview in linear layout

        myAdapter = new MyAdapter(this,getMyList());

        recyclerView.setAdapter(myAdapter);

    }

    private ArrayList<Employee> getMyList(){

        ArrayList<Employee> employees = new ArrayList<>();

        Employee employee = new Employee();
        employee.setName("Tee Yu June");
        employee.setPosition("Manager");
        employee.setImg(R.drawable.extracted_email);

        employees.add(employee);

        return employees;
    }
}
