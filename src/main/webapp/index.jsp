<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Patients App</h1>
    <p>Server time: <%= new java.util.Date() %></p>
    <a href="/patients">View/Edit Patients</a> | <a href="/search">Search</a> | <a href="/stats">Statistics</a> | <a href="/export">Download JSON</a>
</body>
</html>
