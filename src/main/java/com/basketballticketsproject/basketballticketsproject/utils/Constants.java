package com.basketballticketsproject.basketballticketsproject.utils;

public class Constants {

    public static String NOMBRE_PDF_ENTRADAS = "Entradas.pdf";

    public static String REPLACE_BASE64 = "data:application/pdf;base64,";

    public static final String DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm";

    //La contraseña tiene que tener: un numero, sin espacios, minuscula, mayuscula, un caracter epecial, y minimo 8 caracteres
    public static final String PASSWORD_REGEX = "^(?=.*\\d)(?=\\S+$)(?=.*[@#$%^&+=*_-])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    public static final int NUM_ENTRADAS = 50;

}
