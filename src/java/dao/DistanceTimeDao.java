package dao;

import metier.Coordinate;
import metier.DistanceTime;

/**
 *
 * @author clementruffin
 */
public interface DistanceTimeDao extends DaoT<DistanceTime> {
    public DistanceTime findById(int id);
    public DistanceTime findByCoord(Coordinate coordFrom, Coordinate coordTo);
}
