package metier;

import dao.DaoFactory;
import dao.PersistenceType;
import dao.RouteDao;
import dao.RoutingParametersDao;
import dao.TourDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import utils.CoordinatesCalc;
import utils.CostCalc;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "TOUR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tour.findAll", query = "SELECT t FROM Tour t"),
    @NamedQuery(name = "Tour.findById", query = "SELECT t FROM Tour t WHERE t.id = :id")
})
public class Tour implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Basic(optional = false)
    @Column(name = "ID")
    private int id;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tour")
    private List<Route> listRoutes;

    public Tour() {
        this.listRoutes = new ArrayList();
    }
    
    public Tour(Tour t) {
        this.id = t.getId();
        this.listRoutes = t.getListRoutes();
    }

    public Tour(List<Route> listRoutes) {
        this.listRoutes = listRoutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Route> getListRoutes() {
        Collections.sort(this.listRoutes);
        return listRoutes;
    }
    
    public Route getFirstCustomer() {
        Collections.sort(this.listRoutes);
        for(Route r : this.listRoutes) {
            if(r.getLocationType() == LocationType.CUSTOMER)
                return r;
        }
        return null;
    }
    
    public Route getLastRoute() {
        Collections.sort(this.listRoutes);
        Collections.reverse(this.listRoutes);
        return this.listRoutes.get(0);
    }
    
    public SwapLocation getSwapLocation() {
        for(Route r : this.getListRoutes()) {
            if(r.getLocationType() == LocationType.SWAP_LOCATION)
                return (SwapLocation) r.getLocation();
        }
        return null;
    }
    
    public int getPositionOfSwap() {
        for(Route r : this.getListRoutes()) {
            if(r.getLocationType() == LocationType.SWAP_LOCATION && r.getSwapAction() == SwapAction.SWAP)
                return r.getPosition();
        }
        return 0;
    }
    
    public Route getSwapLocationOfSwap() {
        for(Route r : this.getListRoutes()) {
            if(r.getLocationType() == LocationType.SWAP_LOCATION && r.getSwapAction() == SwapAction.SWAP)
                return r;
        }
        return null;
    }

    public void setListRoutes(List<Route> listRoutes) {
        this.listRoutes = listRoutes;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tour other = (Tour) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "Tour{" 
                + "id=" + id 
                + ", listRoutes=\n" + listRoutes 
                + "}\n";
    }
    
    public Collection<Tour> allTours() {
        TourDao tourManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getTourDao();
        return tourManager.findAll();
    }
    
    public Collection<Route> listRoutesOrdered() {
        RouteDao routeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRouteDao();
        return routeManager.findByTour(this);
    }
    
    /**
     * Calcule le temps total de la tournée.
     * @return
     * @throws Exception 
     */
    public double getTourTime() throws Exception {
        CoordinatesCalc calc = new CoordinatesCalc();
        RoutingParameters parameters = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao().find();
        
        Coordinate lastCoordinate = null;
        double timeTotal = 0;
        
        for(Route r : this.getListRoutes()) {
            Location l = r.getLocation();
            
            // Si c'est un client, on ajoute le temps de service
            // Si c'est un swap location, on ajoute le temps d'opération
            if(r.getLocationType() == LocationType.CUSTOMER) {
                Customer c = (Customer) r.getLocation();
                timeTotal += c.getServiceTime();
            } else if(r.getLocationType() == LocationType.SWAP_LOCATION) {
                switch(r.getSwapAction()) {
                    case PARK:
                        timeTotal += parameters.getParkTime();
                        break;
                    case PICKUP:
                        timeTotal += parameters.getPickupTime();
                        break;
                    case SWAP:
                        timeTotal += parameters.getSwapTime();
                        break;
                    case EXCHANGE:
                        timeTotal += parameters.getExchangeTime();
                        break;
                }
            }
            
            if(lastCoordinate != null) {
                timeTotal += calc.getTimeBetweenCoord(lastCoordinate, l.getCoordinate());
                lastCoordinate =  l.getCoordinate();
            } else {
                lastCoordinate =  l.getCoordinate();
            }
        }
        
        return timeTotal;
    }
    
    /**
     * Calcule le temps total de la tournée (appel depuis le site web).
     * @return
     * @throws Exception 
     */
    public double getTourTimeFromBase() throws Exception {
        CoordinatesCalc calc = new CoordinatesCalc();
        
        RoutingParametersDao parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        RoutingParameters parameters = parametersManager.find();
        
        Coordinate lastCoordinate = null;
        double timeTotal = 0;
        
        for(Route r : this.listRoutesOrdered()) {
            Location l = r.getLocation();
            
            // Si c'est un client, on ajoute le temps de service
            // Si c'est un swap location, on ajoute le temps d'opération
            if(r.getLocationType() == LocationType.CUSTOMER) {
                Customer c = (Customer) r.getLocation();
                timeTotal += c.getServiceTime();
            } else if(r.getLocationType() == LocationType.SWAP_LOCATION) {
                switch(r.getSwapAction()) {
                    case PARK:
                        timeTotal += parameters.getParkTime();
                        break;
                    case PICKUP:
                        timeTotal += parameters.getPickupTime();
                        break;
                    case SWAP:
                        timeTotal += parameters.getSwapTime();
                        break;
                    case EXCHANGE:
                        timeTotal += parameters.getExchangeTime();
                        break;
                }
            }
            
            if(lastCoordinate != null) {
                timeTotal += calc.getTimeBetweenCoord(lastCoordinate, l.getCoordinate());
                lastCoordinate =  l.getCoordinate();
            } else {
                lastCoordinate =  l.getCoordinate();
            }
        }
        
        return timeTotal;
    }
    
    /**
     * Calcule la distance totale de la tournée.
     * @return
     * @throws Exception 
     */
    public double getTourDistance() throws Exception {
        CoordinatesCalc calc = new CoordinatesCalc();
        
        Coordinate lastCoordinate = null;
        double distanceTotal = 0;
        
        for(Route r : this.listRoutesOrdered()) {
            Location l = r.getLocation();
            
            if (lastCoordinate != null) {
                distanceTotal += calc.getDistanceBetweenCoord(lastCoordinate, l.getCoordinate());
                lastCoordinate =  l.getCoordinate();
            } else {
                lastCoordinate =  l.getCoordinate();
            }
        }
        
        return distanceTotal;
    }
    
    /**
     * Calcule la distance effectuée par les remorques sur la tournée.
     * @return
     * @throws Exception 
     */
    public double getTrailerDistance() throws Exception {
        CoordinatesCalc calc = new CoordinatesCalc();
        
        Coordinate lastCoordinate = null;
        double distanceTotal = 0;
        
        for(Route r : this.listRoutesOrdered()) {
            Location l = r.getLocation();
            
            if (lastCoordinate != null) {
                if (r.isTrailerAttached()) {
                    distanceTotal += calc.getDistanceBetweenCoord(lastCoordinate, l.getCoordinate());
                    lastCoordinate =  l.getCoordinate();
                }
            } else {
                lastCoordinate =  l.getCoordinate();
            }
        }
        
        return distanceTotal;
    }
    
    /**
     * Calcule la quantité de produit transportée sur la tournée.
     * @return
     * @throws Exception 
     */
    public double getTourQuantity() throws Exception {
        double quantity = 0;
        
        for(Route r : this.listRoutes) {
            //Parcours chaque client
            if(r.getLocationType() == LocationType.CUSTOMER) {
                quantity += r.getQty1() + r.getQty2();
            }
        }
        
        return quantity;
    }
    
    /**
     * Calcule la quantité de produit transportée par la première remorque.
     * @return
     * @throws Exception 
     */
    public double getFirstTrailerQuantity() throws Exception {
        double quantity = 0;
        
        for(Route r : this.listRoutes) {
            // Parcours chaque client
            if(r.getLocationType() == LocationType.CUSTOMER) {
                quantity += r.getQty1();
            }
        }
        
        return quantity;
    }
    
    /**
     * Calcule la quantité de produit transportée par la deuxième remorque.
     * @return
     * @throws Exception 
     */
    public double getLastTrailerQuantity() throws Exception {
        double quantity = 0;
        
        for(Route r : this.listRoutes) {
            // Parcours chaque client
            if(r.getLocationType() == LocationType.CUSTOMER) {
                quantity += r.getQty2();
            }
        }
        
        return quantity;
    }
    
    /**
     * Calcule le coût total de la tournée (camion + remorque).
     * @return
     * @throws Exception 
     */
    public double getTotalCost() throws Exception {
        double total = 0;
        
        total += this.getTotalTruckCost();
        total += this.getTotalTrailerCost();
        
        return total;
    }
    
    /**
     * Calcule le coût total du camion sur la tournée.
     * @return
     * @throws Exception 
     */
    public double getTotalTruckCost() throws Exception {
        return getTruckUsageCost()
                + getTruckDistanceCost() 
                + getTruckTimeCost();
    }
    
    /**
     * Calcule le coût d'utilisation du camion.
     * @return 
     */
    public double getTruckUsageCost() {
        return new CostCalc().getTruckUsageCost();
    }
    
    /**
     * Calcule le coût kilométrique du camion.
     * @return
     * @throws Exception 
     */
    public double getTruckDistanceCost() throws Exception {
        return new CostCalc().getTruckDistanceCost(this.getTourDistance());
    }
    
    /**
     * Calcule le coût horaire du camion.
     * @return
     * @throws Exception 
     */
    public double getTruckTimeCost() throws Exception {
        return new CostCalc().getTruckTimeCost(this.getTourTimeFromBase());
    }
    
    /**
     * Calcule le coût total de la remorque sur la tournée.
     * @return
     * @throws Exception 
     */
    public double getTotalTrailerCost() throws Exception {
        return getTrailerUsageCost()
                + getTrailerDistanceCost();
    }
    
    /**
     * Calcule le coût d'utilisation de la remorque.
     * @return 
     */
    public double getTrailerUsageCost() {
        double total = new CostCalc().getTrailerUsageCost();
        
        Route r = this.getListRoutes().get(0);
        
        if (r.isTrailerAttached() || r.getLastTrailer() != 0) {
            return total;
        } else {
            return 0; // En mode camion, pas de surcoût
        }
    }
    
    /**
     * Calcule le coût kilométrique de la remorque.
     * @return
     * @throws Exception 
     */
    public double getTrailerDistanceCost() throws Exception {
        return new CostCalc().getTrailerDistanceCost(this.getTrailerDistance());
    }
    
}
