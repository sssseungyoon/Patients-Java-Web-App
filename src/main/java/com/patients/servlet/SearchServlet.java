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
        String query = request.getParameter("q");
        Model model = Model.getInstance();
        request.setAttribute("columnNames", model.getColumnNames());
        request.setAttribute("results", model.search(query));
        request.setAttribute("query", query);
        request.getRequestDispatcher("/search.jsp").forward(request, response);
    }
}
