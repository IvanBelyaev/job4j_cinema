package ru.job4j.cinema.exception;

import java.sql.SQLException;

public class CinemaException extends Exception {
    public CinemaException(SQLException ex) {
        super(ex);
    }

    public CinemaException(String message) {
        super(message);
    }
}
