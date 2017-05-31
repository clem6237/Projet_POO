package metier;

import dao.DaoFactory;
import dao.PersistenceType;
import dao.SwapLocationDao;
import java.io.Serializable;
import java.util.Collection;
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
@Table(name = "SWAPLOCATION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SwapLocation.findAll", query = "SELECT s FROM SwapLocation s")
})
public class SwapLocation extends Location implements Serializable {
    
    public SwapLocation() {
    }

    public SwapLocation(String id, String postalCode, String city, Coordinate coordinate) {
        super(id, postalCode, city, coordinate);
    }
    
    @Override
    public String toString() {
        return "SwapLocation { " 
                + "id=" + this.getId() 
                + ", postalCode=" + this.getPostalCode() 
                + ", city=" + this.getCity() 
                + ", coordinate=" + this.getCoordinate()
                + " } \n";
    }
    
    public Collection<SwapLocation> allSwapLocations() {
        SwapLocationDao swapLocationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getSwapLocationDao();
        return swapLocationManager.findAll();
    }
}
