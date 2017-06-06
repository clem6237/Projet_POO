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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        Collections.sort(this.listRoutes);
        for(Route r : this.getListRoutes()) {
            if(r.getLocationType() == LocationType.SWAP_LOCATION)
                return (SwapLocation) r.getLocation();
        }
        return null;
    }
    
    public int getPositionOfSwap() {
        Collections.sort(this.listRoutes);
        for(Route r : this.getListRoutes()) {
            if(r.getLocationType() == LocationType.SWAP_LOCATION)
                return r.getPosition();
        }
        return 0;
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
    
    public double getTourTime() {
        CoordinatesCalc calc = new CoordinatesCalc();
        
        Coordinate lastCoordinate = null;
        double timeTotal = 0;
        
        for(Route r : this.listRoutes) {
            Location l = r.getLocation();
            //Si c'est un client, on ajoute le temps de service
            if(r.getLocationType() == LocationType.CUSTOMER) {
                Customer c = (Customer) r.getLocation();
                timeTotal += c.getServiceTime();
            }
            
            if(lastCoordinate != null) {
                try {
                    timeTotal += calc.getTotalTimeBetweenCoord(lastCoordinate, l.getCoordinate());
                } catch (Exception ex) {
                    Logger.getLogger(Tour.class.getName()).log(Level.SEVERE, null, ex);
                }
                lastCoordinate =  l.getCoordinate();
            } else {
                lastCoordinate =  l.getCoordinate();
            }
        }
        
        return timeTotal;
    }
    
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
    
    public double getFirstTrailerQuantity() throws Exception {
        double quantity = 0;
        
        for(Route r : this.listRoutes) {
            //Parcours chaque client
            if(r.getLocationType() == LocationType.CUSTOMER) {
                quantity += r.getQty1();
            }
        }
        
        return quantity;
    }
    
    public double getLastTrailerQuantity() throws Exception {
        double quantity = 0;
        
        for(Route r : this.listRoutes) {
            //Parcours chaque client
            if(r.getLocationType() == LocationType.CUSTOMER) {
                quantity += r.getQty2();
            }
        }
        
        return quantity;
    }
    
    public double getTotalCost() throws Exception {
        double total = 0;
        
        double distanceTour = 0;
        double distanceFirstTrailer = 0;
        double distanceSecondTrailer = 0;
        
        CoordinatesCalc coordCalc = new CoordinatesCalc();
        
        Iterator it = this.listRoutesOrdered().iterator();
        
        Route routeFrom = (Route) it.next();
        Route routeTo;
        
        while (it.hasNext()) {
            routeTo = (Route) it.next();
            
            double distance = coordCalc.getDistanceBetweenCoord(routeFrom.getLocation().getCoordinate(), routeTo.getLocation().getCoordinate());
            distanceTour += distance;
            
            if (routeFrom.isTrailerAttached()) {
                distanceFirstTrailer += distance;
                distanceSecondTrailer += distance;
            } else if (routeFrom.getFirstTrailer() == 1) {
                distanceFirstTrailer += distance;
            } else if (routeFrom.getFirstTrailer() == 2) {
                distanceSecondTrailer += distance;
            }
        }
        
        total += calcTruckCost(distanceTour, this.getTourTime());
        total += calcTrailerCost(distanceFirstTrailer, this.getTourTime());
        total += calcTrailerCost(distanceSecondTrailer, this.getTourTime());
        
        return total;
    }
    
    public double calcTruckCost(double distance, double time) {
        RoutingParametersDao routingParametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        RoutingParameters parameters = routingParametersManager.find();
        
        return parameters.getTruckUsageCost()
                + (parameters.getTruckDistanceCost() * (distance / 100)) 
                + (parameters.getTruckTimeCost() * (time / 3600));
    }
    
    public double calcTrailerCost(double distance, double time) {
        RoutingParametersDao routingParametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        RoutingParameters parameters = routingParametersManager.find();
        
        return parameters.getTrailerUsageCost()
                + (parameters.getTrailerDistanceCost() * (distance / 100)) 
                + (parameters.getTrailerTimeCost() * (time / 3600));
    }
}
