$(document).on("click", ".list-group-item", function onClickList() {
    var type = $(this).parent().attr('id');
    
    console.log("onClick list "+type);
    
    switch(type) {
        case 'camions':
            break;
            
        case 'clients':
            var coordX = $(this).attr("coordX");
            var coordY = $(this).attr("coordY");
            var latlng = new google.maps.LatLng(coordY, coordX);
            
            // Map centré sur le client
            map.setCenter(latlng);
            map.setZoom(16);
            
            // Scroll en haut pour visualiser la map
            window.scrollTo(0,0);
            
            break;
    }
});


var directionDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var infowindows;

var depots = new Array();
var swapLocations = new Array();
var customers = new Array();
var routes = new Map();

function initialize() {
    var latlng;
    map = new google.maps.Map(document.getElementById("maps"), {});
    infowindow = new google.maps.InfoWindow(); 
    
    var i;

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
            }  
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
            }  
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
            }  
        })(marker)); 
    }
    
    if (i === 0) {
        latlng = new google.maps.LatLng(50.435255, 2.823530);
        map.setCenter(latlng);
        map.setZoom(16);
    }
    
}