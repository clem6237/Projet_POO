package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contient des méthodes utiles
 * @author clementruffin
 */
public class Utils {
    
    /**
     * Log un message dans la console, avec l'heure du message.
     * @param message 
     */
    public static void log(String message) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        
        System.out.println(dateFormat.format(date) + " - " + message);
    }
    
    /**
     * Sécurise l'écriture de données dans une ligne d'un fichier.
     * @param value
     * @param line
     * @return
     * @throws Exception 
     */
    public static String write(String value, String line) throws Exception {

        if (value == null) {
            value = "";
        }

        boolean needQuote = false;

        if (value.contains("\n")) {
            needQuote = true;
        }

        if (value.contains(";")) {
            needQuote = true;
        }

        if (value.contains("\"")) {
            needQuote = true;
            value = value.replaceAll("\"", "\"\"");
        }

        if(needQuote) {
            value = "\"" + value + "\"";
        }

        return line += value;
    }
}
