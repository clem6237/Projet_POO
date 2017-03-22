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
public class Depot extends Location {
    public Depot() {
    }
    
    @Override
    public String toString() {
        return "Depot { " 
                + "id=" + this.getId() 
                + ", postalCode=" + this.getPostalCode() 
                + ", city=" + this.getCity() 
                + ", idCoordinate=" + this.getIdCoordinate() 
                + " } \n";
    }
}
