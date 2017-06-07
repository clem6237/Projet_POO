<script>
    var fleet = false, swapActions = false;
    $(document).on("change", "input[type=file]", function knowIfNotNull() {
        $("p#error").remove();
        
        var fileExt = $(this).val();
        fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
        
        if (fileExt != ".csv" && fileExt.trim()) {
            $(this).after("<p id='error'>Le fichier n'est pas au bon format</p>");
            $(this).addClass("error");
            
            if($(this).attr("name") == "fleet") {
                fleet = false;
            } else if($(this).attr("name") == "swapActions") {
                swapActions = false;
            }
        } else {
            $(this).removeClass("error");
            
            if($(this).attr("name") == "fleet") {
                if($(this).val().trim().length > 0)
                    fleet = true;
                else 
                    fleet = false;
            } else if($(this).attr("name") == "swapActions") {
                if($(this).val().trim())
                    swapActions = true;
                else 
                    swapActions = false;
            }
        }
        
        if(fleet && swapActions)
            $("#BtnNext").removeClass("disabled");
        else 
            $("#BtnNext").addClass("disabled");
        
    });
    
    $(document).on("click", "#BtnNext", function checkDisable() {
        if($(this).hasClass( "disabled" ))
            return;
        else
          $('#send').submit();  
    });
</script>
<div id="helpBox" class="helpBoxHide"></div>

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="localisation">
                <div></div> <div class="active"></div> <div></div>
            </div>
            <form id="send" action="Controleur" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="vue" value="2"/>
                <input type="hidden" name="action" value="next" />
                <div class="container">
                    <h1 class="text-primary">Import du fichier de flotte et des actions</h1>
                    <br/>
                    <label>Fichier de la flotte <span class="obligatory">*</span></label>
                    <img class="helpFile" src="./IMG/help.png" onmouseover="showHelp('fleet')" onmouseout="hideHelp()"/><br/>
                    <input type="file" name="fleet" pattern="*.csv" accept=".csv"/><br/>
                    
                    <br/>
                    <label>Caractéristiques des actions<span class="obligatory">*</span></label>
                    <img class="helpFile" src="./IMG/help.png" onmouseover="showHelp('swapActions')" onmouseout="hideHelp()"/><br/>
                    <input type="file" name="swapActions" pattern="*.csv" accept=".csv" /><br/>
                </div>
                <ul class="pager">
                  <li id="BtnNext" class="next disabled">
                    <a>Suivant</a>
                  </li>
                  <li class="next">
                    <a href="Controleur?vue=2&action=previous">Retour</a>
                  </li>
                  <li class="next">
                    <a href="index.jsp">Annuler</a>
                  </li>
                </ul>
            </form>
        </div>
    </div>
</div>