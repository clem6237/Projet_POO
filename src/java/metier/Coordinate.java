package metier;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "COORDINATE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Coordinate.findAll", query = "SELECT c FROM Coordinate c"),
    @NamedQuery(name = "Coordinate.findById", query = "SELECT c FROM Coordinate c WHERE c.id = :id"),
    @NamedQuery(name = "Coordinate.findByCoord", query = "SELECT c FROM Coordinate c WHERE c.coordX = :coordX AND c.coordY = :coordY")
})
public class Coordinate implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private int id;
    
    @Basic(optional = false)
    @Column(name = "COORDX")
    private double coordX;
    
    @Basic(optional = false)
    @Column(name = "COORDY")
    private double coordY;

    public Coordinate() {
    }

    public Coordinate(int id, double coordX, double coordY) {
        this.id = id;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCoordX() {
        return coordX;
    }

    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.id;
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
        final Coordinate other = (Coordinate) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Coordinates{" 
                + "id=" + id 
                + ", coordX=" + coordX 
                + ", coordY=" + coordY 
                + "}";
    }
}
