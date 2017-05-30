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
            
            map.setCenter(latlng);
            map.setZoom(16);
            
            break;
    }
});


var directionDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var addrs = new Array();
var locations = new Array();
var infowindows;

function initialize() {
    var latlng;// = new google.maps.LatLng(locations[0].coordY, locations[0].coordX);
    
    map = new google.maps.Map(document.getElementById("maps"), {});
    infowindow = new google.maps.InfoWindow(); 
    
    var i;
    
    for (i = 0; i < locations.length; i++) {
        
        latlng = new google.maps.LatLng(locations[i].coordY, locations[i].coordX);
        
        var marker = new google.maps.Marker({
            position: latlng,
            map: map,
            title: locations[i].id + "<br/>" + locations[i].postalCode + " - " + locations[i].city
        });  
        
        if (locations[i].id.indexOf('D') > -1) {
            map.setCenter(latlng);
            map.setZoom(8);
            
            marker.setIcon('http://maps.google.com/mapfiles/ms/icons/red-dot.png')
        } else if (locations[i].id.indexOf('S') > -1) {
            marker.setIcon('http://maps.google.com/mapfiles/ms/icons/yellow-dot.png')
        } else if (locations[i].id.indexOf('C') > -1) {
            marker.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png')
        }
        
        google.maps.event.addListener(marker, 'click', (function(marker) {  
           return function() {  
               infowindow.setContent(marker.title);  
               infowindow.open(map, marker);  
           }  
         })(marker)); 
    }
}