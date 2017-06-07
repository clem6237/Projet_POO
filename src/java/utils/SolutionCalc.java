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
    
    RoutingParameters parameters;
    
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
                    } else if(qty1Total > parameters.getBodyCapacity() && (tour.getLastTrailerQuantity() + customer.getOrderedQty()) <= parameters.getBodyCapacity() && !attached){                        
                        //Si il n'y a plus de place dans la remorque 1 et que l'on a pas de 2éme remorque
                        if(tour.getLastTrailerQuantity() == 0){ 
                            //Si le camion a le temps de faire toute les actions
                            SwapLocation swapLocation = canGoToSwapLocation(tour, customer);
                            
                            if(swapLocation != null) {
                                addAttachedTrailerAndSwapLocation(tour, swapLocation, customer);
                                isServe = true;
                                break;
                            }
                        } else { //Si on a que le deuxième remorque
                            //Récupérer le SwapLocation
                            SwapLocation sp = tour.getSwapLocation();
                            
                            //Tps total : tourTps + go to client + go to SwapLocation + PickUp + go to Dépot
                            double tpsTotal = tour.getTourTime()
                                        + calc.getTimeBetweenCoord(tour.getLastRoute().getLocation().getCoordinate(), customer.getCoordinate())
                                        + customer.getServiceTime()
                                        + calc.getTimeBetweenCoord(customer.getCoordinate(), sp.getCoordinate())
                                        + parameters.getPickupTime()
                                        + getTimeReturn(sp.getCoordinate());
                                    
                            //Vérifie s'il reste de la place et que l'on a le temps
                            if(tpsTotal <= parameters.getOperatingTime()) {
                                addCustumer(tour, customer, false);
                                isServe = true;
                                break;
                            }                                
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
     * Cette méthode permet de vérifie si le camion peut mettre en place un Swap
     * @param tour
     * @param c le client où l'on souhaite passer après le Swap
     * @return le SwapLocation par lequel on passe
     * @throws Exception 
     */
    public SwapLocation canGoToSwapLocation(Tour tour, Customer c) throws Exception {   
        CoordinatesCalc calc = new CoordinatesCalc();
        Depot depot = depotManager.find();     
        double tpsTotal = 0;
        
        //Récupérer le 1er client
        Customer c1 = (Customer) tour.getFirstCustomer().getLocation();
        
        //Récupérer le SwapLocation le plus proche du dépôt
        SwapLocation swap = new SwapLocation();
        swap = swap.getNear(c1.getCoordinate());
        
        //Calcul du temps de trajet du Dépot au swap + time to park + aller au client
        tpsTotal += calc.getTimeBetweenCoord(depot.getCoordinate(), swap.getCoordinate())
                 + parameters.getParkTime()
                 + calc.getTimeBetweenCoord(swap.getCoordinate(), c1.getCoordinate());
        
        //Calcule le temps pour le reste de la tournée
        List<Route> list = tour.getListRoutes();
        tpsTotal += calculTime(list, 2, list.size());
        
        //Calcul tps last client to Swap + tps to swap + tps Swap to client que l'on souhaite ajouter
        tpsTotal += calc.getTimeBetweenCoord(tour.getListRoutes().get(tour.getListRoutes().size() - 1).getLocation().getCoordinate(), swap.getCoordinate())
                        + parameters.getSwapTime()
                        + calc.getTimeBetweenCoord(swap.getCoordinate(), c.getCoordinate());
        
        //Calcul tps retour au SwapLocation + tps to PickUp + tps du retour dépot
        tpsTotal += c.getServiceTime() 
                    +calc.getTimeBetweenCoord(c.getCoordinate(), swap.getCoordinate()) 
                    + parameters.getPickupTime()
                    + getTimeReturn(swap.getCoordinate());
        
        if(tpsTotal <= parameters.getOperatingTime())
            return swap;
        return null;
    }
    
    /**
     * La fonction permet d'attache une remorque et de passer dans un Swaplocation
     * @param t
     * @param swapLocation
     * @param c 
     */
    public void addAttachedTrailerAndSwapLocation(Tour t, SwapLocation swapLocation, Customer c) {
        //Parcours les Routes pour attacher la remorque et modifier les positions permettant l'ajout du SwapLocation
        List<Route> list = t.getListRoutes();
        ListIterator<Route> iter = list.listIterator();
        while (iter.hasNext()) {
            Route r = iter.next();
            if(r.getPosition() >= 2) {
                //Modifie les positions des routes après le SwapLocation
                 r.setPosition(r.getPosition() + 1);
                 r.setLastTrailer(2);
            } else if(r.getPosition() == 1) {
                //Ajoute la remorque au dépot 
                r.setLastTrailer(2);
                r.setTrailerAttached(true);
            }
        }
                
        //Ajouter une route en position 2 pour déposer la remorque
        Route route = new Route();
        route.setTour(t);
        route.setPosition(2);
        route.setLocation(swapLocation);
        route.setLocationType(LocationType.SWAP_LOCATION);
        route.setTrailerAttached(true);
        route.setFirstTrailer(1);
        route.setLastTrailer(2);
        route.setSwapAction(SwapAction.PARK);
        route.setQty1(0);
        route.setQty2(0);
        list.add(route);
               
        //Ajoute une route à la suite pour aller au SwapLocation
        route = new Route();
        route.setTour(t);
        route.setPosition(list.size()+ 1);
        route.setLocation(swapLocation);
        route.setLocationType(LocationType.SWAP_LOCATION);
        route.setTrailerAttached(false);
        route.setFirstTrailer(list.get(list.size() - 1).getFirstTrailer());
        route.setLastTrailer(list.get(list.size() - 1).getLastTrailer());
        route.setSwapAction(SwapAction.SWAP);
        route.setQty1(0);
        route.setQty2(0);
        list.add(route);
        
        //Ajoute une route pour aller chez le client
        route = new Route();
        route.setTour(t);
        route.setPosition(list.size() + 1);
        route.setLocation(c);
        route.setLocationType(LocationType.CUSTOMER);
        route.setTrailerAttached(false);
        route.setFirstTrailer(2);
        route.setLastTrailer(1);
        route.setSwapAction(SwapAction.NONE);
        route.setQty1(0);
        route.setQty2(c.getOrderedQty());
        list.add(route);
        
        t.setListRoutes(list);
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
        
        //Si le camions avait une remorque
        SwapLocation sp = t.getSwapLocation();
        if(sp != null) {
            attachTrailer = true;
            firstTrailer = 2;
            lastTrailer = 1;
            
            //Dans état précédent
            Route route = new Route();
            route.setTour(t);
            route.setPosition(listRoutes.size() + 1);
            route.setLocation(sp);
            route.setLocationType(LocationType.SWAP_LOCATION);
            route.setTrailerAttached(false);
            route.setFirstTrailer(last.getFirstTrailer());
            route.setLastTrailer(last.getLastTrailer());
            route.setSwapAction(SwapAction.PICKUP);
            route.setQty1(0);
            route.setQty2(0);
            listRoutes.add(route);
        }
        
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
}
