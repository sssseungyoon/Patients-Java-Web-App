<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<html>
<head>
    <title>Search Results</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        tr:nth-child(even) { background-color: #fafafa; }
    </style>
</head>
<body>
    <a href="/">Home</a> | <a href="/patients">All Patients</a>
    <h1>Search</h1>
    <form action="/search" method="get">
        <input type="text" name="q" value="<%= request.getAttribute("query") != null ? request.getAttribute("query") : "" %>" size="40" />
        <button type="submit">Search</button>
    </form>
    <%
        ArrayList<String[]> results = (ArrayList<String[]>) request.getAttribute("results");
        String query = (String) request.getAttribute("query");
        if (query != null && !query.isEmpty()) {
    %>
        <p><%= results.size() %> result(s) for "<%= query %>"</p>
        <% if (!results.isEmpty()) { %>
        <table>
            <tr>
                <% for (String col : (String[]) request.getAttribute("columnNames")) { %>
                    <th><%= col %></th>
                <% } %>
            </tr>
            <% for (String[] patient : results) { %>
                <tr>
                    <% for (String value : patient) { %>
                        <td><%= value %></td>
                    <% } %>
                </tr>
            <% } %>
        </table>
        <% } %>
    <% } %>
</body>
</html>
