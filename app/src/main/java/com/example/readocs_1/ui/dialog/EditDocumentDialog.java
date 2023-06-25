package com.example.readocs_1.ui.dialog;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.readocs_1.R;
import com.example.readocs_1.databaseUtils.DatabaseUtilsActivity;

import java.util.ArrayList;

public class EditDocumentDialog extends DatabaseUtilsActivity {

    //Кнопки для редактирования документа
    private Button btnFavourite, btnReadNow, btnDeferred, btnRead, btnCollections, btnRename, btnDelete;
    private String name, path; //Имя и путь к файлу
    private ArrayList<Integer> collections; //Коллекции файла

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document_dialog);

        //Изменение размера диалогового окна
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels / 1.6);
        params.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels / 1.1);
        this.getWindow().setAttributes(params);

        //Получение имени, пути, коллекций файла
        name = getIntent().getStringExtra("fileName");
        path = getIntent().getStringExtra("filePath");

        collections = getIntent().getIntegerArrayListExtra("fileCollections");

        setTitle(name); //Установка заголовка активности

        btnFavourite = findViewById(R.id.btnFavourite);
        btnReadNow = findViewById(R.id.btnReadNow);
        btnDeferred = findViewById(R.id.btnDeferred);
        btnRead = findViewById(R.id.btnRead);
        btnCollections = findViewById(R.id.btnCollections);
        btnRename = findViewById(R.id.btnRename);
        btnDelete = findViewById(R.id.btnDelete);

        changePrimaryColorButton();

        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int colorBtn = ((ColorDrawable)btnFavourite.getBackground()).getColor(); //Получение цвета кнопки
                //Если кнопка не выделена (документа нет в коллекции "Избранное"), то добавление в коллекцию и выделение кнопки
                if (colorBtn == Color.parseColor("#00FFFFFF")){
                    addDocumentInCollection("Избранное", path);
                    btnFavourite.setBackgroundColor(Color.parseColor("#74217D"));
                } else { //Иначе, удаление из коллекции и снятие выделения кнопки
                    deleteDocumentFromCollection("Избранное", path);
                    btnFavourite.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                }
            }
        });

        btnCollections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddDocumentToCollectionDialog.class);
                intent.putExtra("fileName", name);
                intent.putExtra("filePath", path);
                intent.putExtra("fileCollections", collections);
                startActivity(intent);

                finish();
            }
        });

        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RenameDocumentDialog.class);
                intent.putExtra("fileName", name);
                intent.putExtra("filePath", path);
                startActivity(intent);

                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DeleteDocumentDialog.class);
                intent.putExtra("fileName", name);
                intent.putExtra("filePath", path);
                startActivity(intent);

                finish(); //Закрытие диалогового окна редактирования
            }
        });
    }

    //Изменение цветов кнопок
    @SuppressLint("ResourceAsColor")
    private void changePrimaryColorButton(){
        btnFavourite.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        btnReadNow.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        btnDeferred.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        btnRead.setBackgroundColor(Color.parseColor("#00FFFFFF"));

        for (int id: collections){
            switch (id) {
                case(0):
                    btnFavourite.setBackgroundColor(Color.parseColor("#74217D"));
                    break;
                case(1):
                    btnReadNow.setBackgroundColor(Color.parseColor("#74217D"));
                    break;
                case(2):
                    btnDeferred.setBackgroundColor(Color.parseColor("#74217D"));
                    break;
                case(3):
                    btnRead.setBackgroundColor(Color.parseColor("#74217D"));
                    break;
            }
        }
    }

    //Изменение статуса чтения документа при нажатии на кнопки статуса чтения
    public void onClickChangeReadStatus(View view) {
        Button curButton = (Button)view; //Текущая кнопка

        //Список кнопок смены статуса чтения
        ArrayList<Button> listButtons = new ArrayList<Button>();
        listButtons.add(btnReadNow);
        listButtons.add(btnDeferred);
        listButtons.add(btnRead);

        int colorCurBtn = ((ColorDrawable)curButton.getBackground()).getColor();
        //Если кнопка не выделена (документа нет в выбранной коллекции), то добавление в коллекцию и выделение кнопки
        if (colorCurBtn == Color.parseColor("#00FFFFFF")){
            addDocumentInCollection(curButton.getText().toString(), path);
            curButton.setBackgroundColor(Color.parseColor("#74217D"));
            //Снятие выделения для остальных кнопок статуса чтения и удаление документа из остальных коллекций
            for (Button button: listButtons) {
                if (button != curButton){
                    int colorBtn = ((ColorDrawable)curButton.getBackground()).getColor();
                    if (colorBtn == Color.parseColor("#74217D")) {
                        deleteDocumentFromCollection(button.getText().toString(), path);
                        button.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                    }
                }
            }
        } else { //Иначе, удаление из коллекции и снятие выделения кнопки
            deleteDocumentFromCollection(curButton.getText().toString(), path);
            curButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        }
    }
}