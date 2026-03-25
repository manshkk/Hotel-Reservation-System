import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url="jdbc:mysql://localhost:3306/Hotel_db";

    private static final String username="root";

    private static final String password="rewn@0123";

    private static  Statement stmt = null;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("loading  the database...");
        }
        catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());

        }
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        newReservation(connection, scanner);
                        break;
                    case 2:
                        checkReservation(connection);
                        break;
                    case 3:
                        getRoom(connection, scanner);
                        break;
                    case 4:
                        updateRoom(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public static void newReservation(Connection connection, Scanner sc) throws SQLException {
        sc.nextLine();
        System.out.print("guest name: ");
        String guestName = sc.nextLine();
        System.out.print("Room number: ");
        int  roomNumber = sc.nextInt();
        System.out.print("Contact number: ");
        String contactNumber = sc.next();
        String query = "insert into Reservation (guest_name, room_number, contact_number) values " +
                "('" + guestName + "', " + roomNumber + ", '" + contactNumber + "');";
        stmt =  connection.createStatement();
        int rowAffected = stmt.executeUpdate(query);

        if(rowAffected>0) {
            System.out.println("Reservation has been added to the database.");
        }
        else {
            System.out.println("Reservation has NOT been added to the database.");
        }
    }
    public static void checkReservation(Connection connection) throws SQLException {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from Reservation");

        while(rs.next()) {
            System.out.println("_____________________________________________________________________________________");
            System.out.println("Reservationn id: "+rs.getInt("reservation_id"));
            System.out.println("Guest name: "+rs.getString("guest_name"));
            System.out.println("Room number: "+rs.getString("room_number"));
            System.out.println("Contact number: "+rs.getString("contact_number"));
            System.out.println("Rerservation Date: "+rs.getDate("reservation_date"));

        }


    }
    public static void getRoom(Connection connection, Scanner sc) {
        sc.nextLine(); // Clear buffer from menu

        System.out.print("Enter reservation ID: ");
        int reservationId = sc.nextInt();
        sc.nextLine(); // FIX: Clear the newline left by nextInt()

        System.out.print("Enter guest name: ");
        String guestName = sc.nextLine(); // Use nextLine() in case they have a middle/last name

        // The query is now correctly spaced and quoted
        String query = "SELECT room_number FROM reservation " +
                "WHERE reservation_id = " + reservationId +
                " AND guest_name = '" + guestName + "'";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            if (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                // Added spaces for better readability
                System.out.println("Guest: " + guestName + " and Room Number: " + roomNumber);
            } else {
                System.out.println("No reservation found for ID " + reservationId + " and Name " + guestName);
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }

    public static void updateRoom(Connection connection, Scanner sc) throws SQLException {

        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine(); // Consume the newline character

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteReservation(Connection connection, Scanner sc) throws SQLException {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}
