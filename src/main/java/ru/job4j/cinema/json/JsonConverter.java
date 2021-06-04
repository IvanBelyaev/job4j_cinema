package ru.job4j.cinema.json;

import org.json.simple.JSONObject;
import ru.job4j.cinema.model.Ticket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonConverter {
    public static String toJson(Collection<Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Ticket ticket : tickets) {
            sb.append('{');
            sb.append(JSONObject.toString("row", ticket.getRow()));
            sb.append(',');
            sb.append(JSONObject.toString("cell", ticket.getCell()));
            sb.append("},");
        }
        sb.setLength(sb.length() - 1);
        sb.append(']');
        return sb.toString();
    }

    public static void main(String[] args) {
        Ticket ticket1 = new Ticket(1, 2, 3);
        Ticket ticket2 = new Ticket(4, 5, 6);
        List<Ticket> list = new ArrayList<>();
        list.add(ticket1);
        list.add(ticket2);
        System.out.println(toJson(list));
    }
}
