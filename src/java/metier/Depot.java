package metier;

import dao.DaoFactory;
import dao.DepotDao;
import dao.PersistenceType;
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
@Table(name = "DEPOT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Depot.findAll", query = "SELECT d FROM Depot d"),
    @NamedQuery(name = "Depot.find", query = "SELECT d FROM Depot d"),
})
public class Depot extends Location implements Serializable {
    
    public Depot() {
    }

    public Depot(String id, String postalCode, String city, Coordinate coordinate) {
        super(id, postalCode, city, coordinate);
    }
    
    @Override
    public String toString() {
        return "Depot { " 
                + "id=" + this.getId() 
                + ", postalCode=" + this.getPostalCode() 
                + ", city=" + this.getCity() 
                + ", coordinate=" + this.getCoordinate()
                + " } \n";
    }
    
    public Collection<Depot> allDepots() {
        DepotDao depotManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDepotDao();
        return depotManager.findAll();
    }
}
