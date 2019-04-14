package cc.util;


import java.text.*;
import java.util.*;

public final class UDate {

    private static String displayFormatStr = "dd/MM/yyyy";
    private static String dbFormatStr = "yyyy-MM-dd";
    private static String dbDateReturnFormat = "yyyy-MM-dd";
    private static String displayTimeFormat = "h:mm a";
    public static String DATE_FORMAT_ERROR = "<incorrect date format>";
    private static String dbTimeFormat = "HH:mm:ss";
    private static final SimpleDateFormat DB_DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DISPLAY_DATETIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static int DISPLAY_FORMAT = 1;
    public static int DB_FORMAT = 2;
    public static String[] monthNames = {"", "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"};
    public static String[] monthNamesShort = {"", "Jan", "Feb", "Mar", "Apr",
        "May", "Jun", "Jul", "Aug",
        "Sep", "Oct", "Nov", "Dec"};

    public static void setDisplayDateFormat(String newDisplayDateFormat) {

        displayFormatStr = newDisplayDateFormat;

    }

    public static String convertToCustomFormat(java.util.Date date, String formatStr) {
        SimpleDateFormat fmt = new SimpleDateFormat(formatStr);

        java.util.Date dt = new java.util.Date(date.getTime());
        String dateStr = fmt.format(dt);

        return dateStr;
    }

    public static String getDisplayDateFormat() {
        return displayFormatStr;
    }

    public static void setDBDateFormat(String newDBDateFormat) {

        dbFormatStr = newDBDateFormat;
    }

    public static void setDBDateReturnFormat(String newDBDateFormat) {

        dbDateReturnFormat = newDBDateFormat;
    }

    public static java.sql.Date currentDateTimeDB() {

        java.sql.Date dt = new java.sql.Date(System.currentTimeMillis());

        return dt;
    }

    public static String nowDBString() {

        java.sql.Date dt = currentDateTimeDB();

        //return dt.toString();  //--Gopi

        SimpleDateFormat dbFmt = new SimpleDateFormat(dbFormatStr);

        return dbFmt.format(dt);
    }

    public static String nowDisplayString() {

        SimpleDateFormat fmt = new SimpleDateFormat(displayFormatStr);

        java.util.Date dt = new java.util.Date();
        String dateStr = fmt.format(dt);

        return dateStr;

    }

    public static String convertDateToDB(String strDate) {
        //this function convert date to SQL dateformat yyyy/mm/dd
        java.sql.Date d = new java.sql.Date(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return new java.sql.Date(sdf.parse(strDate).getTime()).toString();
        } catch (ParseException e) {
            return d.toString();
        }
    }

    public static String nowCustomFormat(String formatStr) {
        SimpleDateFormat fmt = new SimpleDateFormat(formatStr);

        java.util.Date dt = new java.util.Date();
        String dateStr = fmt.format(dt);

        return dateStr;
    }

    public static String convertDateToDB(java.util.Date date) {
        //this function convert date to SQL dateformat yyyy/mm/dd
        java.sql.Date d = new java.sql.Date(0);

        SimpleDateFormat sdf = new SimpleDateFormat(dbFormatStr);

        try {
            return sdf.format(date);
        } catch (Exception e) {
            return d.toString();
        }
    }

    public static String displayToDB(String dateStr) {

        SimpleDateFormat fmt = new SimpleDateFormat(displayFormatStr);

        java.sql.Date dt = null;

        try {
            dt = new java.sql.Date(fmt.parse(dateStr).getTime());
        } catch (ParseException e) {
            return DATE_FORMAT_ERROR;
        }

        //return dt.toString(); //--Gopi

        SimpleDateFormat dbFmt = new SimpleDateFormat(dbFormatStr);

        return dbFmt.format(dt);
    }
    
    public static String displayToDBTimestamp(String displayTimeStamp) {
        try {
            return DB_DATETIME_FORMATTER.format(DISPLAY_DATETIME_FORMATTER.parse(displayTimeStamp));
        } catch (Exception e) {
            return DATE_FORMAT_ERROR;
        }
    }

    public static String dbToDisplay(String dateStr) {

        if (dateStr.equalsIgnoreCase("NA")) {
            return "NA";
        }

        SimpleDateFormat dbFmt = new SimpleDateFormat(dbDateReturnFormat);

        java.util.Date dt = null;

        try {
            dt = new java.util.Date(dbFmt.parse(dateStr).getTime());
        } catch (ParseException e) {
            return DATE_FORMAT_ERROR;
        }

        SimpleDateFormat displayFmt = new SimpleDateFormat(displayFormatStr);

        return displayFmt.format(dt);
    }

    public static String dbToDisplay(java.sql.Date date) {

        return dbToDisplay(date.toString());
    }

    public static java.sql.Time parseTime(String timeStr, String fmt) {

        SimpleDateFormat timeFmt = new SimpleDateFormat(fmt);

        try {
            return new java.sql.Time(timeFmt.parse(timeStr).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static java.sql.Time currentTime() {

        Calendar cal = new GregorianCalendar();

        //	int hour12 = cal.get(Calendar.HOUR);            // 0..11
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);     // 0..23
        int min = cal.get(Calendar.MINUTE);             // 0..59
        //	String ampm = cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM";

        return parseTime(hour24 + ":" + min, "H:mm");
    }

    public static String formatTimeForDisplay(java.sql.Time time) {

        SimpleDateFormat timeFmt = new SimpleDateFormat(displayTimeFormat);

        return timeFmt.format(time);
    }

    public static String formatTimeForDisplay(java.util.Date time) {

        SimpleDateFormat timeFmt = new SimpleDateFormat(displayTimeFormat);

        return timeFmt.format(time);
    }

    public static String formatTimeFor24HourDisplay(java.sql.Time time) {

        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm");

        return timeFmt.format(time);
    }

    public static String getDateDiffInDay(String dateStr1, String dateStr2) {
        java.util.Date dt1 = null;
        java.util.Date dt2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String retValue = null;
        try {

            dt1 = new java.util.Date(sdf.parse(dateStr1).getTime());
            dt2 = new java.util.Date(sdf.parse(dateStr2).getTime());

            long diff = dt1.getTime() - dt2.getTime();
            long day = (diff / (1000 * 60 * 60 * 24));
            retValue = Long.toString(day);
            return retValue;

        } catch (ParseException e) {
            return null;
        }



    }

    public static int getDateDiffInDay(java.util.Date dt1, java.util.Date dt2) {

        try {

            long diff = dt1.getTime() - dt2.getTime();
            long day = (diff / (1000 * 60 * 60 * 24));

            return new Long(day).intValue();

        } catch (Exception e) {
            return 0;
        }
    }

    public static String calculateAge(String dob)
            throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar dateOfBirth = new GregorianCalendar();
        dob = convertDateToDB(dob);
        dateOfBirth.setTime(sdf.parse(dob));

        // Create a calendar object with today's date
        Calendar today = Calendar.getInstance();

        // Get age based on year
        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
        int ageCalculated = 0;
        int month = 0;
        int day = 0;
        // Add the tentative age to the date of birth to get this year's birthday
        dateOfBirth.add(Calendar.YEAR, age);
        ageCalculated = age;
        // If this year's birthday has not happened yet, subtract one from age
        if (today.before(dateOfBirth)) {
            age--;
        }
        if (age > 0) {
            return age + " Years ";
        }
        if (age == 0) {
            month = today.get(Calendar.MONTH) - dateOfBirth.get(Calendar.MONTH);
        }
        if (month < 0) {
            return (12 + month) + " Months ";
        } else if (month > 0) {
            return month + " Months ";
        } else {
            day = today.get(Calendar.DATE) - dateOfBirth.get(Calendar.DATE);
        }
        if (day < 0) {
            if (ageCalculated == 1 && month == 0) {
                return "11 Months";
            } else {
                return java.lang.Math.abs(day) + " Days";
            }
        } else {
            return "1 Day";
        }
    }

    public static int calculateAgeinMonths(String dob)
            throws Exception {
        String calcAge = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar dateOfBirth = new GregorianCalendar();
        dob = convertDateToDB(dob);
        dateOfBirth.setTime(sdf.parse(dob));

        // Create a calendar object with today's date
        Calendar today = Calendar.getInstance();

        // Get age based on year
        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
        int month = 0;
        int ageCalculated = 0;
        ageCalculated = age;
        // Add the tentative age to the date of birth to get this year's birthday
        dateOfBirth.add(Calendar.YEAR, age);

        // If this year's birthday has not happened yet, subtract one from age
        if (today.before(dateOfBirth)) {
            age--;
        }
        month = today.get(Calendar.MONTH) - dateOfBirth.get(Calendar.MONTH);
        if (age == 0 && month == 0 && ageCalculated == 1) {
            month = 11;
        } else {
            if (month < 0) {
                month = 12 + month;
            }
            month = age * 12 + month;
        }
        return month;
    }

    public static GregorianCalendar getGregorianDate(String inputDate) {
        String[] dateDetails = inputDate.split("/");

        GregorianCalendar gregDate = new GregorianCalendar(Integer.parseInt(dateDetails[2]),
                Integer.parseInt(dateDetails[1]) - 1, Integer.parseInt(dateDetails[0]));

        return gregDate;
    }

    public static String getDisplayStr(GregorianCalendar gregDate) {
        String dateStr = gregDate.get(Calendar.YEAR) + "-" + gregDate.get(Calendar.MONTH) + "-" + gregDate.get(Calendar.DATE);
        return dbToDisplay(dateStr);
    }

    public static String getDbStr(GregorianCalendar gregDate) {
        String str = gregDate.get(Calendar.DATE) + "/" + gregDate.get(Calendar.MONTH) + "/" + gregDate.get(Calendar.YEAR);
        return displayToDB(str);
    }

    public static int getDayOfWeek(java.util.Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static java.util.Date extractDate(java.util.Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);

        return cal.getTime();
    }

    public static java.util.Date extractTime(java.util.Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        cal.clear();

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);

        return cal.getTime();
    }

    public static java.util.Date addDaysToDate(java.util.Date date, int numDays) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.DAY_OF_MONTH, numDays);

        return cal.getTime();
    }

    public static java.sql.Time addMinutesToTime(java.sql.Time time, int mins) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        cal.add(Calendar.MINUTE, mins);

        return new java.sql.Time(cal.getTime().getTime());
    }

    public static String getFormattedDate(String dateStr, String format) { //format : mm/yyyy;
        try {
            String retStr = null;
            String temp = null;
            String[] tempArray = null;
            if ("mm/yyyy".equals(format) && !"".equals(dateStr) && dateStr != null) {
                tempArray = UString.split(dateStr, '/');
                temp = tempArray[1] + "/" + tempArray[2];
            }
            return temp;
        } catch (Exception e) {
            return DATE_FORMAT_ERROR;
        }
    }

    public static java.util.Date getDateObject(String dateStr, int inputFormat) {
        //TODO: Have to change this function if DB format changes
        java.util.Date resultDate = null;
        String[] tempArray = null;
        Calendar cal = java.util.Calendar.getInstance();

        if (inputFormat == DISPLAY_FORMAT) {
            tempArray = dateStr.split("/");
            cal.set(Integer.parseInt(tempArray[2]), Integer.parseInt(tempArray[1]) - 1, Integer.parseInt(tempArray[0]));
            resultDate = cal.getTime();

        } else if (inputFormat == DB_FORMAT) {
            if ("yyyy-MM-dd".equals(dbFormatStr)) {
                tempArray = dateStr.split("-");
                cal.set(Integer.parseInt(tempArray[0]), Integer.parseInt(tempArray[1]) - 1, Integer.parseInt(tempArray[2]));
                resultDate = cal.getTime();

            } else if ("mm/dd/yyyy".equals(dbFormatStr)) {
                tempArray = dateStr.split("/");
                cal.set(Integer.parseInt(tempArray[2]), Integer.parseInt(tempArray[0]) - 1, Integer.parseInt(tempArray[1]));
                resultDate = cal.getTime();

            } else {
                resultDate = null;
            }
        }

        return resultDate;
    }

    public static String getDateStr(java.util.Date inputDate, int returnDateFormat) {

        SimpleDateFormat sdf = null;

        if (returnDateFormat == DISPLAY_FORMAT) {
            sdf = new SimpleDateFormat(displayFormatStr);
        } else if (returnDateFormat == DB_FORMAT) {
            sdf = new SimpleDateFormat(dbFormatStr);
        }

        return sdf.format(inputDate);
    }

    public static String getDate(int year, int month, int day, String dateFormat) {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);

        return fmt.format(cal.getTime());
    }

    public static String getTimeInDBFormat(String time)
            throws Exception {

        if (time == null || "".equals(time) || time.length() > 5) {
            throw new Exception("Invalid time format : " + time);
        }

        int length = time.length();
        String minutes = "", hours = "", timeInDBFormat = null;
        switch (length) {
            case 1:
                if (0 >= Integer.parseInt(time)) {
                    throw new Exception("Invalid time format : " + time);
                }

                timeInDBFormat = "0" + time + ":00";
                break;
            case 2:
                if (0 >= Integer.parseInt(time) || 24 <= Integer.parseInt(time)) {
                    throw new Exception("Invalid time format : " + time);
                }

                timeInDBFormat = time + ":00";
                break;
            case 3:
                hours = time.substring(0, 1);
                minutes = time.substring(1, time.length());
                if (0 >= Integer.parseInt(hours) || 60 <= Integer.parseInt(minutes)) {
                    throw new Exception("Invalid time format : " + time);
                }

                timeInDBFormat = "0" + hours + ":" + minutes;
                break;
            case 4:
                hours = time.substring(0, 2);
                minutes = time.substring(2, time.length());
                if (0 >= Integer.parseInt(hours) || 24 <= Integer.parseInt(hours) || 60 <= Integer.parseInt(minutes)) {
                    throw new Exception("Invalid time format : " + time);
                }

                timeInDBFormat = hours + ":" + minutes;
                break;
            case 5:
                String[] timeArray = time.split(":");
                hours = timeArray != null && !"".equals(timeArray[0]) ? timeArray[0] : null;
                minutes = timeArray != null && timeArray.length > 1 && !"".equals(timeArray[1]) ? timeArray[1] : null;
                if (hours == null || minutes == null || 0 >= Integer.parseInt(hours) || 24 <= Integer.parseInt(hours)
                        || 60 <= Integer.parseInt(minutes)) {
                    throw new Exception("Invalid time format : " + time);
                }

                timeInDBFormat = time;
                break;
            default:
                throw new Exception("Invalid time format : " + time);
        }

        return timeInDBFormat;
    }

    public static String getMonthYear(String dbDate) {
        if (null == dbDate || "".equals(dbDate.trim()) || dbDate.length() != 10) {
            return dbDate;
        }

        String datePart[] = dbDate.split("-");
        int year = Integer.parseInt(datePart[0]);
        int monthNo = Integer.parseInt(datePart[1]);

        return monthNames[monthNo] + " " + year;

    }

    public static String currentTimeStr() {

        Calendar cal = new GregorianCalendar();

        int hour24 = cal.get(Calendar.HOUR_OF_DAY);     // 0..23
        int min = cal.get(Calendar.MINUTE);             // 0..59
        int second = cal.get(Calendar.SECOND);

        String hour24Str = Integer.toString(hour24);
        String minStr = Integer.toString(min);
        String secondStr = Integer.toString(second);

        if (min < 10) {
            minStr = "0" + min;
        }

        if (hour24 < 10) {
            hour24Str = "0" + hour24;
        }

        if (second < 10) {
            secondStr = "0" + second;
        }


        return (hour24Str + ":" + minStr + ":" + secondStr);
    }

    public static String formatTimeForDB(java.util.Date time) {

        SimpleDateFormat timeFmt = new SimpleDateFormat(dbTimeFormat);

        return timeFmt.format(time);
    }

    public static String currentDateTimeDBStr() {
        return nowDBString() + " " + formatTimeForDB(UDate.currentDateTimeDB());
    }

    public static String addToDate(String inDate, int addWhat, int addNumber, String inFormat, String outFormat)
            throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat(inFormat);

        Calendar cal = new GregorianCalendar();
        cal.setTime(sdf.parse(inDate));

        cal.add(addWhat, addNumber);

        Date dt = cal.getTime();
        sdf = new SimpleDateFormat(outFormat);

        return sdf.format(dt);
    }

    public static int getRemainingDaysInMonth(java.util.Date curDate)
            throws Exception {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(curDate);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
    }
}

