<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%!
    private String h(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private String validationAttrs(String col) {
        switch (col) {
            case "ID":        return "required";
            case "BIRTHDATE": return "required pattern=\"[0-9]{4}-[0-9]{2}-[0-9]{2}\" title=\"Required. Format: YYYY-MM-DD\"";
            case "DEATHDATE": return "pattern=\"[0-9]{4}-[0-9]{2}-[0-9]{2}\" title=\"Format: YYYY-MM-DD (leave blank if alive)\"";
            case "FIRST":     return "required";
            case "LAST":      return "required";
            case "SSN":       return "pattern=\"[0-9]{3}-[0-9]{2}-[0-9]{4}\" title=\"Format: ###-##-#### (e.g. 123-45-6789)\"";
            case "GENDER":    return "pattern=\"[MF]\" title=\"M or F\"";
            case "MARITAL":   return "pattern=\"[MS]\" title=\"M (married) or S (single)\"";
            case "ZIP":       return "pattern=\"[0-9]{5}\" title=\"5-digit ZIP code\"";
            default:          return "";
        }
    }

    private String placeholder(String col) {
        switch (col) {
            case "BIRTHDATE": return "YYYY-MM-DD";
            case "DEATHDATE": return "YYYY-MM-DD or leave blank";
            case "GENDER":    return "M or F";
            case "MARITAL":   return "M or S";
            case "SSN":       return "###-##-####";
            case "ZIP":       return "12345";
            default:          return "";
        }
    }
%>
<html>
<head>
    <title><%= ((Integer) request.getAttribute("index")) < 0 ? "Add Patient" : "Edit Patient" %></title>
    <style>
        body { font-family: sans-serif; padding: 16px; }
        h1 { margin-bottom: 20px; }
        .error-box { background: #fdf0f0; border: 1px solid #e57373; border-radius: 4px; padding: 12px 16px; margin-bottom: 20px; color: #b71c1c; }
        .error-box ul { margin: 6px 0 0 0; padding-left: 20px; }
        table { border-collapse: collapse; }
        td { padding: 6px 10px; vertical-align: middle; }
        td:first-child { font-weight: bold; text-align: right; color: #555; width: 140px; }
        input[type=text] { width: 320px; padding: 5px; font-size: 14px; border: 1px solid #ccc; border-radius: 3px; }
        input[type=text]:invalid { border-color: #e57373; background: #fff8f8; }
        input[type=text]:valid:not(:placeholder-shown) { border-color: #81c784; }
        .hint { font-size: 11px; color: #888; margin-top: 2px; }
        .actions { margin-top: 20px; }
        button[type=submit] { padding: 8px 20px; font-size: 14px; background: #4a90d9; color: white; border: none; border-radius: 3px; cursor: pointer; }
        button[type=submit]:hover { background: #357abd; }
        a { color: #4a90d9; }
    </style>
</head>
<body>
    <a href="/">Home</a> | <a href="/patients">All Patients</a> | <a href="/search">Search</a> | <a href="/stats">Statistics</a> | <a href="/export">Download JSON</a>

    <%
        String[] cols = (String[]) request.getAttribute("columnNames");
        String[] patient = (String[]) request.getAttribute("patient");
        int index = (Integer) request.getAttribute("index");
        boolean isAdd = index < 0;
        List<String> errors = (List<String>) request.getAttribute("errors");
    %>

    <h1><%= isAdd ? "Add New Patient" : "Edit Patient" %></h1>

    <% if (errors != null && !errors.isEmpty()) { %>
    <div class="error-box">
        <strong>Please fix the following errors:</strong>
        <ul>
            <% for (String err : errors) { %>
                <li><%= h(err) %></li>
            <% } %>
        </ul>
    </div>
    <% } %>

    <form action="/patients" method="post" id="editForm" novalidate>
        <input type="hidden" name="action" value="<%= isAdd ? "add" : "edit" %>" />
        <% if (!isAdd) { %>
            <input type="hidden" name="index" value="<%= index %>" />
        <% } %>

        <table>
            <% for (int i = 0; i < cols.length; i++) {
                String val = (patient[i] != null) ? patient[i] : "";
                String ph = placeholder(cols[i]);
            %>
            <tr>
                <td><%= h(cols[i]) %></td>
                <td>
                    <input type="text" name="field_<%= h(cols[i]) %>"
                           value="<%= h(val) %>"
                           <%= validationAttrs(cols[i]) %>
                           <% if (!ph.isEmpty()) { %>placeholder="<%= h(ph) %>"<% } %> />
                </td>
            </tr>
            <% } %>
        </table>

        <div class="actions">
            <button type="submit"><%= isAdd ? "Add Patient" : "Save Changes" %></button>
            &nbsp; <a href="/patients">Cancel</a>
        </div>
    </form>

    <script>
        document.getElementById("editForm").addEventListener("submit", function (e) {
            var errors = [];
            var birth = document.querySelector("[name='field_BIRTHDATE']");
            var death = document.querySelector("[name='field_DEATHDATE']");

            // Trigger native HTML5 validation first
            if (!this.checkValidity()) {
                this.reportValidity();
                e.preventDefault();
                return;
            }

            // Cross-field: death must be after birth
            if (birth && death && birth.value && death.value) {
                if (death.value <= birth.value) {
                    errors.push("DEATHDATE must be after BIRTHDATE.");
                }
            }

            if (errors.length > 0) {
                e.preventDefault();
                alert(errors.join("\n"));
            }
        });
    </script>
</body>
</html>
