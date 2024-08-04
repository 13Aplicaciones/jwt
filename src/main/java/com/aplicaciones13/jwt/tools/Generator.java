package com.aplicaciones13.jwt.tools;

public class Generator {
    public static final String NUMBERS = "0123456789";
    public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String SPECIAL = "!@#$%^&*_=+-";

    /**
     * Método que genera un número aleatorio
     * 
     * @param min
     * @param max
     * @return int
     */
    public static String generateRandom(String alphabet, int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int)(alphabet.length() * Math.random());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();         
    }
}
