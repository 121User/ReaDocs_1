package com.example.readocs_1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.readocs_1.Collection;
import com.example.readocs_1.MainActivity;
import com.example.readocs_1.R;
import com.example.readocs_1.databaseUtils.DatabaseUtilsFragment;
import com.example.readocs_1.databinding.FragmentCollectionsBinding;
import com.example.readocs_1.ui.dialog.CreateCollectionDialog;
import com.example.readocs_1.ui.dialog.EditCollectionDialog;

import java.util.ArrayList;

public class CollectionsFragment  extends DatabaseUtilsFragment {

    public ListView lvCollections; //ListView для вывода списка коллекций
    public TextView tvNoCollections; //TextView с информацией об отсутствии коллекций

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentCollectionsBinding binding = FragmentCollectionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lvCollections = binding.lvCollections;
        tvNoCollections = binding.tvNoCollections;

        listCollections = getListCollections();
        formationViews();

        Button btnCreateCollection = binding.btnCreateCollection;
        btnCreateCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateCollectionDialog.class);
                startActivity(intent);
                listCollections = getListCollections();
                formationViews();
            }
        });

        //Передача текущего фрагмента в главное окно
        ((MainActivity) getActivity()).curFragment = this;
        ((MainActivity) getActivity()).textOfSearch = "";

        return root;
    }
    public ArrayList<Collection> listCollections = new ArrayList<>(); //Список коллекций

    //Создание адаптера для списка документов
    private final BaseAdapter adapter = new BaseAdapter() {
        //Получение количества документов в списке
        @Override public int getCount() {
            return listCollections.size();
        }
        //Получение документа из списка
        @Override public Collection getItem(int i) {
            return listCollections.get(i);
        }
        //Получение номера документа из списка
        @Override public long getItemId(int i) {
            return i;
        }
        //Вывод в список информации документа
        @Override public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.list_item_coll, viewGroup, false);
            }

            Collection coll = getItem(i);

            TextView name = v.findViewById(R.id.tvCollName);
            name.setText(coll.getCollName()); //Вывод имени коллекции
            TextView quantity = v.findViewById(R.id.tvQuantityDocuments);

            //Выбор окончания слова "Документ" и вывод количества документов в коллекции
            if(coll.getCollQuantityDocuments() % 100 > 10 && coll.getCollQuantityDocuments() % 100 < 15){
                String quantityInColl = coll.getCollQuantityDocuments() + " документов";
                quantity.setText(quantityInColl);
            }
            else if(coll.getCollQuantityDocuments() % 10 == 1){
                String quantityInColl = coll.getCollQuantityDocuments() + " документ";
                quantity.setText(quantityInColl);
            }
            else if(coll.getCollQuantityDocuments() % 10 == 2 || coll.getCollQuantityDocuments() % 10 == 3 || coll.getCollQuantityDocuments() % 10 == 4){
                String quantityInColl = coll.getCollQuantityDocuments() + " документа";
                quantity.setText(quantityInColl);
            }
            else{
                String quantityInColl = coll.getCollQuantityDocuments() + " документов";
                quantity.setText(quantityInColl);
            }
            return v;
        }
    };

    //Формирование представления списка коллекций
    public void formationViews() {
        //Установка адаптера в ListView
        lvCollections.setAdapter(adapter);
        //Открытие активности для просмотра списка документов выбранной коллекции
        lvCollections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), DocsFromCollectionActivity.class);
                intent.putExtra("collName", listCollections.get(i).getCollName());
                startActivity(intent);
            }
        });
        lvCollections.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), EditCollectionDialog.class);
                intent.putExtra("collName", listCollections.get(i).getCollName());
                startActivity(intent);

                return true;
            }
        });

        //Вывод информации об отсутствии коллекций
        if(lvCollections.getCount() == 0){
            lvCollections.setVisibility(View.GONE);
            tvNoCollections.setVisibility(View.VISIBLE);
        } else {
            lvCollections.setVisibility(View.VISIBLE);
            tvNoCollections.setVisibility(View.GONE);
        }
    }

    //Обновление списка при возвращении на фрагмент
    @Override
    public void onResume() {
        super.onResume();
        if(((MainActivity) getActivity()).textOfSearch.isEmpty()){
            listCollections = getListCollections();
            formationViews();
        }
        else {
            ((MainActivity) getActivity()).updateListWithSearch();
        }
    }
}