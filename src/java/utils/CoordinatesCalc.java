package utils;

import dao.DaoFactory;
import dao.DistanceTimeDao;
import dao.PersistenceType;
import metier.Coordinate;
import metier.DistanceTime;

/**
 *
 * @author clementruffin
 */
public class CoordinatesCalc {
    DistanceTimeDao distanceTimeManager;
            
    public CoordinatesCalc() {
        distanceTimeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDistanceTimeDao();
    }
    
    public double getTotalDistanceBetweenCoord(Coordinate coordFrom, Coordinate coordTo) throws Exception {
        DistanceTime distanceTimeGo = distanceTimeManager.findByCoord(coordFrom, coordTo);
        DistanceTime distanceTimeBack = distanceTimeManager.findByCoord(coordTo, coordFrom);
        
        return distanceTimeGo.getDistance() + distanceTimeBack.getDistance();
    }
    
    public double getTotalTimeBetweenCoord(Coordinate coordFrom, Coordinate coordTo) throws Exception {
        DistanceTime distanceTimeGo = distanceTimeManager.findByCoord(coordFrom, coordTo);
        DistanceTime distanceTimeBack = distanceTimeManager.findByCoord(coordTo, coordFrom);
        
        return distanceTimeGo.getTime() + distanceTimeBack.getTime();
    }
    
    public double getDistanceBetweenCoord(Coordinate coordFrom, Coordinate coordTo) throws Exception {
        DistanceTime distanceTime = distanceTimeManager.findByCoord(coordFrom, coordTo);
        return distanceTime.getDistance();
    }
    
    public double getTimeBetweenCoord(Coordinate coordFrom, Coordinate coordTo) throws Exception {
        DistanceTime distanceTime = distanceTimeManager.findByCoord(coordFrom, coordTo);
        return distanceTime.getTime();
    }
}
