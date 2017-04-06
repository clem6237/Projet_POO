package dao;

/**
 *
 * @author clementruffin
 */
public class DaoFactoryJpa extends DaoFactory {

    @Override
    public RoutingParametersDao getRoutingParametersDao() {
        return JpaRoutingParametersDao.getInstance();
    }

    @Override
    public CoordinateDao getCoordinateDao() {
        return JpaCoordinateDao.getInstance();
    }
    
    @Override
    public DistanceTimeDao getDistanceTimeDao() {
        return JpaDistanceTimeDao.getInstance();
    }

    @Override
    public LocationDao getLocationDao() {
        return JpaLocationDao.getInstance();
    }

    @Override
    public DepotDao getDepotDao() {
        return JpaDepotDao.getInstance();
    }
    
    @Override
    public SwapLocationDao getSwapLocationDao() {
        return JpaSwapLocationDao.getInstance();
    }
    
    @Override
    public CustomerDao getCustomerDao() {
        return JpaCustomerDao.getInstance();
    }

    @Override
    public TourDao getTourDao() {
        return JpaTourDao.getInstance();
    }

    @Override
    public RouteDao getRouteDao() {
        return JpaRouteDao.getInstance();
    }
}
