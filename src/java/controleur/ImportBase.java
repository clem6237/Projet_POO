package controleur;

import dao.CoordinateDao;
import dao.DaoFactory;
import dao.DistanceTimeDao;
import dao.PersistenceType;
import dao.RoutingParametersDao;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import metier.Coordinate;
import metier.DistanceTime;
import metier.RoutingParameters;

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
    
    RoutingParameters routingParameters;

    public ImportBase(String filePath) {
        this.filePath = filePath;
    }
    
    public void importParameters() throws Exception {
        RoutingParametersDao parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        
        routingParameters = new RoutingParameters();
        this.importFleetFile();
        this.importSwapActionsFile();
        
        parametersManager.create(routingParameters);
    }
    
    private void importFleetFile() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameFleet));
        String line = line = br.readLine();
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            switch (data[0])
            {
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
    
    private void importSwapActionsFile() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameSwapActions));
        String line = line = br.readLine();
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            switch (data[0])
            {
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
    
    public void importCoordinates() throws Exception
    {
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameCoordinates));
        String line = line = br.readLine();
        
        int idCoord = 1;
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");
            
            Coordinate c = new Coordinate(idCoord, Double.parseDouble(data[0]), Double.parseDouble(data[1]));
            coordinateManager.create(c);
            
            idCoord++;
        }
        
        br.close();
        
        this.importDistanceTime(coordinateManager);
    }
    
    private void importDistanceTime(CoordinateDao coordinateManager) throws Exception
    {
        DistanceTimeDao distanceTimeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDistanceTimeDao();
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameDistanceTime));
        String line = line = br.readLine();
        
        Collection<DistanceTime> listDistanceTime = new ArrayList();
        int idCoordFrom = 1;
        double distance, time;
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");
            
            for (int j = 0; j < data.length; j++ ) {
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
        
        br.close();
    }
}
