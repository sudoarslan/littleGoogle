<%@ page import="java.util.Vector" %>
<%@ page import="lg.TitleExtractor" %>
<%@ page errorPage="showError.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/uikit/3.0.0-beta.20/css/uikit.min.css"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/uikit/3.0.0-beta.20/js/uikit.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/uikit/3.0.0-beta.20/js/uikit-icons.min.js"></script>
    <link rel="stylesheet" href="style.css">
</head>


<%
    int resultSize = -1;
    if (request.getAttribute("queryResult") != null) {
        resultSize = ((Vector<String>)request.getAttribute("queryResult")).size();
    }
%>

<body>
<div align="center">
    <div class="uk-card uk-card-hover uk-card-body">
        <h1 style="margin-bottom: 40px">Little Google</h1>
        <form action="/main" method="post">
            <input class="uk-input uk-form-large searchInput" name="query" type="text"
                   placeholder="What are you looking for?"/>
            <button class="uk-button uk-button-primary uk-button-large">Search</button>
        </form>
    </div>
    <hr class="uk-divider-icon">
</div>
<div class="uk-card uk-card-body" style="margin-top: 20px" align="center">

    <% if(resultSize==0) {%>
       <h1 class="uk-h1">No results</h1>
    <% } %>

    <c:forEach items="${queryResult}" var="url">
        <div class="uk-card uk-card-hover uk-card-body searchResult" align="left">
            <div class="uk-card-badge uk-label">See Similar</div>
            <a href="${url}" style="text-decoration: none"><h6 class="uk-card-title" style="margin-bottom: 5px">${TitleExtractor.getPageTitle(url)}</h6></a>
            <a href="${url}"><p>${url}</p></a>
        </div>
    </c:forEach>
</div>
</div>
</body>
</html>
