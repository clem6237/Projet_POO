package utils;

import dao.DaoFactory;
import dao.PersistenceType;
import dao.RoutingParametersDao;
import metier.RoutingParameters;

/**
 * Contient les méthodes permettant de calculer les coûts d'utilisation et les
 * coûts kimométriques et horaires des camions et remorques.
 * @author clementruffin
 */
public class CostCalc {
    RoutingParametersDao routingParametersManager;
    RoutingParameters parameters;
    
    public CostCalc() {
        routingParametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        parameters = routingParametersManager.find();
    }
    
    /**
     * Calcule le coût d'utilisation du camion.
     * @return 
     */
    public double getTruckUsageCost() {
        return parameters.getTruckUsageCost();
    }
    
    /**
     * Calcule le coût kilométrique du camion.
     * @param distance
     * @return 
     */
    public double getTruckDistanceCost(double distance) {
        return parameters.getTruckDistanceCost() * (distance / 1000);
    }
    
    /**
     * Calcule le coût horaire du camion.
     * @param time
     * @return 
     */
    public double getTruckTimeCost(double time) {
        return parameters.getTruckTimeCost() * (time / 3600);
    }
    
    /**
     * Calcule le coût d'utilisation d'une remorque.
     * @return 
     */
    public double getTrailerUsageCost() {
        return parameters.getTrailerUsageCost();
    }
    
    /**
     * Calcule le coût kilométrique d'une remorque.
     * @param distance
     * @return 
     */
    public double getTrailerDistanceCost(double distance) {
        return parameters.getTrailerDistanceCost() * (distance / 1000);
    }
    
    /**
     * Calcule le coût horaire d'une remorque.
     * @param time
     * @return 
     */
    public double getTrailerTimeCost(double time) {
        return parameters.getTrailerTimeCost() * (time / 3600);
    }
}
