package dao;

import java.util.Collection;
import javax.persistence.Query;
import metier.Location;
import metier.Route;
import metier.Tour;

/**
 *
 * @author clementruffin
 */
public class JpaRouteDao extends JpaDaoT<Route> implements RouteDao {
    private static JpaRouteDao instance;
    
    private JpaRouteDao () {
        super();
    }

    protected static JpaRouteDao getInstance () { 
        if(instance == null) {
            instance = new JpaRouteDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM Route");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }
    
    @Override
    public Collection findAll() {
        Query query = em.createNamedQuery("Route.findAll"); 
        
        return (Collection<Location>) query.getResultList();
    }

    @Override
    public Collection<Route> findByTour(Tour tour) {
        Query query = em.createNamedQuery("Route.findByTour"); 
        query.setParameter("tour", tour);
        
        return (Collection<Route>) query.getResultList();
    }

    @Override
    public Route findByTourPosition(Tour tour, int position) {
        Query query = em.createNamedQuery("Route.findByTourPosition"); 
        query.setParameter("tour", tour);
        query.setParameter("position", position);
        
        return (Route) query.getSingleResult();
    }
}
