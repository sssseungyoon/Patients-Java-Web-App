<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<html>
<head>
    <title>Patients</title>
    <style>
        body { font-family: sans-serif; padding: 16px; }
        table { border-collapse: collapse; width: 100%; margin-top: 12px; }
        th, td { border: 1px solid #ccc; padding: 6px 8px; text-align: left; white-space: nowrap; }
        th { background-color: #f2f2f2; }
        tr:nth-child(even) { background-color: #fafafa; }
        .btn-edit { padding: 3px 10px; font-size: 12px; background: #4a90d9; color: white; border: none; border-radius: 3px; cursor: pointer; text-decoration: none; }
        .btn-edit:hover { background: #357abd; }
        .btn-delete { padding: 3px 10px; font-size: 12px; background: #d9534f; color: white; border: none; border-radius: 3px; cursor: pointer; }
        .btn-delete:hover { background: #b52b27; }
        .btn-add { padding: 7px 16px; font-size: 14px; background: #5cb85c; color: white; border: none; border-radius: 3px; cursor: pointer; text-decoration: none; display: inline-block; margin-top: 10px; }
        .btn-add:hover { background: #449d44; }
        td.actions { display: flex; gap: 4px; }
    </style>
</head>
<body>
    <a href="/">Home</a> | <a href="/search">Search</a> | <a href="/stats">Statistics</a> | <a href="/export">Download JSON</a>
    <h1>Patients</h1>
    <a href="/patients?action=addForm" class="btn-add">+ Add Patient</a>
    <a href="/export" class="btn-add" style="background:#6c757d;margin-left:8px;">&#8595; Download JSON</a>
    <table>
        <tr>
            <th>Actions</th>
            <% for (String col : (String[]) request.getAttribute("columnNames")) { %>
                <th><%= col %></th>
            <% } %>
        </tr>
        <%
            ArrayList<String[]> patients = (ArrayList<String[]>) request.getAttribute("patients");
            for (int i = 0; i < patients.size(); i++) {
                String[] p = patients.get(i);
        %>
            <tr>
                <td class="actions">
                    <a href="/patients?action=editForm&index=<%= i %>" class="btn-edit">Edit</a>
                    <form method="post" action="/patients" style="display:inline"
                          onsubmit="return confirm('Delete this patient?')">
                        <input type="hidden" name="action" value="delete" />
                        <input type="hidden" name="index" value="<%= i %>" />
                        <button type="submit" class="btn-delete">Delete</button>
                    </form>
                </td>
                <% for (String value : p) { %>
                    <td><%= value != null ? value : "" %></td>
                <% } %>
            </tr>
        <% } %>
    </table>
</body>
</html>
