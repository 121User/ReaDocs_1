package com.example.readocs_1.documentView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;

import android.graphics.pdf.PdfRenderer;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readocs_1.R;

import java.io.File;
import java.io.IOException;

public class PdfActivity extends AppCompatActivity {

    private static final String CURRENT_PAGE = ""; //Номер текущей страницы
    private String path; //Путь к файлу
    private ImageView imgViewPdf; //ImageView для вывода страницы документа
    private ImageButton ibBack, ibNext; //Кнопки перемещения между страницами "Назад" и "Вперед"

    private int currentPage = 0; //Текущий номер страницы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        //Представление кнопки Назад в ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //Изменение цвета ActionBar
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_500)));
        }

        getWindow().setNavigationBarColor(getResources().getColor(R.color.purple_900)); //Установление цвета панели навигации

        //Получение данных документа из Intent
        path = getIntent().getStringExtra("filePath"); //Путь к файлу
        setTitle(getIntent().getStringExtra("fileName")); //Установка заголовка активности

        //Получение номера страницы
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(CURRENT_PAGE, 0);
        }
        imgViewPdf = findViewById(R.id.imgViewPdf);
        ibBack = findViewById(R.id.ibBack);
        ibNext = findViewById(R.id.ibNext);

        //Ползунок настройки масштаба (Минимум = 3, Максимум = 15, Начальное значение = 5)
        SeekBar sbScale = findViewById(R.id.sbScale);
        //Прослушиватель изменения значения ползунка
        sbScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Метод изменения значения ползунка
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Изменение мастштаба
                currentZoomLevel = seekBar.getProgress();
                displayPage(curPage.getIndex());
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
        imgViewPdf.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                currentZoomLevel = 5;
                sbScale.setProgress(5);
                displayPage(curPage.getIndex());
                return false;
            }
        });
    }

    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page curPage; //Текущая страница
    private ParcelFileDescriptor descriptor; //Дескриптор документа
    private float currentZoomLevel = 5; //Текущий параметр масштабирования

    @Override public void onStart() {
        super.onStart();
        try {
            openPdfRenderer();
            displayPage(currentPage);
        } catch (Exception e) {
//            Toast.makeText(this, "Документ защищен паролем.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //Подготовка документа к открытию
    private void openPdfRenderer() {
        File file = new File(path);
        descriptor = null;
        pdfRenderer = null;
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(descriptor);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //
    private void displayPage(int index) {
        if (pdfRenderer.getPageCount() <= index) return;
        //Закрытие текущей страницы
        if (curPage != null) curPage.close();
        //Открытие нужной страницы
        curPage = pdfRenderer.openPage(index);
        //Определение размеров и создание изображения
        int newWidth = (int) (getResources().getDisplayMetrics().widthPixels * curPage.getWidth() / 72
                * currentZoomLevel / 45);
        int newHeight =
                (int) (getResources().getDisplayMetrics().heightPixels * curPage.getHeight() / 72
                        * currentZoomLevel / 90);
        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        curPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        //Отображение результата
        imgViewPdf.setImageBitmap(bitmap);
        //Проверка доступности кнопок
        int pageCount = pdfRenderer.getPageCount();
        if (index != 0) ibBack.setVisibility(View.VISIBLE);
        else ibBack.setVisibility(View.INVISIBLE);
        if (index + 1 < pageCount) ibNext.setVisibility(View.VISIBLE);
        else ibNext.setVisibility(View.INVISIBLE);
    }
    //Сохранение номера текущей страницы
    @Override protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (curPage != null) {
            outState.putInt(CURRENT_PAGE, curPage.getIndex());
        }
    }

    //Прослушиватель кнопок перемещения между страницами
    @SuppressLint("NonConstantResourceId")
    public void onClickMove(View view) {
        switch (view.getId()) {
            case R.id.ibBack: {
                //Переход на предыдущую страницу
                int index = curPage.getIndex() - 1;
                displayPage(index);
                break;
            }
            case R.id.ibNext: {
                //Переход на следующую страницу
                int index = curPage.getIndex() + 1;
                displayPage(index);
                break;
            }
        }
    }

    @Override public void onDestroy() {
        try {
            closePdfRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    //Закрытие документа
    private void closePdfRenderer() throws IOException {
        if (curPage != null) curPage.close();
        if (pdfRenderer != null) pdfRenderer.close();
        if (descriptor != null) descriptor.close();
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
