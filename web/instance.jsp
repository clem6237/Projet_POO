<%-- 
    Document   : instance
    Created on : 12 juin 2017, 13:24:52
    Author     : Clément
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
    <script type="text/javascript" src="http://netdna.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <link href="http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.3.0/css/font-awesome.min.css"
    rel="stylesheet" type="text/css">
    <link href="CSS/style.css" rel="stylesheet" type="text/css">
    <title>SB-VRP</title>
  </head>
  <body>
    <div class="navbar navbar-default navbar-static-top">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-brand" href="index.jsp"><span>SB-VRP</span></a>
        </div>
        <div class="collapse navbar-collapse" id="navbar-ex-collapse">
          <ul class="nav navbar-nav">
            <li>
              <a href="Controleur?action=previous&vue=2">Nouveau Calcul</a>
            </li>   
            <li class="active">
                <a href="instance.jsp">Voir l'instance</a>
            </li>
            <li>
              <a href="Controleur?action=export">Exporter en CSV</a>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="container">
        <jsp:useBean id="parameters" class="metier.RoutingParameters" />
        <jsp:useBean id="depots" class="metier.Depot" />
        <jsp:useBean id="swapLocations" class="metier.SwapLocation" />
        <jsp:useBean id="customers" class="metier.Customer" />
        
        <h1 class="text-primary">Instance courante</h1>
        
        <h3 class="text-primary">Flotte</h3>
        <table class="table table-hover">
            <thead>
                <th>Type</th>
                <th>Capacité</th>
                <th>Coûts d'utilisation (en MU/usage)</th>
                <th>Coûts kilométrique (en MU/km)</th>
                <th>Coûts horaire (en MU/h)</th>
                <th>Temps d'opération (en sec)</th>
            </thead>
            <tbody>
                <tr>
                    <td>CAMION</td>
                    <td></td>
                    <td>${parameters.find().truckUsageCost}</td>
                    <td>${parameters.find().truckDistanceCost}</td>
                    <td>${parameters.find().truckTimeCost}</td>
                    <td>${parameters.find().operatingTime}</td>
                </tr>
                <tr>
                    <td>REMORQUE</td>
                    <td></td>
                    <td>${parameters.find().trailerUsageCost}</td>
                    <td>${parameters.find().trailerDistanceCost}</td>
                    <td>${parameters.find().trailerTimeCost}</td>
                    <td>${parameters.find().operatingTime}</td>
                </tr>
                <tr>
                    <td>SWAP BODY</td>
                    <td>${parameters.find().bodyCapacity}</td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td>${parameters.find().operatingTime}</td>
                </tr>
            </tbody>
        </table>
        
        <h3 class="text-primary">Swap Actions</h3>
        <table class="table table-hover">
            <thead>
                <th>Action</th>
                <th>Temps (en sec)</th>
            </thead>
            <tbody>
                <tr>
                    <td>PARK</td>
                    <td>${parameters.find().parkTime}</td>
                </tr>
                <tr>
                    <td>SWAP</td>
                    <td>${parameters.find().swapTime}</td>
                </tr>
                <tr>
                    <td>EXCHANGE</td>
                    <td>${parameters.find().exchangeTime}</td>
                </tr>
                <tr>
                    <td>PICKUP</td>
                    <td>${parameters.find().pickupTime}</td>
                </tr>
            </tbody>
        </table>
                
        <h3 class="text-primary">Emplacements</h3>
        <table class="table table-hover">
            <thead>
                <th>Type</th>
                <th>ID</th>
                <th>Code Postal</th>
                <th>Ville</th>
                <th>Coordonnée X</th>
                <th>Coordonnée Y</th>
                <th>Quantité commandée</th>
                <th>Accessible</th>
                <th>Temps de service (en sec)</th>
            </thead>
            <tbody>
                <c:forEach var="depot" items="${depots.allDepots()}">
                    <tr class="danger">
                        <td>DÉPÔT</td>
                        <td>${depot.id}</td>
                        <td>${depot.postalCode}</td>
                        <td>${depot.city}</td>
                        <td>${depot.coordinate.coordX}</td>
                        <td>${depot.coordinate.coordY}</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                </c:forEach>
                <c:forEach var="swapLocation" items="${swapLocations.allSwapLocations()}">
                    <tr class="warning">
                        <td>SWAP LOCATION</td>
                        <td>${swapLocation.id}</td>
                        <td>${swapLocation.postalCode}</td>
                        <td>${swapLocation.city}</td>
                        <td>${swapLocation.coordinate.coordX}</td>
                        <td>${swapLocation.coordinate.coordY}</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                </c:forEach>
                <c:forEach var="customer" items="${customers.allCustomers()}">
                    <tr class="success">
                        <td>CLIENT</td>
                        <td>${customer.id}</td>
                        <td>${customer.postalCode}</td>
                        <td>${customer.city}</td>
                        <td>${customer.coordinate.coordX}</td>
                        <td>${customer.coordinate.coordY}</td>
                        <td>${customer.orderedQty}</td>
                        <td>${customer.accessible}</td>
                        <td>${customer.serviceTime}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
  </body>
</html>