package metier;

import dao.DaoFactory;
import dao.PersistenceType;
import dao.SwapLocationDao;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import utils.CoordinatesCalc;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "SWAPLOCATION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SwapLocation.findAll", query = "SELECT c FROM SwapLocation c"),
    @NamedQuery(name = "SwapLocation.findById", query = "SELECT c FROM SwapLocation c WHERE c.id = :id")
})
public class SwapLocation extends Location implements Serializable {
    
    public SwapLocation() {
    }

    public SwapLocation(String id, String postalCode, String city, Coordinate coordinate) {
        super(id, postalCode, city, coordinate);
    }
    
    public SwapLocation(SwapLocation s) {
        super(s.getId(), s.getPostalCode(), s.getCity(), s.getCoordinate());
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
    
    /**
     * Recherche le SwapLocation le plus proche de la position
     * @param coordinate
     * @return 
     */
    public SwapLocation getNearest(Coordinate coordinate) {
        SwapLocationDao swapLocationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getSwapLocationDao();
        List<SwapLocation> list = (List<SwapLocation>)swapLocationManager.findAll();
        
        CoordinatesCalc calc = new CoordinatesCalc();
        SwapLocation nearSwap = null;
        
        for(SwapLocation swap : list) {
            if(nearSwap == null) {
                nearSwap = swap;
            } else {
                try {
                    if(calc.getTotalTimeBetweenCoord(nearSwap.getCoordinate(), coordinate) > calc.getTotalTimeBetweenCoord(swap.getCoordinate(), coordinate))
                        nearSwap = swap;
                } catch (Exception ex) {
                    Logger.getLogger(SwapLocation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return nearSwap;
    }
}
