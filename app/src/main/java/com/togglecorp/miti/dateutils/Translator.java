package com.togglecorp.miti.dateutils;

public class Translator {

    public final static char[] DIGITS_NEPALI = new char[] {
            '०', '१', '२', '३', '४', '५', '६', '७','८', '९'
    };
    public final static String[] MONTHS_NEPALI = {
            "बैशाख", "जेष्ठ", "अषाढ", "श्रावण", "भाद्र", "असोज", "कात्तिक", "मंसिर", "पौष", "माघ", "फाल्गुन", "चैत्र"
    };
    public final static String[] DAYS_NEPALI = {
            "आईतबार", "सोमबार", "मंगलबार", "बुधबार", "बिहीबार", "शुक्रबार", "शनिबार"
    };

//    public final static char[] DIGITS_ENGLISH = new char[] {
//            '0', '1', '2', '3', '4', '5', '6', '7','8', '9'
//    };
//    public final static String[] MONTHS_ENGLISH = {
//            "Baishakh", "Jestha", "Ashadh", "Shrawan", "Bhadra", "Ashoj", "Kartik", "Mangsir", "Poush", "Magh", "Falgun", "Chaitra"
//    };
//    public final static String[] DAYS_ENGLISH = {
//            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
//    };

    public static String getMonth(int month) {
        return MONTHS_NEPALI[month-1];
    }

    public static String getDay(int day) {
        return DAYS_NEPALI[day];
    }

    public static String getShortDay(int day) {
        String longDay = getDay(day);
        return longDay.substring(0, longDay.indexOf("बार"));
//        return longDay.substring(0, 3);
    }

    public static String getNumber(String english) {
        String nepali = "";
        for (int i=0; i<english.length(); ++i) {
            char c = english.charAt(i);
            if (c >= '0' && c <= '9')
                nepali += DIGITS_NEPALI[Integer.parseInt(c+"")];
            else
                nepali += c;
        }
        return nepali;
    }
}