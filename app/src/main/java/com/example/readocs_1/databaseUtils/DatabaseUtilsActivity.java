package com.example.readocs_1.databaseUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class DatabaseUtilsActivity extends AppCompatActivity {

    //Создание БД
    public void createDatabaseOrIgnore() {
        //Создание или открытие базы данных "ReaDocs"
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание таблиц БД, если они не существуют
        database.execSQL("CREATE TABLE IF NOT EXISTS Documents (id INTEGER, Name TEXT, Path TEXT, Format TEXT, Size INTEGER, UNIQUE(id))");
        database.execSQL("CREATE TABLE IF NOT EXISTS Collections (id INTEGER, Name TEXT, UNIQUE(id))");
        database.execSQL("CREATE TABLE IF NOT EXISTS CollectionDocument (CollID INTEGER, DocID INTEGER, UNIQUE(CollID, DocID))");

        //Заполнение таблицы Collections первоначальными данными
        database.execSQL("INSERT OR IGNORE INTO Collections VALUES " +
                "(0, 'Избранное'), (1, 'Читаю'), (2, 'Отложено'), (3, 'Прочитано');");
        database.close();
    }

//Работа с документами
    public ArrayList<String> listExistingDocumentPaths = new ArrayList<>(); //Пути ко всем документам, существующим в папке Download
    //Добавление документа в базу данных, если он еще не добавлен
    public void addDocument(String nameDoc, String pathDoc, String formatDoc, int sizeDoc) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        Cursor queryDoc = database.rawQuery("SELECT * FROM Documents;", null);

        listExistingDocumentPaths.add(pathDoc);

        //Проверка по пути к файлу
        while (queryDoc.moveToNext()) {
            String path = queryDoc.getString(2);
            if (path.equals(pathDoc)){
                queryDoc.close();
                database.close();
                return;
            }
        }

        int id = getIdDocumentToCreate();
        database.execSQL("INSERT INTO Documents VALUES " +
                "(" + id + ", '" + nameDoc + "', '" + pathDoc + "', '" + formatDoc + "', " + sizeDoc + ");");

        queryDoc.close();
        database.close();
    }

    //Получение id для добавления документа, поиск пробела в списке id документов
    private int getIdDocumentToCreate() {
        ArrayList<Integer> listIdDocs = getListIdDocuments();
        int id = 0;
        while (listIdDocs.contains(id)) {
            id++;
        }
        return id;
    }

    //Получение списка id документов
    public ArrayList<Integer> getListIdDocuments(){
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        Cursor queryDoc = database.rawQuery("SELECT * FROM Documents;", null);

        ArrayList<Integer> listIdDocs = new ArrayList<>(); //Список документов

        while(queryDoc.moveToNext()){
            int idDoc = queryDoc.getInt(0);
            listIdDocs.add(idDoc);
        }
        queryDoc.close();
        database.close();

        return listIdDocs;
    }

    //Проверка существования всех документов из базы данных в папке Download
    public void checkExistenceDocuments(){

        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        Cursor queryDoc = database.rawQuery("SELECT * FROM Documents;", null);

        while (queryDoc.moveToNext()) {
            String path = queryDoc.getString(2);
            //Если в папке Download нет документа, удаление из базы данных
            if (!listExistingDocumentPaths.contains(path)) {
                deleteDocumentFromCollections(path);
                deleteDocumentFromDatabase(path);
            }
        }
        queryDoc.close();
        database.close();
    }


    //Изменение имени документа
    public void renameDocument(String newNameDoc, String newPathDoc, String oldPathDoc) {
        //Получение файла по старому пути документа
        String[] oldPath = oldPathDoc.split(" --- ");
        String folderNumber = oldPath[0], oldPathDocWithoutFolderNumber = oldPath[1];
        File oldFile = new File(oldPathDocWithoutFolderNumber);
        //Получение файла по новому пути документа
        String[] newPath = newPathDoc.split(" --- ");
        String newPathDocWithoutFolderNumber = newPath[1];
        File newFile = new File(newPathDocWithoutFolderNumber);
        //Проверка уникальности пути документа
        if (checkPathDoc(newPathDoc)) {
            if (oldFile.renameTo(newFile)) {
                renameDocumentInDatabase(newNameDoc, newPathDoc, oldPathDocWithoutFolderNumber, folderNumber);
                //Уведомление об успешном изменении имени
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Имя файла успешно изменено на " + newNameDoc, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //Уведомление об ошибке при изменении имени
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Не удалось изменить имя файла", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            //Уведомление о неуникальности имени
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Документ с таким именем уже существует", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Изменение имени документа в базе данных
    public void renameDocumentInDatabase(String newNameDoc, String newPathDoc, String oldPathDoc, String folderNumber) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);

        ContentValues values = new ContentValues();
        //Обновление имени документа в БД
        values.put("Name", newNameDoc);
        database.update("Documents", values, "id=?", new String[]{getIdDocumentByPath(oldPathDoc) + ""});
        //Обновление пути документа в БД
        values.put("Path", newPathDoc);
        database.update("Documents", values, "id=?", new String[]{getIdDocumentByPath(oldPathDoc) + ""});

        database.close();
    }

    //Проверка уникальности пути документа
    public Boolean checkPathDoc(String newPathDoc) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для вывода документов
        Cursor queryDoc = database.rawQuery("SELECT * FROM Documents;", null);

        while (queryDoc.moveToNext()) {
            String pathDoc = queryDoc.getString(2); //Имя документа в таблице Documents
            if (pathDoc.equals(newPathDoc)) {
                queryDoc.close();
                database.close();
                return false;
            }
        }
        queryDoc.close();
        database.close();
        return true;
    }

    //Удаление данных о документе
    public void deleteDocument(String nameDoc, String path) {
        //Получение файла по пути документа
        String[] pathDoc = path.split(" --- ");
        File file = new File(pathDoc[1]);

        if (file.delete()) {
            deleteDocumentFromCollections(path);
            deleteDocumentFromDatabase(path);
            //Уведомление об успешном удалении
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Файл " + nameDoc + " успешно удален", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //Уведомление об ошибке при удалении
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Не удалось удалить файл " + nameDoc, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Удаление документа из всех коллекций в базе данных
    public void deleteDocumentFromCollections(String pathDoc) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);

        int idDoc = getIdDocumentByPath(pathDoc); //Индекс документа в таблице Documents

        database.delete("CollectionDocument", "DocID=?", new String[]{Integer.toString(idDoc)});
        database.close();
    }

    //Удаление данных о документе из базы данных
    public void deleteDocumentFromDatabase(String pathDoc) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        database.delete("Documents", "id=?", new String[]{getIdDocumentByPath(pathDoc) + ""});
        database.close();
    }


    //Добавление указанного документа в указанную коллекцию
    public void addDocumentInCollection(String nameColl, String pathDoc) {
        int idDoc = getIdDocumentByPath(pathDoc); //Индекс документа в таблице Documents
        int idColl = getIdCollectionByName(nameColl); //Индекс коллекции в таблице Collections

        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        database.execSQL("INSERT INTO CollectionDocument VALUES (" + idColl + ", " + idDoc + ")");
        database.close();
    }

    //Удаление указанного документа из указанной коллекции
    public void deleteDocumentFromCollection(String nameColl, String pathDoc) {
        int idDoc = getIdDocumentByPath(pathDoc); //Индекс документа в таблице Documents
        int idColl = getIdCollectionByName(nameColl); //Индекс коллекции в таблице Collections

        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        database.delete("CollectionDocument", "CollID=? AND DocID=?", new String[]{Integer.toString(idColl), Integer.toString(idDoc)});
        database.close();
    }

    //Получение id документа по пути
    public int getIdDocumentByPath(String pathDoc) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для получения документов
        Cursor queryDoc = database.rawQuery("SELECT * FROM Documents;", null);

        //Получение id документа в таблице Documents
        int id = 0;
        while (queryDoc.moveToNext()) {
            String path = queryDoc.getString(2);
            if (path.contains(pathDoc)) {
                id = queryDoc.getInt(0);
                break;
            }
        }
        queryDoc.close();
        database.close();

        return id;
    }


//Работа с коллекциями
    //Получение id коллекции по имени
    public int getIdCollectionByName(String nameColl) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для получения коллекций
        Cursor queryColl = database.rawQuery("SELECT * FROM Collections;", null);

        int id = 0;
        while (queryColl.moveToNext()) {
            String name = queryColl.getString(1);
            if (name.equals(nameColl)) {
                id = queryColl.getInt(0);
                break;
            }
        }
        queryColl.close();
        database.close();

        return id;
    }

    //Добавление коллекции в базу данных
    public void addCollection(String nameColl) {
        if (nameColl.equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Имя коллекции не может быть пустым", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (checkNameColl(nameColl)) {
            SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
            Cursor queryColl = database.rawQuery("SELECT * FROM Collections;", null);

            int idColl = getIdCollectionToCreate();

            database.execSQL("INSERT INTO Collections VALUES " +
                    "(" + idColl + ", '" + nameColl + "');");
            queryColl.close();
            database.close();

            //Уведомление об успешном создании файла
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Коллекция \"" + nameColl + "\" успешно создана", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //Уведомление о неуникальности имени
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Коллекция с таким именем уже существует", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Получение id для создания пользовательской коллекции, поиск пробела в списке id коллекций
    private int getIdCollectionToCreate() {
        ArrayList<Integer> listIdColls = getListIdCollections();
        int id = 4;
        while (listIdColls.contains(id)) {
            id++;
        }
        return id;
    }

    //Получение списка id пользовательских коллекций
    public ArrayList<Integer> getListIdCollections(){
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        Cursor queryColl = database.rawQuery("SELECT * FROM Collections;", null);

        ArrayList<Integer> listIdColls = new ArrayList<>(); //Список коллекций

        while(queryColl.moveToNext()){
            int idColl = queryColl.getInt(0);
            if(idColl > 3){
                listIdColls.add(idColl);
            }
        }
        queryColl.close();
        database.close();

        return listIdColls;
    }

    //Изменение имени коллекции
    public void renameCollection(String newNameColl, String oldNameColl) {
        if (newNameColl.equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Имя коллекции не может быть пустым", Toast.LENGTH_SHORT);
            toast.show();
        } else if (checkNameColl(newNameColl)) {
            SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);

            ContentValues values = new ContentValues();
            //Обновление имени документа в БД, где Path = oldPath
            values.put("Name", newNameColl);
            database.update("Collections", values, "Name=?", new String[]{oldNameColl});
            database.close();

            //Уведомление об успешном изменении имени
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Имя файла успешно изменено на " + newNameColl, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //Уведомление о неуникальности имени
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Коллекция с таким именем уже существует", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Проверка уникальности имени коллекции
    public Boolean checkNameColl(String newNameColl) {
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        Cursor queryColl = database.rawQuery("SELECT * FROM Collections;", null);

        while (queryColl.moveToNext()) {
            String nameColl = queryColl.getString(1); //Имя коллекции в таблице Collection
            if (nameColl.equals(newNameColl)) {
                queryColl.close();
                database.close();
                return false;
            }
        }
        queryColl.close();
        database.close();
        return true;
    }

    //Удаление данных о коллекции
    public void deleteCollection(String nameColl) {
        int idColl = getIdCollectionByName(nameColl); //Индекс коллекции

        SQLiteDatabase database = getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Удаление коллекции
        database.delete("Collections", "id=?", new String[]{Integer.toString(idColl)});
        //Удаление всех докуаментов из коллекции
        database.delete("CollectionDocument", "CollID=?", new String[]{Integer.toString(idColl)});

        database.close();
    }
}
