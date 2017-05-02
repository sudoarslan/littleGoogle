<%@ page import="java.util.Vector" %>
<%@ page import="lg.PageInfo" %>
<%@ page import="lg.WPair" %>
<%@ page import="java.util.Set" %>
<%@ page errorPage="showError.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/uikit/3.0.0-beta.20/css/uikit.min.css"/>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/uikit/3.0.0-beta.20/js/uikit.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/uikit/3.0.0-beta.20/js/uikit-icons.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
</head>


<style>
    .searchInput {
        display: inline-block;
        width: 50%;
    }

    .searchResult {
        width: 55%;
        padding-top: 10px;
        padding-bottom: 10px;
    }

    .uk-card-title {
        margin-bottom: 10px;
    }

    .searchResult p {
        margin-top: 0px;
        margin-bottom: 0px;
    }

    .searchResult > ul {
        margin-bottom: 0px;
        margin-top: 0px;
    }

    #slider {
        width: 50%;
        margin-top: 0px;
    }


</style>


<%
    String topk = "20";
    String simValue = "1";
    Vector<PageInfo> qr = new Vector<PageInfo>();
    if (request.getAttribute("queryResult") != null) {
        qr = ((Vector<PageInfo>) request.getAttribute("queryResult"));
    }
    if (request.getParameter("topk") != null) {
        topk = request.getParameter("topk");
    }
    if (request.getParameter("simValue") != null) {
        simValue = request.getParameter("simValue");
    }
%>

<body>
<div align="center">
    <div class="uk-card uk-card-hover uk-card-body">
        <h1 style="margin-bottom: 40px" onclick="window.location.href='/main'">Little Google</h1>
        <form action="/main" method="get">
            <input id="top_k_results" style="visibility: hidden; width: 0px; height: 0px" name="topk"
                   value="<%=topk%>"/>
            <input id="simValueInput" style="visibility: hidden; width: 0px; height: 0px" name="simValue"
                   value="<%=simValue%>"/>
            <input class="uk-input uk-form-large searchInput" name="query" id="inputSearchQuery" type="text" value="<%
                if(qr.size()>0){
                    out.print(request.getParameter("query"));
                }
            %>" placeholder="What are you looking for?"/>
            <button class="uk-button uk-button-primary uk-button-large">Search</button>
            <div style="width: 50%; margin-top: 20px">
                <h4 style="text-align: left; margin-bottom: 0px"><b>Maximum number of results: </b><span
                        id="slideSelector"><%=topk%></span></h4>
            </div>
            <div style="margin-top: 8px" id="slider"></div>

            <div style="width: 50%;">
                <table style="width:100%; margin-top: 10px">
                    <tr>
                        <td><h4 style="margin-bottom: 0px">PageRank</h4></td>
                        <td><b><h4 id="simValue" style="text-align: center; margin-bottom: 0px"><%=simValue%></h4></b></td>
                        <td><h4 style="text-align: right; margin-bottom: 0px">CosSim</h4></td>
                    </tr>
                </table>
                <div style="margin-top: 8px" id="slider2"></div>
            </div>
        </form>
    </div>
    <hr class="uk-divider-icon">
</div>
<div class="uk-card uk-card-body" style="margin-top: 20px;" align="center">



    <% if (request.getAttribute("suggest")!=null) { %>
    <h2 class="uk-h2" style="text-align: center; width: 55%">Do you mean: <b><a onclick="reQuery('<%=request.getAttribute("suggest")%>')"><%=request.getAttribute("suggest")%></a></b></h2>
    <% } %>

    <% if (qr.size() == 0) {%>
    <h1 class="uk-h1">No results</h1>
    <% } else { %>
    <h2 class="uk-h2" style="text-align: left; width: 55%">Found <b><%=qr.size()%>
    </b> results in <b><%=request.getAttribute("time")%> seconds</b></h2>
    <% } %>



    <% for (PageInfo result : qr) {%>
    <div class="uk-card uk-card-hover uk-card-body searchResult" align="left">
        <button class="uk-card-badge uk-label" onclick="reQuery('<%
            for (int i = 0; i < result.KeywordVector.size(); i++) {
                String output = "";
                output += result.KeywordVector.elementAt(i).Key;
                    if (i != result.KeywordVector.size() - 1) {
                        output += " ";
                    }
                 out.print(output);
            }
            %>')">See Similar
        </button>
        <a href="<%=result.Url%>" style="text-decoration: none">
            <h6 class="uk-card-title"><%=result.Title%>
            </h6>
        </a>
        <a href="<%=result.Url%>"><b style="text-decoration: none">Url: </b><%=result.Url%></a>
        <p><b>Score: </b><%=result.Score%>
        <p><b>Last Modified: </b><%=result.LastModifiedDate%> | <b>Page Size: </b><%=result.SizeOfPage%> bytes</p>
        </p>
        <p>
            <% for (int i = 0; i < result.KeywordVector.size(); i++) {
                String output = "";
                output += result.KeywordVector.elementAt(i).Key + ": " + result.KeywordVector.elementAt(i).Value;
                if (i != result.KeywordVector.size() - 1) {
                    output += " | ";
                }
                out.print(output);
            } %>
        </p>

        <%
            if (result.ParentLinkVector.size() == 1 && result.ParentLinkVector.elementAt(0).equals("N/A")) {
                out.print("<p><b>No Parent Links</b></p>");
            } else {
        %>
        <p><b><%=result.ParentLinkVector.size()%> Parent Link(s) Found:</p></b>
        <ul class="uk-list uk-list-bullet">
            <% for (String s : result.ParentLinkVector) { %>
            <li style="margin-top: 0px"><a href="<%=s%>"><p><%=s%></p></a></li>
            <% } %>
        </ul>
        <% } %>

        <%
            if (result.ChildLinkVector.size() == 1 && result.ChildLinkVector.elementAt(0).equals("N/A")) {
                out.print("<p><b>No Child Links</b></p>");
            } else {
        %>
        <p><b><%=result.ChildLinkVector.size()%> Child Link(s) Found:</p></b>
        <ul class="uk-list uk-list-bullet">
            <% for (String s : result.ChildLinkVector) { %>
            <li style="margin-top: 0px"><a href="<%=s%>"><p><%=s%></p></a></li>
            <% } %>
        </ul>
        <% } %>
    </div>
    <% } %>

</div>
</div>

<script type="text/javascript">

    $(function () {
        $("#slider").slider({
            min: 1,
            max: 50,
            step: 1,
            value: parseInt("<%=topk%>"),
            slide: function (event, ui) {
                $("#slideSelector").html(ui.value)
                $("#top_k_results").val(ui.value)
            }
        });
    });

    $(function () {
        $("#slider2").slider({
            min: 0,
            max: 1,
            step: 0.1,
            value: parseFloat("<%=simValue%>"),
            slide: function (event, ui) {
                $("#simValue").html(ui.value)
                $("#simValueInput").val(ui.value)
            }
        });
    });


    $( function() {
        var suggestions = [];
        <% if (request.getAttribute("queryHistory") != null) { for (String s: (Set<String>) request.getAttribute("queryHistory")) { %>
        suggestions.push("<%=s%>");
        <% } } %>
        $( "#inputSearchQuery" ).autocomplete({
            minLength: 0,
            source: suggestions
        });
    } );
    function reQuery(str) {
        window.location.href = "/main?topk=" + $("#slider").slider("value") + "&simValue=" + $("#slider2").slider("value") + "&query=" + str;
    }
</script>


</body>
</html>
