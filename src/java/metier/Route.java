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
public class Route {
    private int idTour;
    private int position;
    private String location;
    private LocationType locationType;
    private boolean trailerAttached;
    private int firstTrailer;
    private int lastTrailer;
    private SwapAction swapAction;
    private double qty1;
    private double qty2;

    public Route() {
    }

    public Route(int idTour, int position, String location, LocationType locationType, boolean trailerAttached, int firstTrailer, int lastTrailer, SwapAction swapAction, double qty1, double qty2) {
        this.idTour = idTour;
        this.position = position;
        this.location = location;
        this.locationType = locationType;
        this.trailerAttached = trailerAttached;
        this.firstTrailer = firstTrailer;
        this.lastTrailer = lastTrailer;
        this.swapAction = swapAction;
        this.qty1 = qty1;
        this.qty2 = qty2;
    }

    public int getIdTour() {
        return idTour;
    }

    public void setIdTour(int idTour) {
        this.idTour = idTour;
    }

    public int getPosition() {
        return position;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isTrailerAttached() {
        return trailerAttached;
    }

    public void setTrailerAttached(boolean trailerAttached) {
        this.trailerAttached = trailerAttached;
    }

    public int getFirstTrailer() {
        return firstTrailer;
    }

    public void setFirstTrailer(int firstTrailer) {
        this.firstTrailer = firstTrailer;
    }

    public int getLastTrailer() {
        return lastTrailer;
    }

    public void setLastTrailer(int lastTrailer) {
        this.lastTrailer = lastTrailer;
    }

    public SwapAction getSwapAction() {
        return swapAction;
    }

    public void setSwapAction(SwapAction swapAction) {
        this.swapAction = swapAction;
    }

    public double getQty1() {
        return qty1;
    }

    public void setQty1(double qty1) {
        this.qty1 = qty1;
    }

    public double getQty2() {
        return qty2;
    }

    public void setQty2(double qty2) {
        this.qty2 = qty2;
    }

    @Override
    public String toString() {
        return "Route { " 
                + "idTour=" + idTour 
                + ", position=" + position 
                + ", trailerAttached=" + trailerAttached 
                + ", firstTrailer=" + firstTrailer 
                + ", lastTrailer=" + lastTrailer 
                + ", swapAction=" + swapAction 
                + ", qty1=" + qty1 
                + ", qty2=" + qty2 
                + " }";
    }
}
