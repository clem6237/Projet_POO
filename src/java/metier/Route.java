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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "ROUTE", uniqueConstraints={
   @UniqueConstraint(columnNames={"idTour", "position"})})
@XmlRootElement
public class Route implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "IDTOUR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private int idTour;
    
    @Basic(optional = false)
    @Column(name = "POSITION")
    private int position;
    
    @Basic(optional = false)
    @Column(name = "LOCATION")
    private String location;
    
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

    public Route(int idTour, int position, String location, LocationType locationType, boolean trailerAttached, int firstTrailer, int lastTrailer, SwapAction swapAction, double qty1, double qty2) {
        this.idTour = idTour;
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

    public int getIdTour() {
        return idTour;
    }

    public void setIdTour(int idTour) {
        this.idTour = idTour;
    }

    public int getPosition() {
        return position;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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
        int hash = 7;
        hash = 59 * hash + this.idTour;
        hash = 59 * hash + this.position;
        hash = 59 * hash + Objects.hashCode(this.location);
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
        if (this.idTour != other.idTour) {
            return false;
        }
        if (this.position != other.position) {
            return false;
        }
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Route { " 
                + "idTour=" + idTour 
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
