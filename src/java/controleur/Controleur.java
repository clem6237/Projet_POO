/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controleur;

import dao.CoordinateDao;
import dao.DaoFactory;
import dao.PersistenceType;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import metier.Coordinate;
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
        int nextPage = 1;
        
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
                            if(!coordinatesFile.equals(""))
                                session.setAttribute(ATT_SESSION_COORDINATES_FILE, coordinatesFile);
                            
                            String distancesFile = request.getParameter("distances");      
                            if(!distancesFile.equals(""))
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
                            response.sendError(response.SC_BAD_REQUEST, "error");
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
                            response.sendError(response.SC_BAD_REQUEST, "error");
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
                            this.importFiles();
                        }
                    break;
                default :
                    response.sendError(response.SC_BAD_REQUEST, "error");
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
    
    public void importFiles() throws Exception {
        if (!ATT_SESSION_FLEET_FILE.isEmpty()) {
            ImportBase.importFleetFile(ATT_SESSION_FLEET_FILE);
        }
        
        if (!ATT_SESSION_SWAPACTIONS_FILE.isEmpty()) {
            ImportBase.importSwapActionsFile(ATT_SESSION_SWAPACTIONS_FILE);
        }
        
        if (!ATT_SESSION_COORDINATES_FILE.isEmpty()) {
            ImportBase.importCoordinates(ATT_SESSION_COORDINATES_FILE);
        }
        
        if (!ATT_SESSION_DISTANCES_FILE.isEmpty()) {
            ImportBase.importDistanceTime(ATT_SESSION_DISTANCES_FILE);
        }
        
        if (!ATT_SESSION_LOCATIONS_FILE.isEmpty()) {
            ImportBase.importLocations(ATT_SESSION_LOCATIONS_FILE);
        }
    }
    
    private void getCoordinates() {
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        List<Coordinate> list = (List<Coordinate>) coordinateManager.findAll();
    }
}
