package test;

import dao.CoordinateDao;
import dao.CustomerDao;
import dao.DaoException;
import dao.DaoFactory;
import dao.DistanceTimeDao;
import dao.LocationDao;
import dao.PersistenceType;
import dao.RouteDao;
import dao.RoutingParametersDao;
import dao.TourDao;
import java.io.BufferedReader;
import java.io.FileReader;
import metier.Customer;
import metier.Location;
import metier.LocationType;

/**
 *
 * @author clementruffin
 */
public class Version2 {
    
    static String filePath = "Projet2017/";
    final String fileNameLocations = "small_normal/Locations.csv";
    final String fileNameSolutions = "small_normal/Solution.csv";
    
    RoutingParametersDao parametersManager;
    DistanceTimeDao distanceTimeManager;
    CoordinateDao coordinateManager;
    LocationDao locationManager;
    CustomerDao customerManager;
    TourDao tourManager;
    RouteDao routeManager;
    
    int idCoordDepot;
    //int nbTour = 1;
    //double totalCost = 0;
    
    public static void main(String[] args) throws Exception
    {
        Version2 test = new Version2();
        
        test.initialize();
        
        // Importation des paramètres, coordonnées & emplacements
        ImportBase importBase = new ImportBase(filePath);
        importBase.importParameters();
        importBase.importCoordinates();
        
        // Importation des locations
        test.importLocations();
        
        // Algorithme de création des tournées
        //test.scanCustomerRequests();
    }
    
    public void initialize() throws DaoException
    {
        parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        distanceTimeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDistanceTimeDao();
        locationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getLocationDao();
        customerManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCustomerDao();
        tourManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getTourDao();
        routeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRouteDao();
        
        routeManager.deleteAll();
        tourManager.deleteAll();
        customerManager.deleteAll();
        locationManager.deleteAll();
        distanceTimeManager.deleteAll();
        coordinateManager.deleteAll();
        parametersManager.deleteAll();
    }
    
    public void importLocations() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameLocations));
        String line = line = br.readLine();
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            switch (data[0])
            {
                case "DEPOT" :
                    Location depot = new Location();
                    depot.setId(data[1]);
                    depot.setPostalCode(data[2]);
                    depot.setCity(data[3]);
                    depot.setCoordinate(coordinateManager.findByCoord(Double.parseDouble(data[4]), Double.parseDouble(data[5])));
                    depot.setType(LocationType.DEPOT);
                    idCoordDepot = depot.getCoordinate().getId();
                    locationManager.create(depot);
                    break;
                case "SWAP_LOCATION" :
                    Location swapLocation = new Location();
                    swapLocation.setId(data[1]);
                    swapLocation.setPostalCode(data[2]);
                    swapLocation.setCity(data[3]);
                    swapLocation.setCoordinate(coordinateManager.findByCoord(Double.parseDouble(data[4]), Double.parseDouble(data[5])));
                    swapLocation.setType(LocationType.SWAP_LOCATION);
                    locationManager.create(swapLocation);
                    break;
                case "CUSTOMER" :
                    Customer customer = new Customer();
                    customer.setId(data[1]);
                    customer.setPostalCode(data[2]);
                    customer.setCity(data[3]);
                    customer.setCoordinate(coordinateManager.findByCoord(Double.parseDouble(data[4]), Double.parseDouble(data[5])));
                    customer.setType(LocationType.CUSTOMER);
                    customer.setOrderedQty(Double.valueOf(data[6]));
                    customer.setAccessible(data[7].equals("1"));
                    customer.setServiceTime(Double.valueOf(data[8]));
                    customerManager.create(customer);
                    break;
            }
        }
        
        br.close();
    }
    
    /*public void scanCustomerRequests() throws Exception
    {
        List<Map<String, String>> mappedData = new ArrayList();
        
        for (Customer customer : allCustomers)
        {
            List<Map<String, String>> map = this.processCustomerRequest(customer);
            if (map != null)
            {
                mappedData.addAll(map);
            }
        }
        
        System.out.println("TOTAL : " + totalCost);
        this.createSolutions(mappedData);
    }
    
    public List<Map<String, String>> processCustomerRequest(Customer customer) throws Exception
    {
        List<Map<String, String>> mappedData = new ArrayList();
        Map<String, String> map = new HashMap();
        
        double distanceTotal = this.getDistanceBetweenCoord(idCoordDepot, customer.getCoordinate().getId()) 
                + this.getDistanceBetweenCoord(customer.getCoordinate().getId(), idCoordDepot);
        
        double timeFrom = this.getTimeBetweenCoord(idCoordDepot, customer.getCoordinate().getId());
        double timeTo = this.getTimeBetweenCoord(customer.getCoordinate().getId(), idCoordDepot);
        double timeTotal = customer.getServiceTime() + timeFrom + timeTo;
        
        //System.out.println(customer.getId() + " - " + String.valueOf(timeFrom) + " / " + String.valueOf(timeTo));
       
        if (timeTotal > parameters.getOperatingTime())
        {
            System.out.println(customer.getId() + " - Livraison impossible (" + timeTotal + " / " + parameters.getOperatingTime() + ")");
            return null;
        }
        
        Truck truck = new Truck();
        truck.setDistanceTravelled(distanceTotal);
        truck.setTransitTime(timeTotal);
        
        Trailer[] trailers = null;
        
        if (customer.getOrderedQty() > parameters.getBodyCapacity())
        {
            if (!customer.isAccessible())
            {
                System.out.println(customer.getId() + " - Livraison impossible (quantité)");
                return null;
            }
            else
            {
                trailers = new Trailer[1];
                
                Trailer trailer1 = new Trailer();
                trailer1.setDistanceTravelled(distanceTotal);
                trailer1.setTransitTime(timeTotal);
                
                trailers[0] = trailer1;
            }
        }
        else
        {
            trailers = new Trailer[0];
        }
        
        double coutTotal = calcTotalCost(truck, trailers);
        totalCost += coutTotal;
        
        System.out.println(customer.getId() + " - Coût = " + String.valueOf(coutTotal));
        
        map = new HashMap<String, String>();
        map.put("TOUR_ID", String.valueOf(nbTour));
        map.put("TOUR_POSITION", "1");
        map.put("LOCATION_ID", allLocations.get(0).getId());
        map.put("LOCATION_TYPE", "DEPOT");
        map.put("SEMI_TRAILER_ATTACHED", trailers.length == 1 ? "1" : "0");
        map.put("SWAP_BODY_TRUCK", "1");
        map.put("SWAP_BODY_SEMI_TRAILER", trailers.length == 1 ? "2" : "0");
        map.put("SWAP_ACTION", String.valueOf(SwapAction.NONE));
        map.put("SWAP_BODY_1_QUANTITY", "0");
        map.put("SWAP_BODY_2_QUANTITY", "0");
        
        mappedData.add(map);
        
        map = new HashMap<String, String>();
        map.put("TOUR_ID", String.valueOf(nbTour));
        map.put("TOUR_POSITION", "2");
        map.put("LOCATION_ID", customer.getId());
        map.put("LOCATION_TYPE", "CUSTOMER");
        map.put("SEMI_TRAILER_ATTACHED", trailers.length == 1 ? "1" : "0");
        map.put("SWAP_BODY_TRUCK", "1");
        map.put("SWAP_BODY_SEMI_TRAILER", trailers.length == 1 ? "2" : "0");
        map.put("SWAP_ACTION", String.valueOf(SwapAction.NONE));
        map.put("SWAP_BODY_1_QUANTITY", String.valueOf(customer.getOrderedQty() > parameters.getBodyCapacity() ? parameters.getBodyCapacity() : customer.getOrderedQty()));
        map.put("SWAP_BODY_2_QUANTITY", String.valueOf(customer.getOrderedQty() > parameters.getBodyCapacity() ? customer.getOrderedQty() - parameters.getBodyCapacity() : 0));
        
        mappedData.add(map);
        
        map = new HashMap<String, String>();
        map.put("TOUR_ID", String.valueOf(nbTour));
        map.put("TOUR_POSITION", "3");
        map.put("LOCATION_ID", allLocations.get(0).getId());
        map.put("LOCATION_TYPE", "DEPOT");
        map.put("SEMI_TRAILER_ATTACHED", trailers.length == 1 ? "1" : "0");
        map.put("SWAP_BODY_TRUCK", "1");
        map.put("SWAP_BODY_SEMI_TRAILER", trailers.length == 1 ? "2" : "0");
        map.put("SWAP_ACTION", String.valueOf(SwapAction.NONE));
        map.put("SWAP_BODY_1_QUANTITY", "0");
        map.put("SWAP_BODY_2_QUANTITY", "0");
        
        mappedData.add(map);
        
        nbTour++;
        
        return mappedData;
    }
    
    public double getDistanceBetweenCoord(int idCoordFrom, int idCoordTo) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameDistanceTime));
        String line = br.readLine();
        
        int i = 0;
        int j = 2 * idCoordTo;
        double distance;
        
        while ((line = br.readLine()) != null)
        {
            if (i == idCoordFrom)
            {
                String[] data = line.split(";");

                distance = Double.parseDouble(data[j]);
                
                return distance;
            }
            
            i++;
        }
        
        br.close();
        
        return 0;
    }
    
    public double getTimeBetweenCoord(int idCoordFrom, int idCoordTo) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameDistanceTime));
        String line = br.readLine();
        
        int i = 0;
        int j = 2 * idCoordTo + 1;
        double time;
        
        while ((line = br.readLine()) != null)
        {
            if (i == idCoordFrom)
            {
                String[] data = line.split(";");

                time = Double.parseDouble(data[j]);
                
                return time;
            }
            
            i++;
        }
        
        br.close();
        
        return 0;
    }
    
    public double calcTotalCost(Truck truck, Trailer[] trailers)
    {
        double total = 0;
        
        total += calcTruckCost(truck.getDistanceTravelled(), truck.getTransitTime());
        
        for (Trailer trailer : trailers) {
            total += calcTrailerCost(trailer.getDistanceTravelled(), trailer.getTransitTime());
        }
        
        return total;
    }
    
    public double calcTruckCost(double distance, double time)
    {
        return parameters.getTruckUsageCost()
                + (parameters.getTruckDistanceCost() * (distance / 100)) 
                + (parameters.getTruckTimeCost() * (time / 3600));
    }
    
    public double calcTrailerCost(double distance, double time)
    {
        return parameters.getTrailerUsageCost()
                + (parameters.getTrailerDistanceCost() * (distance / 100)) 
                + (parameters.getTrailerTimeCost() * (time / 3600));
    }
    
    public void createSolutions(List<Map<String, String>> mappedData) throws Exception
    {
        String[] titles = { 
            "TOUR_ID", 
            "TOUR_POSITION", 
            "LOCATION_ID", 
            "LOCATION_TYPE",
            "SEMI_TRAILER_ATTACHED",
            "SWAP_BODY_TRUCK",
            "SWAP_BODY_SEMI_TRAILER",
            "SWAP_ACTION",
            "SWAP_BODY_1_QUANTITY",
            "SWAP_BODY_2_QUANTITY"
        };
        
        writeSolutions(mappedData, titles);
    }
    
    public void writeSolutions(List<Map<String, String>> mappedData, String[] titles) throws Exception
    {
        FileWriter fw = new FileWriter(filePath + fileNameSolutions);
        BufferedWriter bw = new BufferedWriter(fw);
        
        boolean first = true;
        
        for (String title : titles) {
            
            if (first) {
                first = false;
            } else {
                bw.write(";");
            }
            
            write(title, bw);
        }
        
        bw.write("\n");
        
        for (Map<String, String> oneData : mappedData) {
            first = true;
            
            for (String title : titles) {
                if (first) {
                    first = false;
                } else {
                    bw.write(";");
                }
                
                final String value = oneData.get(title);
                write(value, bw);
            }
            
            bw.write("\n");    
        }
        
        bw.close();
        fw.close();
    }
    
    private void write(String value, BufferedWriter bw) throws Exception {

        if (value == null) {
            value = "";
        }

        boolean needQuote = false;

        if (value.contains("\n")) {
            needQuote = true;
        }

        if (value.contains(";")) {
            needQuote = true;
        }

        if (value.contains("\"")) {
            needQuote = true;
            value = value.replaceAll("\"", "\"\"");
        }

        if(needQuote) {
            value = "\"" + value + "\"";
        }

        bw.write(value);
    }*/
}
