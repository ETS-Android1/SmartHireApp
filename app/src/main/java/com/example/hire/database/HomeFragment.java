package com.example.hire.database;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire.Employee;
import com.example.hire.StartExtractActivity;
import com.example.hire.recyclerview.MyAdapter;
import com.example.hire.R;
import com.example.hire.databinding.ActivityFabForEmployeeListBinding;
import com.example.hire.recyclerview.SwipeToDeleteCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeFragment extends Fragment implements MyAdapter.OnItemClickListener {

    MyAdapter myAdapter;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private ArrayList<Employee> employees;
    private ValueEventListener mDBListener;

    private ActivityFabForEmployeeListBinding binding;
    private EmployeeViewModel mEmployeeViewModel;

    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = ActivityFabForEmployeeListBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)  {
        super.onViewCreated(view, savedInstanceState);

        employees = new ArrayList<>();

        navController = Navigation.findNavController(view);

        /*mEmployeeViewModel = new ViewModelProvider(this).get(EmployeeViewModel.class);

        mEmployeeViewModel.getAllWords().observe(getViewLifecycleOwner(), new Observer<List<EmployeeEntity>>() {
            @Override
            public void onChanged(@Nullable final List<EmployeeEntity> employees) {
                // Update the cached copy of the words in the adapter.
                myAdapter.setEmployees(employees);
            }
        });*/

        //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right );

        //set up recycler view
        setUpEmployeeRecyclerView();
        setUpSwipeRecyclerView();

        if(checkConnectivity()){

            binding.progressBarRecylerView.setVisibility(ProgressBar.INVISIBLE);
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
        }else{
            Toast.makeText(getActivity(),"No Internet",Toast.LENGTH_LONG).show();
        }

        binding.fabAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navController.navigate(R.id.action_homeFragment_to_uploadFragment);
                // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Recruit Options")
                        .setItems(R.array.recruit_options, (dialog, options) -> {
                            if(options==0){
                                navController.navigate(R.id.action_homeFragment_to_manualForm);
                                Toast.makeText(getActivity(),"Manually Recruit",Toast.LENGTH_LONG).show();
                            }else if(options ==1){
                                Toast.makeText(getActivity(),"A.I. Recruit",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), StartExtractActivity.class);
                                startActivity(intent);
                            }
                        });

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                builder.create().show();
                //Intent intent = new Intent(getActivity(), StartExtractActivity.class);
                //startActivity(intent);
            }
        });
    }

    public void setUpEmployeeRecyclerView(){

        binding.include.recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        myAdapter = new MyAdapter(getActivity(),employees);

        binding.include.recyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(this);

        //set up swipe to delete functions
        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(myAdapter));

        //itemTouchHelper.attachToRecyclerView(binding.include.recyclerView);


    }

    public void setUpSwipeRecyclerView(){

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                switch (direction){
                    case ItemTouchHelper.RIGHT:
                        onCallEmployeeClick(viewHolder.getAdapterPosition());
                        myAdapter.notifyDataSetChanged();
                        break;
                    case ItemTouchHelper.LEFT:
                        onDeleteClick(viewHolder.getAdapterPosition());
                        Toast.makeText(getActivity(),getString(R.string.delete),Toast.LENGTH_SHORT).show();
                        myAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(),R.color.green))
                        .addSwipeLeftActionIcon(R.drawable.delete)
                        .addSwipeRightActionIcon(R.drawable.employee_call)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.include.recyclerView);

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
        String employeeResume = selectedItem.getResumeImageUrl();

        /*Intent intent = new Intent(getActivity(), EmployeeProfile.class);
        intent.putExtra("EMPLOYEE_NAME",name);
        intent.putExtra("EMPLOYEE_POSITION",employeePosition);
        intent.putExtra("EMPLOYEE_PHONE",employeePhoneNum);
        intent.putExtra("EMPLOYEE_EMAIL",employeeEmail);
        intent.putExtra("EMPLOYEE_AGE",employeeAge);
        intent.putExtra("EMPLOYEE_SKILLS",employeeSkills);
        intent.putExtra("EMPLOYEE_EDUCATION",employeeEducation);
        intent.putExtra("EMPLOYEE_PHOTO",profilePhoto);
        intent.putExtra("EMPLOYEE_ADDRESS",employeeAddress);
        startActivity(intent);*/


        Bundle bundle = new Bundle();
        bundle.putString("EMPLOYEE_NAME",name);
        bundle.putString("EMPLOYEE_POSITION",employeePosition);
        bundle.putString("EMPLOYEE_PHONE",employeePhoneNum);
        bundle.putString("EMPLOYEE_EMAIL",employeeEmail);
        bundle.putInt("EMPLOYEE_AGE",employeeAge);
        bundle.putString("EMPLOYEE_SKILLS",employeeSkills);
        bundle.putString("EMPLOYEE_EDUCATION",employeeEducation);
        bundle.putString("EMPLOYEE_PHOTO",profilePhoto);
        bundle.putString("EMPLOYEE_RESUME",employeeResume);
        bundle.putString("EMPLOYEE_ADDRESS",employeeAddress);
        navController.navigate(R.id.action_homeFragment_to_employeeProfile,bundle);
    }

    @Override
    public void onCallEmployeeClick(int position) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + employees.get(position).getmPhoneNumber()));
        startActivity(intent);
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

        //showUndoSnackbar();

    }

    /*private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(binding.coordinateLayoutEmployeeList, R.string.snackbar_delete, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.undo, v -> {
                    Snackbar snackbarUndo = Snackbar.make(binding.coordinateLayoutEmployeeList, R.string.undo_success, Snackbar.LENGTH_SHORT);
                    snackbarUndo.show();
                    undoDelete(getView());
                });
        snackbar.show();
    }*/

    /*private void undoDelete(View view) {
        mListItems.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDBListener!=null){
            mDatabaseRef.removeEventListener(mDBListener);
        }
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
