package com.example.hire.frgaments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.hire.R;
import com.example.hire.recyclerviewEducation.Education;
import com.example.hire.recyclerviewEducation.EducationAdapter;
import com.example.hire.recylerviewSkills.Skills;
import com.example.hire.databinding.FragmentManualFormBinding;
import com.example.hire.recylerviewSkills.SkillsAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ManualForm extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentManualFormBinding binding;
    private RadioButton radioButton;
    private ArrayList<Skills> skills;
    private ArrayList<Education> educations;
    private SkillsAdapter skillsAdapter;
    private EducationAdapter educationAdapter;

    private int radioId =0 ,undoRadioId =0;
    private boolean isCheckBoxChecked;

    private String undoName,undoPhone,undoEmail,undoAge,undoAddress;
    private ArrayList<Skills> undoSkills;
    private ArrayList<Education> undoEducations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentManualFormBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        skills = new ArrayList<>();
        educations = new ArrayList<>();

        //set up skills recyler view
        binding.recyclerViewSkills.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        skillsAdapter = new SkillsAdapter(skills);
        binding.recyclerViewSkills.setAdapter(skillsAdapter);

        //set up education recyler view
        binding.recyclerViewEducation.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        educationAdapter = new EducationAdapter(educations);
        binding.recyclerViewEducation.setAdapter(educationAdapter);

        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getActivity(),R.array.level,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSkills.setAdapter(adapter);
        binding.spinnerSkills.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),R.array.phone,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPhone.setAdapter(adapter1);
        binding.spinnerPhone.setOnItemSelectedListener(this);

        binding.buttonAddManualSkills.setOnClickListener(v -> {
            Skills newSkills = new Skills(binding.editTextManualSkills.getText().toString(),binding.spinnerSkills.getSelectedItem().toString());
            skills.add(newSkills);
            skillsAdapter.notifyDataSetChanged();
            //String selectedItem = binding.spinnerSkills.getSelectedItem().toString();
            binding.editTextManualSkills.getText().clear();
            //Toast.makeText(getActivity(),selectedItem,Toast.LENGTH_SHORT).show();

        });

        binding.buttonAddManualEducation.setOnClickListener( v -> {
            Education newEducation = new Education(binding.editTextManualEducation.getText().toString());
            educations.add(newEducation);
            educationAdapter.notifyDataSetChanged();
            binding.editTextManualEducation.getText().clear();
        });

        binding.buttonManualDone.setOnClickListener(v -> {
            if(binding.radioMale.isChecked() || binding.radioFemale.isChecked()){
                radioId = binding.radioGroupGender.getCheckedRadioButtonId();
                radioButton = view.findViewById(radioId);
                Toast.makeText(getActivity(),Integer.toString(radioId),Toast.LENGTH_SHORT).show();
            }else{
                radioId=0;
            }


            if(binding.checkBoxManualTerms.isChecked()){
                Toast.makeText(getActivity(),"Checked",Toast.LENGTH_SHORT).show();
                postDataToDatabase();
            }else{
                Toast.makeText(getActivity(),"Unchecked",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.manual_form_overflow,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.clearAll:
                saveClear();
                clearAll();
                showSnackBar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        if(parent.getId() == R.id.spinnerSkills){
            Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void saveClear(){
        undoName = binding.editTextManualName.getText().toString();
        undoPhone = binding.editTextManualPhoneNum.getText().toString();
        undoEmail = binding.editTextManualEmail.getText().toString();
        undoAge = binding.editTextManualAge.getText().toString();
        if(binding.radioMale.isChecked() || binding.radioFemale.isChecked()){
            radioId = binding.radioGroupGender.getCheckedRadioButtonId();
        }else{
            radioId=0;
        }
        undoRadioId = radioId;
        undoAddress = binding.editTextManualAddress.getText().toString();
        undoSkills = new ArrayList<>(skills);
        undoEducations = new ArrayList<>(educations);
        isCheckBoxChecked = binding.checkBoxManualTerms.isChecked();
    }

    public void undo(View view){
        binding.editTextManualName.setText(undoName);
        binding.editTextManualPhoneNum.setText(undoPhone);
        binding.editTextManualEmail.setText(undoEmail);
        binding.editTextManualAge.setText(undoAge);
        if(!(undoRadioId == 0)){
            radioButton = view.findViewById(undoRadioId);
            radioButton.setChecked(true);
            Toast.makeText(getActivity(),radioButton.getText(),Toast.LENGTH_SHORT).show();
        }
        binding.editTextManualAddress.setText(undoAddress);
        skills.addAll(undoSkills);
        educations.addAll(undoEducations);
        skillsAdapter.notifyDataSetChanged();
        educationAdapter.notifyDataSetChanged();
        binding.checkBoxManualTerms.setChecked(isCheckBoxChecked);


    }

    public void clearAll(){
        binding.editTextManualName.getText().clear();
        binding.editTextManualPhoneNum.getText().clear();
        binding.editTextManualEmail.getText().clear();
        binding.editTextManualAge.getText().clear();
        binding.radioGroupGender.clearCheck();
        //binding.radioFemale.setChecked(false);
        //binding.radioMale.setChecked(false);
        binding.editTextManualAddress.getText().clear();
        binding.editTextManualSkills.getText().clear();
        binding.editTextManualEducation.getText().clear();
        skills.removeAll(skills);
        educations.removeAll(educations);
        skillsAdapter.notifyDataSetChanged();
        educationAdapter.notifyDataSetChanged();
        binding.checkBoxManualTerms.setChecked(false);
    }

    private void postDataToDatabase() {
        //write your code here
    }

    public void showSnackBar(){
        Snackbar snackbar = Snackbar.make(binding.coordinateLayoutManualForm,R.string.snackbar_delete,Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.undo, v -> {
                    Snackbar snackbarUndo = Snackbar.make(binding.coordinateLayoutManualForm,R.string.undo_success,Snackbar.LENGTH_SHORT);
                    snackbarUndo.show();
                    undo(getView());
                });
        snackbar.show();
    }
}
