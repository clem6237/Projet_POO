package utils;

import dao.DaoFactory;
import dao.DistanceTimeDao;
import dao.PersistenceType;
import metier.Coordinate;
import metier.DistanceTime;

/**
 * Contient les méthodes permettant de calculer la distance et le temps de 
 * parcours entre deux coordonnées.
 * @author clementruffin
 */
public class CoordinatesCalc {
    DistanceTimeDao distanceTimeManager;
            
    public CoordinatesCalc() {
        distanceTimeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDistanceTimeDao();
    }
    
    /**
     * Calcule la distance aller-retour entre deux coordonnées.
     * @param coordFrom
     * @param coordTo
     * @return
     * @throws Exception 
     */
    public double getTotalDistanceBetweenCoord(Coordinate coordFrom, Coordinate coordTo) throws Exception {
        DistanceTime distanceTimeGo = distanceTimeManager.findByCoord(coordFrom, coordTo);
        DistanceTime distanceTimeBack = distanceTimeManager.findByCoord(coordTo, coordFrom);
        
        return distanceTimeGo.getDistance() + distanceTimeBack.getDistance();
    }
    
    /**
     * Calcule le temps de parcours aller-retour entre deux coordonnées.
     * @param coordFrom
     * @param coordTo
     * @return
     * @throws Exception 
     */
    public double getTotalTimeBetweenCoord(Coordinate coordFrom, Coordinate coordTo) throws Exception {
        DistanceTime distanceTimeGo = distanceTimeManager.findByCoord(coordFrom, coordTo);
        DistanceTime distanceTimeBack = distanceTimeManager.findByCoord(coordTo, coordFrom);
        
        return distanceTimeGo.getTime() + distanceTimeBack.getTime();
    }
    
    /**
     * Calcule la distance simple d'une coordonnée à une autre.
     * @param coordFrom
     * @param coordTo
     * @return
     * @throws Exception 
     */
    public double getDistanceBetweenCoord(Coordinate coordFrom, Coordinate coordTo) throws Exception {
        DistanceTime distanceTime = distanceTimeManager.findByCoord(coordFrom, coordTo);
        return distanceTime.getDistance();
    }
    
    /**
     * Calcule le temps de parcours simple d'une coordonnée à une autre.
     * @param coordFrom
     * @param coordTo
     * @return
     * @throws Exception 
     */
    public double getTimeBetweenCoord(Coordinate coordFrom, Coordinate coordTo) throws Exception {
        DistanceTime distanceTime = distanceTimeManager.findByCoord(coordFrom, coordTo);
        return distanceTime.getTime();
    }
}
