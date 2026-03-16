<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<html>
<head>
    <title>Search Results</title>
    <style>
        body { font-family: sans-serif; padding: 16px; }
        table { border-collapse: collapse; width: 100%; margin-top: 12px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        tr:nth-child(even) { background-color: #fafafa; }
        .search-section { margin-bottom: 24px; padding: 12px; border: 1px solid #ddd; border-radius: 4px; }
        .search-section h3 { margin-top: 0; }
        select, input[type=text] { padding: 6px; font-size: 14px; }
        button { padding: 6px 14px; font-size: 14px; cursor: pointer; }
        .divider { text-align: center; color: #999; margin: 12px 0; font-style: italic; }
    </style>
</head>
<body>
    <a href="/">Home</a> | <a href="/patients">All Patients</a> | <a href="/stats">Statistics</a> | <a href="/export">Download JSON</a>
    <h1>Search</h1>

    <%
        String[] cols = (String[]) request.getAttribute("columnNames");
        String distinctValuesJson = (String) request.getAttribute("distinctValuesJson");
        String selectedCol = (String) request.getAttribute("selectedCol");
        String selectedVal = (String) request.getAttribute("selectedVal");
        String query = (String) request.getAttribute("query");
        if (selectedCol == null) selectedCol = "";
        if (selectedVal == null) selectedVal = "";
        if (query == null) query = "";
    %>

    <!-- Column + Value search -->
    <div class="search-section">
        <h3>Filter by Column</h3>
        <form action="/search" method="get" id="colForm">
            <select name="col" id="colSelect">
                <option value="">-- Select column --</option>
                <% for (String c : cols) { %>
                    <option value="<%= c %>" <%= c.equals(selectedCol) ? "selected" : "" %>><%= c %></option>
                <% } %>
            </select>
            &nbsp;
            <select name="val" id="valSelect">
                <option value="">-- Select value --</option>
            </select>
            &nbsp;
            <button type="submit">Filter</button>
            <% if (!selectedCol.isEmpty()) { %>
                &nbsp;<a href="/search">Clear</a>
            <% } %>
        </form>
    </div>

    <div class="divider">— or —</div>

    <!-- Free-text search -->
    <div class="search-section">
        <h3>Full-text Search</h3>
        <form action="/search" method="get">
            <input type="text" name="q" value="<%= query %>" size="40" placeholder="Search across all fields..." />
            <button type="submit">Search</button>
        </form>
    </div>

    <!-- Results -->
    <%
        ArrayList<String[]> results = (ArrayList<String[]>) request.getAttribute("results");
        String searchMode = (String) request.getAttribute("searchMode");
        boolean hasSearch = ("column".equals(searchMode) && !selectedCol.isEmpty())
                         || ("text".equals(searchMode) && !query.isEmpty());
    %>
    <% if (hasSearch) { %>
        <p><strong><%= results.size() %></strong> result(s)
        <% if ("column".equals(searchMode)) { %>
            where <strong><%= selectedCol %></strong> = "<strong><%= selectedVal %></strong>"
        <% } else { %>
            for "<strong><%= query %></strong>"
        <% } %>
        </p>
        <% if (!results.isEmpty()) { %>
        <table>
            <tr>
                <% for (String c : cols) { %><th><%= c %></th><% } %>
            </tr>
            <% for (String[] patient : results) { %>
                <tr>
                    <% for (String value : patient) { %>
                        <td><%= value != null ? value : "" %></td>
                    <% } %>
                </tr>
            <% } %>
        </table>
        <% } %>
    <% } %>

    <script>
        var distinctValues = <%= distinctValuesJson %>;
        var colSelect = document.getElementById("colSelect");
        var valSelect = document.getElementById("valSelect");
        var preselectedVal = "<%= selectedVal.replace("\"", "\\\"") %>";

        function populateValues(col) {
            valSelect.innerHTML = "<option value=''>-- Select value --</option>";
            if (!col || !distinctValues[col]) return;
            var values = distinctValues[col];
            for (var i = 0; i < values.length; i++) {
                var opt = document.createElement("option");
                opt.value = values[i];
                opt.text = values[i] === "" ? "(empty)" : values[i];
                if (values[i] === preselectedVal) opt.selected = true;
                valSelect.appendChild(opt);
            }
        }

        // Populate on page load if a column is already selected
        populateValues(colSelect.value);

        colSelect.addEventListener("change", function () {
            preselectedVal = "";
            populateValues(this.value);
        });
    </script>
</body>
</html>
