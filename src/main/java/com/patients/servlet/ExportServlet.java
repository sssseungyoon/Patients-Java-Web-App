package com.patients.servlet;

import com.patients.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/export")
public class ExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Model model = Model.getInstance();

        // Save a copy alongside the CSV on disk
        String jsonFilePath = model.getFilePath().replaceAll("\\.csv$", ".json");
        try {
            model.saveToJSON(jsonFilePath);
        } catch (IOException e) {
            System.err.println("Warning: could not save JSON to disk: " + e.getMessage());
        }

        // Stream the same JSON to the browser as a download
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"patients.json\"");
        model.writeJSON(resp.getWriter());
    }
}
