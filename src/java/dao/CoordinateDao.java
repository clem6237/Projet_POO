package dao;

import metier.Coordinate;

/**
 *
 * @author clementruffin
 */
public interface CoordinateDao extends DaoT<Coordinate> {
    public Coordinate findById(int id);
    public Coordinate findByCoord(double coordX, double coordY);
}
