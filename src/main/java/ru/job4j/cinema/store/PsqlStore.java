package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.exception.WrongTicketException;
import ru.job4j.cinema.exception.WrongUserException;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private Logger logger = LoggerFactory.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(new FileReader("db.properties"))) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public List<Ticket> getTicketsForSession(int sessionId) {
        List<Ticket> result = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM ticket WHERE session_id = ?")) {
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int row = rs.getInt("row");
                int cell = rs.getInt("cell");
                result.add(new Ticket(sessionId, row, cell));
            }
        } catch (SQLException ex) {
            logger.error("Exception in getTicketsForSession()", ex);
        }
        return result;
    }

    @Override
    public int addUser(User user) throws WrongUserException {
        User userFromBD = findUserByEmail(user.getEmail());
        if (userFromBD != null) {
            if (!userFromBD.equals(user)) {
                throw new WrongUserException("Similar user already exists");
            }
        } else {
            insertUser(user);
            userFromBD = findUserByEmail(user.getEmail());
        }
        return userFromBD.getId();
    }

    private User findUserByEmail(String email) {
        User user = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement
                     ("SELECT * FROM account WHERE email = ?")
        ) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String phone = rs.getString("phone");
                user = new User(id, username, email, phone);
            }
        } catch (SQLException ex) {
            logger.error("Exception in findUserByEmail()", ex);
        }
        return user;
    }

    private void insertUser(User user) throws WrongUserException {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement
                     ("INSERT INTO account (username, email, phone) VALUES (?, ?, ?)")
        ) {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("unique constraint")) {
                throw new WrongUserException(ex);
            } else {
                logger.error("Exception in getTicketsForSession()", ex);
            }
        }
    }

    @Override
    public void addTickets(List<Ticket> tickets, int accountId) throws WrongTicketException {
        try (Connection cn = pool.getConnection()) {
            try (PreparedStatement ps = cn.prepareStatement
                    ("INSERT INTO ticket (session_id, row, cell, account_id) values (?, ?, ?, ?)")
            ) {
                cn.setAutoCommit(false);
                for (Ticket ticket : tickets) {
                    ps.setLong(1, ticket.getSessionId());
                    ps.setInt(2, ticket.getRow());
                    ps.setInt(3, ticket.getCell());
                    ps.setInt(4, accountId);
                    ps.addBatch();
                }
                ps.executeBatch();
                cn.commit();
                cn.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.error("Exception in addTickets()", ex);
                logger.error("The transaction will be rolled back.");
                cn.rollback();
                throw new WrongTicketException("These tickets are already sold out");
            }
        } catch (SQLException ex) {
            logger.error("Exception in addTickets()", ex);
        }
    }
}
