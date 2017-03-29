package dao;

import javax.persistence.Query;
import metier.Customer;

/**
 *
 * @author clementruffin
 */
public class JpaCustomerDao extends JpaDaoT<Customer> implements CustomerDao {
    private static JpaCustomerDao instance;
    
    private JpaCustomerDao () {
        super();
    }

    protected static JpaCustomerDao getInstance () { 
        if(instance == null) {
            instance = new JpaCustomerDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM Location WHERE Location.DType = 'Customer'");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }
}
