/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author suhas
 */
public class OPMSCalendar {
    
    public static String monthNames[] = {"January","February","March","April","May","June","July","August","September",
                                        "October","November","December"};
    
    public static String weekNames[] = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    public static int getTotalDaysInMonth(int month, int year) {
        int totalDays;
        if ((month == 3) || (month == 5) || (month == 8) || (month == 10)) {
            totalDays = 30;
        } else {
            totalDays = 31;
            if (month == 1) {
                if ((year % 4) != 0) {
                    totalDays = 28;
                } else {
                    totalDays = 29;
                }
            }
        }
        return totalDays;
    }
    
    public static int getFirstDayOfMonth(int year,int month){ /* 1-sunday,2-monday....7-saturday*/
        return (new GregorianCalendar(year,month,1)).get(Calendar.DAY_OF_WEEK);
    }
}
