package test;

import utils.ImportBase;
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
import metier.Coordinate;
import metier.Customer;
import metier.Depot;
import metier.LocationType;
import metier.Route;
import metier.RoutingParameters;
import metier.SwapAction;
import metier.Tour;
import utils.CoordinatesCalc;
import utils.Utils;

/**
 *
 * @author clementruffin
 */
public class Version2 {
    
    static String filePath = "Projet2017/";
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
        Version2 test = new Version2();
        
        Utils.log("Démarrage");
        test.initialize();
        
        // Importation des paramètres, coordonnées & emplacements
        ImportBase importBase = new ImportBase(filePath);
        importBase.importParameters();
        //importBase.importCoordinates();
        importBase.importLocations();
        
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
        customerManager.deleteAll();
        swapLocationManager.deleteAll();
        depotManager.deleteAll();
        locationManager.deleteAll();
        //distanceTimeManager.deleteAll();
        //coordinateManager.deleteAll();
        parametersManager.deleteAll();
    }
    
    public void scanCustomerRequests() throws Exception {
        Collection<Customer> allCustomers = customerManager.findAll();
        
        for (Customer customer : allCustomers) {
            this.processCustomerRequest(customer);
        }
        
        Utils.log("Tournées créées");
    }
    
    public void processCustomerRequest(Customer customer) throws Exception {
        Depot depot = depotManager.find();
        Coordinate coordDepot = depot.getCoordinate();
        
        CoordinatesCalc calc = new CoordinatesCalc();
        
        double distanceTotal = calc.getTotalDistanceBetweenCoord(coordDepot, customer.getCoordinate());
        double timeTotal = calc.getTotalTimeBetweenCoord(coordDepot, customer.getCoordinate())
                + customer.getServiceTime();
        
        RoutingParameters parameters = parametersManager.find();
        
        if (timeTotal > parameters.getOperatingTime()) {
            Utils.log("ERREUR - Livraison impossible (Client '" + customer.getId() + "' -> " 
                    + timeTotal + " / " + parameters.getOperatingTime() + ")");
            return;
        }
        
        boolean attachTrailer = false;
        
        if (customer.getOrderedQty() > parameters.getBodyCapacity()) {
            if (!customer.isAccessible()) {
                Utils.log("ERREUR - Livraison impossible (Client '" + customer.getId() + "' -> " 
                    + customer.getOrderedQty() + " / " + parameters.getBodyCapacity() + " <=> NON ACCESSIBLE)");
                return;
            } else {
                attachTrailer = true;
            }
        }
        
        Tour tour = new Tour();
        Route route;
        Collection<Route> listRoutes = new ArrayList();
        
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
        
        route = new Route();
        route.setTour(tour);
        route.setPosition(3);
        route.setLocation(depot);
        route.setLocationType(LocationType.DEPOT);
        route.setTrailerAttached(attachTrailer);
        route.setFirstTrailer(1);
        route.setLastTrailer(attachTrailer ? 2 : 0);
        route.setSwapAction(SwapAction.NONE);
        route.setQty1(0);
        route.setQty2(0);
        listRoutes.add(route);
        
        tour.setListRoutes(listRoutes);
        tourManager.create(tour);
    }
    
    public void createSolution() throws Exception {
        FileWriter fw = new FileWriter(filePath + fileNameSolutions);
        BufferedWriter bw = new BufferedWriter(fw);
        
        String[] titles = { 
            "TOUR_ID", 
            "TOUR_POSITION", 
            "LOCATION_ID", 
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
        
        for (Route route : listRoutes) {
            first = true;
           
            String value = "";
            
            value += "R" + route.getTour().getId() + ";";
            value += route.getPosition() + ";";
            value += route.getLocation().getId() + ";";
            value += (route.isTrailerAttached() ? "1" : "0") + ";";
            value += route.getFirstTrailer() + ";";
            value += route.getLastTrailer() + ";";
            value += route.getSwapAction() + ";";
            value += route.getQty1() + ";";
            value += route.getQty2() + ";";
            
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
