package controleur;

import dao.DaoFactory;
import dao.PersistenceType;
import dao.RouteDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
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
import metier.Route;
import metier.Tour;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import utils.ImportBase;
import utils.SolutionCalc;
import utils.Utils;

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
    
    private final Map<String, FileItem> files = new HashMap<>();
    private boolean importCoord = false;

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
                
                case "export": // Export CSV de la solution
                    System.out.println("Export: OK");
                    this.exportSolution(request, response);
                    break;
                    
                case "previous" : // Page précédente
                    String vueP = request.getParameter("vue");
                    switch(vueP){
                        case "2" :
                            System.out.println("Prev: Page 2 OK");
                            
                            nextPage = 1;
                            request.setAttribute("active", nextPage);
                            forward("/calcul.jsp", request, response);
                        break; 
                        case "3" :
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
        
            // Récupération des valeurs passées en POST par le formulaire
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
                    
                } else { // Fichiers uploadés par l'utilisateur
                    
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
                
        try (PrintWriter out = response.getWriter()) {
            
            switch(action){
            
                case "next" : // Page suivante
                    
                    switch(vue){
                    
                        case "1" : 
                            System.out.println("Next: Page 1 OK");
                            
                            // Initialisation des données de la page en session
                            session.removeAttribute(ATT_SESSION_COORDINATES_FILE);
                            session.removeAttribute(ATT_SESSION_DISTANCES_FILE);
                            
                            // Récupération des infos de la page
                            if(coordinatesFile != null && !coordinatesFile.equals("") )
                                session.setAttribute(ATT_SESSION_COORDINATES_FILE, coordinatesFile);
                            
                            if(distancesFile != null && !distancesFile.equals(""))
                                session.setAttribute(ATT_SESSION_DISTANCES_FILE, distancesFile);
                            
                            if(coordinatesFile != null && !coordinatesFile.equals("") 
                                    && distancesFile != null && !distancesFile.equals("")) {
                                importCoord = true;
                            }
                            
                            // Passage à la page suivante
                            nextPage = 2;
                            request.setAttribute("active", nextPage);
                            forward("/calcul.jsp", request, response);
                        break; 
                        
                        case "2" : 
                            System.out.println("Next: Page 2 OK");
                            
                            // Initialisation des données de la page en session
                            session.removeAttribute(ATT_SESSION_FLEET_FILE);
                            session.removeAttribute(ATT_SESSION_SWAPACTIONS_FILE);
                            
                            // Récupération des infos de la page
                            
                            // Si un des fichiers est NULL, on reste sur la page
                            if(fleetFile.equals("") || swapActionFile.equals("")) {
                                if(fleetFile.equals(""))
                                    request.setAttribute("fleet", "error");
                                if(swapActionFile.equals(""))
                                    request.setAttribute("swapActions", "error");
                                
                                request.setAttribute("active", 2);
                                forward("/calcul.jsp", request, response);
                            } else {
                                // Enregistrement des fichiers en session
                                session.setAttribute(ATT_SESSION_FLEET_FILE, fleetFile);
                                session.setAttribute(ATT_SESSION_SWAPACTIONS_FILE, swapActionFile);
                                
                                // Passage à la page suivante
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
                    
                case "calcul" :
                        System.out.println("Calcul: OK");
                        
                        // Initialisation des données de la page en session
                        session.removeAttribute(ATT_SESSION_LOCATIONS_FILE);
                        
                        // Récupération des infos de la page
                        // Si le fichier est NULL, on reste sur la page
                        if(locationsFile.equals("")) {
                            request.setAttribute("locations", "error");
                            request.setAttribute("active", 3);
                            forward("/calcul.jsp", request, response);
                        } else {
                            session.setAttribute(ATT_SESSION_LOCATIONS_FILE, locationsFile);
                            
                            // Lancement du calcul
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
    
    /**
     * Importe les fichiers uploadés par l'utilisateur dans la base de données
     * @param request
     * @param response 
     */
    public void importFiles(HttpServletRequest request, HttpServletResponse response) {
        
        try {
            // Suppression des paramètres, tournées et emplacements
            ImportBase.resetSolution();
            
            // Si l'utilisateur a uploadé les fichiers DistanceTimeCoordinates 
            // et DistanceTimeData, on les importe. 
            // Sinon, on conserve les anciennes données
            if (importCoord) {
                ImportBase.importCoordinatesFromWeb(
                        files.get(ATT_SESSION_COORDINATES_FILE),
                        files.get(ATT_SESSION_DISTANCES_FILE));
            }
            
            // Import des fichiers Fleet et SwapActions
            ImportBase.importParametersFromWeb(
                    files.get(ATT_SESSION_FLEET_FILE), 
                    files.get(ATT_SESSION_SWAPACTIONS_FILE));

            // Import du fichier Locations
            ImportBase.importLocationsFromWeb(
                    files.get(ATT_SESSION_LOCATIONS_FILE));
            
        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Lance le calcul de la solution optimale de livraison
     * @param request
     * @param response 
     */
    public void calcSolution(HttpServletRequest request, HttpServletResponse response) {
        SolutionCalc calc = new SolutionCalc();
        Date start, end;
        
        try {
            // Initialisation des DAO (et suppression des tournées existantes)
            calc.initialize();
            
            start = new Date();
            Utils.log("Calcul démarré");
            
            // Génération de la solution
            calc.scanCustomerRequests();
            
            end = new Date();
            long diffInMillies = end.getTime() - start.getTime();
            Utils.log("Calcul effectué en " + diffInMillies + " ms");
            
        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Exporte la solution générée en fichier CSV
     * @param request
     * @param response
     * @throws Exception 
     */
    public void exportSolution(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/csv");
        response.setHeader("content-disposition","filename=Solution.csv"); // set the file name to whatever required..
        
        try (PrintWriter out = response.getWriter()) {
            
            String line = "";
            
            String[] titles = { 
                "TOUR_ID", 
                "TOUR_POSITION", 
                "LOCATION_ID", 
                "LOCATION_TYPE",
                "SEMI_TRAILER_ATTACHED",
                "SWAP_BODY_TRAILER",
                "SWAP_BODY_SEMI_TRAILER",
                "SWAP_ACTION",
                "SWAP_BODY_1_QUANTITY",
                "SWAP_BODY_2_QUANTITY"
            };

            boolean first = true;

            // Ligne d'en-têtes
            for (String title : titles) {
                if (first) {
                    first = false;
                } else {
                    line += ";";
                }

                line = Utils.write(title, line);
            }

            out.println(line);
            
            RouteDao routeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRouteDao();
            Collection<Route> listRoutes = routeManager.findAll();
        
            int nbTour = 0;
            Tour lastTour = null;

            // Parcours de l'ensemble des routes (ordonnées par tournée et position)
            for (Route route : listRoutes) {
                String value = "";

                Tour tour = route.getTour();
                if (!tour.equals(lastTour)) {
                    nbTour++;
                    lastTour = tour;
                }

                value += "R" + (nbTour) + ";";
                value += route.getPosition() + ";";
                value += route.getLocation().getId() + ";";
                value += route.getLocationType().name() + ";";
                value += (route.isTrailerAttached() ? "1" : "0") + ";";
                value += route.getFirstTrailer() + ";";
                value += route.getLastTrailer() + ";";
                value += route.getSwapAction() + ";";
                value += route.getQty1() + ";";
                value += route.getQty2();

                line = "";
                line = Utils.write(value, line);
                
                out.println(line);
            }

            Utils.log("Solution exportée");

            out.flush();
            out.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
