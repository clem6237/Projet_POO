package dao;

import java.util.Collection;

/**
 *
 * @author clementruffin
 * @param <T>
 */
public interface DaoT<T> {
    public boolean create(T obj) throws DaoException;
    public DaoT find (int id);
    public Collection<T> findAll() ;
    public boolean update (T obj) throws DaoException;
    public boolean delete (T obj) throws DaoException;
    public boolean deleteAll() throws DaoException;
    public void close() ;
}
