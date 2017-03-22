/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier;

/**
 *
 * @author clementruffin
 */
public class Customer extends Location {
    private double orderedQty;
    private boolean accessible;
    private double serviceTime;

    public Customer() {
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
                + ", idCoordinate=" + this.getIdCoordinate()
                + ", orderedQty=" + orderedQty
                + ", accessible=" + accessible
                + ", serviceTime=" + serviceTime
                + " } \n";
    }
}
