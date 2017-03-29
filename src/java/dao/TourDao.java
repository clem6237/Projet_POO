package dao;

import metier.Tour;

/**
 *
 * @author clementruffin
 */
public interface TourDao extends DaoT<Tour> {
    public Tour findById(int id);
}
