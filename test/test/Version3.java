package test;

import dao.CoordinateDao;
import dao.CustomerDao;
import dao.DaoException;
import dao.DaoFactory;
import dao.DepotDao;
import dao.DistanceTimeDao;
import dao.LocationDao;
import dao.PersistenceType;
import dao.RouteDao;
import dao.RoutingParametersDao;
import dao.SwapLocationDao;
import dao.TourDao;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import metier.Coordinate;
import metier.Customer;
import metier.Depot;
import metier.Location;
import metier.LocationType;
import metier.Route;
import metier.RoutingParameters;
import metier.SwapAction;
import metier.Tour;
import utils.CoordinatesCalc;
import utils.ImportBase;
import utils.Utils;

/**
 *
 * @author clementruffin
 */
public class Version3 {
    
    static String filePath = "Projet2017/";
    static String fileNameFleet = "small_normal/Fleet.csv";
    static String fileNameSwapActions = "small_normal/SwapActions.csv";
    static String fileNameCoordinates = "dima/DistanceTimesCoordinates.csv";
    static String fileNameDistanceTime = "dima/DistanceTimesData.csv";
    static String fileNameLocations = "small_normal/Locations.csv";
    
    final String fileNameSolutions = "small_normal/Solution.csv";
    
    RoutingParametersDao parametersManager;
    DistanceTimeDao distanceTimeManager;
    CoordinateDao coordinateManager;
    LocationDao locationManager;
    DepotDao depotManager;
    SwapLocationDao swapLocationManager;
    CustomerDao customerManager;
    TourDao tourManager;
    RouteDao routeManager;
    
    public static void main(String[] args) throws Exception {
        Version3 test = new Version3();
        
        Utils.log("Démarrage");
        test.initialize();
        
        // Importation des paramètres, coordonnées & emplacements
        //ImportBase.importParameters(filePath + fileNameFleet, filePath + fileNameSwapActions);
        //ImportBase.importCoordinates(filePath + fileNameCoordinates);
        //ImportBase.importDistanceTime(filePath + fileNameDistanceTime);
        //ImportBase.importLocations(filePath + fileNameLocations);
        
        // Algorithme de création des tournées
        test.scanCustomerRequests();
        
        // Exportation de la solution
        test.createSolution();
    }
    
    public void initialize() throws DaoException {
        parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        distanceTimeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDistanceTimeDao();
        locationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getLocationDao();
        depotManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDepotDao();
        swapLocationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getSwapLocationDao();
        customerManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCustomerDao();
        tourManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getTourDao();
        routeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRouteDao();
        
        routeManager.deleteAll();
        tourManager.deleteAll();
        /*customerManager.deleteAll();
        swapLocationManager.deleteAll();
        depotManager.deleteAll();
        locationManager.deleteAll();
        //distanceTimeManager.deleteAll();
        //coordinateManager.deleteAll();
        parametersManager.deleteAll();*/
    }
    
    public void scanCustomerRequests() throws Exception {
        RoutingParameters parameters = parametersManager.find();
        
        Tour lastTour = null;
        List<Tour> tournees = new ArrayList();
        Collection<Customer> allCustomers = customerManager.findAll();
        
        for (Customer customer : allCustomers) {
            System.out.println(customer);
            //Récupére le dernier tour
            if(! tournees.isEmpty()) {
                lastTour = tournees.get(tournees.size() - 1);
            }
            
            if(lastTour != null) {
                //Vérifie si le client peut être livré à temps
                double tpsCustomer = lastTour.getTourTime() + customer.getServiceTime() + getTimeReturn(customer.getCoordinate());
                if(tpsCustomer > parameters.getOperatingTime()) {
                    //Création d'un nouveau tour
                    endedTour(lastTour);
                    tournees.add(createNewTour(customer));
                } else {
                    double qty1Total = lastTour.getFirstTrailerQuantity()+ customer.getOrderedQty();
                    double tourTotal = lastTour.getTourQuantity()+ customer.getOrderedQty();
                    
                    //Si le camion a une remorque
                    boolean attached = ((Route) lastTour.getListRoutes().get(lastTour.getListRoutes().size() - 1)).isTrailerAttached();
                
                    if (qty1Total < parameters.getBodyCapacity() && !attached) {
                        //Ajoute la route      
                        addCustumer(lastTour, customer, attached);
                    } else if(tourTotal < parameters.getBodyCapacity() * 2 && attached && customer.isAccessible()) {
                        //Ajoute la route
                        addCustumer(lastTour, customer, attached);
                    } else if((qty1Total > parameters.getBodyCapacity() && !attached) 
                                || (tourTotal > parameters.getBodyCapacity() * 2 && attached && customer.isAccessible())){
                        //Création d'un nouveau tour
                        endedTour(lastTour);
                        tournees.add(createNewTour(customer));
                    } 
                }
            } else {                
                //Création d'un nouveau tour
                tournees.add(createNewTour(customer));
            }
        }
        //Termine la derniére tournée
        lastTour = tournees.get(tournees.size() - 1);
        endedTour(lastTour);
        
        Utils.log("Tournées créées");
    }
    
    public Tour createNewTour(Customer customer) throws Exception {
        System.out.println("Création d'un nouveau tour");
        
        Depot depot = depotManager.find();        
        RoutingParameters parameters = parametersManager.find();
        
        //Vérifie si on a besoin d'un train
        boolean attachTrailer = false;

        if (customer.getOrderedQty() > parameters.getBodyCapacity()) {
            if (!customer.isAccessible()) {
                Utils.log("ERREUR - Livraison impossible (Client '" + customer.getId() + "' -> " 
                     + customer.getOrderedQty() + " / " + parameters.getBodyCapacity() + " <=> NON ACCESSIBLE)");
                return null;
            } else {
                attachTrailer = true;
            }
        }
                
        Tour tour = new Tour();
        Route route;
        List<Route> listRoutes = new ArrayList();
        
        route = new Route();
        route.setTour(tour);
        route.setPosition(1);
        route.setLocation(depot);
        route.setLocationType(LocationType.DEPOT);
        route.setTrailerAttached(attachTrailer);
        route.setFirstTrailer(1);
        route.setLastTrailer(attachTrailer ? 2 : 0);
        route.setSwapAction(SwapAction.NONE);
        route.setQty1(0);
        route.setQty2(0);
        listRoutes.add(route);
        
        route = new Route();
        route.setTour(tour);
        route.setPosition(2);
        route.setLocation(customer);
        route.setLocationType(LocationType.CUSTOMER);
        route.setTrailerAttached(attachTrailer);
        route.setFirstTrailer(1);
        route.setLastTrailer(attachTrailer ? 2 : 0);
        route.setSwapAction(SwapAction.NONE);
        route.setQty1(customer.getOrderedQty() > parameters.getBodyCapacity() ? parameters.getBodyCapacity() : customer.getOrderedQty());
        route.setQty2(customer.getOrderedQty() > parameters.getBodyCapacity() ? customer.getOrderedQty() - parameters.getBodyCapacity() : 0);
        listRoutes.add(route);
        
        tour.setListRoutes(listRoutes);
        return tour;
    }
    
    public Tour addCustumer(Tour t, Customer c, boolean attachTrailer) throws Exception {
        System.out.println("Ajout d'une route");   
        RoutingParameters parameters = parametersManager.find();
        
        List<Route> listRoutes = t.getListRoutes();
                
        Route route = new Route();
        route.setTour(t);
        route.setPosition(listRoutes.size() + 1);
        route.setLocation(c);
        route.setLocationType(LocationType.CUSTOMER);
        route.setTrailerAttached(attachTrailer);
        route.setFirstTrailer(1);
        route.setLastTrailer(attachTrailer ? 2 : 0);
        route.setSwapAction(SwapAction.NONE);
        route.setQty1(c.getOrderedQty() > parameters.getBodyCapacity() ? parameters.getBodyCapacity() : c.getOrderedQty());
        route.setQty2(c.getOrderedQty() > parameters.getBodyCapacity() ? c.getOrderedQty() - parameters.getBodyCapacity() : 0);
        listRoutes.add(route);
        
        t.setListRoutes(listRoutes);
        return t;
    }
    
    public Tour endedTour(Tour t) throws Exception {        
        Depot depot = depotManager.find();        
        RoutingParameters parameters = parametersManager.find();
        
        List<Route> listRoutes = t.getListRoutes();
        
        boolean attachTrailer = false;
        if(((Route) listRoutes.get(listRoutes.size() - 1)).isTrailerAttached())
            attachTrailer = true;
                
        Route route = new Route();
        route.setTour(t);
        route.setPosition(listRoutes.size() + 1);
        route.setLocation(depot);
        route.setLocationType(LocationType.DEPOT);
        route.setTrailerAttached(attachTrailer);
        route.setFirstTrailer(1);
        route.setLastTrailer(attachTrailer ? 2 : 0);
        route.setSwapAction(SwapAction.NONE);
        route.setQty1(0);
        route.setQty2(0);
        listRoutes.add(route);
        
        t.setListRoutes(listRoutes);        
        tourManager.create(t);
        return t;
    }
    
    public double getTimeReturn(Coordinate coordinate) throws Exception {
        Depot depot = depotManager.find();
        Coordinate coordDepot = depot.getCoordinate();
        
        CoordinatesCalc calc = new CoordinatesCalc();
        
        return calc.getTotalTimeBetweenCoord(coordinate, coordDepot);
    }
    
    public void createSolution() throws Exception {
        FileWriter fw = new FileWriter(filePath + fileNameSolutions);
        BufferedWriter bw = new BufferedWriter(fw);
        
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
        
        for (String title : titles) {
            if (first) {
                first = false;
            } else {
                bw.write(";");
            }
            
            write(title, bw);
        }
        
        bw.write("\n");
        
        Collection<Route> listRoutes = routeManager.findAll();
        
        int nbTour = 0;
        Tour lastTour = null;
        
        for (Route route : listRoutes) {
            first = true;
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
            
            write(value, bw);
            bw.write("\n");    
        }
        
        Utils.log("Solution exportée");
        
        bw.close();
        fw.close();
    }
    
    private void write(String value, BufferedWriter bw) throws Exception {

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

        bw.write(value);
    }
}
