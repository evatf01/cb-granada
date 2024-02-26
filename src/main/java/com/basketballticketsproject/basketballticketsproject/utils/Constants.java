package com.basketballticketsproject.basketballticketsproject.utils;

public class Constants {

    public static String NOMBRE_PDF_ENTRADAS = "Entradas.pdf";

    public static String REPLACE_BASE64 = "data:application/pdf;base64,";

    public static final String DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm";
    public static final String DATE_FORMATTER_CARPTETAS = "yyyy-MM-dd";

    //La contraseña tiene que tener: un numero, sin espacios, minuscula, mayuscula, un caracter epecial, y minimo 8 caracteres
    public static final String PASSWORD_REGEX = "^(?=.*\\d)(?=\\S+$)(?=.*[@#$%^&+=*_-])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    public static final String ENTRADAS_PATH = "../Entradas";

    public static final int NUM_ENTRADAS = 50;

    public static String EMAIL_ASUNTO = "PARTIDOS GRANADA";


    public static String EMAIL_MENSAJE = "Partidos disponibles actualmente: \n";


}
