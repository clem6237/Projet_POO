package utils;

import dao.CoordinateDao;
import dao.CustomerDao;
import dao.DaoFactory;
import dao.DistanceTimeDao;
import dao.LocationDao;
import dao.PersistenceType;
import dao.RoutingParametersDao;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import metier.Coordinate;
import metier.Customer;
import metier.Depot;
import metier.DistanceTime;
import metier.RoutingParameters;
import metier.SwapLocation;

/**
 *
 * @author clementruffin
 */
public class ImportBase {
    final String filePath;
    final String fileNameFleet = "small_normal/Fleet.csv";
    final String fileNameSwapActions = "small_normal/SwapActions.csv";
    final String fileNameCoordinates = "dima/DistanceTimesCoordinates.csv";
    final String fileNameDistanceTime = "dima/DistanceTimesData.csv";
    final String fileNameLocations = "small_normal/Locations.csv";
    
    public ImportBase(String filePath) {
        this.filePath = filePath;
    }
    
    public void importParameters() throws Exception {
        RoutingParametersDao parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        RoutingParameters routingParameters = new RoutingParameters();
        
        this.importFleetFile(routingParameters);
        this.importSwapActionsFile(routingParameters);
        
        parametersManager.create(routingParameters);
        Utils.log("Import Paramètres OK");
    }
    
    private void importFleetFile(RoutingParameters routingParameters) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameFleet));
        String line = br.readLine();
        
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");

            switch (data[0]) {
                case "TRUCK" :
                    routingParameters.setTruckDistanceCost(Double.parseDouble(data[2]));
                    routingParameters.setTruckTimeCost(Double.parseDouble(data[3]));
                    routingParameters.setTruckUsageCost(Double.parseDouble(data[4]));
                    break;
                case "SEMI_TRAILER" :
                    routingParameters.setTrailerDistanceCost(Double.parseDouble(data[2]));
                    routingParameters.setTrailerTimeCost(Double.parseDouble(data[3]));
                    routingParameters.setTrailerUsageCost(Double.parseDouble(data[4]));
                    break;
                case "SWAP_BODY" :
                    routingParameters.setBodyCapacity(Double.parseDouble(data[1]));
                    routingParameters.setOperatingTime(Double.parseDouble(data[5]));
                    break;
            }
        }
        
        br.close();
    }
    
    private void importSwapActionsFile(RoutingParameters routingParameters) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameSwapActions));
        String line = br.readLine();
        
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");

            switch (data[0]) {
                case "PARK" :
                    routingParameters.setParkTime(Double.parseDouble(data[1]));
                    break;
                case "SWAP" :
                    routingParameters.setSwapTime(Double.parseDouble(data[1]));
                    break;
                case "EXCHANGE" :
                    routingParameters.setExchangeTime(Double.parseDouble(data[1]));
                    break;
                case "PICKUP" :
                    routingParameters.setPickupTime(Double.parseDouble(data[1]));
                    break;
            }
        }
        
        br.close();
    }
    
    public void importCoordinates() throws Exception {
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameCoordinates));
        String line = br.readLine();
        
        int idCoord = 1;
        
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");
            
            Coordinate c = new Coordinate(idCoord, Double.parseDouble(data[0]), Double.parseDouble(data[1]));
            coordinateManager.create(c);
            
            idCoord++;
        }
        
        Utils.log("Import Coordonnées OK");
        
        br.close();
        
        this.importDistanceTime(coordinateManager);
    }
    
    private void importDistanceTime(CoordinateDao coordinateManager) throws Exception {
        DistanceTimeDao distanceTimeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDistanceTimeDao();
        
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameDistanceTime));
        String line = br.readLine();
        
        Collection<DistanceTime> listDistanceTime = new ArrayList();
        int idCoordFrom = 1;
        double distance, time;
        
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");
            
            for (int j = 0; j < data.length; j++) {
                DistanceTime distanceTime = new DistanceTime(
                        coordinateManager.findById(idCoordFrom), 
                        coordinateManager.findById(j/2+1), 
                        Double.parseDouble(data[j]), 
                        Double.parseDouble(data[j+1]));
                
                listDistanceTime.add(distanceTime);
                j++;
            }
            
            idCoordFrom++;
        }
        
        distanceTimeManager.createAll(listDistanceTime);
        
        Utils.log("Import Mapping OK");
        
        br.close();
    }
    
    public void importLocations() throws Exception {
        LocationDao locationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getLocationDao();
        CustomerDao customerManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCustomerDao();
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameLocations));
        String line = br.readLine();
        
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");

            switch (data[0]) {
                case "DEPOT" :
                    Depot depot = new Depot();
                    depot.setId(data[1]);
                    depot.setPostalCode(data[2]);
                    depot.setCity(data[3]);
                    depot.setCoordinate(coordinateManager.findByCoord(Double.parseDouble(data[4]), Double.parseDouble(data[5])));
                    locationManager.create(depot);
                    break;
                case "SWAP_LOCATION" :
                    SwapLocation swapLocation = new SwapLocation();
                    swapLocation.setId(data[1]);
                    swapLocation.setPostalCode(data[2]);
                    swapLocation.setCity(data[3]);
                    swapLocation.setCoordinate(coordinateManager.findByCoord(Double.parseDouble(data[4]), Double.parseDouble(data[5])));
                    locationManager.create(swapLocation);
                    break;
                case "CUSTOMER" :
                    Customer customer = new Customer();
                    customer.setId(data[1]);
                    customer.setPostalCode(data[2]);
                    customer.setCity(data[3]);
                    customer.setCoordinate(coordinateManager.findByCoord(Double.parseDouble(data[4]), Double.parseDouble(data[5])));
                    customer.setOrderedQty(Double.valueOf(data[6]));
                    customer.setAccessible(data[7].equals("1"));
                    customer.setServiceTime(Double.valueOf(data[8]));
                    customerManager.create(customer);
                    break;
            }
        }
        
        Utils.log("Import Emplacements OK");
        
        br.close();
    }
}
