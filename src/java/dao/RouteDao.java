package dao;

import metier.Route;
import metier.Tour;

/**
 *
 * @author clementruffin
 */
public interface RouteDao extends DaoT<Route> {
    public Route findByTour(Tour tour);
    public Route findByTourPosition(Tour tour, int position);
}
