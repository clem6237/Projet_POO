package metier;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "LOCATION")
@XmlRootElement
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    
    @Basic(optional = false)
    @Column(name = "POSTALCODE")
    private String postalCode;
    
    @Basic(optional = false)
    @Column(name = "CITY")
    private String city;
    
    @ManyToOne(optional = false)
    @Column(name = "IDCOORDINATE")
    private int idCoordinate;
    
    @Basic(optional = false)
    @Column(name = "TYPE")
    private LocationType type;
    
    public Location() {
    }

    public Location(String id, String postalCode, String city, int idCoordinate) {
        this.id = id;
        this.postalCode = postalCode;
        this.city = city;
        this.idCoordinate = idCoordinate;
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

    public int getIdCoordinate() {
        return idCoordinate;
    }

    public void setIdCoordinate(int idCoordinate) {
        this.idCoordinate = idCoordinate;
    }    

    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.postalCode);
        hash = 67 * hash + Objects.hashCode(this.city);
        hash = 67 * hash + this.idCoordinate;
        hash = 67 * hash + Objects.hashCode(this.type);
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
        if (this.idCoordinate != other.idCoordinate) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.postalCode, other.postalCode)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (this.type != other.type) {
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
                + ", idCoordinate=" + idCoordinate 
                + ", type=" + type
                + " } \n";
    }
}
