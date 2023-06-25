package com.example.readocs_1.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readocs_1.Document;
import com.example.readocs_1.R;
import com.example.readocs_1.documentView.PdfActivity;
import com.example.readocs_1.documentView.TxtActivity;
import com.example.readocs_1.ui.dialog.EditDocumentDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DocsFromCollectionActivity extends AppCompatActivity {

    private String collName; //Имя коллекции
    private ListView lvDocuments; //ListView для вывода списка документов
    private TextView tvNoDocuments; //TextView с информацией об отсутствии документов в коллекции
    private ImageButton ibDropdownShowOrHide; //Кнопка для показа или скрытия выпадающей области
    private LinearLayout dropdownArea; //Выпадающая область с инйормацией о количестве документов и кнопок смены фильтрации
    private Button btnFilterTxt, btnFilterPdf, btnFilterDocOrDocx; //Кнопки смены фильтрации списка документов
    private TextView tvQuantityDocuments; //TextView для вывода количества документов в списке
    public String textOfFilter = ""; //Строка фильтрации
    private String textOfSearch = ""; //Текст поиска


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs_from_collection);
        //Представление кнопки Назад в ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //Изменение цвета ActionBar
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_500)));
        }
        //Установление цвета панели навигации
        getWindow().setNavigationBarColor(getResources().getColor(R.color.purple_900));

        //Получение имени коллекции из Intent
        collName = getIntent().getStringExtra("collName");
        setTitle(collName); //Установка заголовка активности

        lvDocuments = findViewById(R.id.lvDocuments);
        tvNoDocuments = findViewById(R.id.tvNoDocuments);

        tvQuantityDocuments = findViewById(R.id.tvQuantityDocuments);
        btnFilterTxt = findViewById(R.id.btnFilterTxt);
        btnFilterPdf = findViewById(R.id.btnFilterPdf);
        btnFilterDocOrDocx = findViewById(R.id.btnFilterDocOrDocx);

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

        dropdownArea = findViewById(R.id.dropdownArea);
        ibDropdownShowOrHide = findViewById(R.id.ibDropdownShowOrHide);
        //Показать или скрыть выпадающую область фильтрации
        ibDropdownShowOrHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHideDropdown();
            }
        });

        listDocuments = getListDocs();
        formationViews();
    }

    //Обновление списка при возвращении на фрагмент
    @Override
    public void onResume() {
        super.onResume();
        if(textOfSearch.isEmpty() && textOfFilter.isEmpty()){
            listDocuments = getListDocs();
            formationViews();
        }
        else {
            updateListWithSearch();
            updateListWithFilter();
        }
    }

    //Отключение фильтрации и поиска при выходе
    @Override
    public void onDestroy() {
        super.onDestroy();
        textOfFilter = "";
        textOfSearch = "";
    }

    //Возвращение к главному окну
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Document> listDocuments = new ArrayList<>(); //Список документов

    //Создание адаптера для списка документов
    private final BaseAdapter adapter = new BaseAdapter() {
        //Получение количества документов в списке
        @Override public int getCount() {
            return listDocuments.size();
        }
        //Получение документа из списка
        @Override public Document getItem(int i) {
            return listDocuments.get(i);
        }
        //Получение номера документа из списка
        @Override public long getItemId(int i) {
            return i;
        }
        //Вывод в список информации документа
        @Override public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.list_item_doc, viewGroup, false);
            }

            Document file = getItem(i);
            TextView name = v.findViewById(R.id.tvFileName);
            name.setText(file.getDocNameForUser()); //Вывод имени файла
            TextView path = v.findViewById(R.id.tvFilePath);
            path.setText(file.getDocPathForUser()); //Вывод пути к файлу
            TextView format = v.findViewById(R.id.tvFileFormat);
            format.setText(file.getDocFormat()); //Вывод формата файла
            TextView size = v.findViewById(R.id.tvFileSize);
            size.setText(file.getDocSize()); //Вывод размера файла

            ImageView ivFileFavouriteStatus = v.findViewById(R.id.ivFileFavouriteStatus);
            ivFileFavouriteStatus.setImageResource(R.drawable.ic_menu_space); //Удаление статуса "Избранное"
            for (int idColl: file.getDocCollections()){
                if (idColl == 0){
                    ivFileFavouriteStatus.setImageResource(R.drawable.ic_menu_favourites); //Добавление статуса "Избранное"
                    break;
                }
            }

            ImageView ivFileReadStatus = v.findViewById(R.id.ivFileReadStatus);
            ivFileReadStatus.setImageResource(R.drawable.ic_menu_space); //Удаление статуса Чтения
            for (int id: file.getDocCollections()){
                switch (id) {
                    case(1):
                        ivFileReadStatus.setImageResource(R.drawable.ic_menu_read_now); //Добавление статуса "Читаю"
                        break;
                    case(2):
                        ivFileReadStatus.setImageResource(R.drawable.ic_menu_deferred); //Добавление статуса "Отложено"
                        break;
                    case(3):
                        ivFileReadStatus.setImageResource(R.drawable.ic_menu_read); //Добавление статуса "Прочитано"
                        break;
                }
            }

            return v;
        }
    };

    //Формирование представления списка документов
    private void formationViews() {
        lvDocuments.setAdapter(adapter); //Установка адаптера в ListView
        updateQuantityDocuments(); //Обновление вывода количества документов
        //Открытие активности для просмотра документа при выборе документа из списка
        lvDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                //Открытие активности для PDF-файла
                if (listDocuments.get(i).getDocFormat().equals("PDF")) {
                    intent = new Intent(view.getContext(), PdfActivity.class);
                }
                //Открытие активности для TXT, DOC или DOCX файла
                else{
                    intent = new Intent(view.getContext(), TxtActivity.class);
                }
                intent.putExtra("fileName", listDocuments.get(i).getDocName());
                intent.putExtra("filePath", listDocuments.get(i).getDocPath());
                startActivity(intent);
            }
        });
        lvDocuments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), EditDocumentDialog.class);
                intent.putExtra("fileName", listDocuments.get(i).getDocName());
                intent.putExtra("filePath", listDocuments.get(i).getDocPathWithFolderNumber());
                intent.putExtra("fileCollections", listDocuments.get(i).getDocCollections());
                startActivity(intent);

                return true;
            }
        });

        //Вывод информации об отсутствии документов в коллекции
        if(lvDocuments.getCount() == 0){
            lvDocuments.setVisibility(View.GONE);
            tvNoDocuments.setVisibility(View.VISIBLE);
        } else {
            lvDocuments.setVisibility(View.VISIBLE);
            tvNoDocuments.setVisibility(View.GONE);
        }
    }

    //Получение списка документов из указанной коллекции
    public ArrayList<Document> getListDocuments(String nameCollection){
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для вывода документов
        Cursor queryDoc = database.rawQuery("SELECT * FROM Documents;", null);
        Cursor queryColl = database.rawQuery("SELECT * FROM Collections;", null);
        Cursor queryCollDoc = database.rawQuery("SELECT * FROM CollectionDocument;", null);

        ArrayList<Document> listDocuments = new ArrayList<>(); //Список документов

        //Проверка принадлежности документа к указанной коллекции и добавление в список
        //Обход таблицы Documents
        while(queryDoc.moveToNext()){
            int idDoc = queryDoc.getInt(0); //Номер документа в таблице Documents
            //Обход таблицы CollectionDocument
            while(queryCollDoc.moveToNext()){
                int idDocInCollDoc = queryCollDoc.getInt(1); //Номер документа в таблице CollectionDocument
                if (idDoc == idDocInCollDoc){
                    int idCollInCollDoc = queryCollDoc.getInt(0); //Номер коллекции в таблице CollectionDocument
                    //Обход таблицы Collections
                    while(queryColl.moveToNext()){
                        int idColl = queryColl.getInt(0); //Номер коллекции в таблице Collections
                        if (idColl == idCollInCollDoc){
                            String nameColl= queryColl.getString(1);
                            if (nameColl.equals(nameCollection)){
                                String name = queryDoc.getString(1);
                                String path = queryDoc.getString(2);
                                String format = queryDoc.getString(3);
                                int sizeDoc = queryDoc.getInt(4);
                                ArrayList<Integer> collections = getListCollectionsWithDocument(idDoc);

                                listDocuments.add(new Document(name, path, format, sizeDoc, collections));
                            }
                            break;
                        }
                    }
                    queryColl.close();
                    queryColl = database.rawQuery("SELECT * FROM Collections;", null);
                }
            }
            queryCollDoc.close();
            queryCollDoc = database.rawQuery("SELECT * FROM CollectionDocument;", null);
        }
        queryDoc.close();
        queryColl.close();
        queryCollDoc.close();
        database.close();

        listDocuments = sortListDocuments(listDocuments);

        return listDocuments;
    }

    //Сортировка списка документов
    private ArrayList<Document> sortListDocuments(ArrayList<Document> listDocuments){
        Collections.sort(listDocuments, new Comparator<Document>() {
            @Override
            public int compare(Document doc1, Document doc2) {
                return doc1.getDocPath().compareToIgnoreCase(doc2.getDocPath());
            }
        });
        return listDocuments;
    }

    //Получение списка id коллекций, в которых есть документ
    public ArrayList<Integer> getListCollectionsWithDocument(int idDoc) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для получения коллекций
        Cursor queryCollDoc = database.rawQuery("SELECT * FROM CollectionDocument;", null);

        ArrayList<Integer> collectionsWithDoc = new ArrayList<>();
        while(queryCollDoc.moveToNext()){
            int idDocInCollDoc = queryCollDoc.getInt(1);
            if (idDoc == idDocInCollDoc){
                int idCollInCollDoc = queryCollDoc.getInt(0);
                collectionsWithDoc.add(idCollInCollDoc);
            }
        }
        return collectionsWithDoc;
    }


    //Создание выпадающего меню настроек экрана
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        //Обработка поиска
        searchView.setQueryHint("Введите название документа");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textOfSearch = s;
                updateListWithSearch();

                return true;
            }
        });
        return true;
    }

    //Обновление списка, учитывая текст поиска
    public void updateListWithSearch(){
        listDocuments = getListDocs();
        //Проверка активности фильтрации
        if(!textOfFilter.isEmpty()) {
            updateListWithFilter();
        }
        ArrayList<Document> listDocs = listDocuments;

        //Изменение списка документов
        if(textOfSearch.isEmpty()){
            formationViews();
            //Вывод информации об отсутствии документов с указанным названием
            if(listDocs.isEmpty()){
                lvDocuments.setVisibility(View.GONE);
                tvNoDocuments.setVisibility(View.VISIBLE);
            } else {
                lvDocuments.setVisibility(View.VISIBLE);
                tvNoDocuments.setVisibility(View.GONE);
            }
        } else{
            ArrayList<Document> preparedListDocs = searchInListDocuments(listDocs, textOfSearch.toLowerCase());
            listDocuments = preparedListDocs;
            formationViews();
            //Вывод информации об отсутствии документов с указанным названием
            if(preparedListDocs.isEmpty()){
                lvDocuments.setVisibility(View.GONE);
                tvNoDocuments.setVisibility(View.VISIBLE);
            } else {
                lvDocuments.setVisibility(View.VISIBLE);
                tvNoDocuments.setVisibility(View.GONE);
            }
        }
    }

    //Поиск в списке документов по имени документа
    private ArrayList<Document> searchInListDocuments(ArrayList<Document> listDoc, String search){
        ArrayList<Document> filteredListDoc = new ArrayList<>();
        for(Document doc: listDoc){
            if(doc.getDocNameForUser().toLowerCase().contains(search)){
                filteredListDoc.add(doc);
            }
        }
        return filteredListDoc;
    }

    //Получение списка документов текущей коллекции
    public ArrayList<Document> getListDocs(){
        return getListDocuments(collName);
    }


    //Показать или скрыть выпадающую область фильтрации
    public void showOrHideDropdown(){
        if(dropdownArea.getVisibility() == View.GONE){
            dropdownArea.setVisibility(View.VISIBLE);
            ibDropdownShowOrHide.setImageResource(R.drawable.ic_dropdown_area_hide);
        }
        else {
            dropdownArea.setVisibility(View.GONE);
            ibDropdownShowOrHide.setImageResource(R.drawable.ic_dropdown_area_show);
        }
    }


    //Изменение фильтрации списка документов
    public void onClickChangeFilter(View view) {
        Button curButton = (Button)view; //Текущая кнопка

        //Список кнопок смены фильтра
        ArrayList<Button> listButtons = new ArrayList<Button>();
        listButtons.add(btnFilterTxt);
        listButtons.add(btnFilterPdf);
        listButtons.add(btnFilterDocOrDocx);

        //Если строка фильтра пуста или она не равна тексту текущей кнопки смены фильтрации, то изменение строки и обновление списка
        if (textOfFilter.isEmpty() || !textOfFilter.equals(curButton.getText().toString())){
            //Проверка активности поиска для выбора списка документов
            if(textOfSearch.isEmpty()){
                listDocuments = getListDocs();
                formationViews();
            } else {
                textOfFilter = "";
                updateListWithSearch();
            }

            textOfFilter = curButton.getText().toString();
            updateListWithFilter();

            curButton.setBackgroundColor(Color.parseColor("#74217D"));
            //Снятие выделения для остальных кнопок смены фильтрации списка
            for (Button button: listButtons) {
                if (button != curButton){
                    button.setBackgroundColor(Color.parseColor("#FF311F46"));
                }
            }
        } else { //Иначе выключение фильтрации и вывод полного списка
            textOfFilter = "";
            //Если поиск не активен, вывод полного списка
            if(textOfSearch.isEmpty() ){
                listDocuments = getListDocs();
                formationViews();
            }
            else { //Иначе вывод списка с учетом поиска
                updateListWithSearch();
            }
            curButton.setBackgroundColor(Color.parseColor("#FF311F46"));
        }
    }

    //Обновление списка документов с учетом текущей фильтрации
    public void updateListWithFilter(){
        ArrayList<Document> preparedListDocs = new ArrayList<>();
        for(Document doc: listDocuments){
            if(textOfFilter.equals("DOC | DOCX")){
                if(doc.getDocFormat().equals("DOC")){
                    preparedListDocs.add(doc);
                }
                else if(doc.getDocFormat().equals("DOCX")){
                    preparedListDocs.add(doc);
                }
            }
            else{
                if(doc.getDocFormat().contains(textOfFilter)){
                    preparedListDocs.add(doc);
                }
            }
        }
        listDocuments = preparedListDocs;
        formationViews();
    }

    //Изменение вывода количества документов в списке
    public void updateQuantityDocuments(){
        //Выбор окончания слова "Документ" и вывод количества документов в коллекции
        int quantity = listDocuments.size();
        String stringQuantity;
        if(quantity % 100 > 10 && quantity % 100 < 15){
            stringQuantity = quantity + " документов";
        }
        else if(quantity % 10 == 1){
            stringQuantity = quantity + " документ";
        }
        else if(quantity % 10 == 2 || quantity % 10 == 3 || quantity % 10 == 4){
            stringQuantity = quantity + " документа";
        }
        else{
            stringQuantity = quantity + " документов";
        }
        tvQuantityDocuments.setText(stringQuantity);
    }
}