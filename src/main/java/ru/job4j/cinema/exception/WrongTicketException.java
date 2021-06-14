package ru.job4j.cinema.exception;

import java.sql.SQLException;

public class WrongTicketException extends CinemaException {

    public WrongTicketException(SQLException ex) {
        super(ex);
    }

    public WrongTicketException(String message) {
        super(message);
    }
}
