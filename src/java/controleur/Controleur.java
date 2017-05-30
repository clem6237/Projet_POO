package controleur;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import utils.ImportBase;

/**
 *
 * @author Anais
 */
@WebServlet(name = "controleur", urlPatterns = {"/Controleur"})
public class Controleur extends HttpServlet {
    public static final String ATT_SESSION_COORDINATES_FILE = "coordinatesFile";    
    public static final String ATT_SESSION_DISTANCES_FILE = "distancesFile";
    public static final String ATT_SESSION_FLEET_FILE = "fleet";    
    public static final String ATT_SESSION_SWAPACTIONS_FILE = "swapActions";
    public static final String ATT_SESSION_LOCATIONS_FILE = "locations";
    
    HttpSession session;
    
    private final String UPLOAD_DIRECTORY = "files";
    private final int TAILLE_TAMPON = 10240;
    private final Map<String, FileItem> files = new HashMap<>();

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        session = request.getSession();
        int nextPage;
        
        String action = request.getParameter("action");
        try (PrintWriter out = response.getWriter()) {
            switch(action){
                case "export":
                    System.out.println("Exporter la solution");
                    break;
                case "next" :
                    String vue = request.getParameter("vue");
                    switch(vue){
                        case "1" : 
                            //On initialise le donnée de cette page en session
                            session.removeAttribute(ATT_SESSION_COORDINATES_FILE);
                            session.removeAttribute(ATT_SESSION_DISTANCES_FILE);
                            
                            //Récupére les infos de la page
                            String coordinatesFile = request.getParameter("coordinates");
                            if(coordinatesFile != null && !coordinatesFile.equals("") )
                                session.setAttribute(ATT_SESSION_COORDINATES_FILE, coordinatesFile);
                            
                            String distancesFile = request.getParameter("distances");      
                            if(distancesFile != null && !distancesFile.equals(""))
                                session.setAttribute(ATT_SESSION_DISTANCES_FILE, distancesFile);
                            
                            //Passe à la page suivante
                            nextPage = 2;
                            request.setAttribute("active", nextPage);
                            forward("/calcul.jsp", request, response);
                        break; 
                        case "2" : 
                            //On initialise le donnée de cette page en session
                            session.removeAttribute(ATT_SESSION_FLEET_FILE);
                            session.removeAttribute(ATT_SESSION_SWAPACTIONS_FILE);
                            
                            //Récupére les infos de la page
                            String fleetFile = request.getParameter("fleet");
                            String swapActionFile = request.getParameter("swapActions");  
                            
                            //Si un des fichiers est null, on reste sur la page
                            if(fleetFile.equals("") || swapActionFile.equals("")) {
                                if(fleetFile.equals(""))
                                    request.setAttribute("fleet", "error");
                                if(swapActionFile.equals(""))
                                    request.setAttribute("swapActions", "error");
                                
                                request.setAttribute("active", 2);
                                forward("/calcul.jsp", request, response);
                            } else {
                                //On enregistre les fichiers en session
                                session.setAttribute(ATT_SESSION_FLEET_FILE, fleetFile);
                                session.setAttribute(ATT_SESSION_SWAPACTIONS_FILE, swapActionFile);
                                
                                //On passe à la page suivante
                                nextPage = 3;
                                request.setAttribute("active", nextPage);
                                forward("/calcul.jsp", request, response);
                            }
                        break;
                        default :
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
                        break;
                    }
                break; 
                case "previous" :
                    String vueP = request.getParameter("vue");
                    switch(vueP){
                        case "2" :
                            //Info de la page d'import 2
                            System.out.println("Prev: Page 2 OK");
                            
                            nextPage = 1;
                            request.setAttribute("active", nextPage);
                            forward("/calcul.jsp", request, response);
                        break; 
                        case "3" :
                            //Info de la page d'import 3
                            System.out.println("Prev: Page 3 OK");
                            
                            nextPage = 2;
                            request.setAttribute("active", nextPage);
                            forward("/calcul.jsp", request, response);
                        break;
                        default :
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
                        break;
                    }
                    break;
                case "calcul" :
                        //On initialise le donnée de cette page en session
                        session.removeAttribute(ATT_SESSION_LOCATIONS_FILE);
                        
                        //Récupére les infos de la page
                        String locationsFile = request.getParameter("locations");  
                        //Si le fichier est null, on reste sur la page
                        if(locationsFile.equals("")) {
                            request.setAttribute("locations", "error");
                            request.setAttribute("active", 3);
                            forward("/calcul.jsp", request, response);
                        } else {
                            session.setAttribute(ATT_SESSION_LOCATIONS_FILE, locationsFile);
                            //On lance le calcul
                            this.importFiles(request, response);
                        }
                    break;
                default :
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
                break;
            }
        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        session = request.getSession();
        int nextPage;
        
        String action = "", vue = "";
        String coordinatesFile = "", distancesFile = "";
        String fleetFile = "", swapActionFile = "";  
        String locationsFile = "";
        
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        
            for (FileItem item : items) {
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString();

                    switch (fieldName) {
                        case "action":
                            action = fieldValue;
                            break;
                        case "vue":
                            vue = fieldValue;
                            break;
                    }
                    
                } else {
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getName();
                    
                    switch (fieldName) {
                        case "coordinates":
                            coordinatesFile = fieldValue;
                            files.put(ATT_SESSION_COORDINATES_FILE, item);
                            break;
                        case "distances":
                            distancesFile = fieldValue;
                            files.put(ATT_SESSION_DISTANCES_FILE, item);
                            break;
                        case ATT_SESSION_FLEET_FILE:
                            fleetFile = fieldValue;
                            files.put(ATT_SESSION_FLEET_FILE, item);
                            break;
                        case ATT_SESSION_SWAPACTIONS_FILE:
                            swapActionFile = fieldValue;
                            files.put(ATT_SESSION_SWAPACTIONS_FILE, item);
                            break;
                        case ATT_SESSION_LOCATIONS_FILE:
                            locationsFile = fieldValue;
                            files.put(ATT_SESSION_LOCATIONS_FILE, item);
                            break;
                    }
                }
            }
        } catch (FileUploadException ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        //String action = request.getParameter("action");
        
        try (PrintWriter out = response.getWriter()) {
            
            switch(action){
            
                case "export":
                    System.out.println("Exporter la solution");
                    break;
                
                case "next" :
                    //String vue = request.getParameter("vue");
                    
                    switch(vue){
                    
                        case "1" : 
                            //On initialise le donnée de cette page en session
                            session.removeAttribute(ATT_SESSION_COORDINATES_FILE);
                            session.removeAttribute(ATT_SESSION_DISTANCES_FILE);
                            
                            //Récupére les infos de la page
                            //String coordinatesFile = request.getParameter("coordinates");
                            if(coordinatesFile != null && !coordinatesFile.equals("") )
                                session.setAttribute(ATT_SESSION_COORDINATES_FILE, coordinatesFile);
                            
                            //String distancesFile = request.getParameter("distances");      
                            if(distancesFile != null && !distancesFile.equals(""))
                                session.setAttribute(ATT_SESSION_DISTANCES_FILE, distancesFile);
                            
                            //Passe à la page suivante
                            nextPage = 2;
                            request.setAttribute("active", nextPage);
                            forward("/calcul.jsp", request, response);
                        break; 
                        
                        case "2" : 
                            //On initialise le donnée de cette page en session
                            session.removeAttribute(ATT_SESSION_FLEET_FILE);
                            session.removeAttribute(ATT_SESSION_SWAPACTIONS_FILE);
                            
                            //Récupére les infos de la page
                            //String fleetFile = request.getParameter("fleet");
                            //String swapActionFile = request.getParameter("swapActions");  
                            
                            //Si un des fichiers est null, on reste sur la page
                            if(fleetFile.equals("") || swapActionFile.equals("")) {
                                if(fleetFile.equals(""))
                                    request.setAttribute("fleet", "error");
                                if(swapActionFile.equals(""))
                                    request.setAttribute("swapActions", "error");
                                
                                request.setAttribute("active", 2);
                                forward("/calcul.jsp", request, response);
                            } else {
                                //On enregistre les fichiers en session
                                session.setAttribute(ATT_SESSION_FLEET_FILE, fleetFile);
                                session.setAttribute(ATT_SESSION_SWAPACTIONS_FILE, swapActionFile);
                                
                                //On passe à la page suivante
                                nextPage = 3;
                                request.setAttribute("active", nextPage);
                                forward("/calcul.jsp", request, response);
                            }
                        break;
                        
                        default :
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
                        break;
                    }
                break; 
                
                case "previous" :
                    String vueP = request.getParameter("vue");
                    switch(vueP){
                        case "2" :
                            //Info de la page d'import 2
                            System.out.println("Prev: Page 2 OK");
                            
                            nextPage = 1;
                            request.setAttribute("active", nextPage);
                            forward("/calcul.jsp", request, response);
                        break; 
                        case "3" :
                            //Info de la page d'import 3
                            System.out.println("Prev: Page 3 OK");
                            
                            nextPage = 2;
                            request.setAttribute("active", nextPage);
                            forward("/calcul.jsp", request, response);
                        break;
                        default :
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
                        break;
                    }
                break;
                    
                case "calcul" :
                        //On initialise le donnée de cette page en session
                        session.removeAttribute(ATT_SESSION_LOCATIONS_FILE);
                        
                        //Récupére les infos de la page
                        //String locationsFile = request.getParameter("locations");  
                        //Si le fichier est null, on reste sur la page
                        if(locationsFile.equals("")) {
                            request.setAttribute("locations", "error");
                            request.setAttribute("active", 3);
                            forward("/calcul.jsp", request, response);
                        } else {
                            session.setAttribute(ATT_SESSION_LOCATIONS_FILE, locationsFile);
                            
                            //On lance le calcul
                            this.importFiles(request, response);
                            this.calcSolution(request, response);
                            
                            forward("index.jsp", request, response);
                        }
                break;
                    
                default :
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
                break;
            }
        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void forward(String url, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(url);
        rd.forward(request, response);
    }
    
    public void importFiles(HttpServletRequest request, HttpServletResponse response) {
        try {
            
            ImportBase.resetSolution();
            
            /*if (files.containsKey(ATT_SESSION_COORDINATES_FILE)
                    && files.containsKey(ATT_SESSION_DISTANCES_FILE)) {
                ImportBase.importCoordinatesFromWeb(
                        files.get(ATT_SESSION_COORDINATES_FILE),
                        files.get(ATT_SESSION_DISTANCES_FILE));
            }*/
            
            if (files.containsKey(ATT_SESSION_FLEET_FILE)
                    && files.containsKey(ATT_SESSION_SWAPACTIONS_FILE)) {
                
                ImportBase.importParametersFromWeb(
                        files.get(ATT_SESSION_FLEET_FILE), 
                        files.get(ATT_SESSION_SWAPACTIONS_FILE));
            }
            
            if (files.containsKey(ATT_SESSION_LOCATIONS_FILE)) {
            
                ImportBase.importLocationsFromWeb(
                        files.get(ATT_SESSION_LOCATIONS_FILE));
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void calcSolution(HttpServletRequest request, HttpServletResponse response) {
        
    }
}
