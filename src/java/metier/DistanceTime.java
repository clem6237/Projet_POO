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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "DISTANCETIME")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DistanceTime.findAll", query = "SELECT dt FROM DistanceTime dt"),
    @NamedQuery(name = "DistanceTime.findById", query = "SELECT dt FROM DistanceTime dt WHERE dt.id = :id"),
    @NamedQuery(name = "DistanceTime.findByCoord", query = "SELECT dt FROM DistanceTime dt WHERE dt.coordFrom = :coordFrom AND dt.coordTo = :coordTo")
})
public class DistanceTime implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private int id;
    
    @JoinColumn(name = "COORDFROM", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Coordinate coordFrom;
    
    @JoinColumn(name = "COORDTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Coordinate coordTo;
    
    @Basic(optional = false)
    @Column(name = "DISTANCE")
    private double distance;
    
    @Basic(optional = false)
    @Column(name = "TIME")
    private double time;

    public DistanceTime() {
    }

    public DistanceTime(Coordinate coordFrom, Coordinate coordTo, double distance, double time) {
        this.coordFrom = coordFrom;
        this.coordTo = coordTo;
        this.distance = distance;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coordinate getCoordFrom() {
        return coordFrom;
    }

    public void setCoordFrom(Coordinate coordFrom) {
        this.coordFrom = coordFrom;
    }

    public Coordinate getCoordTo() {
        return coordTo;
    }

    public void setCoordTo(Coordinate coordTo) {
        this.coordTo = coordTo;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.coordFrom);
        hash = 89 * hash + Objects.hashCode(this.coordTo);
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
        final DistanceTime other = (DistanceTime) obj;
        if (!Objects.equals(this.coordFrom, other.coordFrom)) {
            return false;
        }
        return Objects.equals(this.coordTo, other.coordTo);
    }

    @Override
    public String toString() {
        return "DistanceTime{" 
                + "id=" + id 
                + ", coordFrom=" + coordFrom 
                + ", coordTo=" + coordTo 
                + ", distance=" + distance 
                + ", time=" + time 
                + "}";
    }
}
