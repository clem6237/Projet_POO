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
public class Parameters {
    private double parkTime;
    private double swapTime;
    private double exchangeTime;
    private double pickupTime;
    private double truckUsageCost;
    private double truckDistanceCost;
    private double truckTimeCost;
    private double trailerUsageCost;
    private double trailerDistanceCost;
    private double trailerTimeCost;
    private double bodyCapacity;
    private double operatingTime;

    public Parameters() {
    }

    public Parameters(double parkTime, double swapTime, double exchangeTime, double pickupTime, double truckUsageCost, double truckDistanceCost, double truckTimeCost, double trailerUsageCost, double trailerDistanceCost, double trailerTimeCost, double bodyCapacity, double operatingTime) {
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
    public String toString() {
        return "Parameters{" + "parkTime=" + parkTime + ", swapTime=" + swapTime + ", exchangeTime=" + exchangeTime + ", pickupTime=" + pickupTime + ", truckUsageCost=" + truckUsageCost + ", truckDistanceCost=" + truckDistanceCost + ", truckTimeCost=" + truckTimeCost + ", trailerUsageCost=" + trailerUsageCost + ", trailerDistanceCost=" + trailerDistanceCost + ", trailerTimeCost=" + trailerTimeCost + ", bodyCapacity=" + bodyCapacity + ", operatingTime=" + operatingTime + '}';
    }
}
