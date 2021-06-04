package ru.job4j.cinema.servlets;

import org.json.simple.JSONObject;
import ru.job4j.cinema.json.JsonConverter;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HallServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int sessionId = Integer.parseInt(req.getParameter("session_id"));
        List<Ticket> ticketsForSession = PsqlStore.instOf().getTicketsForSession(sessionId);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tickets", JsonConverter.toJson(ticketsForSession));
        try (PrintWriter writer = new PrintWriter(resp.getOutputStream())) {
            String out = jsonObject.toJSONString();
            writer.println(out);
            writer.flush();
        }
    }
}
