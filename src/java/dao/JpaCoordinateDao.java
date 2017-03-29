package dao;

import java.util.Collection;
import javax.persistence.Query;
import metier.Coordinate;

/**
 *
 * @author clementruffin
 */
public class JpaCoordinateDao extends JpaDaoT<Coordinate> implements CoordinateDao {
    private static JpaCoordinateDao instance;
    
    private JpaCoordinateDao () {
        super();
    }

    protected static JpaCoordinateDao getInstance () { 
        if(instance == null) {
            instance = new JpaCoordinateDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM Coordinate");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }

    @Override
    public Collection findAll() {
        Query query = em.createNamedQuery("Coordinate.findAll"); 
        
        return (Collection<Coordinate>) query.getResultList();
    }
    
    @Override
    public Coordinate findById(int id) {
        Query query = em.createNamedQuery("Coordinate.findById"); 
        query.setParameter("id", id);
        
        return (Coordinate) query.getSingleResult();
    }
    
    @Override
    public Coordinate findByCoord(double coordX, double coordY) {
        Query query = em.createNamedQuery("Coordinate.findByCoord"); 
        query.setParameter("coordX", coordX);
        query.setParameter("coordY", coordY);
        
        return (Coordinate) query.getSingleResult();
    }    
}
