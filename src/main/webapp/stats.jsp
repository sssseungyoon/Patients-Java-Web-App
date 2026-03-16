<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.Map, java.util.ArrayList" %>
<%!
    private String jsLabels(Map<String, Long> map) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String k : map.keySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(k.replace("\\","\\\\").replace("\"","\\\"")).append("\"");
            first = false;
        }
        return sb.append("]").toString();
    }

    private String jsData(Map<String, Long> map) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Long v : map.values()) {
            if (!first) sb.append(",");
            sb.append(v);
            first = false;
        }
        return sb.append("]").toString();
    }

    private Map<String, Long> topN(Map<String, Long> map, int n) {
        Map<String, Long> result = new java.util.LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Long> e : map.entrySet()) {
            if (count++ >= n) break;
            result.put(e.getKey(), e.getValue());
        }
        return result;
    }
%>
<html>
<head>
    <title>Patient Statistics</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        body { font-family: sans-serif; padding: 16px; }
        h2 { margin-top: 36px; border-bottom: 1px solid #ddd; padding-bottom: 4px; }
        h3 { margin-top: 0; color: #444; }
        table { border-collapse: collapse; margin-bottom: 24px; }
        th, td { border: 1px solid #ccc; padding: 8px 12px; text-align: left; }
        th { background-color: #f2f2f2; }
        .chart-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 32px; margin-bottom: 40px; }
        .chart-box { background: #fafafa; border: 1px solid #e0e0e0; border-radius: 6px; padding: 16px; }
        .chart-wide { grid-column: 1 / -1; }
        canvas { max-height: 300px; }
        .summary-bar { display: flex; gap: 32px; margin-bottom: 32px; flex-wrap: wrap; }
        .summary-card { background: #f0f4ff; border: 1px solid #c5d0f0; border-radius: 6px; padding: 16px 24px; }
        .summary-card .value { font-size: 2em; font-weight: bold; color: #2a4ab0; }
        .summary-card .label { color: #555; font-size: 0.9em; margin-top: 4px; }
    </style>
</head>
<body>
    <a href="/">Home</a> | <a href="/patients">All Patients</a> | <a href="/search">Search</a> | <a href="/export">Download JSON</a>
    <h1>Patient Statistics</h1>

    <%
        String[] cols = (String[]) request.getAttribute("columnNames");
        double avgAge = (Double) request.getAttribute("averageAge");
        Map<String, Long> avd = (Map<String, Long>) request.getAttribute("aliveVsDeceased");
        Map<String, Long> byGender = (Map<String, Long>) request.getAttribute("byGender");
        Map<String, Long> byMarital = (Map<String, Long>) request.getAttribute("byMarital");
        Map<String, Long> byRace = (Map<String, Long>) request.getAttribute("byRace");
        Map<String, Long> byState = (Map<String, Long>) request.getAttribute("byState");
        Map<String, Long> byCity = (Map<String, Long>) request.getAttribute("byCity");
        Map<String, Long> ageDist = (Map<String, Long>) request.getAttribute("ageDistribution");
        long totalPatients = 0;
        for (Long v : avd.values()) totalPatients += v;
        long aliveCount = avd.getOrDefault("Alive", 0L);
    %>

    <!-- Summary cards -->
    <div class="summary-bar">
        <div class="summary-card">
            <div class="value"><%= totalPatients %></div>
            <div class="label">Total Patients</div>
        </div>
        <div class="summary-card">
            <div class="value"><%= aliveCount %></div>
            <div class="label">Living Patients</div>
        </div>
        <div class="summary-card">
            <div class="value"><%= String.format("%.1f", avgAge) %></div>
            <div class="label">Average Age (living)</div>
        </div>
    </div>

    <!-- Charts -->
    <h2>Charts</h2>
    <div class="chart-grid">

        <div class="chart-box chart-wide">
            <h3>Age Distribution (Living Patients)</h3>
            <canvas id="chartAge"></canvas>
        </div>

        <div class="chart-box">
            <h3>Alive vs Deceased</h3>
            <canvas id="chartAlive"></canvas>
        </div>

        <div class="chart-box">
            <h3>Gender Distribution</h3>
            <canvas id="chartGender"></canvas>
        </div>

        <div class="chart-box">
            <h3>Race Distribution</h3>
            <canvas id="chartRace"></canvas>
        </div>

        <div class="chart-box">
            <h3>Top 10 Cities</h3>
            <canvas id="chartCity"></canvas>
        </div>

    </div>

    <script>
        const COLORS = [
            "#4e79a7","#f28e2b","#e15759","#76b7b2","#59a14f",
            "#edc948","#b07aa1","#ff9da7","#9c755f","#bab0ac"
        ];

        new Chart(document.getElementById("chartAge"), {
            type: "bar",
            data: {
                labels: <%= jsLabels(ageDist) %>,
                datasets: [{ label: "Patients", data: <%= jsData(ageDist) %>,
                    backgroundColor: "#4e79a7", borderRadius: 4 }]
            },
            options: { plugins: { legend: { display: false } },
                       scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } }
        });

        new Chart(document.getElementById("chartAlive"), {
            type: "doughnut",
            data: {
                labels: <%= jsLabels(avd) %>,
                datasets: [{ data: <%= jsData(avd) %>,
                    backgroundColor: ["#59a14f","#e15759"] }]
            },
            options: { plugins: { legend: { position: "bottom" } } }
        });

        new Chart(document.getElementById("chartGender"), {
            type: "doughnut",
            data: {
                labels: <%= jsLabels(byGender) %>,
                datasets: [{ data: <%= jsData(byGender) %>,
                    backgroundColor: ["#4e79a7","#f28e2b","#e15759"] }]
            },
            options: { plugins: { legend: { position: "bottom" } } }
        });

        new Chart(document.getElementById("chartRace"), {
            type: "bar",
            data: {
                labels: <%= jsLabels(byRace) %>,
                datasets: [{ label: "Patients", data: <%= jsData(byRace) %>,
                    backgroundColor: COLORS }]
            },
            options: {
                indexAxis: "y",
                plugins: { legend: { display: false } },
                scales: { x: { beginAtZero: true } }
            }
        });

        <% Map<String, Long> top10 = topN(byCity, 10); %>
        new Chart(document.getElementById("chartCity"), {
            type: "bar",
            data: {
                labels: <%= jsLabels(top10) %>,
                datasets: [{ label: "Patients", data: <%= jsData(top10) %>,
                    backgroundColor: COLORS }]
            },
            options: {
                indexAxis: "y",
                plugins: { legend: { display: false } },
                scales: { x: { beginAtZero: true } }
            }
        });
    </script>

    <!-- Data Tables -->
    <h2>Oldest &amp; Youngest Living Patient</h2>
    <h3>Oldest</h3>
    <table>
        <tr><% for (String c : cols) { %><th><%= c %></th><% } %></tr>
        <tr><% for (String v : (String[]) request.getAttribute("oldestPatient")) { %><td><%= v != null ? v : "" %></td><% } %></tr>
    </table>
    <h3>Youngest</h3>
    <table>
        <tr><% for (String c : cols) { %><th><%= c %></th><% } %></tr>
        <tr><% for (String v : (String[]) request.getAttribute("youngestPatient")) { %><td><%= v != null ? v : "" %></td><% } %></tr>
    </table>

    <h2>Alive vs Deceased</h2>
    <table>
        <tr><th>Status</th><th>Count</th></tr>
        <% for (Map.Entry<String, Long> e : avd.entrySet()) { %>
        <tr><td><%= e.getKey() %></td><td><%= e.getValue() %></td></tr>
        <% } %>
    </table>

    <h2>By Gender</h2>
    <table>
        <tr><th>Gender</th><th>Count</th></tr>
        <% for (Map.Entry<String, Long> e : byGender.entrySet()) { %>
        <tr><td><%= e.getKey() %></td><td><%= e.getValue() %></td></tr>
        <% } %>
    </table>

    <h2>By Marital Status</h2>
    <table>
        <tr><th>Status</th><th>Count</th></tr>
        <% for (Map.Entry<String, Long> e : byMarital.entrySet()) { %>
        <tr><td><%= e.getKey() %></td><td><%= e.getValue() %></td></tr>
        <% } %>
    </table>

    <h2>By Race</h2>
    <table>
        <tr><th>Race</th><th>Count</th></tr>
        <% for (Map.Entry<String, Long> e : byRace.entrySet()) { %>
        <tr><td><%= e.getKey() %></td><td><%= e.getValue() %></td></tr>
        <% } %>
    </table>

    <h2>By State</h2>
    <table>
        <tr><th>State</th><th>Count</th></tr>
        <% for (Map.Entry<String, Long> e : byState.entrySet()) { %>
        <tr><td><%= e.getKey() %></td><td><%= e.getValue() %></td></tr>
        <% } %>
    </table>

    <h2>By City</h2>
    <table>
        <tr><th>City</th><th>Count</th></tr>
        <% for (Map.Entry<String, Long> e : byCity.entrySet()) { %>
        <tr><td><%= e.getKey() %></td><td><%= e.getValue() %></td></tr>
        <% } %>
    </table>
</body>
</html>
