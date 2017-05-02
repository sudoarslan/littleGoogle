<%@ page import="lg.Database" %>
<%@ page errorPage="showError.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" type="text/css"
          href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.10/semantic.min.css">
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"
            integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.10/semantic.min.js"></script>
</head>


<body>

<div style="margin-top: 50px" align="center">
    <h1>Select Stemmed Words</h1>
</div>
<div style="margin-top: 20px;" align="center">

    <div style="width: 40%; zoom: 1.3;">
        <select name="skills" multiple="" class="ui search fluid dropdown">
            <option value="">Stemmed Words</option>
            <%
                Database db = new Database();
                for (String s : db.wordMapTable.getAllKeys(false)) { %>
                <option value="<%=s%>"><%=s%></option>
            <% } %>
        </select>


        <button style="margin-top: 15px" class="ui primary button" onclick="query()">
            Search
        </button>
    </div>

</div>

<script type="text/javascript">
    $(".ui.fluid.dropdown").dropdown({allowLabels: true});
    function query() {
        var selected = $(".ui.fluid.dropdown").dropdown('get value');
        window.location.href = "/main?topk=50&simValue=1&query=" + selected.join(" ");
    }
</script>

</body>
</html>
