package mcgill.ecse321.grocerystore;

import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This class provides String format conversions for use with displaying UI information
 */
public class FormatUtils {

    /**
     * Converts a JDBC time format to AM/PM time format for display
     * ex. 14:00:00 -> 2:00 PM
     *
     * @param time - the JDBC time to convert
     * @return converted string representation of the time
     */
    public static String formatTime(String time) {
        String[] timeComponents = time.split(":");
        int hour = Integer.parseInt(timeComponents[0]) % 12;
        hour += hour == 0 ? 12 : 0;
        String amOrPm = Integer.parseInt(timeComponents[0]) < 12 ? " AM" : " PM";
        return hour + ":" + timeComponents[1] + amOrPm;
    }

    /**
     * Converts a JDBC date format to Month date, year format for display
     * ex. 2022-03-15 -> Mar 15, 2022
     *
     * @param date - the JDBC date to convert as a java.sql.Date object
     * @return converted string representation of the date
     */
    public static String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy", Locale.CANADA);
        return dateFormatter.format(date);
    }

    /**
     * Converts a JDBC date format to Month date, year format for display
     * ex. 2022-03-15 -> Mar 15, 2022
     *
     * @param date - the JDBC date to convert in JDBC string format
     * @return converted string representation of the date
     */
    public static String formatDate(String date) {
        DateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy", Locale.CANADA);
        Date dateToBeFormatted = Date.valueOf(date);
        return dateFormatter.format(dateToBeFormatted);
    }

    /**
     * Converts a double to a $0.00 format to represent prices
     *
     * @param price - double value representing the price to convert
     * @return converted string representation of the price
     */
    public static String formatCurrency(double price) {
        DecimalFormat priceFormat = new DecimalFormat("0.00");
        return "$ " + priceFormat.format(price);
    }
}
