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