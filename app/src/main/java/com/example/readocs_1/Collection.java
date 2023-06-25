package com.example.readocs_1;

public class Collection {
    private final String collName; //Имя коллекции
    private final int collQuantityDocuments; //Количество документов в коллекции

    public Collection(String collName, int collQuantityDocuments) {
        this.collName = collName;
        this.collQuantityDocuments = collQuantityDocuments;
    }

    //Получение имени коллекции
    public String getCollName() {
        return collName;
    }

    //Получение количества документов в коллекции
    public int getCollQuantityDocuments() {
        return collQuantityDocuments;
    }
}