package com.patients.servlet;

import com.patients.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Model model = Model.getInstance();
        String col = request.getParameter("col");
        String val = request.getParameter("val");
        String query = request.getParameter("q");

        request.setAttribute("columnNames", model.getColumnNames());
        request.setAttribute("distinctValuesJson", model.getDistinctValuesJson());
        request.setAttribute("selectedCol", col);
        request.setAttribute("selectedVal", val);
        request.setAttribute("query", query);

        if (col != null && !col.isEmpty() && val != null && !val.isEmpty()) {
            request.setAttribute("results", model.searchByColumn(col, val));
            request.setAttribute("searchMode", "column");
        } else {
            request.setAttribute("results", model.search(query));
            request.setAttribute("searchMode", "text");
        }

        request.getRequestDispatcher("/search.jsp").forward(request, response);
    }
}
