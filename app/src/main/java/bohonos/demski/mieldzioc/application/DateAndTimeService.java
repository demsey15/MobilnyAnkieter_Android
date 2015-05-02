package bohonos.demski.mieldzioc.application;

import java.util.GregorianCalendar;

/**
 * Created by Dominik on 2015-05-02.
 */
public class DateAndTimeService {

    /**
     * Je¿eli liczba jest jednocyfrowa dodaje wiod¹ce zero przed ni¹
     * @param liczba
     * @return
     */
    public static String addFirstZeros(int liczba){
        String zwrot = (liczba < 10 && liczba >= 0) ? "0" + liczba : String.valueOf(liczba);
        return zwrot;
    }

    public static String addFirstTwoZeros(int liczba){
        if(liczba < 10 && liczba >= 0){
            return "00" + liczba;
        }
        else if(liczba >= 10 && liczba < 100){
            return "0" + liczba;
        }
        else return String.valueOf(liczba);
    }

    /**
     *
     * @return zwraca obecn¹ datê i godzinê w postaci:  "YYYY-MM-DD HH:MM:SS.SSS"
     */
    public static String getToday(){
        GregorianCalendar today = new GregorianCalendar();
        return today.get(GregorianCalendar.YEAR) + "-" +
                addFirstZeros(today.get(GregorianCalendar.MONTH) + 1) + "-" +
                addFirstZeros(today.get(GregorianCalendar.DATE))
                + " " + addFirstZeros(today.get(GregorianCalendar.HOUR_OF_DAY))
                + ":" + addFirstZeros(today.get(GregorianCalendar.MINUTE))
                + ":" + addFirstZeros(today.get(GregorianCalendar.SECOND)) + "." +
                addFirstTwoZeros(today.get(GregorianCalendar.MILLISECOND));
    }
}
