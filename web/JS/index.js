$(document).on("click", ".list-group-item", function onClickList() {
    var type = $(this).parent().attr('id');
    console.log("onClick list "+type);
    switch(type) {
        case 'camions':
            break;
        case 'clients':
            break;
    }
});


var directionDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var addrs = new Array();

function initialize() {
    var latlng = new google.maps.LatLng(46.779231, 6.659431);
    var myOptions = {
      zoom: 4,
      center: latlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map(document.getElementById("maps"),myOptions);

    var marker = new google.maps.Marker({
      position: latlng,
      map: map,
      title:"my hometown, Malim Nawar!"
    });
 }