<%-- 
    Document   : index
    Created on : 22 mars 2017, 11:43:01
    Author     : Anais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href='CSS/style.css' rel='stylesheet' />
        <link href='CSS/index.css' rel='stylesheet' />
        <title>JSP Page</title>
    </head>
    <body>
        <c:import url="header.html" />
                
        <h1>Dernière solution</h1>
        <div id="maps"></div>
        
        <fieldset id="listCamions">
            <legend>Camions</legend>
            <div id="camions"></div>
        </fieldset>
        
        <fieldset id="listClients">
            <legend>Clients</legend>
            <div id="clients"></div>
        </fieldset>
    </body>
</html>
