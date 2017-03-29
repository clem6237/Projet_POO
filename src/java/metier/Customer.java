package metier;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
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
@Table(name = "CUSTOMER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.findById", query = "SELECT c FROM Customer c WHERE c.id = :id"),
})
public class Customer extends Location implements Serializable {
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
}
