package com.example.readocs_1.ui.dialog;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.readocs_1.R;
import com.example.readocs_1.databaseUtils.DatabaseUtilsActivity;

public class RenameCollectionDialog extends DatabaseUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename_collection_dialog);

        //Изменение размера диалогового окна
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels / 2.4);
        params.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels / 1.2);
        this.getWindow().setAttributes(params);

        //Получение имени
        String name = getIntent().getStringExtra("collName");

        setTitle(name); //Установка заголовка активности

        EditText etName = findViewById(R.id.etName);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        etName.setText(name); //Вывод старого имени коллекции в EditText

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = etName.getText().toString(); //Получение введенного имени коллекции
                if (!newName.equals(name)){ //Проверка отличия нового имени от старого
                    renameCollection(newName, name);
                }
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