package com.patients.servlet;

import com.patients.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/patients")
public class PatientsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Model model = Model.getInstance();
        request.setAttribute("columnNames", model.getColumnNames());
        request.setAttribute("patients", model.getAllPatients());
        request.getRequestDispatcher("/patients.jsp").forward(request, response);
    }
}
