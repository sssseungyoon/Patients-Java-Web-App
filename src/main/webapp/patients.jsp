<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<html>
<head>
    <title>Patients</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        tr:nth-child(even) { background-color: #fafafa; }
    </style>
</head>
<body>
    <a href="/">Home</a> | <a href="/search">Search</a>
    <h1>Patients</h1>
    <table>
        <tr>
            <% for (String col : (String[]) request.getAttribute("columnNames")) { %>
                <th><%= col %></th>
            <% } %>
        </tr>
        <% for (String[] patient : (ArrayList<String[]>) request.getAttribute("patients")) { %>
            <tr>
                <% for (String value : patient) { %>
                    <td><%= value %></td>
                <% } %>
            </tr>
        <% } %>
    </table>
</body>
</html>
