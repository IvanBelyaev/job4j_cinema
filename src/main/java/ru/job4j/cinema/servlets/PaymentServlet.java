package ru.job4j.cinema.servlets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.exception.WrongTicketException;
import ru.job4j.cinema.exception.WrongUserException;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.store.PsqlStore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class PaymentServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(PaymentServlet.class.getName());
    private ThreadLocal<User> account = new ThreadLocal<>();
    private ThreadLocal<List<Ticket>> tickets = new ThreadLocal<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("text/json");
            resp.setCharacterEncoding("UTF-8");
            parseData(req);
            int accountId = PsqlStore.instOf().addUser(account.get());
            PsqlStore.instOf().addTickets(tickets.get(), accountId);
            sendAnswer(resp, "success");
        } catch (ParseException e) {
            logger.error("Exception in doPost()", e);
        } catch (WrongUserException e) {
            sendAnswer(resp, "wrongUser");
        } catch (WrongTicketException e) {
            sendAnswer(resp, "wrongTicket");
        }
    }

    private void parseData(HttpServletRequest req) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(req.getReader());
        long sessionId = (Long) jsonObject.get("sessionId");
        JSONObject user = (JSONObject) jsonObject.get("user");
        String userName = (String) user.get("name");
        String phone = (String) user.get("phone");
        String email = (String) user.get("email");
        this.account.set(new User(-1, userName, email, phone));
        List<Ticket> list = new ArrayList<>();
        JSONArray places = (JSONArray) jsonObject.get("places");
        for (int i = 0; i < places.size(); i++) {
            JSONObject place = (JSONObject) places.get(i);
            int row = Integer.parseInt((String) place.get("row"));
            int cell = Integer.parseInt((String) place.get("cell"));
            list.add(new Ticket(sessionId, row, cell));
        }
        this.tickets.set(list);
    }

    private void sendAnswer(HttpServletResponse resp, String url) throws IOException {
        JSONObject answer = new JSONObject();
        answer.put("url", url);
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        writer.println(answer.toJSONString());
        writer.flush();
    }
}
