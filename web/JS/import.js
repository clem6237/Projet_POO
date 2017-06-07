document.addEventListener('mousemove', fn, false);

function fn(e) {
    $("#helpBox").css("left", e.pageX + 'px');
    $("#helpBox").css("top", e.pageY + 'px');
}

function showHelp(file) {

    $("#helpBox").removeClass("helpBoxHide");
    $("#helpBox").addClass("helpBoxShow");

    var text = "";

    switch (file) {
        case 'coordinates' :
            text = "<< DistanceTimeCoordinates >> <br/>";
            text += "Ce fichier contient l'ensemble des coordonn&eacute;es (X,Y) utiles. <br/>";
            text += "Format : <br/>";
            text += "&nbsp;&nbsp;&nbsp;&nbsp; X_COORD | Y_COORD";
            break;

        case 'distances' :
            text = "<< DistanceTimeData >> <br/>";
            text += "Ce fichier contient un mapping de l'ensemble des distances et temps de parcours entre les coordonn&eacute;es. <br/>";
            text += "Format : <br/>";
            text += "&nbsp;&nbsp;&nbsp;&nbsp; D_00 | T_00 | D_01 | T_01 | ... <br/>";
            text += "&nbsp;&nbsp;&nbsp;&nbsp; D_10 | T_10 | D_11 | T_11 | ... ";
            break;
            
        case 'fleet' :
            text = "<< Fleet >> <br/>";
            text += "Ce fichier contient les co&ucirc;ts d'utilisation, co&ucirc;ts horaires et kilom&eacute;triques pour les camions et les remorques. <br/>";
            text += "Il contient &eacute;galement la capacit&eacute; des swap bodies et le temps d'op&eacute;ration maximum. <br/>";
            text += "Format : <br/>";
            text += "&nbsp;&nbsp;&nbsp;&nbsp; TYPE | CAPACITY | COSTS [MU/km] | COSTS [MU/h] | COSTS [MU/USAGE] | OPERATING_TIME [s]";
            break;
            
        case 'swapActions' :
            text = "<< SwapActions >> <br/>";
            text += "Ce fichier contient les dur&eacute;es des op&eacute;rations dans les swap locations. <br/>";
            text += "Format : <br/>";
            text += "&nbsp;&nbsp;&nbsp;&nbsp; SWAP_ACTION | DURATION [s]";
            break;
            
        case 'locations' :
            text = "<< Locations >> <br/>";
            text += "Ce fichier contient l'ensemble des emplacements (d&eacute;p&ocirc;t, swap locations & clients). <br/>";
            text += "Il pr&eacute;cise pour chaque emplacement son adresse et ses coordonn&eacute;es. <br/>";
            text += "Pour les clients, on trouve &eacute;galement la quantit&eacute; &agrave; livrer et le temps de service. <br/>";
            text += "Format : <br/>";
            text += "&nbsp;&nbsp;&nbsp;&nbsp; LOCATION_TYPE | LOCATION_ID | POST_CODE | CITY | X_COORD | Y_COORD | QUANTITY | TRAIN_POSSIBLE | SERVICE_TIME [s]";
            break;
    }

    $("#helpBox").html(text);

}

function hideHelp() {
    $("#helpBox").addClass("helpBoxHide");
    $("#helpBox").removeClass("helpBoxShow");
}