package utils;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import metier.Coordinate;
import metier.Customer;
import metier.Depot;
import metier.LocationType;
import metier.Route;
import metier.RoutingParameters;
import metier.SwapAction;
import metier.Tour;

/**
 * Contient les méthodes permettant de calculer la solution optimale de 
 * livraison des commandes clients.
 * @author clementruffin
 */
public class SolutionCalc {
    
    RoutingParametersDao parametersManager;
    DistanceTimeDao distanceTimeManager;
    CoordinateDao coordinateManager;
    LocationDao locationManager;
    DepotDao depotManager;
    SwapLocationDao swapLocationManager;
    CustomerDao customerManager;
    TourDao tourManager;
    RouteDao routeManager;
    
    /**
     * Supprime les tournées enregistrées
     * @throws DaoException 
     */
    public void initialize() throws DaoException {
        parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        depotManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDepotDao();
        customerManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCustomerDao();
        tourManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getTourDao();
        routeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRouteDao();
        
        routeManager.deleteAll();
        tourManager.deleteAll();
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
                        addCustomer(lastTour, customer, attached);
                    } else if(tourTotal < parameters.getBodyCapacity() * 2 && attached && customer.isAccessible()) {
                        //Ajoute la route
                        addCustomer(lastTour, customer, attached);
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
    
    public Tour addCustomer(Tour t, Customer c, boolean attachTrailer) throws Exception {
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
}
