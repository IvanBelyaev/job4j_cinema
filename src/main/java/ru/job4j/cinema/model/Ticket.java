package ru.job4j.cinema.model;

public class Ticket {
    private final long sessionId;
    private final int row;
    private final int cell;

    public Ticket(long sessionId, int row, int cell) {
        this.sessionId = sessionId;
        this.row = row;
        this.cell = cell;
    }

    public long getSessionId() {
        return sessionId;
    }

    public int getRow() {
        return row;
    }

    public int getCell() {
        return cell;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "sessionId=" + sessionId +
                ", row=" + row +
                ", cell=" + cell +
                '}';
    }
}
