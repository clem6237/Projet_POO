/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import test.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import metier.Customer;
import metier.Depot;
import metier.Location;
import metier.Parameters;
import metier.SwapAction;
import metier.SwapLocation;

/**
 *
 * @author clementruffin
 */
public class Version1 {
    
    final String filePath = "/Users/clementruffin/NetBeansProjects/Projet2017/";
    final String fileNameCoordinates = "dima/DistanceTimesCoordinates.csv";
    final String fileNameDistanceTime = "dima/DistanceTimesData.csv";
    final String fileNameFleet = "small_normal/Fleet.csv";
    final String fileNameLocations = "small_normal/Locations.csv";
    final String fileNameSwapActions = "small_normal/SwapActions.csv";
    final String fileNameSolutions = "small_normal/Solutions.csv";
    
    Parameters parameters;
    List<Location> allLocations;
    List<Depot> allDepots;
    List<SwapLocation> allSwapLocations;
    List<Customer> allCustomers;
    
    int nbTour = 1;
    
    public static void main(String[] args) throws Exception
    {
        Version1 test = new Version1();
        
        // Enregistrer les paramètres
        test.importParameters();
        
        // Enregistrer les locations (avec idCoord)
        test.importLocations();
        
        // Algorithme de création des tournées
        test.scanCustomerRequests();
    }
    
    public void importParameters() throws Exception
    {
        parameters = new Parameters();
        this.importFleetFile();
        this.importSwapActionsFile();
    }
    
    public void importFleetFile() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameFleet));
        String line = line = br.readLine();
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            switch (data[0])
            {
                case "TRUCK" :
                    parameters.setTruckDistanceCost(Double.parseDouble(data[2]));
                    parameters.setTruckTimeCost(Double.parseDouble(data[3]));
                    parameters.setTruckUsageCost(Double.parseDouble(data[4]));
                    break;
                case "SEMI_TRAILER" :
                    parameters.setTrailerDistanceCost(Double.parseDouble(data[2]));
                    parameters.setTrailerTimeCost(Double.parseDouble(data[3]));
                    parameters.setTrailerUsageCost(Double.parseDouble(data[4]));
                    break;
                case "SWAP_BODY" :
                    parameters.setBodyCapacity(Double.parseDouble(data[1]));
                    parameters.setOperatingTime(Double.parseDouble(data[5]));
                    break;
            }
        }
        
        br.close();
    }
    
    public void importSwapActionsFile() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameSwapActions));
        String line = line = br.readLine();
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            switch (data[0])
            {
                case "PARK" :
                    parameters.setParkTime(Double.parseDouble(data[1]));
                    break;
                case "SWAP" :
                    parameters.setSwapTime(Double.parseDouble(data[1]));
                    break;
                case "EXCHANGE" :
                    parameters.setExchangeTime(Double.parseDouble(data[1]));
                    break;
                case "PICKUP" :
                    parameters.setPickupTime(Double.parseDouble(data[1]));
                    break;
            }
        }
        
        br.close();
    }
    
    public void importLocations() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameLocations));
        String line = line = br.readLine();
        
        allLocations = new ArrayList<Location>();
        allDepots = new ArrayList<Depot>();
        allSwapLocations = new ArrayList<SwapLocation>();
        allCustomers = new ArrayList<Customer>();
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            switch (data[0])
            {
                case "DEPOT" :
                    Depot depot = new Depot();
                    depot.setId(data[1]);
                    depot.setPostalCode(data[2]);
                    depot.setCity(data[3]);
                    depot.setIdCoordinate(this.searchCoordinates(data[4], data[5]));
                    allDepots.add(depot);
                    break;
                case "SWAP_LOCATION" :
                    SwapLocation swapLocation = new SwapLocation();
                    swapLocation.setId(data[1]);
                    swapLocation.setPostalCode(data[2]);
                    swapLocation.setCity(data[3]);
                    swapLocation.setIdCoordinate(this.searchCoordinates(data[4], data[5]));
                    allSwapLocations.add(swapLocation);
                    break;
                case "CUSTOMER" :
                    Customer customer = new Customer();
                    customer.setId(data[1]);
                    customer.setPostalCode(data[2]);
                    customer.setCity(data[3]);
                    customer.setIdCoordinate(this.searchCoordinates(data[4], data[5]));
                    customer.setOrderedQty(Double.valueOf(data[6]));
                    customer.setAccessible(data[7].equals("1"));
                    customer.setServiceTime(Double.valueOf(data[8]));
                    allCustomers.add(customer);
                    break;
            }
        }
        
        br.close();
        
        allLocations.addAll(allDepots);
        allLocations.addAll(allSwapLocations);
        allLocations.addAll(allCustomers);
    }
    
    public int searchCoordinates(String coordX, String coordY) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameCoordinates));
        String line = br.readLine();
        int idCoord = 0;
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            if (data[0].equals(coordX) && data[1].equals(coordY))
            {
                return idCoord;
            }
            
            idCoord++;
        }
        
        br.close();
        
        return -1;
    }
    
    public void scanCustomerRequests() throws Exception
    {
        List<Map<String, String>> mappedData = new ArrayList();
        
        for (Customer customer : allCustomers)
        {
            mappedData.addAll(this.processCustomerRequest(customer));
        }
        
        this.createSolutions(mappedData);
    }
    
    public List<Map<String, String>> processCustomerRequest(Customer customer) throws Exception
    {
        List<Map<String, String>> mappedData = new ArrayList();
        Map<String, String> map = new HashMap();
        
        double distanceTotal = this.getDistanceBewteenCoord(0, customer.getIdCoordinate()) 
                + this.getDistanceBewteenCoord(customer.getIdCoordinate(), 0);
        
        double timeTotal = customer.getServiceTime()
                + this.getTimeBewteenCoord(0, customer.getIdCoordinate())
                + this.getTimeBewteenCoord(customer.getIdCoordinate(), 0);
        
        Truck truck = new Truck();
        truck.setDistanceTravelled(distanceTotal);
        truck.setTransitTime(timeTotal);
        
        Trailer[] trailers = null;
        
        if (customer.getOrderedQty() > parameters.getBodyCapacity())
        {
            if (!customer.isAccessible())
            {
                System.out.println(customer.getId() + " - Livraison impossible");
                return null;
            }
            else
            {
                trailers = new Trailer[2];
                
                Trailer trailer1 = new Trailer();
                trailer1.setDistanceTravelled(distanceTotal);
                trailer1.setTransitTime(timeTotal);
                
                Trailer trailer2 = new Trailer();
                trailer2.setDistanceTravelled(distanceTotal);
                trailer2.setTransitTime(timeTotal);
                
                trailers[0] = trailer1;
                trailers[1] = trailer2;
            }
        }
        else
        {
            trailers = new Trailer[1];
            
            Trailer trailer = new Trailer();
            trailer.setDistanceTravelled(distanceTotal);
            trailer.setTransitTime(timeTotal);
            
            trailers[0] = trailer;
        }
        
        double coutTotal = calcTotalCost(truck, trailers);
        
        /*Tour tour = new Tour(nbTour++);
        
        Route route1 = new Route();
        route1.setIdTour(tour.getId());
        route1.setPosition(1);
        route1.setTrailerAttached(trailers.length == 2);
        route1.setFirstTrailer(1);
        route1.setLastTrailer(trailers.length == 2 ? 2 : 0);
        route1.setSwapAction(SwapAction.NONE);
        route1.setQty1(customer.getOrderedQty() > parameters.getBodyCapacity() ? parameters.getBodyCapacity() : customer.getOrderedQty());
        route1.setQty2(customer.getOrderedQty() > parameters.getBodyCapacity() ? customer.getOrderedQty() - parameters.getBodyCapacity() : 0);
        
        Route route2 = new Route();
        route2.setIdTour(tour.getId());
        route2.setPosition(2);
        route2.setTrailerAttached(trailers.length == 2);
        route2.setFirstTrailer(1);
        route2.setLastTrailer(trailers.length == 2 ? 2 : 0);
        route2.setSwapAction(SwapAction.NONE);
        route2.setQty1(0);
        route2.setQty2(0);*/
        
        //System.out.println(customer.getName() + " - Coût = " + String.valueOf(coutTotal));
        
        map = new HashMap<String, String>();
        map.put("TOUR_ID", String.valueOf(nbTour));
        map.put("TOUR_POSITION", "1");
        map.put("LOCATION_ID", allLocations.get(0).getId());
        map.put("LOCATION_TYPE", "DEPOT");
        map.put("SEMI_TRAILER_ATTACHED", trailers.length == 2 ? "1" : "0");
        map.put("SWAP_BODY_TRUCK", "1");
        map.put("SWAP_BODY_SEMI_TRAILER", trailers.length == 2 ? "2" : "0");
        map.put("SWAP_ACTION", String.valueOf(SwapAction.NONE));
        map.put("SWAP_BODY_1_QUANTITY", "0");
        map.put("SWAP_BODY_2_QUANTITY", "0");
        
        mappedData.add(map);
        
        map = new HashMap<String, String>();
        map.put("TOUR_ID", String.valueOf(nbTour));
        map.put("TOUR_POSITION", "2");
        map.put("LOCATION_ID", customer.getId());
        map.put("LOCATION_TYPE", "CUSTOMER");
        map.put("SEMI_TRAILER_ATTACHED", trailers.length == 2 ? "1" : "0");
        map.put("SWAP_BODY_TRUCK", "1");
        map.put("SWAP_BODY_SEMI_TRAILER", trailers.length == 2 ? "2" : "0");
        map.put("SWAP_ACTION", String.valueOf(SwapAction.NONE));
        map.put("SWAP_BODY_1_QUANTITY", String.valueOf(customer.getOrderedQty() > parameters.getBodyCapacity() ? parameters.getBodyCapacity() : customer.getOrderedQty()));
        map.put("SWAP_BODY_2_QUANTITY", String.valueOf(customer.getOrderedQty() > parameters.getBodyCapacity() ? customer.getOrderedQty() - parameters.getBodyCapacity() : 0));
        
        mappedData.add(map);
        
        map = new HashMap<String, String>();
        map.put("TOUR_ID", String.valueOf(nbTour));
        map.put("TOUR_POSITION", "3");
        map.put("LOCATION_ID", allLocations.get(0).getId());
        map.put("LOCATION_TYPE", "DEPOT");
        map.put("SEMI_TRAILER_ATTACHED", trailers.length == 2 ? "1" : "0");
        map.put("SWAP_BODY_TRUCK", "1");
        map.put("SWAP_BODY_SEMI_TRAILER", trailers.length == 2 ? "2" : "0");
        map.put("SWAP_ACTION", String.valueOf(SwapAction.NONE));
        map.put("SWAP_BODY_1_QUANTITY", "0");
        map.put("SWAP_BODY_2_QUANTITY", "0");
        
        mappedData.add(map);
        
        nbTour++;
        
        return mappedData;
    }
    
    public double getDistanceBewteenCoord(int idCoordFrom, int idCoordTo) throws Exception
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
    
    public double getTimeBewteenCoord(int idCoordFrom, int idCoordTo) throws Exception
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
    }
}
