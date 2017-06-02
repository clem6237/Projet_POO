/* global google */

var directionDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var infowindows;

var depots = new Array();
var swapLocations = new Array();
var customers = new Array();
var routes = new Map();

$(document).on("click", ".list-group-item", function onClickList() {
    var type = $(this).parent().attr('id');
    
    console.log("onClick list "+type);
    
    switch(type) {
        case 'camions':
            var idTour = $(this).attr("id");
            document.getElementById("selection").innerHTML = "Tourn&eacute;e " + idTour;
            
            calcItineraire(idTour);
            
            // Scroll en haut pour visualiser la map
            //window.scrollTo(0,0);
            
            break;
            
        case 'clients':
            initialize();
            
            var idCustomer = $(this).attr("id");
            document.getElementById("selection").innerHTML = "Client " + idCustomer;
            
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
    initialize();
    document.getElementById("selection").innerHTML = "";
});

function initialize() {
    var latlng;
    var i;

    directionsDisplay = new google.maps.DirectionsRenderer();
    map = new google.maps.Map(document.getElementById("maps"), {});
    infowindow = new google.maps.InfoWindow(); 
    
    /** DEPOTS **/
    for (i = 0; i < depots.length; i++) {
        
        latlng = new google.maps.LatLng(depots[i].coordY, depots[i].coordX);
        
        var marker = new google.maps.Marker({
            position: latlng,
            map: map,
            title: "D&eacute;p&ocirc;t " + depots[i].id + "<br/>" + depots[i].postalCode + " - " + depots[i].city
            //icon: ('http://maps.google.com/mapfiles/ms/icons/red-dot.png')
        });  
        
        map.setCenter(latlng);
        map.setZoom(8);
            
        google.maps.event.addListener(marker, 'click', (function(marker) {  
            return function() {  
                infowindow.setContent(marker.title);  
                infowindow.open(map, marker);  
            };
        })(marker)); 
    }
    
    /** SWAP LOCATIONS **/
    for (i = 0; i < swapLocations.length; i++) {
        
        latlng = new google.maps.LatLng(swapLocations[i].coordY, swapLocations[i].coordX);
        
        var marker = new google.maps.Marker({
            position: latlng,
            map: map,
            title: "Swap Location " + swapLocations[i].id + "<br/>" + swapLocations[i].postalCode + " - " + swapLocations[i].city,
            icon: ('http://maps.google.com/mapfiles/ms/icons/yellow-dot.png')
        });  
        
        google.maps.event.addListener(marker, 'click', (function(marker) {  
            return function() {  
                infowindow.setContent(marker.title);  
                infowindow.open(map, marker);  
            };
        })(marker)); 
    }
    
    /** CUSTOMERS **/
    for (i = 0; i < customers.length; i++) {
        
        latlng = new google.maps.LatLng(customers[i].coordY, customers[i].coordX);
        
        var marker = new google.maps.Marker({
            position: latlng,
            map: map,
            title: "Client " + customers[i].id + "<br/>" + customers[i].postalCode + " - " + customers[i].city,
            icon: ('http://maps.google.com/mapfiles/ms/icons/green-dot.png')
        });  
        
        google.maps.event.addListener(marker, 'click', (function(marker) {  
            return function() {  
                infowindow.setContent(marker.title);  
                infowindow.open(map, marker);  
            };  
        })(marker)); 
    }
    
    if (i === 0) {
        latlng = new google.maps.LatLng(50.435255, 2.823530);
        map.setCenter(latlng);
        map.setZoom(16);
    }
}

function calcItineraire(id) {
   
    map = new google.maps.Map(document.getElementById("maps"), {});
    map.setZoom(4);
    
    directionsDisplay = new google.maps.DirectionsRenderer({});
    directionsDisplay.setMap(map);
console.log(routes);
    var routeInfos = routes.get(parseInt(id));

    var start = new google.maps.LatLng(routeInfos[0].coordY, routeInfos[0].coordX);
    var end = new google.maps.LatLng(routeInfos[routeInfos.length - 1].coordY, routeInfos[routeInfos.length - 1].coordX);
    
    var waypts = [];
    
    for (var i = 0; i < routeInfos.length; i++) {   
        var lat = new google.maps.LatLng(routeInfos[i].coordY, routeInfos[i].coordX);
        waypts.push({
            location: lat,
            stopover: true});
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
}