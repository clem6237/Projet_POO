package dao;

import java.util.Collection;
import javax.persistence.Query;
import metier.RoutingParameters;

/**
 *
 * @author clementruffin
 */
public class JpaRoutingParametersDao extends JpaDaoT<RoutingParameters> implements RoutingParametersDao {
    private static JpaRoutingParametersDao instance;
    
    private JpaRoutingParametersDao () {
        super();
    }

    protected static JpaRoutingParametersDao getInstance () { 
        if(instance == null) {
            instance = new JpaRoutingParametersDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM RoutingParameters");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }

    @Override
    public RoutingParameters find() {
        Query query = em.createNamedQuery("RoutingParameters.find"); 
        
        return (RoutingParameters) query.getSingleResult();
    }

    
}
