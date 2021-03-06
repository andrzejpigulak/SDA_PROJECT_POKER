package com.baza;

import com.sprawdzanie.Sprawdzenie;

import java.sql.*;

public class BazaWygrane {

    private Sprawdzenie sprawdzenie;

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost/Poker";

    private static final String LOGIN = "postgres";
    private static final String PASSWORD = "PASSWORD!";

    private Statement stmt;
    Connection conn;

    public BazaWygrane(Sprawdzenie sprawdzenie) {
        this.sprawdzenie = sprawdzenie;

        try {
            connectToDb();
            addRecordToDb();
            disconnectDB();
            showTable("wygrana");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver error " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL error " + e.getMessage());
        }
    }

    private void addRecordToDb() throws SQLException {

        for (int k = 0; k < sprawdzenie.getListaWygranychWRozdaniu().size(); k++) {

            String query = "select count(*) from wygrana";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();

            int i = rs.getInt("count");

            String record = "insert into wygrana (id_wygrana, imie, wygrana, uklad_kart)" +
                    "values (?, ?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(record);

            preparedStatement.setInt(1, ++i);
            preparedStatement.setString(2, sprawdzenie.getListaImionGraczy().get(k));
            preparedStatement.setInt(3, sprawdzenie.getListaWygranychWRozdaniu().get(k));

            if (sprawdzenie.getLiczba() == 10) {
                preparedStatement.setString(4, "Poker Królewski");
            } else if (sprawdzenie.getLiczba() == 9) {
                preparedStatement.setString(4, "Poker");
            } else if (sprawdzenie.getLiczba() == 8) {
                preparedStatement.setString(4, "Kareta");
            } else if (sprawdzenie.getLiczba() == 7) {
                preparedStatement.setString(4, "Full");
            } else if (sprawdzenie.getLiczba() == 6) {
                preparedStatement.setString(4, "Kolor");
            } else if (sprawdzenie.getLiczba() == 5) {
                preparedStatement.setString(4, "Strit");
            } else if (sprawdzenie.getLiczba() == 4) {
                preparedStatement.setString(4, "Trójka");
            } else if (sprawdzenie.getLiczba() == 3) {
                preparedStatement.setString(4, "Dwie Pary");
            } else if (sprawdzenie.getLiczba() == 2) {
                preparedStatement.setString(4, "Para");
            } else if (sprawdzenie.getLiczba() == 1) {
                preparedStatement.setString(4, "Wysoka karta");
            }

            preparedStatement.executeUpdate();
        }
    }

    private void showTable(String tableName) {

        String query = "SELECT * FROM " + tableName + ";";

        try {
            connectToDb();

            ResultSet rs = stmt.executeQuery(query); // wykonujemy połączenie

            while (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData(); // klasa do przechowywania danych
                int columCounter = rsmd.getColumnCount(); // sprawdzamy liczbę kolumn

                StringBuilder result = new StringBuilder();

                for (int i = 1; i <= columCounter; i++)
                    result = result.append(rsmd.getColumnName(i) + ":" + rs.getString(i) + "\t");
                result.append("\n");
                System.out.println(result.toString());
            }
            disconnectDB();
        } catch (ClassNotFoundException e) {
            System.err.println("Driver error " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL error " + e.getMessage());
        }
    }

    private void connectToDb() throws ClassNotFoundException, SQLException {

        Class.forName(JDBC_DRIVER); // załadowanie sterownika

        conn = DriverManager.getConnection(URL, LOGIN, PASSWORD); //wyszukał bazę

        stmt = conn.createStatement(); //nawiązanie sesji
    }

    private void disconnectDB() throws SQLException {
        stmt.close();
        conn.close();
    }
}
