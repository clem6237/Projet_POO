package dao;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author clementruffin
 * @param <T>
 */
public abstract class JpaDaoT<T> implements DaoT<T> {

    final String namePU = "ProjetPOOPU";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(namePU); 
    EntityManager em;
    EntityTransaction et;
    
    public JpaDaoT() {
        em = emf.createEntityManager();
        et = em.getTransaction(); 
    }

    @Override
    public boolean create(T obj) throws DaoException {
        try {
            et.begin();
            em.persist(obj);
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during creation", e);
        }
    }    

    @Override
    public boolean update(T obj) throws DaoException {
        try {
            et.begin();
            em.merge(obj);
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during update", e);
        }
    }

    @Override
    public boolean delete(T obj) throws DaoException {
        try {
            et.begin();
            em.remove(obj);
            et.commit();
            
            return true;
        } catch (Exception e) {
            throw new DaoException("Error during delete", e);
        }
    }

    @Override
    public boolean deleteAll() throws DaoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public DaoT find(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        if(em != null && em.isOpen()){ 
            em.close();
        }
        if(emf != null && emf.isOpen()) {
            emf.close(); 
        }
    }

}
