/*
 * UString.java
 */

package cc.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import cc.util.NumberToWords;

/**
 *
 * @author suhas
 */
public class UString {
    private static final DecimalFormat CURRENCY_DISPLAY_FORMATTER = new DecimalFormat("#,##0.00");
    
    /** Creates a new instance of UString */
    public UString() {
    }
    
    public static String subString(String inputString, int length)
    throws Exception {
        try  {
            String processString = inputString ;
            if (processString == null)
                return processString ;
            if (processString.length() > length && processString != null )
                processString = processString.substring(0, length ) ;
            return processString ;
        } catch(Exception e)   {
            throw new Exception("sub string length failed.",e) ;
        }
    }
    
    public static String escapeSpecialChars(String inputString) {
        
        if (inputString == null)
            return inputString;
        
        // Here other escape caharacters can be included
        inputString = inputString.replaceAll("'","''") ;
        
        //Made this escape character when working on MySQL DB
        inputString = inputString.replace((CharSequence)"\\", (CharSequence)"\\\\");
        
        return inputString;
    }
    
    public static String[] split(String str,int delim) {
        // Get number of times the delim occurs in inputStr
        int cnt = 0;
        int j = 0;
        while(j < str.length()) {
            if(str.charAt(j) == delim )
                cnt++;
            j++;
        }
        // Allocate return array
        String[] rArray = new String[cnt + 1];
        int i=0; // The index on rArray
        int start=0, end=0;
        for(start = 0; start <= str.length(); start = end + 1) {
            end = str.indexOf(delim, start);
            if(end < 0)
                end = str.length();
            rArray[i++] = str.substring(start, end);
        }
        return rArray;
    }
    
    
    public static String[] split(String str,char delim) {
        // Get number of times the delim occurs in inputStr
        int cnt = 0;
        int j = 0;
        while(j < str.length()) {
            if(str.charAt(j) == delim )
                cnt++;
            j++;
        }
        // Allocate return array
        String[] rArray = new String[cnt + 1];
        int i=0; // The index on rArray
        int start=0, end=0;
        for(start = 0; start <= str.length(); start = end + 1) {
            end = str.indexOf(delim, start);
            if(end < 0)
                end = str.length();
            rArray[i++] = str.substring(start, end);
        }
        return rArray;
    }
    
    public static String formatNumber(double inputNumber, int numDecimalPlaces){
        
        DecimalFormat df = new DecimalFormat() ;
        
        df.setMinimumIntegerDigits(1) ;
        df.setMinimumFractionDigits(numDecimalPlaces) ;
        df.setMaximumFractionDigits(numDecimalPlaces) ;
        
        return df.format(inputNumber) ;
    }
    
    public static String appendStr(String origStr, String appendStr, String delim, boolean ignoreEmptyStr) {
        if (null == origStr || "".equals(origStr)) {
            return appendStr;
        }
        
        if (null == appendStr || "".equals(appendStr)) {
            if (ignoreEmptyStr) {
                return origStr;
            } else {
                appendStr = "";
            }
        }
        
        if (null == delim) {
            delim = "";
        }
        
        return (origStr + delim + appendStr);
    }
    
    // Do not use this function for delim = ""
    public static String appendStr(String origStr, String appendStr, String delim, boolean ignoreEmptyStr, boolean ignoreDuplicates) {
        
        if (UString.isEmpty(origStr) || UString.isEmpty(appendStr) || UString.isEmpty(delim)) {
            // Let it handle whatever way it can
            return appendStr(origStr, appendStr, delim, ignoreEmptyStr);
        }
        
        if (ignoreDuplicates) {
            // see if the given strings are equal (as of now no delim is applied yet)
            if (origStr.equals(appendStr)) {
                return origStr;
            }
            
            // see if the string already exists with given delim
            String searchStr = delim + appendStr;
            if (origStr.indexOf(searchStr, 0) > 0) {
                // Already exists, so dont append
                return origStr;
            }
            
            // in case append string is at the beginning
            searchStr = appendStr + delim;
            if (origStr.indexOf(searchStr, 0) >= 0) {
                // Already exists, so dont append
                return origStr;
            }
        }
        // does not exist OR does not want ignore duplicates, so append and return
        return appendStr(origStr, appendStr, delim, ignoreEmptyStr);
    }
    
    public static String appendStr(String origStr, String appendStr, String delim, String defaultStr) {
        if (null == appendStr || "".equals(appendStr)) {
            appendStr = defaultStr;
        }
        
        if (null == origStr || "".equals(origStr)) {
            return appendStr;
        }
        
        if (null == delim) {
            delim = "";
        }
        
        return (origStr + delim + appendStr);
    }
    
    public static boolean isEmpty(String chkStr) {
        
        return (chkStr == null || "".equals(chkStr.trim()));
//       lines are reduced
//       if (null == chkStr) {
//           return true;
//       }
//       chkStr = chkStr.trim();
//       if ("".equals(chkStr)) {
//           return true;
//       }
//       return false;
        
    }
    
    public static String formatToCurrency(double value) {
        
        return CURRENCY_DISPLAY_FORMATTER.format(value);
    }
    
    public static String formatToCurrency(double value, int scale) {
        BigDecimal bdValue = new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP );
        return CURRENCY_DISPLAY_FORMATTER.format(bdValue);
    }
    
    public static String getWordsForAmt(float invAmt)
    throws Exception {
        String amtInwords =  null;
        int integerPart = 0;
        float decimalPart=0;
        int decimalPoint=0;
        float amountWithDecimalPart = 0;
        integerPart = new Float(invAmt).intValue();
        // 0.005 is added to invAmt to get the correct value of it. other wise there will be differenceof one paisa.
        //int decimalPart = new Float(((invAmt + 0.005) *100 - integerPart*100)).intValue();
        // the above line has been commented since it was not working for amounts exceeding crores.
        StringBuffer stringNum=new StringBuffer(Float.toString(invAmt));
        decimalPoint=stringNum.indexOf(".");
        amountWithDecimalPart=Float.parseFloat(stringNum.substring(decimalPoint,stringNum.length()));
        decimalPart = (amountWithDecimalPart * 100);
        int decimalPart1 = new Float(decimalPart).intValue();
        String numberPartStr = NumberToWords.evaluate(Integer.toString(integerPart));
        String decimalPartStr = NumberToWords.evaluate(Integer.toString(decimalPart1));
        if ("".equals(decimalPartStr))
            decimalPartStr = "Zero";
        amtInwords = numberPartStr + " Rupees " + " and " + decimalPartStr + " Paisa Only";
        return amtInwords;
    }
    
    /**
     *
     * @param str
     * @return
     * Checks weather the given string is integer or not
     */
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
    
    /**
     *
     * @param doubleValue
     * @param scale
     * @return
     * Returns the double value with specified scale after the decimal point
     */
    public static String decimalFormater(String doubleValue, int scale) {
        BigDecimal bdValue = new BigDecimal(doubleValue).setScale(scale, BigDecimal.ROUND_HALF_UP );
        return bdValue.toString();
    }
    
    public static boolean isNumeric(String strExp) {
        try {
            final String Digits	= "(\\p{Digit}+)";
            final String HexDigits  = "(\\p{XDigit}+)";
            // an exponent is 'e' or 'E' followed by an optionally
            // signed decimal integer.
            final String Exp	= "[eE][+-]?"+Digits;
            final String fpRegex	=
                    ("[\\x00-\\x20]*"+	// Optional leading &quot;whitespace&quot;
                    "[+-]?(" +	// Optional sign character
                    "NaN|" +		// "NaN" string
                    "Infinity|" +	// "Infinity" string
                    
                    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+
                    
                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\.("+Digits+")("+Exp+")?)|"+
                    
                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "(\\.)?)|" +
                    
                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
                    
                    ")[pP][+-]?" + Digits + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*");// Optional trailing &quot;whitespace&quot;
            
            if (Pattern.matches(fpRegex, strExp))
                return true;
            else {
                return false;
            }
        } catch(Exception ex) {
            return false;
        }
    }
    
    public static String blankIfNull(String str) {
        if(str == null || "null".equals(str))
            return "";
        else
            return str;
    }
    
    public static String getClobValue(java.sql.Clob clob) {
        try {
            return clob.getSubString(1, new Long(clob.length()).intValue());
        } catch (Exception ex) {
            return "";
        }
    }
    
    public static double roundNumberToDecimalPlaces(double number, int places) {
        double dFormatNum = 0;
        try {
            DecimalFormat df = new DecimalFormat("##00.00");
            df.setMinimumIntegerDigits(1);
            df.setMinimumFractionDigits(places);
            df.setMaximumFractionDigits(places);


            dFormatNum = Double.valueOf(df.format(number));
        } catch (NumberFormatException nfe) {
            return 0;
        }
        return dFormatNum;
    }
}


