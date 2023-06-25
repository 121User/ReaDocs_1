package com.example.readocs_1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class PermissionDialog extends DialogFragment {

    private PermissionInterface mainActivity;

    //Получение контекста MainActivity с помощью интерфейса PermissionInterface при вызове диалогового окна
    //Для выполнения методов при нажатии на кнопку в диалоговом окне
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        mainActivity = (PermissionInterface) context;
    }

    @NonNull
    @SuppressLint("DialogFragmentCallbacksDetector")
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        AlertDialog dialog = builder
                .setTitle("Доступ к файлам") //Название диалогового окна
                .setMessage("Для работы приложения необходимо дать разрешение на доступ к файлам." +
                        "\nБез разрешения приложение не сможет работать.") //Сообщение в диалоговом окне
                .setPositiveButton("Разрешить",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.reqPermission();
                    }
                })
                .setNegativeButton("Отклонить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.notificationPermission();
                        System.exit(0);
                    }
                })
                .create();

        dialog.show();

        //Изменение цвета текста кнопок
        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if(buttonNegative != null) {
            buttonNegative.setTextColor(Color.WHITE);
        }
        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if(buttonPositive != null) {
            buttonPositive.setTextColor(Color.WHITE);
        }
        return dialog;
    }
}