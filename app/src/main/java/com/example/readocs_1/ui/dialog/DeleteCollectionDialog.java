package com.example.readocs_1.ui.dialog;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.readocs_1.R;
import com.example.readocs_1.databaseUtils.DatabaseUtilsActivity;

public class DeleteCollectionDialog extends DatabaseUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_document_dialog);

        //Изменение размера диалогового окна
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels / 3.5);
        params.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels / 1.1);
        this.getWindow().setAttributes(params);

        //Получение имени коллекции
        String name = getIntent().getStringExtra("collName");

        setTitle(name); //Установка заголовка активности

        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCollection(name);
                finish(); //Закрытие диалогового окна редактирования
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //Закрытие диалогового окна редактирования
            }
        });
    }
}