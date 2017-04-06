<%-- 
    Document   : calcul
    Created on : 29 mars 2017, 08:42:40
    Author     : Anais
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
    <script type="text/javascript" src="http://netdna.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <link href="http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.3.0/css/font-awesome.min.css"
    rel="stylesheet" type="text/css">
    <link href="CSS/style.css" rel="stylesheet" type="text/css">
    <title>SB-VRP</title>
  </head>
  <body>
    <c:choose>
        <c:when test="${ requestScope.active != null }">
            <c:set var="active" scope="page" value="${ requestScope.active }"/>
        </c:when>
        <c:otherwise>
            <c:set var="active" scope="page" value="1"/>
        </c:otherwise>
    </c:choose>
    
    <div class="navbar navbar-default navbar-static-top">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-brand" href="index.jsp"><span>SB-VRP</span></a>
        </div>
        <div class="collapse navbar-collapse" id="navbar-ex-collapse">
          <ul class="nav navbar-nav">
            <li class="active">
              <a href="Controleur?action=previous&vue=2">Nouveau Calcul</a>
            </li>            
            <li>
              <a href="Controleur?action=export">Exporter en CSV</a>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="vue">
        <c:choose>
            <c:when test="${ active == 1 }">
                <c:import url="vue/import_page_1.jsp" />
            </c:when>
            <c:when test="${ active == 2 }">
                <c:import url="vue/import_page_2.jsp" />
            </c:when>
            <c:when test="${ active == 3 }">
                <c:import url="vue/import_page_3.jsp" />
            </c:when>
        </c:choose>
    </div>
  </body>
</html>