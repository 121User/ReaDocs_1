package com.example.readocs_1.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.readocs_1.databinding.FragmentHelpInformationBinding;

public class HelpInformationFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentHelpInformationBinding binding = FragmentHelpInformationBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}