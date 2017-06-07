/* global google */

var directionDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var infowindows;

var depots = new Array();
var swapLocations = new Array();
var customers = new Array();
var tours = new Map();
var routes = new Map();

var tourCapacity;
var tourOperatingTime;

$(document).on("click", ".list-group-item", function onClickList() {
    var type = $(this).parent().attr('id');
    
    switch(type) {
        case 'camions':
            var idTour = $(this).attr("id");
            document.getElementById("selection").innerHTML = "Tourn&eacute;e " + idTour;
            document.getElementById("maps").className = "mapSlim";
            document.getElementById("tourInfos").className = "tourInfosShow";
            
            calcItineraire(idTour);
            drawChart(idTour);
            
            break;
            
        case 'clients':
            var idCustomer = $(this).attr("id");
            document.getElementById("selection").innerHTML = "Client " + idCustomer;
            document.getElementById("maps").className = "mapLarge";
            document.getElementById("tourInfos").className = "tourInfosHide";
            
            initialize();
            
            var coordX = $(this).attr("coordX");
            var coordY = $(this).attr("coordY");
            var latlng = new google.maps.LatLng(coordY, coordX);
            
            // Map centré sur le client
            map.setCenter(latlng);
            map.setZoom(16);
            
            // Scroll en haut pour visualiser la map
            //window.scrollTo(0,0);
            
            break;
    }
});

$(document).on("click", "#refreshMap", function onClickList() {
    document.getElementById("selection").innerHTML = "";
    
    document.getElementById("maps").className = "mapLarge";
    document.getElementById("tourInfos").className = "tourInfosHide";
    
    initialize();
});

function initialize() {
    var latlng;
    var title;
    var i;

    directionsDisplay = new google.maps.DirectionsRenderer();
    map = new google.maps.Map(document.getElementById("maps"), {});
    infowindow = new google.maps.InfoWindow(); 
    
    /** DEPOTS **/
    for (i = 0; i < depots.length; i++) {
        
        latlng = new google.maps.LatLng(depots[i].coordY, depots[i].coordX);
        title = "D&eacute;p&ocirc;t " + depots[i].id + "<br/>" + depots[i].postalCode + " - " + depots[i].city;
        
        createMarker(map, latlng, title, '');
        
        map.setCenter(latlng);
        map.setZoom(8);
    }
    
    /** SWAP LOCATIONS **/
    for (i = 0; i < swapLocations.length; i++) {
        
        latlng = new google.maps.LatLng(swapLocations[i].coordY, swapLocations[i].coordX);
        title = "Swap Location " + swapLocations[i].id + "<br/>" + swapLocations[i].postalCode + " - " + swapLocations[i].city;
        
        createMarker(map, latlng, title, 'http://maps.google.com/mapfiles/ms/icons/yellow-dot.png');
    }
    
    /** CUSTOMERS **/
    for (i = 0; i < customers.length; i++) {
        
        latlng = new google.maps.LatLng(customers[i].coordY, customers[i].coordX);
        title = "Client " + customers[i].id + "<br/>" + customers[i].postalCode + " - " + customers[i].city;
        
        createMarker(map, latlng, title, 'http://maps.google.com/mapfiles/ms/icons/green-dot.png');
    }
    
    if (i === 0) {
        latlng = new google.maps.LatLng(50.435255, 2.823530);
        map.setCenter(latlng);
        map.setZoom(16);
    }
}

function calcItineraire(id) {
   console.log("calcItineraire");
    map = new google.maps.Map(document.getElementById("maps"), {});
    map.setZoom(4);
    
    directionsDisplay = new google.maps.DirectionsRenderer({ suppressMarkers: true });
    directionsDisplay.setMap(map);

    var routeInfos = routes.get(parseInt(id));

    var start = new google.maps.LatLng(routeInfos[0].coordY, routeInfos[0].coordX);
    var end = new google.maps.LatLng(routeInfos[routeInfos.length - 1].coordY, routeInfos[routeInfos.length - 1].coordX);
    
    var waypts = [];
    
    for (var i = 0; i < routeInfos.length; i++) {   
        var lat = new google.maps.LatLng(routeInfos[i].coordY, routeInfos[i].coordX);
        waypts.push({
            location: lat,
            stopover: true
        });
    }
     
    var request = {
        origin: start, 
        destination: end,
        waypoints: waypts,
        optimizeWaypoints: true,
        travelMode: google.maps.DirectionsTravelMode.DRIVING
    };
    
    directionsService.route(request, function(response, status) {
      if (status === google.maps.DirectionsStatus.OK) {
        directionsDisplay.setDirections(response);
      }
    });
    
    var title;
    var icon;
    var location;
    
    /* POINT DE DÉPART */
    location = routeInfos[routeInfos.length - 1];
    title = "D&eacute;p&ocirc;t " + location.locationId + "<br/>" + location.postalCode + " - " + location.city;
    createMarker(map, end, title, 'http://maps.google.com/mapfiles/ms/icons/red-dot.png');
    
    /* POINT D'ARRIVÉE */
    location = routeInfos[0];
    title = "D&eacute;p&ocirc;t " + location.locationId + "<br/>" + location.postalCode + " - " + location.city;
    createMarker(map, start, title, 'http://maps.google.com/mapfiles/ms/icons/red-dot.png');
    
    /* ÉTAPES */
    for (var i = 1; i < waypts.length - 1; i++) {
        
        location = routeInfos[i];
        
        if (location.locationType === "DEPOT") {
            title = "D&eacute;p&ocirc;t " + location.locationId + "<br/>" + location.postalCode + " - " + location.city;
            icon = 'http://maps.google.com/mapfiles/ms/icons/red-dot.png';
        } else if (location.locationType === "SWAP_LOCATION") {
            title = "Swap Location " + location.locationId + "<br/>" + location.postalCode + " - " + location.city;
            icon = 'http://maps.google.com/mapfiles/ms/icons/yellow-dot.png';
        } else if (location.locationType === "CUSTOMER") {
            title = "Client " + location.locationId + "<br/>" + location.postalCode + " - " + location.city;
            icon = 'http://maps.google.com/mapfiles/ms/icons/green-dot.png';
        }
        
        createMarker(map, waypts[i].location, title, icon);
        
    }

    var tour = tours.get(parseInt(id));
    var tourQuantity = tour[0].quantity;
    var tourTime = tour[0].time / 60;
    var tourTotalCost = tour[0].totalCost;
    
    console.log(tour);
    
    document.getElementById("tourFilling").innerHTML = tourQuantity + " / " + (routeInfos[0].trailer ? (2 * tourCapacity + " unités (mode train)") : (tourCapacity + " unités (mode camion)"));
    document.getElementById("tourTransitTime").innerHTML = tourTime.toFixed(2) + " / " + tourOperatingTime.toFixed(2) + " minutes";
    document.getElementById("tourTotalCost").innerHTML = tourTotalCost.toFixed(2) + " €";
    
    moveProgressBar(
            "tourFillingProgress", 
            tourQuantity / (routeInfos[0].trailer ? 2 * tourCapacity : tourCapacity) * 100);
            
    moveProgressBar(
            "tourTransitTimeProgress", 
            tourTime / tourOperatingTime * 100);
}

function createMarker(map, latlng, label, icon) {
    var marker = new google.maps.Marker({
        position: latlng,
        map: map,
        icon: icon,
        title: label,
        zIndex: Math.round(latlng.lat() * -100000) << 5
    });

    google.maps.event.addListener(marker, 'click', function() {
        infowindow.setContent(marker.title);
        infowindow.open(map, marker);
    });
}

function moveProgressBar(barName, value) {

    var elem = document.getElementById(barName); 
    var width = value > 100 ? 100 : value;
    
    elem.style.width = width + '%';
    elem.innerHTML = value.toFixed(2) + ' %';
    
    if (value > 100) {
        elem.style.backgroundColor = "darkred";
    } else if (value < 50) {
        elem.style.backgroundColor = "darkorange";
    } else {
        elem.style.backgroundColor = "green";
    }
}

function drawChart(id) {
    var tour = tours.get(parseInt(id));
    
    var data = google.visualization.arrayToDataTable([
        ['Coût', 'Euros'],
        ['Camion - Utilisation', tour[0].truck.usageCost],
        ['Camion - Coût horaire', tour[0].truck.timeCost],
        ['Camion - Coût kilométrique', tour[0].truck.distanceCost],
        ['Remorques - Utilisation', tour[0].trailers.usageCost],
        ['Remorques - Coût kilométrique', tour[0].trailers.firstTrailerCost + tour[0].trailers.lastTrailerCost]
    ]);
    
    var options = {
        chartArea: {
            width: '100%', 
            height: '60%',
            top: 0
        },
        legend: 'none'
    };
    
    var chart = new google.visualization.PieChart(document.getElementById("pieChart"));
    chart.draw(data, options);
}