package dao;

import java.util.Collection;
import metier.Coordinate;
import metier.DistanceTime;

/**
 *
 * @author clementruffin
 */
public interface DistanceTimeDao extends DaoT<DistanceTime> {
    public boolean createAll(Collection<DistanceTime> listDistanceTime) throws DaoException;
    public DistanceTime findById(int id);
    public DistanceTime findByCoord(Coordinate coordFrom, Coordinate coordTo);
}
