<script>
    var locations = false;
    $(document).on("change", "input[type=file]", function knowIfNotNull() {
         $("p#error").remove();
        
        var fileExt = $(this).val();
        fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
        
        if (fileExt != ".csv" && fileExt.trim()) {
            $(this).after("<p id='error'>Le fichier n'est pas au bon format</p>");
            $(this).addClass("error");
            
            locations = false;
        } else {
            $(this).removeClass("error");
            
            if($(this).val().trim().length > 0)
                locations = true;
            else 
                locations = false;
        }
        
        if(locations)
            $("#BtnNext").removeClass("disabled");
        else 
            $("#BtnNext").addClass("disabled");
    });
    
     $(document).on("click", "#BtnNext", function checkDisable() {
        if($(this).hasClass( "disabled" ))
            return;
        else
        {
            //$('#send').submit();  
            $("#loading").css("display", "block");
            $("body").css("overflow-y", "hidden");
            event.preventDefault();
            var form = $('#send')[0];
            var data = new FormData(form);
            $("#BtnNext").prop("disabled", true);

            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: "Controleur",
                data: data,
                processData: false,
                contentType: false,
                cache: false,
                success: function (data) {

                    console.log("SUCCESS : ", data);
                    document.location.href = "./index.jsp"

                },
                error: function (e) {

                    console.log("ERROR : ", e);
                    

                }
            });
        }
    });
</script>

<div id="loading" style="display: none; text-align: center;">
    <img id="loading-img" src="./IMG/loading.gif" />
    <h2>Chargement en cours <img height="40px" src="./IMG/dots-loader.gif"/></h2>
</div>

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="localisation">
                <div></div> <div></div> <div class="active"></div>
            </div>
            <form id="send" action="Controleur" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="vue" value="3"/>
                <input type="hidden" name="action" value="calcul" />
                <div class="container">
                    <h1 class="text-primary">Import du fichier de localisation</h1>
                    <br/>
                    <label>Fichier de localisation <span class="obligatory">*</span></label><br/>
                    <input type="file" name="locations" pattern="*.csv" accept=".csv"/><br/>
                </div>
                <ul class="pager">
                  <li id="BtnNext" class="disabled next end">
                    <a>Lancer le calcul</a>
                  </li>
                  <li class="next">
                    <a href="Controleur?vue=3&action=previous">Retour</a>
                  </li>
                  <li class="next">
                    <a href="index.jsp">Annuler</a>
                  </li>
                </ul>
           </form>
        </div>
    </div>
</div>