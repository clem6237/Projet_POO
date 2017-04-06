package metier;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "SWAPLOCATION")
@XmlRootElement
public class SwapLocation extends Location implements Serializable {
    
    public SwapLocation() {
    }

    public SwapLocation(String id, String postalCode, String city, Coordinate coordinate) {
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
