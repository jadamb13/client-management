package DBAccess;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** A class to communicate with the DB about Appointments. */
public class DBAppointment {

    /**
     Queries DB to gather data and create Appointment objects to be added to ObservableList.

     @return appointmentList list of Appointment objects representing data in the DB

     */
    public static ObservableList<Appointment> getAllAppointments(){
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

        try{
            // SQL statement
            String sql = "SELECT Appointment_ID, Title, Description, Location, Contact_Name, Type, Start, End, Customer_ID, User_ID, appointments.Contact_ID\n" +
                    "FROM client_schedule.appointments JOIN contacts WHERE appointments.Contact_ID = contacts.Contact_ID;";

            // Create a PreparedStatement
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);

            // Execute query and get results
            ResultSet rs = ps.executeQuery();

            // Work through result set one row at a time
            while(rs.next()){
                int appointmentId = rs.getInt("Appointment_ID");
                int customerId = rs.getInt("Customer_ID");
                int contactId = rs.getInt("Contact_ID");
                int userId = rs.getInt("User_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String contact = rs.getString("Contact_Name");
                String type = rs.getString("Type");
                Timestamp start = rs.getTimestamp("Start");
                Timestamp end = rs.getTimestamp("End");

                Appointment a = new Appointment(appointmentId, customerId, contactId, userId, title, description, location, contact, type, start, end);
                appointmentList.add(a);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return appointmentList;
    }


    /**
     Gathers data entered by user and combines with generated fields to insert new Appointments into DB.

     */
    public static void createAppointment(int customerId, int contactId, int userId, String title, String description,
                                         String location, String type, Timestamp start, Timestamp end) {
        try {
            // Get maximum Appointment ID already in table and set new Appointment ID to (max + 1)
            String max_sql = "SELECT MAX(Appointment_ID) as MAX FROM appointments";
            PreparedStatement maxPs = JDBC.getConnection().prepareStatement(max_sql);
            ResultSet rs = maxPs.executeQuery();
            rs.next();
            int appointmentId = rs.getInt("MAX") + 1;

            // Prepared statement to insert new Appointment into DB
            String sql = "INSERT INTO appointments VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // Same every time
            Timestamp createDate = new Timestamp(System.currentTimeMillis());
            String createdBy = "script";
            String lastUpdatedBy = "script";
            Timestamp lastUpdate = new Timestamp(System.currentTimeMillis());

            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, appointmentId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, location);
            ps.setString(5, type);
            ps.setTimestamp(6, start);
            ps.setTimestamp(7, end);
            ps.setTimestamp(8, createDate);
            ps.setString(9, createdBy);
            ps.setTimestamp(10, lastUpdate);
            ps.setString(11, lastUpdatedBy);
            ps.setInt(12, customerId);
            ps.setInt(13, userId);
            ps.setInt(14, contactId);

            ps.execute();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

}
