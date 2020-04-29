package com.example.hire.frgaments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.hire.R;
import com.example.hire.recylerviewSkills.Skills;
import com.example.hire.databinding.FragmentManualFormBinding;

import java.util.ArrayList;

public class ManualForm extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentManualFormBinding binding;
    private RadioButton radioButton;
    private ArrayList<Skills> skills;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentManualFormBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        skills = new ArrayList<>();

        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getActivity(),R.array.level,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSkills.setAdapter(adapter);
        binding.spinnerSkills.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),R.array.phone,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPhone.setAdapter(adapter1);
        binding.spinnerPhone.setOnItemSelectedListener(this);

        binding.buttonAddManualSkills.setOnClickListener(v -> {
            String selectedItem = binding.spinnerSkills.getSelectedItem().toString();
            binding.editTextManualSkills.getText().clear();
            Toast.makeText(getActivity(),selectedItem,Toast.LENGTH_SHORT).show();
        });

        binding.buttonAddManualEducation.setOnClickListener( v -> {
            binding.editTextManualEducation.getText().clear();
        });

        binding.buttonManualDone.setOnClickListener(v -> {
            if(radioButton!=null){
                int radioId = binding.radioGroupGender.getCheckedRadioButtonId();
                radioButton = view.findViewById(radioId);
                Toast.makeText(getActivity(),radioButton.getText(),Toast.LENGTH_SHORT).show();
            }
            if(binding.checkBoxManualTerms.isChecked()){
                Toast.makeText(getActivity(),"Checked",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),"Unchecked",Toast.LENGTH_SHORT).show();
            }
        });


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
}
