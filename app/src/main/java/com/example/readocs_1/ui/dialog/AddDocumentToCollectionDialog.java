package com.example.readocs_1.ui.dialog;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readocs_1.Collection;
import com.example.readocs_1.R;
import com.example.readocs_1.databaseUtils.DatabaseUtilsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddDocumentToCollectionDialog extends DatabaseUtilsActivity {

    private String name, path; //Имя и путь к файлу
    private ArrayList<Integer> listCollections; //Коллекции файла
    private ListView lvCollections; //ListView для вывода списка коллекций

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document_to_collection_dialog);

        //Изменение размера диалогового окна
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels / 1.7);
        params.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels / 1.1);
        this.getWindow().setAttributes(params);

        //Получение имени, пути, коллекций файла
        name = getIntent().getStringExtra("fileName");
        path = getIntent().getStringExtra("filePath");
        listCollections = getIntent().getIntegerArrayListExtra("fileCollections");

        setTitle(name); //Установка заголовка активности

        lvCollections = findViewById(R.id.lvCollections);
    }
    private ArrayList<String> listCollectionNames = new ArrayList<>(); //Список коллекций

    //Создание адаптера для списка документов
    private final BaseAdapter adapter = new BaseAdapter() {
        //Получение количества документов в списке
        @Override public int getCount() {
            return listCollectionNames.size();
        }
        //Получение документа из списка
        @Override public String getItem(int i) {
            return listCollectionNames.get(i);
        }
        //Получение номера документа из списка
        @Override public long getItemId(int i) {
            return i;
        }
        //Вывод в список информации документа
        @Override public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.list_item_add_to_coll, viewGroup, false);
            }
            String collName = getItem(i);

            TextView name = v.findViewById(R.id.tvCollName);
            name.setText(collName); //Вывод имени коллекции

            LinearLayout layoutListItem = v.findViewById(R.id.layoutListItem);
            layoutListItem.setBackgroundColor(Color.parseColor("#FF311F46"));
            for (int collId: listCollections){
                if(collId > 3 && getIdCollectionByName(collName) == collId){
                    layoutListItem.setBackgroundColor(Color.parseColor("#74217D"));
                }
            }
            return v;
        }
    };

    //Формирование представления списка коллекций
    private void formationViews() {
        formationList();
        //Установка адаптера в ListView
        lvCollections.setAdapter(adapter);
        //Открытие активности для просмотра списка документов выбранной коллекции
        lvCollections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LinearLayout layoutListItem = view.findViewById(R.id.layoutListItem);
                //Получение текущего цвета коллекции
                int background = ((ColorDrawable)layoutListItem.getBackground()).getColor();
                //Изменение цвета заднего фона коллекции
                if (background == Color.parseColor("#FF311F46")) {
                    layoutListItem.setBackgroundColor(Color.parseColor("#74217D"));
                    addDocumentInCollection(listCollectionNames.get(i), path);
                }else{
                    layoutListItem.setBackgroundColor(Color.parseColor("#FF311F46"));
                    deleteDocumentFromCollection(listCollectionNames.get(i), path);
                }
            }
        });
    }

    //Обновление списка при возвращении на фрагмент
    @Override
    public void onResume() {
        super.onResume();
        formationViews();
    }

    //Формирование списка документов
    private void formationList() {
        listCollectionNames = getListCollectionNames();
        if(listCollectionNames.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Пользовательских коллекций не найдено", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    //Получение списка имен пользовательских коллекций
    public ArrayList<String> getListCollectionNames(){
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        Cursor queryColl = database.rawQuery("SELECT * FROM Collections;", null);

        ArrayList<String> listCollectionNames = new ArrayList<>(); //Список коллекций

        while(queryColl.moveToNext()){
            int idColl = queryColl.getInt(0);
            if(idColl > 3) {
                String nameColl = queryColl.getString(1);
                listCollectionNames.add(nameColl);
            }
        }
        queryColl.close();
        database.close();

        listCollectionNames = sortListCollections(listCollectionNames);

        return listCollectionNames;
    }

    //Сортировка списка имен коллекций
    private ArrayList<String> sortListCollections(ArrayList<String> listCollectionNames){
        Collections.sort(listCollectionNames);
        return listCollectionNames;
    }
}