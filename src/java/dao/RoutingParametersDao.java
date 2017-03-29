package dao;

import metier.RoutingParameters;

/**
 *
 * @author clementruffin
 */
public interface RoutingParametersDao extends DaoT<RoutingParameters> {
    public RoutingParameters find();
}
