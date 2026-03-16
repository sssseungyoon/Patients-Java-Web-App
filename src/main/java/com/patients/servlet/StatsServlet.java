package com.patients.servlet;

import com.patients.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/stats")
public class StatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Model model = Model.getInstance();

        req.setAttribute("columnNames", model.getColumnNames());
        req.setAttribute("oldestPatient", model.getOldestPatient());
        req.setAttribute("youngestPatient", model.getYoungestPatient());
        req.setAttribute("averageAge", model.getAverageAge());
        req.setAttribute("aliveVsDeceased", model.countAliveVsDeceased());
        req.setAttribute("byCity", model.countByCity());
        req.setAttribute("byState", model.countByState());
        req.setAttribute("byGender", model.countByGender());
        req.setAttribute("byMarital", model.countByMaritalStatus());
        req.setAttribute("byRace", model.countByRace());
        req.setAttribute("ageDistribution", model.getAgeDistribution());

        req.getRequestDispatcher("/stats.jsp").forward(req, resp);
    }
}
