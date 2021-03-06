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
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import metier.Coordinate;
import metier.Customer;
import metier.Depot;
import metier.LocationType;
import metier.Route;
import metier.RoutingParameters;
import metier.SwapAction;
import metier.SwapLocation;
import metier.Tour;
import utils.CoordinatesCalc;
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
    
    RoutingParameters parameters;
    
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
        
        //Variable utile pour le calcul
        parameters = parametersManager.find();
        
        routeManager.deleteAll();
        tourManager.deleteAll();
    }
    
    /**
     * Méthode qui génére les tournées
     * @throws Exception 
     */
    public void scanCustomerRequests() throws Exception {
        CoordinatesCalc calc = new CoordinatesCalc();
        
        List<Tour> tournees = new ArrayList();        
        List<Customer> allCustomers = (List<Customer>) customerManager.findAll();
        Collections.sort(allCustomers);
        
        for (Customer customer : allCustomers) {
            //Regarde tout les tournées
            if(tournees.isEmpty()) {
                //Création d'un nouveau tour
                tournees.add(createNewTour(customer));
            } else {
                ListIterator<Tour> iter = tournees.listIterator();
                boolean isServe = false;
                while (iter.hasNext()) {
                    Tour tour = iter.next();
                    //Vérifie si on a la place pour le client
                    double qty1Total = tour.getFirstTrailerQuantity()+ customer.getOrderedQty();
                    double tourTotal = tour.getTourQuantity()+ customer.getOrderedQty();

                    //Si le camion a une remorque
                    boolean attached = ((Route) tour.getListRoutes().get(tour.getListRoutes().size() - 1)).isTrailerAttached();

                    if ((qty1Total < parameters.getBodyCapacity() && !attached) || (tourTotal < parameters.getBodyCapacity() * 2 && attached && customer.isAccessible())) {
                        //Vérifie si on a le tps
                        if(canAddCostumer(tour, customer)) {
                            //Ajoute la route      
                            addCustumer(tour, customer, true);
                            isServe = true;
                            break;                            
                        }
                    }
                }
                
                if(! isServe) {
                    //Création d'un nouveau tour
                    iter.add(createNewTour(customer));
                }
            }
        }
        
        //Termine toutes les tournées
        for(Tour t : tournees)
            endedTour(t);
        
        Utils.log("Tournées créées");
    }
    
    /**
     * La méthode crée une nouveau tour
     * @param customer client à visiter
     * @return tour créé
     * @throws Exception 
     */
    public Tour createNewTour(Customer customer) throws Exception {        
        Depot depot = depotManager.find();
        
        //Vérifie si on a besoin d'un train
        boolean attachTrailer = false;
        if (customer.getOrderedQty() > parameters.getBodyCapacity()) {
            attachTrailer = true;
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
    
    /**
     * La méthode vérifie si le camion à le temps de passer chez le client
     * @param tour la tournée
     * @param c le client à ajouter dans le Body1
     * @return 
     * @throws Exception 
     */
    public boolean canAddCostumer(Tour tour, Customer c) throws Exception {        
        CoordinatesCalc calc = new CoordinatesCalc();
        
        Route swapRoute = tour.getSwapLocationOfSwap();
        double tpsTotal = 0.0;
        
        if(swapRoute != null) {
            //Récupére le temps Avant le Swap   
            tpsTotal += calculTime(tour.getListRoutes(), 1, swapRoute.getPosition() - 1);

            //Ajoute le temps pour aller au client + Customer to Swap + Swap to nextCustomer
            tpsTotal += calc.getTimeBetweenCoord(tour.getListRoutes().get(swapRoute.getPosition() - 2).getLocation().getCoordinate(), c.getCoordinate())
                    + calc.getTimeBetweenCoord(c.getCoordinate(), swapRoute.getLocation().getCoordinate())
                    + c.getServiceTime()
                    + parameters.getSwapTime();

            //Ajoute le temps pour le reste de la tournée            
            tpsTotal += calculTime(tour.getListRoutes(), swapRoute.getPosition(), tour.getListRoutes().size());
            
            //Temps pour récupére remorque et aller au dépot
            tpsTotal += calc.getTimeBetweenCoord(tour.getLastRoute().getLocation().getCoordinate(), swapRoute.getLocation().getCoordinate())
                        + parameters.getPickupTime()
                        + getTimeReturn(swapRoute.getLocation().getCoordinate());
        } else {
            tpsTotal = tour.getTourTime() 
                + calc.getTimeBetweenCoord(tour.getLastRoute().getLocation().getCoordinate(), c.getCoordinate())
                + c.getServiceTime() + getTimeReturn(c.getCoordinate());
        }
        
        return tpsTotal <= parameters.getOperatingTime();
    }
    
    /**
     * Cette méthode permet de calculer le temps de service pour une liste de route
     * @param list
     * @param begin 
     * @param end
     * @return le temps
     * @throws Exception 
     */
    public double calculTime(List<Route> list, int begin, int end) throws Exception {
        CoordinatesCalc calc = new CoordinatesCalc();
        double tpsTotal = 0.0;
        
        for(int i = begin; i < end; i++) {
            if(list.get(i).getLocationType() == LocationType.SWAP_LOCATION) {
                switch(list.get(i).getSwapAction()) {
                    case PARK:
                        tpsTotal += parameters.getParkTime();
                        break;
                    case PICKUP:
                        tpsTotal += parameters.getPickupTime();
                        break;
                }
            } else if(list.get(i).getLocationType() == LocationType.CUSTOMER) {
                tpsTotal += ((Customer) list.get(i).getLocation()).getServiceTime();
            }
            tpsTotal += calc.getTimeBetweenCoord(list.get(i-1).getLocation().getCoordinate(), list.get(i).getLocation().getCoordinate());
        }
        
        return tpsTotal;
    }
    
    /**
     * Méthode permet d'ajouter un client à une tournée déjà créé
     * @param t la tournée
     * @param c le client
     * @param inFirstTrailer si vaut true, la commande doit être ajouter dans la 1ére remorque
     * @throws Exception 
     */
    public void addCustumer(Tour t, Customer c, boolean inFirstTrailer) throws Exception {
        List<Route> listRoutes = t.getListRoutes();
        
        if(inFirstTrailer) {
            //Récupére la route précédente
            int position = t.getPositionOfSwap();
            
            if(position == 0) {
                //On est jamais passer dans le SwapLocation
                position = t.getListRoutes().size() + 1;
                
                //Récupérer la route précédente
                Route r = t.getLastRoute();
                
                //Ajoute une route à cette position
                Route route = new Route();
                route.setTour(t);
                route.setPosition(position);
                route.setLocation(c);
                route.setLocationType(LocationType.CUSTOMER);
                route.setTrailerAttached(r.isTrailerAttached());
                route.setFirstTrailer(r.getFirstTrailer());
                route.setLastTrailer(r.getLastTrailer());
                route.setSwapAction(SwapAction.NONE);
                route.setQty1(c.getOrderedQty() > parameters.getBodyCapacity() ? parameters.getBodyCapacity() : c.getOrderedQty());
                route.setQty2(c.getOrderedQty() > parameters.getBodyCapacity() ? c.getOrderedQty() - parameters.getBodyCapacity() : 0);
                listRoutes.add(route);

                t.setListRoutes(listRoutes);
            } else {
                //Incrémente les positions des routes allant du SwapLocation jusqu'à la fin
                for(int i = position - 1; i < listRoutes.size(); i++) {
                    Route r = listRoutes.get(i);
                    r.setPosition(r.getPosition() + 1);
                    listRoutes.set(i, r);
                }
                
                //Ajoute une route à cette position
                Route route = new Route();
                route.setTour(t);
                route.setPosition(position);
                route.setLocation(c);
                route.setLocationType(LocationType.CUSTOMER);
                route.setTrailerAttached(false);
                route.setFirstTrailer(1);
                route.setLastTrailer(2);
                route.setSwapAction(SwapAction.NONE);
                route.setQty1(c.getOrderedQty());
                route.setQty2(0);
                listRoutes.add(route);

                t.setListRoutes(listRoutes);
            }
        } else {
            //Récupére la dernière route pour avoir l'état du camion
            Route last = t.getLastRoute();
            
            Route route = new Route();
            route.setTour(t);
            route.setPosition(listRoutes.size() + 1);
            route.setLocation(c);
            route.setLocationType(LocationType.CUSTOMER);
            route.setTrailerAttached(last.isTrailerAttached());
            route.setFirstTrailer(last.getFirstTrailer());
            route.setLastTrailer(last.getLastTrailer());
            route.setSwapAction(SwapAction.NONE);
            route.setQty1(0);
            route.setQty2(c.getOrderedQty());
            listRoutes.add(route);
            
            t.setListRoutes(listRoutes);
        }     
    }
    
    public void endedTour(Tour t) throws Exception {        
        Depot depot = depotManager.find();
        
        List<Route> listRoutes = t.getListRoutes();
        Route last = t.getLastRoute();
            
        boolean attachTrailer = false;
        int firstTrailer = 1;
        int lastTrailer = 0;
        
        if(last.isTrailerAttached()) {
            attachTrailer = true;
            lastTrailer = last.getLastTrailer();
        }
        
        Route route = new Route();
        route.setTour(t);
        route.setPosition(listRoutes.size() + 1);
        route.setLocation(depot);
        route.setLocationType(LocationType.DEPOT);
        route.setTrailerAttached(attachTrailer);
        route.setFirstTrailer(firstTrailer);
        route.setLastTrailer(lastTrailer);
        route.setSwapAction(SwapAction.NONE);
        route.setQty1(0);
        route.setQty2(0);
        listRoutes.add(route);
        
        t.setListRoutes(listRoutes); 
        tourManager.create(t);
    }
    
    public double getTimeReturn(Coordinate coordinate) throws Exception {
        Depot depot = depotManager.find();        
        CoordinatesCalc calc = new CoordinatesCalc();
                
        return calc.getTimeBetweenCoord(coordinate, depot.getCoordinate());
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
