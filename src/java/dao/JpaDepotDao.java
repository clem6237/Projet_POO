package dao;

import javax.persistence.Query;
import metier.Depot;

/**
 *
 * @author clementruffin
 */
public class JpaDepotDao extends JpaDaoT<Depot> implements DepotDao {
    private static JpaDepotDao instance;
    
    private JpaDepotDao () {
        super();
    }

    protected static JpaDepotDao getInstance () { 
        if(instance == null) {
            instance = new JpaDepotDao(); 
        }
        return instance;
    }
    
    @Override
    public boolean deleteAll() throws DaoException {
        try {
            et.begin();
            
            Query query = em.createNativeQuery("DELETE FROM Location WHERE DType = 'Depot'");
            query.executeUpdate();
            
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during deleteAll", e);
        }
    }

    @Override
    public Depot find() {
        Query query = em.createNamedQuery("Depot.find"); 
        
        return (Depot) query.getSingleResult();
    }
}
