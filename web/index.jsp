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
        <link href="CSS/style.css" rel="stylesheet" type="text/css">
        <script src="JS/index.js"></script>
        <title>SB-VRP</title>
    </head>
  
    <body onload="initialize()">
        <jsp:useBean id="locations" class="metier.Location" />
        <jsp:useBean id="routes" class="metier.Route" />
        <jsp:useBean id="customers" class="metier.Customer" />
    
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
                    <div id="locations" value="${locations.allLocations()}"></div>
                    
                    <script>
                        <c:forEach items="${locations.allLocations()}" var="location">
                            var locationDetail = new Object();
                            locationDetail.id = '${location.id}';
                            locationDetail.postalCode = ${location.postalCode};
                            locationDetail.city = '${location.city}';
                            locationDetail.coordX = ${location.coordinate.coordX};
                            locationDetail.coordY = ${location.coordinate.coordY};
                            
                            locations.push(locationDetail);
                        </c:forEach>
                    </script>
                    
                    <div id="maps" style="float:left;width:100%;height:70%;"></div>
                </div>
            </div>
        </div>
        <div class="section">
            <div class="container">
                <div class="row">
                    <div class="col-md-6">
                        <h3>Camions</h3>
                        <ul id="camions" class="list-group">
                            <c:forEach items="${routes.allRoutes()}" var="route">
                                <li class="list-group-item" id="$(route.id)">${route.id}</li>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h3>Clients</h3>
                        <ul id="clients" class="list-group">
                            <c:forEach items="${customers.allCustomers()}" var="customer">
                                <li class="list-group-item" id="$(customer.id)" coordX="${customer.coordinate.coordX}" coordY="${customer.coordinate.coordY}">${customer.id} - ${customer.postalCode} ${customer.city}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
