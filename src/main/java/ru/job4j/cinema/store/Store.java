package ru.job4j.cinema.store;

import ru.job4j.cinema.exception.WrongTicketException;
import ru.job4j.cinema.exception.WrongUserException;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import java.util.List;

public interface Store {
    List<Ticket> getTicketsForSession(int sessionId);
    int addUser(User user) throws WrongUserException;
    void addTickets(List<Ticket> tickets, int accountId) throws WrongTicketException;
}
