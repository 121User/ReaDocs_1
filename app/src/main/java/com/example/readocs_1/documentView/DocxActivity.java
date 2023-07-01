package com.example.readocs_1.documentView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readocs_1.R;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.IOException;

public class DocxActivity extends AppCompatActivity {

    private String path; //Путь к файлу
    private TextView textViewTxt; //TextView для вывода документа

    private int currentTextSize = 18; //Текущий размер шрифта документа


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt);
        //Представление кнопки Назад в ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //Изменение цвета ActionBar
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_500)));
        }

        getWindow().setNavigationBarColor(getResources().getColor(R.color.purple_900)); //Установление цвета панели навигации

        textViewTxt = findViewById(R.id.textViewTxt);

        //Получение данных документа из Intent
        path = getIntent().getStringExtra("filePath"); //Путь к файлу
        setTitle(getIntent().getStringExtra("fileName")); //Установка заголовка активности

        //Ползунок настройки масштаба (Минимум = 15, Максимум = 35, Начальное значение = 18)
        SeekBar sbScale = findViewById(R.id.sbScale);
        //Прослушиватель изменения значения ползунка
        sbScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Метод изменения значения ползунка
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Изменение мастштаба
                currentTextSize = seekBar.getProgress();
                displayTxtDoc();
            }
            //Метод начала изменения значения ползунка
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            //Метод окончания изменения значения ползунка
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        //Возвращение в начальный масштаб при долгом касании
        textViewTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                currentTextSize = 18;
                sbScale.setProgress(18);
                displayTxtDoc();
                return false;
            }
        });
    }

    @Override public void onStart() {
        super.onStart();
        displayTxtDoc();
    }

    //Чтение doc или docx документа и вывод
    private void displayTxtDoc(){
        textViewTxt.setTextSize((float)currentTextSize); //Изменение размера шрифта

        try (FileInputStream fileInput = new FileInputStream(path)) {
            XWPFDocument targetFile = new XWPFDocument(fileInput);
            //Создание объекта конструктора для извлечения текста из документа word
            XWPFWordExtractor wordExtractor = new XWPFWordExtractor(targetFile);
            String textOut = wordExtractor.getText();
            //Вывод текста документа
            textViewTxt.setText(textOut);
        } catch (IOException excevption) {
            Toast.makeText(this, "Документ защищен паролем.", Toast.LENGTH_SHORT).show();
            finish();
        }


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
}