<%-- 
    Document   : index
    Created on : 22 mars 2017, 11:43:01
    Author     : Anais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
        <script type="text/javascript" src="http://netdna.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
        <link href="http://code.google.com/apis/maps/documentation/javascript/examples/default.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?key=AIzaSyBBBWw6VTe0VjBqdS8DssFqVUdg2O9wORI&language=fr&sensor=false"></script>
        <link href="http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.3.0/css/font-awesome.min.css"
        rel="stylesheet" type="text/css">
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        
        <link href="CSS/style.css" rel="stylesheet" type="text/css">
        <script src="JS/index.js"></script>
        <script type="text/javascript">
            google.charts.load('current', {'packages':['corechart']});
            //google.charts.setOnLoadCallback(drawChart);
        </script>
        <title>SB-VRP</title>
    </head>
  
    <body onload="initialize()">
        <jsp:useBean id="depots" class="metier.Depot" />
        <jsp:useBean id="swapLocations" class="metier.SwapLocation" />
        <jsp:useBean id="customers" class="metier.Customer" />
        <jsp:useBean id="tours" class="metier.Tour" />
        <jsp:useBean id="route" class="metier.Route" />
        <jsp:useBean id="parameters" class="metier.RoutingParameters" />
        
        <div class="navbar navbar-default navbar-static-top">
            <div class="container">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#"><span>SB-VRP</span></a>
                </div>
                <div class="collapse navbar-collapse" id="navbar-ex-collapse">
                    <ul class="nav navbar-nav">
                        <li>
                            <a href="calcul.jsp">Nouveau Calcul</a>
                        </li>
                        <li>
                            <a href="instance.jsp">Voir l'instance</a>
                        </li>
                        <li>
                            <a href="Controleur?action=export">Exporter en CSV</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="section">
            <div class="container">
                <div class="row">
                    <h1 class="text-primary">Dernière Solution</h1>
                    <script>
                        <c:forEach items="${depots.allDepots()}" var="depot">
                            var depotDetail = new Object();
                            depotDetail.id = '${depot.id}';
                            depotDetail.postalCode = ${depot.postalCode};
                            depotDetail.city = '${depot.city}';
                            depotDetail.coordX = ${depot.coordinate.coordX};
                            depotDetail.coordY = ${depot.coordinate.coordY};
                            
                            depots.push(depotDetail);
                        </c:forEach>
                        <c:forEach items="${swapLocations.allSwapLocations()}" var="swapLocation">
                            var swapLocationDetail = new Object();
                            swapLocationDetail.id = '${swapLocation.id}';
                            swapLocationDetail.postalCode = ${swapLocation.postalCode};
                            swapLocationDetail.city = '${swapLocation.city}';
                            swapLocationDetail.coordX = ${swapLocation.coordinate.coordX};
                            swapLocationDetail.coordY = ${swapLocation.coordinate.coordY};
                            
                            swapLocations.push(swapLocationDetail);
                        </c:forEach>
                        <c:forEach items="${customers.allCustomers()}" var="customer">
                            var customerDetail = new Object();
                            customerDetail.id = '${customer.id}';
                            customerDetail.postalCode = ${customer.postalCode};
                            customerDetail.city = '${customer.city}';
                            customerDetail.coordX = ${customer.coordinate.coordX};
                            customerDetail.coordY = ${customer.coordinate.coordY};
                            
                            customers.push(customerDetail);
                        </c:forEach>
                        <c:forEach items="${tours.allTours()}" var="tour">
                            var tourDetail = new Object();
                            tourDetail.id = ${tour.id};
                            tourDetail.quantity = ${tour.getTourQuantity()};
                            tourDetail.time = ${tour.getTourTimeFromBase()};
                            tourDetail.totalCost = ${tour.getTotalCost()};
                            
                            tourDetail.truckUsageCost = ${tour.getTruckUsageCost()};
                            tourDetail.truckDistanceCost = ${tour.getTruckDistanceCost()};
                            tourDetail.truckTimeCost = ${tour.getTruckTimeCost()};
                            
                            tourDetail.trailersUsageCost = ${tour.getTrailerUsageCost()};
                            tourDetail.trailerDistanceCost = ${tour.getTrailerDistanceCost()};
                               
                            var tourMap = tours.get(tourDetail.id);
                            if (!tourMap) tourMap = new Array();
                            tourMap.push(tourDetail);
                            
                            tours.set(tourDetail.id, tourMap);
                        </c:forEach>
                        <c:forEach items="${route.allRoutes()}" var="route">
                            var routeDetail = new Object();
                            routeDetail.tour = ${route.tour.id};
                            routeDetail.position = ${route.position};
                            routeDetail.coordX = ${route.location.coordinate.coordX};
                            routeDetail.coordY = ${route.location.coordinate.coordY};
                            routeDetail.locationType = '${route.locationType}';
                            routeDetail.locationId = '${route.location.id}';
                            routeDetail.postalCode = ${route.location.postalCode};
                            routeDetail.city = '${route.location.city}';
                            routeDetail.trailer = ${route.trailerAttached};

                            var routeMap = routes.get(routeDetail.tour);
                            if (!routeMap) routeMap = new Array();
                            routeMap.push(routeDetail);

                            routes.set(routeDetail.tour, routeMap);
                        </c:forEach>
                            
                    </script>

                    <input type="button" id="refreshMap" value="Rafraîchir la carte" style="margin-left: 20px;"/>
                    <h2 id="selection" style="text-align: center;"></h2>
                    <div id="maps" class="mapLarge"></div>
                    <div id="tourInfos" class="tourInfosHide">
                        <script>
                            tourCapacity = ${parameters.find().bodyCapacity};
                            tourOperatingTime = ${parameters.find().operatingTime / 60};
                        </script>
                        <h3>Remplissage du camion</h3>
                        <p id="tourFilling"></p>
                        <div id="progressBar">
                            <div id="tourFillingProgress"></div>
                        </div>
                        <hr/>
                        <h3>Temps de parcours</h3>
                        <p id="tourTransitTime"></p>
                        <div id="progressBar">
                            <div id="tourTransitTimeProgress"></div>
                        </div>
                        <hr/>
                        <h3>Coût total</h3>
                        <p id="tourTotalCost"></p>
                        <div id="pieChart" style="width: 100%; height: 300px;"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="section">
            <div class="container">
                <div class="row">
                    <div class="col-md-6">
                        <h3>Camions (${tours.allTours().size()})</h3>
                        <ul id="camions" class="list-group">
                            <c:forEach items="${tours.allTours()}" var="tour">
                                <li class="list-group-item" id="${tour.id}">
                                    Tournée ${tour.id}
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h3>Clients (${customers.allCustomers().size()})</h3>
                        <ul id="clients" class="list-group">
                            <c:forEach items="${customers.allCustomers()}" var="customer">
                                <li class="list-group-item" id="${customer.id}" coordX="${customer.coordinate.coordX}" coordY="${customer.coordinate.coordY}">
                                    ${customer.id} - ${customer.postalCode} ${customer.city}
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
