package com.example.readocs_1;

import android.annotation.SuppressLint;
import java.util.ArrayList;

public class Document {
    private final String docName; //Имя файла
    private final String docPath; //Путь к файлу
    private final String docFormat; //Формат файла
    private final float docSize; //Размер файла
    private final ArrayList<Integer> docCollections; //Коллекции файла

    public Document(String docName, String docPath, String docFormat, long docSize, ArrayList<Integer> docCollections) {
        this.docName = docName;
        this.docPath = docPath;
        this.docFormat = docFormat;
        this.docSize = docSize;
        this.docCollections = docCollections;
    }

    //Получение имени файла
    public String getDocName() {
        return docName;
    }
    //Получение имени файла для пользователя (без формата файла)
    public String getDocNameForUser() {
        String[] name = docName.split("\\.");
        return name[0];
    }

    //Получение пути к файлу
    public String getDocPath() {
        String[] path = docPath.split(" --- ");
        return path[1];
    }
    //Получение пути к файлу с номером папки
    public String getDocPathWithFolderNumber() {
        return docPath;
    }
    //Получение пути к файлу для пользователя (начиная с папки Download)
    public String getDocPathForUser() {
        String[] path = docPath.split("Download");
        return "Download" + path[1];
    }
    //Получение пути к файлу с уровнем его каталога для сортировки списка
    public String getDocPathLevel() {
        return docPath;
    }

    //Получение формата файла
    public String getDocFormat() {
        return docFormat;
    }

    @SuppressLint("DefaultLocale")
    //Получение размера файла
    public String getDocSize() {
        if (docSize >= 1024*1024*1024) return roundSize(docSize/(1024*1024*1024)) + " ГБ";
        else if (docSize >= 1024*1024) return roundSize(docSize/(1024*1024)) + " МБ";
        else if (docSize >= 1024) return roundSize(docSize/1024) + " КБ";
        else return roundSize(docSize) + " байт";
    }
    //Округление размера файла
    @SuppressLint("DefaultLocale")
    private String roundSize(float size){
        if(String.format("%.1f",size).endsWith(",0")){
            return Integer.toString((int)size);
        }
        else return String.format("%.1f", size);
    }

    //Получение коллекций файла
    public ArrayList<Integer> getDocCollections() {
        return docCollections;
    }
}