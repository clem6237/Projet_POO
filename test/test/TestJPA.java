/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import dao.CoordinateDao;
import dao.CustomerDao;
import dao.DaoException;
import dao.DaoFactory;
import dao.LocationDao;
import dao.PersistenceType;
import dao.RouteDao;
import dao.RoutingParametersDao;
import dao.TourDao;
import metier.Coordinate;
import metier.Customer;
import metier.Location;
import metier.LocationType;

/**
 *
 * @author clementruffin
 */
public class TestJPA {
    
    public static void main(String[] args) throws DaoException {
        RoutingParametersDao routingParametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        LocationDao locationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getLocationDao();
        CustomerDao customerManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCustomerDao();
        TourDao tourManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getTourDao();
        RouteDao routeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRouteDao();
        
        tourManager.deleteAll();
        routeManager.deleteAll();
        customerManager.deleteAll();
        locationManager.deleteAll();
        coordinateManager.deleteAll();
        routingParametersManager.deleteAll();
        
        
        
        
        
        
        Coordinate c1 = new Coordinate(1, 2.5, 3.5);
        coordinateManager.create(c1);
        
        Location loc1 = new Location("Depot", "59000", "LILLE", c1, LocationType.DEPOT);
        locationManager.create(loc1);
        
        Customer cust1 = new Customer("Clement", "62300", "LENS", coordinateManager.findById(1), LocationType.CUSTOMER, 250, false, 500);
        customerManager.create(cust1);
    }
}
