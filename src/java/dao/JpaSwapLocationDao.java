package dao;

import java.util.Collection;
import javax.persistence.Query;
import metier.SwapLocation;

/**
 *
 * @author clementruffin
 */
public class JpaSwapLocationDao extends JpaDaoT<SwapLocation> implements SwapLocationDao {
    private static JpaSwapLocationDao instance;
    
    private JpaSwapLocationDao () {
        super();
    }

    protected static JpaSwapLocationDao getInstance () { 
        if(instance == null) {
            instance = new JpaSwapLocationDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM Location WHERE DType = 'SwapLocation'");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }

    @Override
    public Collection<SwapLocation> findAll() {
        Query query = em.createNamedQuery("SwapLocation.findAll"); 
        
        return (Collection<SwapLocation>) query.getResultList();
    }

}
