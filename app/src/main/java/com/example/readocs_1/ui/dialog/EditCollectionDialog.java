package com.example.readocs_1.ui.dialog;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.readocs_1.R;
import com.example.readocs_1.databaseUtils.DatabaseUtilsActivity;

public class EditCollectionDialog extends DatabaseUtilsActivity {

    private String name; //Имя коллекции

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_collection_dialog);

        //Изменение размера диалогового окна
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels / 3.3);
        params.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels / 1.1);
        this.getWindow().setAttributes(params);

        //Получение имени коллекции
        name = getIntent().getStringExtra("collName");

        setTitle(name); //Установка заголовка активности

        //Кнопки для редактирования коллекции
        Button btnRename = findViewById(R.id.btnRename);
        Button btnDelete = findViewById(R.id.btnDelete);

        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RenameCollectionDialog.class);
                intent.putExtra("collName", name);
                startActivity(intent);

                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DeleteCollectionDialog.class);
                intent.putExtra("collName", name);
                startActivity(intent);

                finish(); //Закрытие диалогового окна редактирования
            }
        });
    }
}