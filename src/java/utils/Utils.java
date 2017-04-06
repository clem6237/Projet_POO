package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author clementruffin
 */
public class Utils {
    
    public static void log(String message) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        
        System.out.println(dateFormat.format(date) + " - " + message);
    }
}
