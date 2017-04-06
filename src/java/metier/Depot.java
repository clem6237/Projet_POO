package metier;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "DEPOT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Depot.find", query = "SELECT d FROM Depot d")
})
public class Depot extends Location implements Serializable {
    
    public Depot() {
    }

    public Depot(String id, String postalCode, String city, Coordinate coordinate) {
        super(id, postalCode, city, coordinate);
    }
    
    @Override
    public String toString() {
        return "Customer { " 
                + "id=" + this.getId() 
                + ", postalCode=" + this.getPostalCode() 
                + ", city=" + this.getCity() 
                + ", coordinate=" + this.getCoordinate()
                + " } \n";
    }
}
