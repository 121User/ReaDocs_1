package com.example.readocs_1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.readocs_1.databaseUtils.DatabaseUtilsActivity;
import com.example.readocs_1.databinding.ActivityMainBinding;
import com.example.readocs_1.ui.AllDocumentsFragment;
import com.example.readocs_1.ui.CollectionsFragment;
import com.example.readocs_1.ui.DeferredFragment;
import com.example.readocs_1.ui.FavouritesFragment;
import com.example.readocs_1.ui.ReadFragment;
import com.example.readocs_1.ui.ReadNowFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends DatabaseUtilsActivity implements PermissionInterface {

    public Fragment curFragment; //Текущий фрагмент
    public String textOfSearch = ""; //Текст поиска
    private AppBarConfiguration mAppBarConfiguration;
    private static final int PERMISSION_STORAGE = 101; //Код разрешения на доступ к файлам
    //Получение пути до внешнего хранилища к папке Download
    private final String path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.purple_900)); //Установление цвета панели навигации

        //Получение представления
        com.example.readocs_1.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        //Добавление основных фрагментов в mAppBarConfiguration (у которых можно открыть меню по кнопке)
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_allDocuments, R.id.nav_readNow, R.id.nav_favourites, R.id.nav_deferred, R.id.nav_read, R.id.nav_collections, R.id.nav_help_information)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Проверка разрешения на доступ к файлам
        if (PermissionUtils.hasPermissions(this)) {
            //Разрешение предоставлено
            createDatabaseOrIgnore();
            formationDatabase(path, 0);
            checkExistenceDocuments();
        } else {
            //Разрешение не предоставлено
            PermissionDialog dialog = new PermissionDialog();
            dialog.show(getSupportFragmentManager(), "permission");
            dialog.setCancelable(false); //Диалоговое окно не закрывается, если нажать на кнопку "Назад" в панели навигации
        }
    }

    //Отправление запроса разрешения на доступ к файлам
    @Override
    public void reqPermission() {
        PermissionUtils.requestPermissions(this, PERMISSION_STORAGE);
    }

    //Вывод сообщения пользователю при отказе в доступе к файлам
    @Override
    public void notificationPermission() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Необходимо разрешение на доступ к файлам", Toast.LENGTH_SHORT);
        toast.show();
    }

    //Проверка наличия разрешения для версий Android 11 или выше и изменение возможностей
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PERMISSION_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (PermissionUtils.hasPermissions(this)) {
                    //Разрешение предоставлено
                    getBaseContext().deleteDatabase("ReaDocs.db");
                    createDatabaseOrIgnore();
                    formationDatabase(path, 0);
                } else {
                    // Разрешение не предоставлено
                    notificationPermission();
                    System.exit(0);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Проверка наличия разрешения для версий Android от 6 до 11 и изменение возможностей
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Разрешение предоставлено
                getBaseContext().deleteDatabase("ReaDocs.db");
                createDatabaseOrIgnore();
                formationDatabase(path, 0);
            } else {
                // Разрешение не предоставлено
                notificationPermission();
                System.exit(0);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Формирование базы данных (параметры: путь и номер текущего каталога)
    private void formationDatabase(String path, int numberOfDirectory) {
        try {
            int countDirectory = 0; //Количество подкаталогов в текущем каталоге
            java.io.File directory = new java.io.File(path); //Открытие переданного пути
            java.io.File[] fileList = directory.listFiles(); //Список файлов в исходной папке (Download)
            assert fileList != null;
            //Просмотр файлов и папок в исходной папке (Download)
            for (java.io.File file : fileList) {
                //Если папка, то просматриваем ее
                if (file.isDirectory()) {
                    countDirectory++;
                    int newNumber = numberOfDirectory*10 + numberOfDirectory + countDirectory;
                    formationDatabase(file.getAbsolutePath(), newNumber);
                } else {
                    String fileName = file.getName();
                    //Проверка формата файла и добавление в базу данных
                    if (fileName.endsWith(".pdf")) {
                        addDocument(fileName, numberOfDirectory + " --- " + file.getAbsolutePath(), "PDF", (int) file.length());
                    }
                    if (fileName.endsWith(".txt")) {
                        addDocument(fileName, numberOfDirectory + " --- " + file.getAbsolutePath(), "TXT", (int) file.length());
                    }
                    if (fileName.endsWith(".docx")) {
                        addDocument(fileName, numberOfDirectory + " --- " + file.getAbsolutePath(), "DOCX", (int) file.length());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Создание строки поиска и выпадающего меню фильтров в верхней части экрана
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

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

    //Переход вверх по иерархии действий приложения
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    //Обновление списка, учитывая текст поиска
    public void updateListWithSearch(){
        //Определение класса текущего фрагмента и получения списка в нем

        //Класс с коллекциями
        if (curFragment instanceof CollectionsFragment){
            CollectionsFragment curCollFragment = (CollectionsFragment)curFragment;
            ArrayList<Collection> listCollections =  curCollFragment.getListCollections();
            //Изменение списка коллекций
            if(textOfSearch.isEmpty()){
                curCollFragment.listCollections = listCollections;
                curCollFragment.formationViews();
                //Вывод информации об отсутствии коллекций с указанным названием
                if(listCollections.isEmpty()){
                    curCollFragment.lvCollections.setVisibility(View.GONE);
                    curCollFragment.tvNoCollections.setVisibility(View.VISIBLE);
                } else {
                    curCollFragment.lvCollections.setVisibility(View.VISIBLE);
                    curCollFragment.tvNoCollections.setVisibility(View.GONE);
                }
            } else{
                ArrayList<Collection> preparedListColls = searchInListCollections(listCollections, textOfSearch.toLowerCase());
                curCollFragment.listCollections = preparedListColls;
                curCollFragment.formationViews();
                //Вывод информации об отсутствии коллекций с указанным названием
                if(preparedListColls.isEmpty()){
                    curCollFragment.lvCollections.setVisibility(View.GONE);
                    curCollFragment.tvNoCollections.setVisibility(View.VISIBLE);
                } else {
                    curCollFragment.lvCollections.setVisibility(View.VISIBLE);
                    curCollFragment.tvNoCollections.setVisibility(View.GONE);
                }
            }
            return;
        }

        //Классы с документами
        ArrayList<Document> listDocuments = new ArrayList<>();
        if (curFragment instanceof AllDocumentsFragment){
            AllDocumentsFragment allFr = (AllDocumentsFragment)curFragment;

            allFr.listDocuments = allFr.getListDocs();
            //Проверка активности фильтрации
            if(!allFr.textOfFilter.isEmpty()) {
                allFr.updateListWithFilter();
            }
            listDocuments = allFr.listDocuments;
        }
        if (curFragment instanceof DeferredFragment){
            DeferredFragment deferredFr = (DeferredFragment)curFragment;

            deferredFr.listDocuments = deferredFr.getListDocs();
            //Проверка активности фильтрации
            if(!deferredFr.textOfFilter.isEmpty()) {
                deferredFr.updateListWithFilter();
            }
            listDocuments = deferredFr.listDocuments;
        }
        if (curFragment instanceof FavouritesFragment){
            FavouritesFragment favouritesFr = (FavouritesFragment)curFragment;

            favouritesFr.listDocuments = favouritesFr.getListDocs();
            //Проверка активности фильтрации
            if(!favouritesFr.textOfFilter.isEmpty()) {
                favouritesFr.updateListWithFilter();
            }
            listDocuments = favouritesFr.listDocuments;
        }
        if (curFragment instanceof ReadFragment){
            ReadFragment readFr = (ReadFragment)curFragment;

            readFr.listDocuments = readFr.getListDocs();
            //Проверка активности фильтрации
            if(!readFr.textOfFilter.isEmpty()) {
                readFr.updateListWithFilter();
            }
            listDocuments = readFr.listDocuments;
        }
        if (curFragment instanceof ReadNowFragment){
            ReadNowFragment readNowFr = (ReadNowFragment)curFragment;

            readNowFr.listDocuments = readNowFr.getListDocs();
            //Проверка активности фильтрации
            if(!readNowFr.textOfFilter.isEmpty()) {
                readNowFr.updateListWithFilter();
            }
            listDocuments = readNowFr.listDocuments;
        }

        TemplateDocumentFragment curDocFragment = (TemplateDocumentFragment)curFragment;
        //Изменение списка документов
        if(textOfSearch.isEmpty()){
            curDocFragment.listDocuments = listDocuments;
            curDocFragment.formationViews();
            //Вывод информации об отсутствии документов с указанным названием
            if(listDocuments.isEmpty()){
                curDocFragment.lvDocuments.setVisibility(View.GONE);
                curDocFragment.tvNoDocuments.setVisibility(View.VISIBLE);
            } else {
                curDocFragment.lvDocuments.setVisibility(View.VISIBLE);
                curDocFragment.tvNoDocuments.setVisibility(View.GONE);
            }
        } else{
            ArrayList<Document> preparedListDocs = searchInListDocuments(listDocuments, textOfSearch.toLowerCase());
            curDocFragment.listDocuments = preparedListDocs;
            curDocFragment.formationViews();
            //Вывод информации об отсутствии документов с указанным названием
            if(preparedListDocs.isEmpty()){
                curDocFragment.lvDocuments.setVisibility(View.GONE);
                curDocFragment.tvNoDocuments.setVisibility(View.VISIBLE);
            } else {
                curDocFragment.lvDocuments.setVisibility(View.VISIBLE);
                curDocFragment.tvNoDocuments.setVisibility(View.GONE);
            }
        }
    }

    //Поиск в списке документов по имени документа
    private ArrayList<Document> searchInListDocuments(ArrayList<Document> listDoc, String search){
        ArrayList<Document> preparedListDocs = new ArrayList<>();
        for(Document doc: listDoc){
            if(doc.getDocNameForUser().toLowerCase().contains(search)){
                preparedListDocs.add(doc);
            }
        }
        return preparedListDocs;
    }

    //Поиск в списке коллекций по имени коллекции
    private ArrayList<Collection> searchInListCollections(ArrayList<Collection> listColl, String search){
        ArrayList<Collection> preparedListColls = new ArrayList<>();
        for(Collection coll: listColl){
            if(coll.getCollName().toLowerCase().contains(search)){
                preparedListColls.add(coll);
            }
        }
        return preparedListColls;
    }
}