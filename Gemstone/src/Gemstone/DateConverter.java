/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author SBANTA
 * - 04/04/2012 - updated for Gemstone
 */
public class DateConverter {

    public static boolean IsDateVariableSet = false;
    public static Long FirstDateGroup = null;
    public static Long SecondDateGroup = null;
    public static Long ThirdDateGroup = null;
    public static Long FourthDateGroup = null;
    public static Long FifthDateGroup = null;

    public static Long GetTimeInMsFromDate(Date datetoformat) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(datetoformat);
        return cal1.getTimeInMillis();

    }

    public static String GetCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  H:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        return dateFormat.format(date) + ".000";

    }

    public static Date GetCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  H:mm:ss");
        Date date = new Date();

        return date;
    }

    public static void main(String[] args) {
    }

    public static int GetCurrentYear(Date AddedDate) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(GetTimeInMsFromDate(AddedDate));
        return cal.get(Calendar.YEAR);
    }

    public static String GetDateFromLong(Long longdate) {

        Date date = new Date(longdate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


//      dataformat =  DateFormat.getDateInstance(DateFormat.LONG);
        String s4 = dateFormat.format(date);
        return s4;

    }
}
