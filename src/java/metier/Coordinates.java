package metier;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "COORDINATES")
@XmlRootElement
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private int id;
    
    @Basic(optional = false)
    @Column(name = "COORDX")
    private double coordX;
    
    @Basic(optional = false)
    @Column(name = "COORDY")
    private double coordY;

    public Coordinates() {
    }

    public Coordinates(int id, double coordX, double coordY) {
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
        hash = 79 * hash + this.id;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.coordX) ^ (Double.doubleToLongBits(this.coordX) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.coordY) ^ (Double.doubleToLongBits(this.coordY) >>> 32));
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
        final Coordinates other = (Coordinates) obj;
        if (this.id != other.id) {
            return false;
        }
        if (Double.doubleToLongBits(this.coordX) != Double.doubleToLongBits(other.coordX)) {
            return false;
        }
        if (Double.doubleToLongBits(this.coordY) != Double.doubleToLongBits(other.coordY)) {
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
