package dao;

import java.util.Collection;
import java.util.Date;
import javax.persistence.Query;
import metier.Coordinate;
import metier.DistanceTime;

/**
 *
 * @author clementruffin
 */
public class JpaDistanceTimeDao extends JpaDaoT<DistanceTime> implements DistanceTimeDao {
    private static JpaDistanceTimeDao instance;
    
    private JpaDistanceTimeDao () {
        super();
    }

    protected static JpaDistanceTimeDao getInstance () { 
        if(instance == null) {
            instance = new JpaDistanceTimeDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean createAll(Collection<DistanceTime> listDistanceTime) throws DaoException {
        try {
            et.begin();
            
            for (DistanceTime distanceTime : listDistanceTime) {
                em.persist(distanceTime);
            }
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during creation", e);
        }
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM DistanceTime");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }

    @Override
    public Collection findAll() {
        Query query = em.createNamedQuery("DistanceTime.findAll"); 
        
        return (Collection<Coordinate>) query.getResultList();
    }

    @Override
    public DistanceTime findById(int id) {
        Query query = em.createNamedQuery("DistanceTime.findById"); 
        query.setParameter("id", id);
        
        return (DistanceTime) query.getSingleResult();
    }

    @Override
    public DistanceTime findByCoord(Coordinate coordFrom, Coordinate coordTo) {
        Query query = em.createNamedQuery("DistanceTime.findByCoord"); 
        query.setParameter("coordFrom", coordFrom);
        query.setParameter("coordTo", coordTo);
        
        return (DistanceTime) query.getSingleResult();
    }
}
