<script>
    $(document).on("change", "input[type=file]", function checkfile() {
        $("p#error").remove();
        
        var fileExt = $(this).val();
        fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
        
        if (fileExt != ".csv" && fileExt.trim()) {
            $(this).after("<p id='error'>Le fichier n'est pas au bon format</p>");
            $(this).addClass("error");
        } else {
            $(this).removeClass("error");
        }
    });
</script>
<div id="helpBox" class="helpBoxHide"></div>

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="localisation">
                <div class="active"></div> <div></div> <div></div>
            </div>
            <form id="send" action="Controleur" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="vue" value="1"/>
                <input type="hidden" name="action" value="next" />
                <div class="container">
                    <h1 class="text-primary">Import du fichier des coordonnées et des distances</h1>
                    <br/>
                    <label>Fichier des coordonnées</label>
                    <img class="helpFile" src="./IMG/help.png" onmouseover="showHelp('coordinates')" onmouseout="hideHelp()"/><br/>
                    <input type="file" name="coordinates" accept=".csv" /><br/>
                    <br/>
                    <label>Fichier des distances</label>
                    <img class="helpFile" src="./IMG/help.png" onmouseover="showHelp('distances')" onmouseout="hideHelp()"/><br/><br/>
                    <input type="file" name="distances" accept=".csv" /><br/>
                </div>
                <ul class="pager">
                  <li class="next">
                    <a onclick="$('#send').submit()">Suivant</a>
                  </li>
                  <li class="next">
                    <a href="index.jsp">Annuler</a>
                  </li>
                </ul>
            </form>
        </div>
    </div>
</div>