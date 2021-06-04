package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import java.util.List;

public interface Store {
    List<Ticket> getTicketsForSession(int sessionId);
    void addUser(User user);
    int findUserByEmail(String email);
    boolean addTickets(List<Ticket> tickets, int accountId);
}
