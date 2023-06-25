package com.example.readocs_1.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.readocs_1.Document;
import com.example.readocs_1.MainActivity;
import com.example.readocs_1.TemplateDocumentFragment;
import com.example.readocs_1.databinding.FragmentFavouritesBinding;

import java.util.ArrayList;

public class FavouritesFragment  extends TemplateDocumentFragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentFavouritesBinding binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        collName = "Избранное"; //Имя коллекции

        lvDocuments = binding.lvDocuments;
        tvNoDocuments = binding.tvNoDocuments;

        tvQuantityDocuments = binding.tvQuantityDocuments;
        btnFilterTxt = binding.btnFilterTxt;
        btnFilterPdf = binding.btnFilterPdf;
        btnFilterDocOrDocx = binding.btnFilterDocOrDocx;

        btnFilterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangeFilter(view);
            }
        });
        btnFilterPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangeFilter(view);
            }
        });
        btnFilterDocOrDocx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangeFilter(view);
            }
        });

        dropdownArea = binding.dropdownArea;
        ibDropdownShowOrHide = binding.ibDropdownShowOrHide;
        //Показать или скрыть выпадающую область фильтрации
        ibDropdownShowOrHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHideDropdown();
            }
        });

        //Передача текущего фрагмента в главное окно
        ((MainActivity) getActivity()).curFragment = this;
        ((MainActivity) getActivity()).textOfSearch = "";

        listDocuments = getListDocs();
        formationViews();
        return root;
    }
}