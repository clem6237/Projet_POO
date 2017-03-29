package dao;

/**
 *
 * @author clementruffin
 */
public abstract class DaoFactory {
    
    public abstract RoutingParametersDao getRoutingParametersDao();
    public abstract CoordinateDao getCoordinateDao();
    public abstract DistanceTimeDao getDistanceTimeDao();
    public abstract LocationDao getLocationDao();
    public abstract CustomerDao getCustomerDao();
    public abstract TourDao getTourDao();
    public abstract RouteDao getRouteDao();
    
    public static DaoFactory getDaoFactory (PersistenceType type) {
        
        switch (type) {
            case JPA :
                return new DaoFactoryJpa();  
        }
        
        return null;
    }
    
}
