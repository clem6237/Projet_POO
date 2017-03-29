package metier;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "CUSTOMER")
@XmlRootElement
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

    public Customer(String id, String postalCode, String city, Coordinate coordinate, LocationType type, double orderedQty, boolean accessible, double serviceTime) {
        super(id, postalCode, city, coordinate, type);
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
