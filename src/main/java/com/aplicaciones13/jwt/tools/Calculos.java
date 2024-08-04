package com.aplicaciones13.jwt.tools;
import java.util.Date;
import java.util.Calendar;

/**
 * Clase que realiza cálculos
 * 
 * @Modifier omargo33
 * @since 2024-07-29
 */
public class Calculos {

    /**
     * Método que suma días a una fecha
     * 
     * @param date
     * @param days
     * @return Date
     */
    public static Date addDays(Date date, int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); 
        calendar.add(Calendar.DAY_OF_YEAR, days);  
        return calendar.getTime(); 
    }
}
