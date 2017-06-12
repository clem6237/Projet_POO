package metier;

import dao.DaoFactory;
import dao.DepotDao;
import dao.PersistenceType;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import dao.CustomerDao;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
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
@Table(name = "CUSTOMER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.findById", query = "SELECT c FROM Customer c WHERE c.id = :id"),
})
public class Customer extends Location implements Serializable, Comparable<Customer> {
    @Basic(optional = false)
    @Column(name = "ORDEREDQTY")
    private double orderedQty;
    
    @Basic(optional = false)
    @Column(name = "ACCESSIBLE")
    private boolean accessible;
    
    @Basic(optional = false)
    @Column(name = "SERVICETIME")
    private double serviceTime;
    
    public Customer() {
    }

    public Customer(String id, String postalCode, String city, Coordinate coordinate, double orderedQty, boolean accessible, double serviceTime) {
        super(id, postalCode, city, coordinate);
        this.orderedQty = orderedQty;
        this.accessible = accessible;
        this.serviceTime = serviceTime;
    }
    
    public double getOrderedQty() {
        return orderedQty;
    }

    public void setOrderedQty(double orderedQty) {
        this.orderedQty = orderedQty;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }
 
    @Override
    public String toString() {
        return "Customer { " 
                + "id=" + this.getId() 
                + ", postalCode=" + this.getPostalCode() 
                + ", city=" + this.getCity() 
                + ", coordinate=" + this.getCoordinate()
                + ", orderedQty=" + orderedQty
                + ", accessible=" + accessible
                + ", serviceTime=" + serviceTime
                + " } \n";
    }

    public Collection<Customer> allCustomers() {
        CustomerDao customerManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCustomerDao();
        return customerManager.findAll();
    }
    
    @Override
    public int compareTo(Customer o) {
        DepotDao depotManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDepotDao();
        Coordinate coordDepot = depotManager.find().getCoordinate();
        
        CoordinatesCalc calc = new CoordinatesCalc();
        double distanceC1 = 0.0, distanceC2 = 0.0;
        
        try {
            
            distanceC1 = calc.getTimeBetweenCoord(this.getCoordinate(), coordDepot);
            distanceC2 = calc.getTimeBetweenCoord(o.getCoordinate(), coordDepot);
            
        } catch (Exception ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Lequel est le plus proche du dépot     
        int distanceComp = Double.compare(distanceC1, distanceC2);
        
        if(distanceComp == 0) {
            // S'il sont à la même distance
            return Double.compare(this.getOrderedQty(), o.getOrderedQty());
        } else 
            return distanceComp;
    }    
}
