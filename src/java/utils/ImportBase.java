package utils;

import controleur.Controleur;
import dao.CoordinateDao;
import dao.CustomerDao;
import dao.DaoFactory;
import dao.DistanceTimeDao;
import dao.LocationDao;
import dao.PersistenceType;
import dao.RouteDao;
import dao.RoutingParametersDao;
import dao.TourDao;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import metier.Coordinate;
import metier.Customer;
import metier.Depot;
import metier.DistanceTime;
import metier.RoutingParameters;
import metier.SwapLocation;
import org.apache.commons.fileupload.FileItem;

/**
 * Contient les méthodes permettant d'importer l'ensemble des données en base
 * (fichiers de flotte, des swap actions, des emplacements, des coordonnées).
 * @author clementruffin
 */
public class ImportBase {
    private static final int TAILLE_TAMPON = 10240;
    
    /**
     * Supprime les données en base (paramètres, tournées et emplacements).
     * @throws Exception 
     */
    public static void resetSolution() throws Exception {
        RoutingParametersDao parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        TourDao tourManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getTourDao();
        RouteDao routeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRouteDao();
        LocationDao locationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getLocationDao();
        
        parametersManager.deleteAll();
        routeManager.deleteAll();
        tourManager.deleteAll();
        locationManager.deleteAll();
        
        Utils.log("Reset OK");
    }
    
    /**
     * Importe les fichiers de paramétrage (flotte et swap actions) depuis un
     * fichier uploadé via l'interface web.
     * @param fleet
     * @param swapActions
     * @throws Exception 
     */
    public static void importParametersFromWeb(FileItem fleet, FileItem swapActions) throws Exception {
        RoutingParametersDao parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        RoutingParameters routingParameters = new RoutingParameters();
        
        try {
            // Récupération des fichiers
            InputStream contentFleet = fleet.getInputStream();
            InputStream contentSwapActions = swapActions.getInputStream();
            
            // Lecture de fichiers 
            BufferedInputStream inputFleet = new BufferedInputStream(contentFleet, TAILLE_TAMPON);
            BufferedInputStream inputSwapActions = new BufferedInputStream(contentSwapActions, TAILLE_TAMPON);
            
            // Importation
            ImportBase.importFleetFile(routingParameters, "", inputFleet);
            ImportBase.importSwapActionsFile(routingParameters, "", inputSwapActions);
            
        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Insertion de l'enregistrement dans la table de paramétrage
        parametersManager.create(routingParameters);
    }
    
    @Deprecated
    public static void importParameters(String fileNameFleet, String fileNameSwapActions) throws Exception {
        RoutingParametersDao parametersManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getRoutingParametersDao();
        parametersManager.deleteAll();
        
        RoutingParameters routingParameters = new RoutingParameters();
        
        ImportBase.importFleetFile(routingParameters, fileNameFleet, null);
        ImportBase.importSwapActionsFile(routingParameters, fileNameSwapActions, null);
        
        parametersManager.create(routingParameters);
    }

    /**
     * Importe le fichier de flotte en base.
     * @param routingParameters
     * @param fileNameFleet
     * @param input
     * @throws Exception 
     */
    public static void importFleetFile(RoutingParameters routingParameters, String fileNameFleet, BufferedInputStream input) throws Exception {
        BufferedReader br;
        
        // Fichier local ou fichier uploadé via l'interface web
        if (!fileNameFleet.equals("")) {
            br = new BufferedReader(new FileReader(fileNameFleet));
        } else {
            br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        }
        
        br.readLine();
        String line;
        
        // Parcours des lignes
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
        
        Utils.log("Import <Flotte> OK");
    }
    
    /**
     * Importe le fichier des swap actions en base.
     * @param routingParameters
     * @param fileNameSwapActions
     * @param input
     * @throws Exception 
     */
    public static void importSwapActionsFile(RoutingParameters routingParameters, String fileNameSwapActions, BufferedInputStream input) throws Exception {
        BufferedReader br;
        
        // Fichier local ou fichier uploadé via l'interface web
        if (!fileNameSwapActions.equals("")) {
            br = new BufferedReader(new FileReader(fileNameSwapActions));
        } else {
            br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        }
        
        br.readLine();
        String line;
        
        // Parcours des lignes
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
        
        Utils.log("Import <Swap Actions> OK");
    }
    
    /**
     * Importe les coordonnées et les distances/temps de parcours entre elles
     * depuis un fichier uploadé via l'interface web.
     * @param coordinates
     * @param distanceTime
     * @throws Exception 
     */
    public static void importCoordinatesFromWeb(FileItem coordinates, FileItem distanceTime) throws Exception {
        DistanceTimeDao distanceTimeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDistanceTimeDao();
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        
        // Suppression des données en base
        distanceTimeManager.deleteAll();
        coordinateManager.deleteAll();
        
        // Récupération des fichiers
        InputStream contentCoord = coordinates.getInputStream();
        InputStream contentDistTime = distanceTime.getInputStream();
        
        // Lecture des fichiers
        BufferedInputStream inputCoord = new BufferedInputStream(contentCoord, TAILLE_TAMPON);
        BufferedInputStream inputDistTime = new BufferedInputStream(contentDistTime, TAILLE_TAMPON);
            
        // Importation
        ImportBase.importCoordinates("", inputCoord);
        ImportBase.importDistanceTime("", inputDistTime);
    }
    
    /**
     * Importe les coordonnées en base.
     * @param fileName
     * @param input
     * @throws Exception 
     */
    public static void importCoordinates(String fileName, BufferedInputStream input) throws Exception {
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        
        BufferedReader br;
        
        // Fichier local ou fichier uploadé via l'interface web
        if (!fileName.equals("")) {
            br = new BufferedReader(new FileReader(fileName));
        } else {
            br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        }
        
        br.readLine();
        String line;
        
        int idCoord = 1;
        
        // Parcours des lignes
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");
            
            Coordinate c = new Coordinate(idCoord, Double.parseDouble(data[0]), Double.parseDouble(data[1]));
            coordinateManager.create(c);
            
            idCoord++;
        }
        
        br.close();
        
        Utils.log("Import <Coordonnées> OK");
    }
    
    /**
     * Importe les distances et temps de parcours entre les coordonnées en base.
     * @param fileName
     * @param input
     * @throws Exception 
     */
    public static void importDistanceTime(String fileName, BufferedInputStream input) throws Exception {
        DistanceTimeDao distanceTimeManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getDistanceTimeDao();
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        
        BufferedReader br;
        
        // Fichier local ou fichier uploadé via l'interface web
        if (!fileName.equals("")) {
            br = new BufferedReader(new FileReader(fileName));
        } else {
            br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        }
        
        br.readLine();
        String line;
        
        Collection<DistanceTime> listDistanceTime = new ArrayList();
        int idCoordFrom = 1;
        double distance, time;
        
        // Parcours des lignes
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");
            
            // Parcours des colonnes
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
        
        br.close();
        distanceTimeManager.createAll(listDistanceTime);
        
        Utils.log("Import <Distances/Temps> OK");
    }
   
    /**
     * Importe les emplacements des dépôts, swap locations et clients depuis
     * un fichier uploadé via l'interface web.
     * @param locations
     * @throws Exception 
     */
    public static void importLocationsFromWeb(FileItem locations) throws Exception {
        // Récupération du fichier
        InputStream content = locations.getInputStream();
        
        // Lecture du fichier
        BufferedInputStream input = new BufferedInputStream(content, TAILLE_TAMPON);
            
        // Importation
        ImportBase.importLocations("", input);
    }
    
    /**
     * Importe les emplacements des dépôts, swap locations et clients en base.
     * @param fileName
     * @param input
     * @throws Exception 
     */
    public static void importLocations(String fileName, BufferedInputStream input) throws Exception {
        LocationDao locationManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getLocationDao();
        CustomerDao customerManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCustomerDao();
        CoordinateDao coordinateManager = DaoFactory.getDaoFactory(PersistenceType.JPA).getCoordinateDao();
        
        BufferedReader br;
        
        // Fichier local ou fichier uploadé via l'interface web
        if (!fileName.equals("")) {
            br = new BufferedReader(new FileReader(fileName));
        } else {
            br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        }
        
        br.readLine();
        String line;
        
        // Parcours des lignes
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
        
        br.close();
        
        Utils.log("Import <Emplacements> OK");
    }
}
