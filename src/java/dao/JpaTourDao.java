package dao;

import java.util.Collection;
import javax.persistence.Query;
import metier.Location;
import metier.Tour;

/**
 *
 * @author clementruffin
 */
public class JpaTourDao extends JpaDaoT<Tour> implements TourDao {
    private static JpaTourDao instance;
    
    private JpaTourDao () {
        super();
    }

    protected static JpaTourDao getInstance () { 
        if(instance == null) {
            instance = new JpaTourDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM Tour");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }
    
    @Override
    public Collection findAll() {
        Query query = em.createNamedQuery("Tour.findAll"); 
        
        return (Collection<Location>) query.getResultList();
    }

    @Override
    public Tour findById(int id) {
        Query query = em.createNamedQuery("Tour.findById"); 
        query.setParameter("id", id);
        
        return (Tour) query.getResultList();
    }
}
