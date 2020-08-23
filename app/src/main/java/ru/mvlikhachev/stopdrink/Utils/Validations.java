package ru.mvlikhachev.stopdrink.Utils;

import com.google.android.material.textfield.TextInputLayout;

public class Validations {

    public static boolean validateName(TextInputLayout textInputName) {

        String nameInput = textInputName
                .getEditText()
                .getText()
                .toString()
                .trim();

        if (nameInput.isEmpty()) {
            textInputName.setError("Введите Ваше имя!");
            return false;
        } else if (nameInput.length() > 10) {
            textInputName.setError("Имя должно быть меньше 15 символов!");
            return false;
        } else {
            textInputName.setError("");
            return true;
        }
    }
}
