package dao;

import java.util.Collection;
import metier.Route;
import metier.Tour;

/**
 *
 * @author clementruffin
 */
public interface RouteDao extends DaoT<Route> {
    public Collection<Route> findByTour(Tour tour);
    public Route findByTourPosition(Tour tour, int position);
}
