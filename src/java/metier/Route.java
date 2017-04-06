package metier;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "ROUTE", uniqueConstraints={
   @UniqueConstraint(columnNames={"tour", "position"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Route.findAll", query = "SELECT r FROM Route r"),
    @NamedQuery(name = "Route.findByTour", query = "SELECT r FROM Route r WHERE r.tour = :tour"),
    @NamedQuery(name = "Route.findByTourPosition", query = "SELECT r FROM Route r WHERE r.tour = :tour AND r.position = :position")
})
public class Route implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private int id;
    
    @JoinColumn(name = "TOUR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Tour tour;
    
    @Basic(optional = false)
    @Column(name = "POSITION")
    private int position;
    
    @JoinColumn(name = "LOCATION", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Location location;
    
    @Basic(optional = false)
    @Column(name = "LOCATIONTYPE")
    private LocationType locationType;
    
    @Basic(optional = false)
    @Column(name = "TRAILERATTACHED")
    private boolean trailerAttached;
    
    @Basic(optional = false)
    @Column(name = "FIRSTTRAILER")
    private int firstTrailer;
    
    @Basic(optional = false)
    @Column(name = "LASTTRAILER")
    private int lastTrailer;
    
    @Basic(optional = false)
    @Column(name = "SWAPACTION")
    private SwapAction swapAction;
    
    @Basic(optional = false)
    @Column(name = "QTY1")
    private double qty1;
    
    @Basic(optional = false)
    @Column(name = "QTY2")
    private double qty2;

    public Route() {
    }

    public Route(Tour tour, int position, Location location, LocationType locationType, boolean trailerAttached, int firstTrailer, int lastTrailer, SwapAction swapAction, double qty1, double qty2) {
        this.tour = tour;
        this.position = position;
        this.location = location;
        this.locationType = locationType;
        this.trailerAttached = trailerAttached;
        this.firstTrailer = firstTrailer;
        this.lastTrailer = lastTrailer;
        this.swapAction = swapAction;
        this.qty1 = qty1;
        this.qty2 = qty2;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public int getPosition() {
        return position;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isTrailerAttached() {
        return trailerAttached;
    }

    public void setTrailerAttached(boolean trailerAttached) {
        this.trailerAttached = trailerAttached;
    }

    public int getFirstTrailer() {
        return firstTrailer;
    }

    public void setFirstTrailer(int firstTrailer) {
        this.firstTrailer = firstTrailer;
    }

    public int getLastTrailer() {
        return lastTrailer;
    }

    public void setLastTrailer(int lastTrailer) {
        this.lastTrailer = lastTrailer;
    }

    public SwapAction getSwapAction() {
        return swapAction;
    }

    public void setSwapAction(SwapAction swapAction) {
        this.swapAction = swapAction;
    }

    public double getQty1() {
        return qty1;
    }

    public void setQty1(double qty1) {
        this.qty1 = qty1;
    }

    public double getQty2() {
        return qty2;
    }

    public void setQty2(double qty2) {
        this.qty2 = qty2;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.tour);
        hash = 97 * hash + this.position;
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
        final Route other = (Route) obj;
        if (this.position != other.position) {
            return false;
        }
        if (!Objects.equals(this.tour, other.tour)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Route { " 
                //+ "tour=" + tour 
                + ", position=" + position 
                + ", trailerAttached=" + trailerAttached 
                + ", firstTrailer=" + firstTrailer 
                + ", lastTrailer=" + lastTrailer 
                + ", swapAction=" + swapAction 
                + ", qty1=" + qty1 
                + ", qty2=" + qty2 
                + " }";
    }
}
