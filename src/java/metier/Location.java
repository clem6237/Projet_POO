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
public class Location {
    private String id;
    private String postalCode;
    private String city;
    private int idCoordinate;
    private LocationType type;

    public Location() {
    }

    public Location(String id, String postalCode, String city, int idCoordinate) {
        this.id = id;
        this.postalCode = postalCode;
        this.city = city;
        this.idCoordinate = idCoordinate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getIdCoordinate() {
        return idCoordinate;
    }

    public void setIdCoordinate(int idCoordinate) {
        this.idCoordinate = idCoordinate;
    }    

    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Location{" 
                + "id=" + id 
                + ", postalCode=" + postalCode 
                + ", city=" + city 
                + ", idCoordinate=" + idCoordinate 
                + ", type=" + type
                + " } \n";
    }
}
