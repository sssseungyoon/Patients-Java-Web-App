package com.patients.servlet;

import com.patients.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/patients")
public class PatientsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Model model = Model.getInstance();
        String action = request.getParameter("action");

        if ("editForm".equals(action)) {
            int index = parseIndex(request.getParameter("index"), -1);
            if (index < 0 || index >= model.getPatientCount()) {
                response.sendRedirect("/patients");
                return;
            }
            request.setAttribute("columnNames", model.getColumnNames());
            request.setAttribute("patient", model.getPatient(index));
            request.setAttribute("index", index);
            request.getRequestDispatcher("/edit.jsp").forward(request, response);
            return;
        }

        if ("addForm".equals(action)) {
            request.setAttribute("columnNames", model.getColumnNames());
            request.setAttribute("patient", new String[model.getColumnNames().length]);
            request.setAttribute("index", -1);
            request.getRequestDispatcher("/edit.jsp").forward(request, response);
            return;
        }

        request.setAttribute("columnNames", model.getColumnNames());
        request.setAttribute("patients", model.getAllPatients());
        request.getRequestDispatcher("/patients.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Model model = Model.getInstance();
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int index = parseIndex(request.getParameter("index"), -1);
            if (index >= 0 && index < model.getPatientCount()) {
                model.deletePatient(index);
            }
            response.sendRedirect("/patients");
            return;
        }

        String[] cols = model.getColumnNames();
        String[] values = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            String v = request.getParameter("field_" + cols[i]);
            values[i] = (v != null) ? v.trim() : "";
        }

        int index = parseIndex(request.getParameter("index"), -1);
        boolean isAdd = "add".equals(action);

        List<String> errors = model.validate(cols, values, isAdd, index);
        if (!errors.isEmpty()) {
            request.setAttribute("columnNames", cols);
            request.setAttribute("patient", values);
            request.setAttribute("index", index);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/edit.jsp").forward(request, response);
            return;
        }

        if (isAdd) {
            model.addPatient(values);
        } else if ("edit".equals(action) && index >= 0 && index < model.getPatientCount()) {
            model.updatePatient(index, values);
        }

        response.sendRedirect("/patients");
    }

    private int parseIndex(String s, int fallback) {
        if (s == null) return fallback;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return fallback; }
    }
}
