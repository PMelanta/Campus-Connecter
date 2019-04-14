/*
 * ULocale.java
 *
 * Created on August 5, 2010, 2:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cc.util;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.text.NumberFormatter;
import org.apache.log4j.Logger;
import cc.base.UBaseException;
import cc.base.UConfig;
import cc.base.UDBAccessException;
import cc.base.URequestContext;

/**
 *
 * @author suhas
 */
public class ULocale {
    
    private static Logger logger = Logger.getLogger(ULocale.class);
    private static HashMap localesMap = null;
    private static HashMap localeCodeMap = null;
    private static HashMap currencyMap = null;

    
    private static final String PARAM_CODE_LOCALE = "LOCALE_CODE";
    private static final String PARAM_CODE_USE_SYMBOL_FOR_CURRENCY_DISPLAY = "USE_SYMBOL_FOR_CURRENCY_DISPLAY";
    
    public static String getCurrencyCode(int key)
    throws UBaseException {
        try {
            Currency localeCurrency = getCurrency(key);
            return localeCurrency.getCurrencyCode();
        } catch (Exception e) {
            throw new UBaseException(e.getMessage(), e);
        }
    }
    
    public static String getCurrencyCode(URequestContext ctxt)
    throws UBaseException {
        return getCurrencyCode(ctxt.getUserEntityRID());
    }
    
    public static String getCurrencySymbol(int key)
    throws UBaseException {
        try {
            Currency localeCurrency = getCurrency(key);
            return localeCurrency.getSymbol();
        } catch (Exception e) {
            throw new UBaseException(e.getMessage(), e);
        }
    }
    
    public static String getCurrencySymbol(URequestContext ctxt)
    throws UBaseException {
        return getCurrencySymbol(ctxt.getUserEntityRID());
    }
    
    public static String getCurrencyDisplayString(int key)
    throws UBaseException {
        try {
            Currency localeCurrency = getCurrency(key);
            
            if(UConfig.getParameterValue(key, PARAM_CODE_USE_SYMBOL_FOR_CURRENCY_DISPLAY, 0) == 1) {
                return getCurrencySymbol(key);
            } else {
                return getCurrencyCode(key);
            }
            
        } catch (Exception e) {
            throw new UBaseException(e.getMessage());
        }
    }
   
    public static String getCurrencyDisplayString(URequestContext ctxt)
    throws UBaseException {
        return getCurrencyDisplayString(ctxt.getUserEntityRID());
    }
    
    public static Locale getLocale(URequestContext ctxt)
    throws UBaseException {
        return getLocale(ctxt.getUserEntityRID());
    }
    
    public static Locale getLocale(int key)
    throws UBaseException {
        try {
            if(localesMap == null)
                localesMap = new HashMap();
            
            if(!localesMap.containsKey(key + "")) {
                String localeCode = getLocaleCode(key);
                Locale localeInstance = null;
                
                if(localeCode != null) {
                    String [] localeCodeParam = localeCode.split("_");
                    if(localeCodeParam.length == 1)
                        localeInstance = new Locale(localeCodeParam[0]);
                    else if(localeCodeParam.length == 2)
                        localeInstance = new Locale(localeCodeParam[0], localeCodeParam[1]);
                    else if(localeCodeParam.length > 2)
                        localeInstance = new Locale(localeCodeParam[0], localeCodeParam[1], localeCode.replaceFirst("[^_]*_[^_]*_",""));
                }
                
                //TODO throw exception when locale is null
                
                localesMap.put(key + "" , localeInstance);
            }
            
            return (Locale) localesMap.get(key + "");
        } catch (Exception e) {
            throw new UBaseException(e.getMessage(), e);
        }
    }
    
    private static Currency getCurrency(int key)
    throws UBaseException {
        try {
            if(currencyMap == null)
                currencyMap = new HashMap();
            
            if(!currencyMap.containsKey(key + "")) {
                Locale currentLocale = getLocale(key);
                Currency currency = Currency.getInstance(currentLocale);
                currencyMap.put(key + "", currency);
            }

            return (Currency) currencyMap.get(key + "");
        } catch (IllegalArgumentException e) {
            throw new UBaseException("Unsupported locale definition", e);
        } catch (Exception e) {
            throw new UBaseException(e.getMessage(), e);
        }
    }
    
    private static String getLocaleCode(int key)
    throws UBaseException {
        try {
            if(localeCodeMap == null)
                localeCodeMap = new HashMap();
            
            if(!localeCodeMap.containsKey(key + "")) {
                String localeCode = UConfig.getParameterValue(key, PARAM_CODE_LOCALE, null);
                localeCodeMap.put(key + "", localeCode);
            }
            
            return (String) localeCodeMap.get(key + "");
            
        } catch (Exception e) {
            throw new UBaseException(e.getMessage(), e);
        }
    }

    public static void clearCache() 
    throws UBaseException {
        try {
            if(localesMap != null)
                localesMap.clear();
            if(localeCodeMap != null)
                localeCodeMap.clear();
            if(currencyMap != null)
                currencyMap.clear();
        } catch (Exception e) {
            throw new UBaseException(e.getMessage(), e);
        }
    }
}

