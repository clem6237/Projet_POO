package dao;

import metier.Coordinate;
import metier.Location;

/**
 *
 * @author clementruffin
 */
public interface LocationDao extends DaoT<Location> {
    public Location findById(int id);
    public Location findByCoord(Coordinate coord);
}
