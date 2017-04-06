package dao;

import java.util.List;
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
        
        List<RoutingParameters> list = query.getResultList();
        
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return new RoutingParameters();
        }
    }

    
}
