package dao;

import metier.Depot;

/**
 *
 * @author clementruffin
 */
public interface DepotDao extends DaoT<Depot> {
    public Depot find();
}
