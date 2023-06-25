package com.example.readocs_1.ui.dialog;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.readocs_1.R;
import com.example.readocs_1.databaseUtils.DatabaseUtilsActivity;

public class CreateCollectionDialog extends DatabaseUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_collection_dialog);

        //Изменение размера диалогового окна
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels / 2.4);
        params.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels / 1.2);
        this.getWindow().setAttributes(params);

        setTitle("Новая коллекция"); //Установка заголовка активности

        EditText etName = findViewById(R.id.etName);
        Button btnCreate = findViewById(R.id.btnCreate);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = etName.getText().toString(); //Получение введенного имени коллекции
                addCollection(newName);
                finish();
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