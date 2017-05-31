package metier;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "ROUTINGPARAMETERS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoutingParameters.find", query = "SELECT rp FROM RoutingParameters rp")
})
public class RoutingParameters implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "PARKTIME")
    private double parkTime;
    
    @Column(name = "SWAPTIME")
    private double swapTime;
    
    @Column(name = "EXCHANGETIME")
    private double exchangeTime;
    
    @Column(name = "PICKUPTIME")
    private double pickupTime;
    
    @Column(name = "TRUCKUSAGECOST")
    private double truckUsageCost;
    
    @Column(name = "TRUCKDISTANCECOST")
    private double truckDistanceCost;
    
    @Column(name = "TRUCKTIMECOST")
    private double truckTimeCost;
    
    @Column(name = "TRAILERUSAGECOST")
    private double trailerUsageCost;
    
    @Column(name = "TRAILERDISTANCECOST")
    private double trailerDistanceCost;
    
    @Column(name = "TRAILERTIMECOST")
    private double trailerTimeCost;
    
    @Column(name = "BODYCAPACITY")
    private double bodyCapacity;
    
    @Column(name = "OPERATINGTIME")
    private double operatingTime;

    public RoutingParameters() {
    }

    public RoutingParameters(double parkTime, double swapTime, double exchangeTime, double pickupTime, double truckUsageCost, double truckDistanceCost, double truckTimeCost, double trailerUsageCost, double trailerDistanceCost, double trailerTimeCost, double bodyCapacity, double operatingTime) {
        this.parkTime = parkTime;
        this.swapTime = swapTime;
        this.exchangeTime = exchangeTime;
        this.pickupTime = pickupTime;
        this.truckUsageCost = truckUsageCost;
        this.truckDistanceCost = truckDistanceCost;
        this.truckTimeCost = truckTimeCost;
        this.trailerUsageCost = trailerUsageCost;
        this.trailerDistanceCost = trailerDistanceCost;
        this.trailerTimeCost = trailerTimeCost;
        this.bodyCapacity = bodyCapacity;
        this.operatingTime = operatingTime;
    }

    public double getParkTime() {
        return parkTime;
    }

    public void setParkTime(double parkTime) {
        this.parkTime = parkTime;
    }

    public double getSwapTime() {
        return swapTime;
    }

    public void setSwapTime(double swapTime) {
        this.swapTime = swapTime;
    }

    public double getExchangeTime() {
        return exchangeTime;
    }

    public void setExchangeTime(double exchangeTime) {
        this.exchangeTime = exchangeTime;
    }

    public double getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(double pickupTime) {
        this.pickupTime = pickupTime;
    }

    public double getTruckUsageCost() {
        return truckUsageCost;
    }

    public void setTruckUsageCost(double truckUsageCost) {
        this.truckUsageCost = truckUsageCost;
    }

    public double getTruckDistanceCost() {
        return truckDistanceCost;
    }

    public void setTruckDistanceCost(double truckDistanceCost) {
        this.truckDistanceCost = truckDistanceCost;
    }

    public double getTruckTimeCost() {
        return truckTimeCost;
    }

    public void setTruckTimeCost(double truckTimeCost) {
        this.truckTimeCost = truckTimeCost;
    }

    public double getTrailerUsageCost() {
        return trailerUsageCost;
    }

    public void setTrailerUsageCost(double trailerUsageCost) {
        this.trailerUsageCost = trailerUsageCost;
    }

    public double getTrailerDistanceCost() {
        return trailerDistanceCost;
    }

    public void setTrailerDistanceCost(double trailerDistanceCost) {
        this.trailerDistanceCost = trailerDistanceCost;
    }

    public double getTrailerTimeCost() {
        return trailerTimeCost;
    }

    public void setTrailerTimeCost(double trailerTimeCost) {
        this.trailerTimeCost = trailerTimeCost;
    }

    public double getBodyCapacity() {
        return bodyCapacity;
    }

    public void setBodyCapacity(double bodyCapacity) {
        this.bodyCapacity = bodyCapacity;
    }

    public double getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(double operatingTime) {
        this.operatingTime = operatingTime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final RoutingParameters other = (RoutingParameters) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Parameters{" 
                + "parkTime=" + parkTime 
                + ", swapTime=" + swapTime 
                + ", exchangeTime=" + exchangeTime 
                + ", pickupTime=" + pickupTime 
                + ", truckUsageCost=" + truckUsageCost 
                + ", truckDistanceCost=" + truckDistanceCost 
                + ", truckTimeCost=" + truckTimeCost 
                + ", trailerUsageCost=" + trailerUsageCost 
                + ", trailerDistanceCost=" + trailerDistanceCost 
                + ", trailerTimeCost=" + trailerTimeCost 
                + ", bodyCapacity=" + bodyCapacity 
                + ", operatingTime=" + operatingTime 
                + "}";
    }
}
