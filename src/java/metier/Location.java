package metier;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "LOCATION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l"),
    @NamedQuery(name = "Location.findById", query = "SELECT l FROM Location l WHERE l.id = :id"),
    @NamedQuery(name = "Location.findByCoord", query = "SELECT l FROM Location l WHERE l.coordinate = :coord")
})
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    
    @Basic(optional = false)
    @Column(name = "POSTALCODE")
    private String postalCode;
    
    @Basic(optional = false)
    @Column(name = "CITY")
    private String city;
    
    @JoinColumn(name = "COORDINATE", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Coordinate coordinate;
    
    @Basic(optional = false)
    @Column(name = "TYPE")
    private LocationType type;
    
    public Location() {
    }

    public Location(String id, String postalCode, String city, Coordinate coordinate, LocationType type) {
        this.id = id;
        this.postalCode = postalCode;
        this.city = city;
        this.coordinate = coordinate;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }    

    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
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
        final Location other = (Location) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Location{" 
                + "id=" + id 
                + ", postalCode=" + postalCode 
                + ", city=" + city 
                + ", coordinate=" + coordinate 
                + ", type=" + type
                + " } \n";
    }
}
