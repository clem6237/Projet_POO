/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import dao.DaoFactory;
import dao.PersistenceType;
import dao.RoutingParametersDao;
import metier.RoutingParameters;

/**
 *
 * @author clementruffin
 */
public class CostCalc {
    RoutingParametersDao routingParametersManager;
    RoutingParameters parameters;
    
    public CostCalc() {
        routingParametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        parameters = routingParametersManager.find();
    }
    
    public double getTruckUsageCost() {
        return parameters.getTruckUsageCost();
    }
    
    public double getTruckDistanceCost(double distance) {
        return parameters.getTruckDistanceCost() * (distance / 100);
    }
    
    public double getTruckTimeCost(double time) {
        return parameters.getTruckTimeCost() * (time / 3600);
    }
    
    public double getTrailerUsageCost() {
        return parameters.getTrailerUsageCost();
    }
    
    public double getTrailerDistanceCost(double distance) {
        return parameters.getTrailerDistanceCost() * (distance / 100);
    }
    
    public double getTrailerTimeCost(double time) {
        return parameters.getTrailerTimeCost() * (time / 3600);
    }
}
