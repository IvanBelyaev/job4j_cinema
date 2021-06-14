package ru.job4j.cinema.exception;

import java.sql.SQLException;

public class WrongUserException extends CinemaException {
    public WrongUserException(SQLException ex) {
        super(ex);
    }

    public WrongUserException(String s) {
        super(s);
    }
}
