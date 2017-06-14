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
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
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
    DepotDao depotManager;
    CustomerDao customerManager;
    TourDao tourManager;
    RouteDao routeManager;
    
    RoutingParameters parameters;
    Depot depot;
    KDTree<Customer> kdTree;
    
    /**
     * Supprime les tournées enregistrées en base.
     * @throws DaoException 
     */
    public void initialize() throws DaoException {
        parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        depotManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDepotDao();
        customerManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCustomerDao();
        tourManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getTourDao();
        routeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRouteDao();
        
        // Variables utile pour le calcul
        parameters = parametersManager.find();
        depot = depotManager.find();        
        
        // Suppression des tournées
        routeManager.deleteAll();
        tourManager.deleteAll();
    }
    
    /**
     * Génére les tournées.
     * @throws Exception 
     */
    public void scanCustomerRequests() throws Exception {
        int nbNotAdded = 0;
        CoordinatesCalc calc = new CoordinatesCalc();
        
        // Récupération de la liste de tous les clients
        List<Customer> allCustomers = (List<Customer>) customerManager.findAll();
        Collections.sort(allCustomers);
        
        // Ordonnancement des clients par proximité immédiate
        allCustomers = orderList(allCustomers);
        
        // Tant que tous les clients ne sont pas affectés à une tournée
        while(!allCustomers.isEmpty()) {
            
            Tour tour = new Tour();
            ListIterator<Customer> iter = allCustomers.listIterator();
            
            // Parcours des clients non affectés
            while (iter.hasNext()) {
                
                Customer customer = iter.next();
                
                // Si la tournée ne contient encore aucune route, elle est
                // initialisée et le client est ajouté
                if(tour.getListRoutes().isEmpty()) {
                    createNewTour(tour, customer);
                    iter.remove();
                    nbNotAdded = 0; 
                } else {
                    
                    // Vérification de la place disponible pour le client
                    double qty1Total = tour.getFirstTrailerQuantity()+ customer.getOrderedQty();
                    double tourTotal = tour.getTourQuantity()+ customer.getOrderedQty();

                    // Si le camion a une remorque
                    boolean attached = ((Route) tour.getListRoutes().get(tour.getListRoutes().size() - 1)).isTrailerAttached();
                    
                    // Mode camion :
                    //  -> Place disponible dans la remorque 1
                    // Mode train :
                    //  -> Place disponible dans la remorque 1 ou 2
                    //  -> ET client accessible par train
                    if ((qty1Total < parameters.getBodyCapacity() && !attached) || (tourTotal < parameters.getBodyCapacity() * 2 && attached && customer.isAccessible())) {
                        
                        // Vérification du temps disponible
                        if(canAddCostumer(tour, customer)) {
                            
                            // Ajout d'une route pour le client
                            addCustomer(tour, customer, true);
                            iter.remove();
                            nbNotAdded = 0;  
                            
                        } else {
                            nbNotAdded++;
                        }
                            
                    }
                    // Mode camion :
                    //  -> Plus de place dans la première remorque
                    //  -> MAIS place disponible dans la deuxième remorque
                    else if(qty1Total > parameters.getBodyCapacity() && (tour.getLastTrailerQuantity() + customer.getOrderedQty()) <= parameters.getBodyCapacity() && !attached){                        
                        
                        // Le camion n'a pas encore de deuxième remorque
                        // Vérification de la possibilité de l'ajouter
                        if(canAddTrailer(tour, customer) && tour.getSwapLocation() == null) {
                            
                            // Passage en mode train
                            addTrailer(tour, customer);
                            iter.remove();
                            nbNotAdded = 0;
                            
                        } else if(tour.getLastTrailerQuantity() == 0){ 
                            
                            // Vérification de la possibilité d'effectuer toutes
                            // les actions des swap location (PARK, SWAP, PICKUP)
                            SwapLocation swapLocation = canGoToSwapLocation(tour, customer);
                            
                            if(swapLocation != null) {
                                
                                // Passage en mode train et création des actions
                                addAttachedTrailerAndSwapLocation(tour, swapLocation, customer);
                                iter.remove();
                                nbNotAdded = 0;
                                
                            } else {
                                nbNotAdded++;
                            }
                            
                        } else { // Le camion a seulement la 2e remorque attachée
                            
                            // Récupération du swap location
                            SwapLocation sp = tour.getSwapLocation();
                            
                            // Calcul du temps :
                            //  -> temps de la tournée
                            //  -> trajet de la dernière position au client
                            //  -> temps de service
                            //  -> trajet du client au swap
                            //  -> temps de PICKUP
                            //  -> trajet du swap au dépôt
                            double tpsTotal = tour.getTourTime()
                                        + calc.getTimeBetweenCoord(tour.getLastRoute().getLocation().getCoordinate(), customer.getCoordinate())
                                        + customer.getServiceTime()
                                        + calc.getTimeBetweenCoord(customer.getCoordinate(), sp.getCoordinate())
                                        + parameters.getPickupTime()
                                        + calc.getTimeBetweenCoord(sp.getCoordinate(), depot.getCoordinate());
                                    
                            // Vérification du temps disponible
                            if(tpsTotal <= parameters.getOperatingTime()) {
                                
                                // Ajout d'une route pour le client
                                addCustomer(tour, customer, false);
                                iter.remove();
                                nbNotAdded = 0; 
                                
                            } else {
                                nbNotAdded++;
                            }                              
                        }
                        
                    } else {
                        nbNotAdded++;
                    }
                    
                    // Au-delà de 10 clients non ajoutés on passe à une autre tournée
                    if(nbNotAdded == 10){
                        break;
                    }
                }
            }
            
            // Clôture de la tournée
            endedTour(tour);
        }
        
        Utils.log("Tournées créées");
    }
    
    /**
     * Ordonne la liste pour avoir les clients les plus proches les un des autres.
     * @param allCustomers Liste des clients à ordonner
     * @return Liste ordonnée
     * @throws KeySizeException
     * @throws KeyDuplicateException 
     */
    public List<Customer> orderList(List<Customer> allCustomers) throws KeySizeException, KeyDuplicateException {
        CoordinatesCalc calc = new CoordinatesCalc(); 
        
        List<Customer> list = new ArrayList<>();
        Customer nearCustomer;
        
        list.add(allCustomers.get(0));
        allCustomers.remove(0);
        
        // Tant que tous les clients ne sont pas ordonnés
        while(!allCustomers.isEmpty()) {

            // Création d'un arbre de répartition
            kdTree = new KDTree<>(2);
            double x[] = new double[2];
            
            // Tous les clients (en dehors du client courant) sont insérés dans l'arbre
            for (Customer c : allCustomers) {
                x[0] = c.getCoordinate().getCoordX();
                x[1] = c.getCoordinate().getCoordY();
                kdTree.insert(x, c);
            }
            
            Customer customer = list.get(list.size() - 1);
            double sx = customer.getCoordinate().getCoordX();
            double sy = customer.getCoordinate().getCoordY();

            // Recherche du client le plus proche
            double s[] = { sx, sy };
            nearCustomer = (Customer) kdTree.nearest(s);
            
            list.add(nearCustomer);
            allCustomers.remove(nearCustomer);
        }
        
        return list;
    }
    
    /**
     * Création d'une nouvelle tournée.
     * @param customer Client à visiter
     * @param tour Tournée à initialiser
     * @throws Exception 
     */
    public void createNewTour(Tour tour, Customer customer) throws Exception {  
        
        // Vérification du besoin d'un train
        boolean attachTrailer = false;
        if (customer.getOrderedQty() > parameters.getBodyCapacity()) {
            attachTrailer = true;
        }
                
        Route route;
        List<Route> listRoutes = new ArrayList();
        
        // Départ du dépôt
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
        
        // Arrivée au client
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
    }
    
    /**
     * Vérificaion du temps disponible d'un camion pour passer chez un client.
     * @param tour Tournée
     * @param c Client à ajouter dans le Body1
     * @return TRUE si le client peut être ajouté
     * @throws Exception 
     */
    public boolean canAddCostumer(Tour tour, Customer c) throws Exception {        
        CoordinatesCalc calc = new CoordinatesCalc();
        
        Route swapRoute = tour.getSwapLocationOfSwap();
        double tpsTotal = 0.0;
        
        if(swapRoute != null) {
            
            // Récupération du temps avant le swap   
            tpsTotal += calculTime(tour.getListRoutes(), 1, swapRoute.getPosition() - 1);

            // Calcul du temps :
            //  -> trajet de la dernière position au client
            //  -> temps de service
            //  -> trajet du client au swap
            //  -> temps du SWAP
            tpsTotal += calc.getTimeBetweenCoord(tour.getListRoutes().get(swapRoute.getPosition() - 2).getLocation().getCoordinate(), c.getCoordinate())
                    + calc.getTimeBetweenCoord(c.getCoordinate(), swapRoute.getLocation().getCoordinate())
                    + c.getServiceTime()
                    + parameters.getSwapTime();

            // Ajout du temps pour le reste de la tournée
            tpsTotal += calculTime(tour.getListRoutes(), swapRoute.getPosition(), tour.getListRoutes().size());
            
            // Calcul du temps :
            //  -> trajet de la dernière position au swap
            //  -> temps du PICKUP
            //  -> trajet du swap au dépôt
            tpsTotal += calc.getTimeBetweenCoord(tour.getLastRoute().getLocation().getCoordinate(), swapRoute.getLocation().getCoordinate())
                        + parameters.getPickupTime()
                        + calc.getTimeBetweenCoord(swapRoute.getLocation().getCoordinate(), depot.getCoordinate());
        } else {
            tpsTotal = tour.getTourTime() 
                + calc.getTimeBetweenCoord(tour.getLastRoute().getLocation().getCoordinate(), c.getCoordinate())
                + c.getServiceTime() 
                + calc.getTimeBetweenCoord(c.getCoordinate(), depot.getCoordinate());
        }
        
        return tpsTotal <= parameters.getOperatingTime();
    }
    
    /**
     * Ajoute un client à une tournée déjà créée.
     * @param t Tournée
     * @param c Client
     * @param inFirstTrailer TRUE si la commande doit être ajoutée dans la 1ére remorque
     * @throws Exception 
     */
    public void addCustomer(Tour t, Customer c, boolean inFirstTrailer) throws Exception {
        List<Route> listRoutes = t.getListRoutes();
        
        // Ajout dans la 1ère remorque
        if(inFirstTrailer) {
            
            // Récupération de la route précédente
            int position = t.getPositionOfSwap();
            
            if(position == 0) { // On est jamais passé dans un swap location
                
                position = t.getListRoutes().size() + 1;
                
                // Récupération de la route précédente
                Route r = t.getLastRoute();
                
                double Qty1 = (c.getOrderedQty() > parameters.getBodyCapacity() ? parameters.getBodyCapacity() : c.getOrderedQty());
                double Qty2 = (c.getOrderedQty() > parameters.getBodyCapacity() ? c.getOrderedQty() - parameters.getBodyCapacity() : 0);
                
                if(r.isTrailerAttached()) {
                    Qty1 = (t.getFirstTrailerQuantity() + c.getOrderedQty() > parameters.getBodyCapacity() ? (parameters.getBodyCapacity() - t.getFirstTrailerQuantity()) : c.getOrderedQty());
                    Qty2 = c.getOrderedQty() - (parameters.getBodyCapacity() - t.getFirstTrailerQuantity());
                }
                
                // Ajout d'une route à cette position
                Route route = new Route();
                route.setTour(t);
                route.setPosition(position);
                route.setLocation(c);
                route.setLocationType(LocationType.CUSTOMER);
                route.setTrailerAttached(r.isTrailerAttached());
                route.setFirstTrailer(r.getFirstTrailer());
                route.setLastTrailer(r.getLastTrailer());
                route.setSwapAction(SwapAction.NONE);
                route.setQty1(Qty1);
                route.setQty2(Qty2);
                listRoutes.add(route);

                t.setListRoutes(listRoutes);
            } else {
                
                // Incrémentation des positions des routes allant 
                // du swap location jusqu'à la fin
                for(int i = position - 1; i < listRoutes.size(); i++) {
                    Route r = listRoutes.get(i);
                    r.setPosition(r.getPosition() + 1);
                    listRoutes.set(i, r);
                }
                
                // Ajout d'une route à cette position
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
        } else { // Ajout dans la 2e remorque
            
            // Récupération de la dernière route pour avoir l'état du camion
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
    
    /**
     * Calcule le temps de service pour une liste de route.
     * @param list Liste des routes
     * @param begin Début
     * @param end Fin
     * @return Temps de service
     * @throws Exception 
     */
    public double calculTime(List<Route> list, int begin, int end) throws Exception {
        CoordinatesCalc calc = new CoordinatesCalc();
        double tpsTotal = 0.0;
        
        for (int i = begin; i < end; i++) {
            
            // Si c'est un client, on ajoute le temps de service
            // Si c'est un swap location, on ajoute le temps d'opération
            if(list.get(i).getLocationType() == LocationType.CUSTOMER) {
                Customer c = (Customer) list.get(i).getLocation();
                tpsTotal += c.getServiceTime();
            } else if(list.get(i).getLocationType() == LocationType.SWAP_LOCATION) {
                switch(list.get(i).getSwapAction()) {
                    case PARK:
                        tpsTotal += parameters.getParkTime();
                        break;
                    case PICKUP:
                        tpsTotal += parameters.getPickupTime();
                        break;
                    case SWAP:
                        tpsTotal += parameters.getSwapTime();
                        break;
                    case EXCHANGE:
                        tpsTotal += parameters.getExchangeTime();
                        break;
                }
            }
            
            tpsTotal += calc.getTimeBetweenCoord(list.get(i-1).getLocation().getCoordinate(), list.get(i).getLocation().getCoordinate());
        }
        
        return tpsTotal;
    }
    
    /**
     * Vérifie si on peut passer en mode camion.
     * @param t Tournée
     * @param c Client à ajouter
     * @return TRUE si on peut passer en mode camion
     * @throws Exception 
     */
    public boolean canAddTrailer(Tour t, Customer c) throws Exception {
        
        // Vérification que ous les clients sont accessible
        for(Route r : t.getListRoutes()) {
            if(r.getLocationType() == LocationType.CUSTOMER)
                if(!((Customer) r.getLocation()).isAccessible())
                    return false;
        }
        
        // Le client à ajouter est accesible aussi
        if(c.isAccessible()) {
            CoordinatesCalc calc = new CoordinatesCalc();
            
            // Calcul du temps :
            //  -> temps déjà passé
            //  -> trajet de la dernière position au client
            //  -> temps de service
            //  -> trajet du client au dépôt
            double tpsTotal = t.getTourTime()
                + calc.getTimeBetweenCoord(t.getLastRoute().getLocation().getCoordinate(), c.getCoordinate())
                + c.getServiceTime()
                + calc.getTimeBetweenCoord(c.getCoordinate(), depot.getCoordinate());
            
            return (tpsTotal < parameters.getOperatingTime());
        }
        
        return false;
    }
    
    /**
     * Tranforme la tournée en mode train et ajoute le client.
     * @param t Tournée
     * @param c Client
     * @throws Exception 
     */
    public void addTrailer(Tour t, Customer c) throws Exception {
          for(Route r : t.getListRoutes()) {
              r.setLastTrailer(2);
              r.setTrailerAttached(true);
          }
          
          addCustomer(t, c, true);
    }
    
    /**
     * Vérifie si le camion peut mettre en place un swap.
     * @param tour Tournée
     * @param c Client où l'on souhaite passer après le swap location
     * @return Swap location par lequel on passe
     * @throws Exception 
     */
    public SwapLocation canGoToSwapLocation(Tour tour, Customer c) throws Exception {   
        CoordinatesCalc calc = new CoordinatesCalc();
        double tpsTotal = 0;
        
        // Récupére le 1er client
        Customer c1 = (Customer) tour.getFirstCustomer().getLocation();
        
        // Récupére le swap location le plus proche du dépôt
        SwapLocation swap = new SwapLocation();
        swap = swap.getNearest(c1.getCoordinate());
        
        if(swap != null) {
            
            // Calcul du temps :
            //  -> trajet du dépôt au swap 
            //  -> temps pour PARK
            //  -> trajet du swap au client
            //  -> temps de service du client
            tpsTotal += calc.getTimeBetweenCoord(depot.getCoordinate(), swap.getCoordinate())
                     + parameters.getParkTime()
                     + calc.getTimeBetweenCoord(swap.getCoordinate(), c1.getCoordinate())
                     + c1.getServiceTime();
            
            // Calcul du temps pour le reste de la tournée
            List<Route> list = tour.getListRoutes();
            tpsTotal += calculTime(list, 2, list.size());
            
            // Calcul du temps :
            //  -> trajet du dernier client au swap
            //  -> temps pour SWAP 
            //  -> trajet du swap au client que l'on veut ajouter
            tpsTotal += calc.getTimeBetweenCoord(tour.getLastRoute().getLocation().getCoordinate(), swap.getCoordinate())
                     + parameters.getSwapTime()
                     + calc.getTimeBetweenCoord(swap.getCoordinate(), c.getCoordinate());

            // Calcul du temps :
            //  -> temps de service du client
            //  -> trajet du client que l'on veut ajouter au swap
            //  -> temps pour PICKUP 
            //  -> trajet du swap au dépôt
            tpsTotal += c.getServiceTime() 
                     + calc.getTimeBetweenCoord(c.getCoordinate(), swap.getCoordinate()) 
                     + parameters.getPickupTime()
                     + calc.getTimeBetweenCoord(swap.getCoordinate(), depot.getCoordinate());

            // On peut ajouter le swap
            if(tpsTotal <= parameters.getOperatingTime())
                return swap;
        }
        
        return null;
    }
    
    /**
     * Attache une remorque et passe dans un swap location.
     * @param t Tournée
     * @param swapLocation SwapLocation
     * @param c Client
     */
    public void addAttachedTrailerAndSwapLocation(Tour t, SwapLocation swapLocation, Customer c) {
        
        // Parcours les routes de la tournée pour attacher la remorque et 
        // modifier les positions permettant l'ajout du swap location
        List<Route> list = t.getListRoutes();
        ListIterator<Route> iter = list.listIterator();
        
        while (iter.hasNext()) {
            Route r = iter.next();
            
            if(r.getPosition() >= 2) {
                // Modification des positions des routes après le swap location
                r.setPosition(r.getPosition() + 1);
                r.setLastTrailer(2);
            } else if(r.getPosition() == 1) {
                // Ajout de la remorque au dépot 
                r.setLastTrailer(2);
                r.setTrailerAttached(true);
            }
        }
                
        // Ajout d'une route en position 2 pour déposer la remorque
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
               
        // Ajoute d'une route à la suite pour aller au swap location
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
        
        // Ajoute d'une route pour aller chez le client
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
     * Clôture d'une tournée.
     * @param t Tournée
     * @throws Exception 
     */
    public void endedTour(Tour t) throws Exception {
        List<Route> listRoutes = t.getListRoutes();
        Route last = t.getLastRoute();
            
        boolean attachTrailer = false;
        int firstTrailer = 1;
        int lastTrailer = 0;
        
        // Si le camion avait une remorque déposée dans un swap location
        SwapLocation sp = t.getSwapLocation();
        if(sp != null) {
            attachTrailer = true;
            firstTrailer = 2;
            lastTrailer = 1;
            
            // Récupération de la remorque
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
        
        // Retour au dépôt
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
}
