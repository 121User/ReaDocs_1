package com.example.readocs_1.databaseUtils;

import static android.content.Context.MODE_PRIVATE;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.fragment.app.Fragment;

import com.example.readocs_1.Collection;
import com.example.readocs_1.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DatabaseUtilsFragment extends Fragment {

//Работа с документами
    //Получение списка документов из указанной коллекции
    public ArrayList<Document> getListDocuments(String nameCollection){
        SQLiteDatabase database = requireActivity().getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для вывода документов
        Cursor queryDoc = database.rawQuery("SELECT * FROM Documents;", null);
        Cursor queryColl = database.rawQuery("SELECT * FROM Collections;", null);
        Cursor queryCollDoc = database.rawQuery("SELECT * FROM CollectionDocument;", null);

        ArrayList<Document> listDocuments = new ArrayList<>(); //Список документов

        //Получение списка для фрагмента AllDocumentsFragment
        if (nameCollection.equals("Все документы")){
            while(queryDoc.moveToNext()){
                int idDoc = queryDoc.getInt(0);
                String nameDoc = queryDoc.getString(1);
                String pathDoc = queryDoc.getString(2);
                String formatDoc = queryDoc.getString(3);
                int sizeDoc = queryDoc.getInt(4);
                ArrayList<Integer> collectionsDoc = getListCollectionsWithDocument(idDoc);

                listDocuments.add(new Document(nameDoc, pathDoc, formatDoc, sizeDoc, collectionsDoc));
            }
        } else {
            //Проверка принадлежности документа к указанной коллекции и добавление в список
            //Обход таблицы Documents
            while(queryDoc.moveToNext()){
                int idDoc = queryDoc.getInt(0); //Номер документа в таблице Documents
                //Обход таблицы CollectionDocument
                while(queryCollDoc.moveToNext()){
                    int idDocInCollDoc = queryCollDoc.getInt(1); //Номер документа в таблице CollectionDocument
                    if (idDoc == idDocInCollDoc){
                        int idCollInCollDoc = queryCollDoc.getInt(0); //Номер коллекции в таблице CollectionDocument
                        //Обход таблицы Collections
                        while(queryColl.moveToNext()){
                            int idColl = queryColl.getInt(0); //Номер коллекции в таблице Collections
                            if (idColl == idCollInCollDoc){
                                String nameColl= queryColl.getString(1);
                                if (nameColl.equals(nameCollection)){
                                    String name = queryDoc.getString(1);
                                    String path = queryDoc.getString(2);
                                    String format = queryDoc.getString(3);
                                    int sizeDoc = queryDoc.getInt(4);
                                    ArrayList<Integer> collections = getListCollectionsWithDocument(idDoc);

                                    listDocuments.add(new Document(name, path, format, sizeDoc, collections));
                                }
                                break;
                            }
                        }
                        queryColl.close();
                        queryColl = database.rawQuery("SELECT * FROM Collections;", null);
                    }
                }
                queryCollDoc.close();
                queryCollDoc = database.rawQuery("SELECT * FROM CollectionDocument;", null);
            }
        }
        queryDoc.close();
        queryColl.close();
        queryCollDoc.close();
        database.close();

        listDocuments = sortListDocuments(listDocuments);

        return listDocuments;
    }

    //Сортировка списка документов по уровню и пути
    private ArrayList<Document> sortListDocuments(ArrayList<Document> listDocuments){
        Collections.sort(listDocuments, new Comparator<Document>() {
            @Override
            public int compare(Document doc1, Document doc2) {
                return doc1.getDocPathLevel().compareToIgnoreCase(doc2.getDocPathLevel());
            }
        });
        return listDocuments;
    }

    //Получение списка id коллекций, в которых есть документ
    public ArrayList<Integer> getListCollectionsWithDocument(int idDoc) {
        SQLiteDatabase database = requireActivity().getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для получения коллекций
        Cursor queryCollDoc = database.rawQuery("SELECT * FROM CollectionDocument;", null);

        ArrayList<Integer> collectionsWithDoc = new ArrayList<>();
        while(queryCollDoc.moveToNext()){
            int idDocInCollDoc = queryCollDoc.getInt(1);
            if (idDoc == idDocInCollDoc){
                int idCollInCollDoc = queryCollDoc.getInt(0);
                collectionsWithDoc.add(idCollInCollDoc);
            }
        }
        return collectionsWithDoc;
    }


//Работа с коллекциями
    //Получение списка пользовательских коллекций
    public ArrayList<Collection> getListCollections(){
        SQLiteDatabase database = requireActivity().getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для вывода документов
        Cursor queryColl = database.rawQuery("SELECT * FROM Collections;", null);

        ArrayList<Collection> listCollections = new ArrayList<>(); //Список коллекций

        while(queryColl.moveToNext()){
            int idColl = queryColl.getInt(0);
            if(idColl > 3) {
                String nameColl = queryColl.getString(1);
                int quantityDocumentsInColl = getQuantityDocumentsInCollection(idColl);
                listCollections.add(new Collection(nameColl, quantityDocumentsInColl));
            }
        }
        queryColl.close();
        database.close();

        listCollections = sortListCollections(listCollections);

        return listCollections;
    }

    //Сортировка списка коллекций по имени
    private ArrayList<Collection> sortListCollections(ArrayList<Collection> listCollections){
        Collections.sort(listCollections, new Comparator<Collection>() {
            @Override
            public int compare(Collection coll1, Collection coll2) {
                return coll1.getCollName().compareToIgnoreCase(coll2.getCollName());
            }
        });
        return listCollections;
    }

    //Получение количества документов в коллекции
    public int getQuantityDocumentsInCollection(int idColl) {
        SQLiteDatabase database = requireActivity().getBaseContext().openOrCreateDatabase("ReaDocs.db", MODE_PRIVATE, null);
        //Создание запроса к БД для получения коллекций
        Cursor queryCollDoc = database.rawQuery("SELECT * FROM CollectionDocument;", null);

        ArrayList<Integer> documentsInCollections = new ArrayList<>();
        while(queryCollDoc.moveToNext()){
            int idCollInCollDoc = queryCollDoc.getInt(0);
            if (idColl == idCollInCollDoc){
                int idDocInCollDoc = queryCollDoc.getInt(0);
                documentsInCollections.add(idDocInCollDoc);
            }
        }
        return documentsInCollections.size();
    }
}
