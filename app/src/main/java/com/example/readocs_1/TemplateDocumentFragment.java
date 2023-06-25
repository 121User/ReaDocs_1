package com.example.readocs_1;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.readocs_1.databaseUtils.DatabaseUtilsFragment;
import com.example.readocs_1.documentView.PdfActivity;
import com.example.readocs_1.documentView.TxtActivity;
import com.example.readocs_1.ui.dialog.EditDocumentDialog;

import java.util.ArrayList;

public class TemplateDocumentFragment extends DatabaseUtilsFragment {

    public String collName = ""; //Имя текущей коллекции (Переопределяется в каждом фрагменте)

    public ListView lvDocuments; //ListView для вывода списка документов
    public TextView tvNoDocuments; //TextView с информацией об отсутствии документов в коллекции
    public ImageButton ibDropdownShowOrHide; //Кнопка для показа или скрытия выпадающей области
    public LinearLayout dropdownArea; //Выпадающая область с инйормацией о количестве документов и кнопок смены фильтрации
    public Button btnFilterTxt, btnFilterPdf, btnFilterDocOrDocx; //Кнопки смены фильтрации списка документов
    public TextView tvQuantityDocuments; //TextView для вывода количества документов в списке
    public String textOfFilter = ""; //Строка фильтрации

    public ArrayList<Document> listDocuments = new ArrayList<>(); //Список документов


    //Создание адаптера для списка документов
    public final BaseAdapter adapter = new BaseAdapter() {
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

            ImageView ivDocFavouriteStatus = v.findViewById(R.id.ivFileFavouriteStatus);
            ivDocFavouriteStatus.setImageResource(R.drawable.ic_menu_space); //Удаление статуса "Избранное"
            for (int idColl: file.getDocCollections()){
                if (idColl == 0){
                    ivDocFavouriteStatus.setImageResource(R.drawable.ic_menu_favourites); //Добавление статуса "Избранное"
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
    public void formationViews() {
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
            if(((MainActivity) getActivity()).textOfSearch.isEmpty()){
                listDocuments = getListDocs();
                formationViews();
            } else {
                textOfFilter = "";
                ((MainActivity) getActivity()).updateListWithSearch();
            }

            textOfFilter = curButton.getText().toString();
            updateListWithFilter();

            ibDropdownShowOrHide.setColorFilter(Color.parseColor("#74217D"));
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
            if(((MainActivity) getActivity()).textOfSearch.isEmpty() ){
                listDocuments = getListDocs();
                formationViews();
            }
            else { //Иначе вывод списка с учетом поиска
                ((MainActivity) getActivity()).updateListWithSearch();
            }
            ibDropdownShowOrHide.setColorFilter(Color.parseColor("#FFD7D7D7"));
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

    //Обновление списка при возвращении на фрагмент
    @Override
    public void onResume() {
        super.onResume();
        if (PermissionUtils.hasPermissions(getContext())) {
            //Разрешение предоставлено
            if(((MainActivity) getActivity()).textOfSearch.isEmpty() && textOfFilter.isEmpty()){
                listDocuments = getListDocs();
                formationViews();
            }
            else {
                ((MainActivity) getActivity()).updateListWithSearch();
                updateListWithFilter();
            }
        }
    }

    //Отключение фильтрации при переходе на другой фрагмент
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textOfFilter = "";
    }

    //Получение списка документов текущей коллекции
    public ArrayList<Document> getListDocs(){
        return getListDocuments(collName);
    }
}