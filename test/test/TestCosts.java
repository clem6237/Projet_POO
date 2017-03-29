/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author clementruffin
 */
public class TestCosts {
    
    final String filePath = "/Users/clementruffin/NetBeansProjects/Projet2017/";
    final String fileNameCoordinates = "dima/DistanceTimesCoordinates.csv";
    final String fileNameDistanceTime = "dima/DistanceTimesData.csv";
    final String fileNameFleet = "small_normal/Fleet.csv";
    final String fileNameLocations = "small_normal/Locations.csv";
    final String fileNameSwapActions = "small_normal/SwapActions.csv";
    
    double operatingTime;
    double truckDistanceCost;
    double truckTimeCost;
    double truckUsageCost;
    double trailerDistanceCost;
    double trailerTimeCost;
    double trailerUsageCost;
    double bodyCapacity;
    
    public static void main(String[] args) throws Exception
    {
        TestCosts test = new TestCosts();
        test.getFleetValues();
        test.scanCustomerRequests();        
    }
    
    public void getFleetValues() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameFleet));
        String line = line = br.readLine();
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            switch (data[0])
            {
                case "TRUCK" :
                    truckDistanceCost = Double.parseDouble(data[2]);
                    truckTimeCost = Double.parseDouble(data[3]);
                    truckUsageCost = Double.parseDouble(data[4]);
                    break;
                case "SEMI_TRAILER" :
                    trailerDistanceCost = Double.parseDouble(data[2]);
                    trailerTimeCost = Double.parseDouble(data[3]);
                    trailerUsageCost = Double.parseDouble(data[4]);
                    break;
                case "SWAP_BODY" :
                    bodyCapacity = Double.parseDouble(data[1]);
                    operatingTime = Double.parseDouble(data[5]);
                    break;
            }
        }
        
        br.close();
    }
    
    public void scanCustomerRequests() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameLocations));
        String line = null;
        
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(";");

            if (data[0].equals("CUSTOMER"))
            {
                processCustomerRequest(data);
            }
        }
        
        br.close();
    }
    
    public void processCustomerRequest(String[] customerRequest) throws Exception
    {
        String customerName = customerRequest[1];
        String coordX = customerRequest[4];
        String coordY = customerRequest[5];
        double orderedQty = Double.valueOf(customerRequest[6]);
        int isAccessible = Integer.valueOf(customerRequest[7]);
        double serviceTime = Double.valueOf(customerRequest[8]);
        
        // Recherche des coordonnées du client
        int idCoord = searchCoordinates(coordX, coordY);
        double[] distanceTime = getDistanceTimesDataDelivery(0, idCoord, serviceTime);
        
        // Instanciation du camion
        Truck truck = new Truck();
        truck.setDistanceTravelled(distanceTime[0]);
        truck.setTransitTime(distanceTime[1]);
        
        // Instanciation des remorques
        Trailer[] trailers = null;
        
        if (orderedQty > bodyCapacity)
        {
            if (isAccessible == 1)
            {
                trailers = new Trailer[2];
                
                Trailer trailer1 = new Trailer();
                trailer1.setDistanceTravelled(distanceTime[0]);
                trailer1.setTransitTime(distanceTime[1]);
                
                Trailer trailer2 = new Trailer();
                trailer2.setDistanceTravelled(distanceTime[0]);
                trailer2.setTransitTime(distanceTime[1]);
                
                trailers[0] = trailer1;
                trailers[1] = trailer2;
            }
            else
            {
                System.out.println(customerName + " - Livraison impossible");
                return;
            }
        }
        else
        {
            trailers = new Trailer[1];
            
            Trailer trailer = new Trailer();
            trailer.setDistanceTravelled(distanceTime[0]);
            trailer.setTransitTime(distanceTime[1]);
            
            trailers[0] = trailer;
        }
        
        // Calcul du coût total
        double coutTotal = calcTotalCost(truck, trailers);
        
        System.out.println(customerName + " - Coût = " + String.valueOf(coutTotal));
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
    
    public double[] getDistanceTimesDataDelivery(int idCoordFrom, int idCoordTo, double serviceTime) throws Exception
    {
        double[] distanceTime = new double[2];
        
        double[] distanceTimeFrom = getDistanceTimesData(idCoordFrom, idCoordTo);
        double[] distanceTimeTo = getDistanceTimesData(idCoordFrom, idCoordTo);
        
        distanceTime[0] = distanceTimeFrom[0] + distanceTimeTo[0];
        distanceTime[1] = distanceTimeFrom[1] + distanceTimeTo[1] + serviceTime;
        
        return distanceTime;
    }
    
    public double[] getDistanceTimesData(int idCoordFrom, int idCoordTo) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileNameDistanceTime));
        String line = br.readLine();
        
        int i = 0;
        int jDistance = 2 * idCoordTo;
        int jTime = 2 * idCoordTo + 1;
        double[] distanceTime = new double[2];
        
        while ((line = br.readLine()) != null)
        {
            if (i == idCoordFrom)
            {
                String[] data = line.split(";");

                distanceTime[0] = Double.parseDouble(data[jDistance]);
                distanceTime[1] = Double.parseDouble(data[jTime]);

                return distanceTime;
            }
            
            i++;
        }
        
        br.close();
        
        return distanceTime;
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
        return truckUsageCost 
                + (truckDistanceCost * (distance / 100)) 
                + (truckTimeCost * (time / 3600));
    }
    
    public double calcTrailerCost(double distance, double time)
    {
        return trailerUsageCost 
                + (trailerDistanceCost * (distance / 100)) 
                + (trailerTimeCost * (time / 3600));
    }
}
