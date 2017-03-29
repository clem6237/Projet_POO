package dao;

import java.util.Collection;
import javax.persistence.Query;
import metier.Coordinate;
import metier.Location;

/**
 *
 * @author clementruffin
 */
public class JpaLocationDao extends JpaDaoT<Location> implements LocationDao {
    private static JpaLocationDao instance;
    
    private JpaLocationDao () {
        super();
    }

    protected static JpaLocationDao getInstance () { 
        if(instance == null) {
            instance = new JpaLocationDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM Location WHERE Location.DType = 'Location'");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }
    
    @Override
    public Collection findAll() {
        Query query = em.createNamedQuery("Location.findAll"); 
        
        return (Collection<Location>) query.getResultList();
    }

    @Override
    public Location findById(int id) {
        Query query = em.createNamedQuery("Location.findById"); 
        query.setParameter("id", id);
        
        return (Location) query.getResultList();
    }

    @Override
    public Location findByCoord(Coordinate coord) {
        Query query = em.createNamedQuery("Location.findByCoord"); 
        query.setParameter("coord", coord);
        
        return (Location) query.getResultList();
    }
}
