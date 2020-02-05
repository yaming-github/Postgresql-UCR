/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.*;
import javax.swing.*;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 */
public class DBProject {

    // reference to physical database connection.
    private Connection _connection = null;

    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Creates a new instance of DBProject
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public DBProject(String dbname, String dbport, String user, String passwd) throws SQLException {

        System.out.print("Connecting to database...");
        try {
            // constructs the connection URL
            String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
            System.out.println("Connection URL: " + url + "\n");

            // obtain a physical connection
            this._connection = DriverManager.getConnection(url, user, passwd);
            System.out.println("Done");
        } catch (Exception e) {
            System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
            System.out.println("Make sure you started postgres on this machine");
            System.exit(-1);
        }//end catch
    }//end DBProject

    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate(String sql) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the update instruction
        stmt.executeUpdate(sql);

        // close the instruction
        stmt.close();
    }//end executeUpdate

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery(String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery(query);

        /*
         ** obtains the metadata object for the returned result set.  The metadata
         ** contains row and column info.
         */
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCol = rsmd.getColumnCount();
        int rowCount = 0;

        // iterates through the result set and output them to standard out.
        boolean outputHeader = true;
        while (rs.next()) {
            if (outputHeader) {
                for (int i = 1; i <= numCol; i++) {
                    System.out.print(rsmd.getColumnName(i) + "\t");
                }
                System.out.println();
                outputHeader = false;
            }
            for (int i = 1; i <= numCol; ++i)
                System.out.print(rs.getString(i) + "\t");
            System.out.println();
            ++rowCount;
        }//end while
        stmt.close();
        return rowCount;
    }//end executeQuery

    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup() {
        try {
            if (this._connection != null) {
                this._connection.close();
            }//end if
        } catch (SQLException e) {
            // ignored.
        }//end try
    }//end cleanup

    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: " + "java [-classpath <classpath>] " + DBProject.class.getName()
                    + " <dbname> <port> <user>");
            return;
        }//end if

        Greeting();
        DBProject esql = null;
        try {
            // use postgres JDBC driver.
            Class.forName("org.postgresql.Driver").newInstance();
            // instantiate the DBProject object and creates a physical
            // connection.
            String dbname = args[0];
            String dbport = args[1];
            String user = args[2];
            esql = new DBProject(dbname, dbport, user, "");

            boolean keepon = true;
            while (keepon) {
                // These are sample SQL statements
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Add new customer");
                System.out.println("2. Add new room");
                System.out.println("3. Add new maintenance company");
                System.out.println("4. Add new repair");
                System.out.println("5. Add new Booking");
                System.out.println("6. Assign house cleaning staff to a room");
                System.out.println("7. Raise a repair request");
                System.out.println("8. Get number of available rooms");
                System.out.println("9. Get number of booked rooms");
                System.out.println("10. Get hotel bookings for a week");
                System.out.println("11. Get top k rooms with highest price for a date range");
                System.out.println("12. Get top k highest booking price for a customer");
                System.out.println("13. Get customer total cost occurred for a give date range");
                System.out.println("14. List the repairs made by maintenance company");
                System.out.println("15. Get top k maintenance companies based on repair count");
                System.out.println("16. Get number of repairs occurred per year for a given hotel room");
                System.out.println("17. < EXIT");

                switch (readChoice()) {
                    case 1:
                        addCustomer(esql);
                        break;
                    case 2:
                        addRoom(esql);
                        break;
                    case 3:
                        addMaintenanceCompany(esql);
                        break;
                    case 4:
                        addRepair(esql);
                        break;
                    case 5:
                        bookRoom(esql);
                        break;
                    case 6:
                        assignHouseCleaningToRoom(esql);
                        break;
                    case 7:
                        repairRequest(esql);
                        break;
                    case 8:
                        numberOfAvailableRooms(esql);
                        break;
                    case 9:
                        numberOfBookedRooms(esql);
                        break;
                    case 10:
                        listHotelRoomBookingsForAWeek(esql);
                        break;
                    case 11:
                        topKHighestRoomPriceForADateRange(esql);
                        break;
                    case 12:
                        topKHighestPriceBookingsForACustomer(esql);
                        break;
                    case 13:
                        totalCostForCustomer(esql);
                        break;
                    case 14:
                        listRepairsMade(esql);
                        break;
                    case 15:
                        topKMaintenanceCompany(esql);
                        break;
                    case 16:
                        numberOfRepairsForEachRoomPerYear(esql);
                        break;
                    case 17:
                        keepon = false;
                        break;
                    default:
                        System.out.println("Unrecognized choice!");
                        break;
                }//end switch
            }//end while
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            // make sure to cleanup the created table and close the connection.
            try {
                if (esql != null) {
                    System.out.print("Disconnecting from database...");
                    esql.cleanup();
                    System.out.println("Done\n\nBye !");
                }//end if
            } catch (Exception e) {
                // ignored.
            }//end try
        }//end try
    }//end main

    public static void Greeting() {
        System.out.println("\n\n*******************************************************\n"
                + "              User Interface      	               \n"
                + "*******************************************************\n");
    }//end Greeting

    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
        int input;
        // returns only if a correct value is given.
        do {
            System.out.print("Please make your choice: ");
            try { // read the integer, parse it and break.
                input = Integer.parseInt(in.readLine());
                break;
            } catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }//end try
        } while (true);
        return input;
    }//end readChoice

    public static void addCustomer(DBProject esql) {
        // Given customer details add the customer in the DB
        try {
            String query = "INSERT INTO Customer(customerID,fName,lName";
            System.out.print("\tEnter customerID:");
            String input1 = in.readLine();
            while (input1.isEmpty()) {
                System.out.println("customerID CANNOT be empty!");
                System.out.print("\tEnter customerID again:");
                input1 = in.readLine();
            }

            System.out.print("\tEnter fName:");
            String input2 = in.readLine();
            while (input2.isEmpty()) {
                System.out.println("fName CANNOT be empty!");
                System.out.print("\tEnter fName again:");
                input2 = in.readLine();
            }

            System.out.print("\tEnter lName:");
            String input3 = in.readLine();
            while (input3.isEmpty()) {
                System.out.println("lName CANNOT be empty!");
                System.out.print("\tEnter lName again:");
                input3 = in.readLine();
            }

            System.out.print("\tEnter Address:");
            String input4 = in.readLine();
            if (!input4.isEmpty())
                query += ",Address";

            System.out.print("\tEnter phNo:");
            String input5 = in.readLine();
            if (!input5.isEmpty())
                query += ",phNo";

            System.out.print("\tEnter DOB:");
            String input6 = in.readLine();
            if (!input6.isEmpty())
                query += ",DOB";

            System.out.print("\tEnter gender(Male/Female/Other):");
            String input7 = in.readLine();
            if (!input7.isEmpty())
                query += ",gender";

            query += ") VALUES(" + input1 + ",'" + input2 + "','" + input3 + "'";

            if (!input4.isEmpty())
                query += ",'" + input4 + "'";

            if (!input5.isEmpty())
                query += ",'" + input5 + "'";

            if (!input6.isEmpty())
                query += ",'" + input6 + "'";

            if (!input7.isEmpty())
                query += ",'" + input7 + "'";

            query += ");";

            esql.executeUpdate(query);
            System.out.println("Success!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end addCustomer

    public static void addRoom(DBProject esql) {
        // Given room details add the room in the DB
        try {
            String query = "INSERT INTO Room VALUES(";
            System.out.print("\tEnter hotelID:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("hotelID CANNOT be empty!");
                System.out.print("\tEnter hotelID again:");
                input = in.readLine();
            }
            query += input + ",";

            System.out.print("\tEnter roomNo:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("roomNo CANNOT be empty!");
                System.out.print("\tEnter roomNo again:");
                input = in.readLine();
            }
            query += input + ",'";

            System.out.print("\tEnter roomType:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("roomType CANNOT be empty!");
                System.out.print("\tEnter roomType again:");
                input = in.readLine();
            }
            query += input + "');";

            esql.executeUpdate(query);
            System.out.println("Success!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end addRoom

    public static void addMaintenanceCompany(DBProject esql) {
        // Given maintenance Company details add the maintenance company in the DB
        try {
            String query = "INSERT INTO MaintenanceCompany(cmpID,name";
            System.out.print("\tEnter cmpID:");
            String input1 = in.readLine();
            while (input1.isEmpty()) {
                System.out.println("cmpID CANNOT be empty!");
                System.out.print("\tEnter cmpID again:");
                input1 = in.readLine();
            }

            System.out.print("\tEnter name:");
            String input2 = in.readLine();
            while (input2.isEmpty()) {
                System.out.println("name CANNOT be empty!");
                System.out.print("\tEnter name again:");
                input2 = in.readLine();
            }

            System.out.print("\tEnter address:");
            String input3 = in.readLine();
            if (!input3.isEmpty())
                query += ",address";

            System.out.print("\tEnter isCertified:");
            String input4 = in.readLine();
            while (input4.isEmpty()) {
                System.out.println("isCertified CANNOT be empty!");
                System.out.print("\tEnter isCertified again:");
                input4 = in.readLine();
            }

            query += ",isCertified) VALUES(" + input1 + ",'" + input2 + "',";

            if (!input3.isEmpty())
                query += "'" + input3 + "',";

            query += input4 + ");";

            esql.executeUpdate(query);
            System.out.println("Success!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end addMaintenanceCompany

    public static void addRepair(DBProject esql) {
        // Given repair details add repair in the DB
        try {
            String query = "INSERT INTO Repair(rID,hotelID,roomNo,mCompany,repairDate";
            System.out.print("\tEnter rID:");
            String input1 = in.readLine();
            while (input1.isEmpty()) {
                System.out.println("rID CANNOT be empty!");
                System.out.print("\tEnter rID again:");
                input1 = in.readLine();
            }

            System.out.print("\tEnter hotelID:");
            String input2 = in.readLine();
            while (input2.isEmpty()) {
                System.out.println("hotelID CANNOT be empty!");
                System.out.print("\tEnter hotelID again:");
                input2 = in.readLine();
            }

            System.out.print("\tEnter roomNo:");
            String input3 = in.readLine();
            while (input3.isEmpty()) {
                System.out.println("roomNo CANNOT be empty!");
                System.out.print("\tEnter roomNo again:");
                input3 = in.readLine();
            }

            System.out.print("\tEnter mCompany:");
            String input4 = in.readLine();
            while (input4.isEmpty()) {
                System.out.println("mCompany CANNOT be empty!");
                System.out.print("\tEnter mCompany again:");
                input4 = in.readLine();
            }

            System.out.print("\tEnter repairDate:");
            String input5 = in.readLine();
            while (input5.isEmpty()) {
                System.out.println("repairDate CANNOT be empty!");
                System.out.print("\tEnter repairDate again:");
                input5 = in.readLine();
            }

            System.out.print("\tEnter description:");
            String input6 = in.readLine();
            if (!input6.isEmpty())
                query += ",description";

            System.out.print("\tEnter repairType:");
            String input7 = in.readLine();
            if (!input6.isEmpty())
                query += ",repairType";

            query += ") VALUES(" + input1 + "," + input2 + "," + input3 + "," + input4 + ",'" + input5 + "'";

            if (!input6.isEmpty())
                query += ",'" + input6;

            if (!input7.isEmpty())
                query += ",'" + input7;

            query += ");";

            esql.executeUpdate(query);
            System.out.println("Success!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end addRepair

    public static void bookRoom(DBProject esql) {
        // Given hotelID, roomNo and customer Name create a booking in the DB
        try {
            String query = "INSERT INTO Booking(bID,customer,hotelID,roomNo,bookingDate,";
            System.out.print("\tEnter bID:");
            String input1 = in.readLine();
            while (input1.isEmpty()) {
                System.out.println("bID CANNOT be empty!");
                System.out.print("\tEnter bID again:");
                input1 = in.readLine();
            }

            System.out.print("\tEnter fName:");
            String input2 = in.readLine();
            while (input2.isEmpty()) {
                System.out.println("fName CANNOT be empty!");
                System.out.print("\tEnter fName again:");
                input2 = in.readLine();
            }

            System.out.print("\tEnter lName:");
            String input3 = in.readLine();
            while (input3.isEmpty()) {
                System.out.println("lName CANNOT be empty!");
                System.out.print("\tEnter lName again:");
                input3 = in.readLine();
            }

            System.out.print("\tEnter hotelID:");
            String input4 = in.readLine();
            while (input4.isEmpty()) {
                System.out.println("hotelID CANNOT be empty!");
                System.out.print("\tEnter hotelID again:");
                input4 = in.readLine();
            }

            System.out.print("\tEnter roomNo:");
            String input5 = in.readLine();
            while (input5.isEmpty()) {
                System.out.println("roomNo CANNOT be empty!");
                System.out.print("\tEnter roomNo again:");
                input5 = in.readLine();
            }

            System.out.print("\tEnter bookingDate:");
            String input6 = in.readLine();
            while (input6.isEmpty()) {
                System.out.println("bookingDate CANNOT be empty!");
                System.out.print("\tEnter bookingDate again:");
                input6 = in.readLine();
            }

            System.out.print("\tEnter noOfPeople:");
            String input7 = in.readLine();
            if (!input7.isEmpty())
                query += "noOfPeople,";

            System.out.print("\tEnter price:");
            String input8 = in.readLine();
            while (input8.isEmpty()) {
                System.out.println("price CANNOT be empty!");
                System.out.print("\tEnter price again:");
                input8 = in.readLine();
            }

            query += "price) VALUES(" + input1 + ",(select customerid from customer where fname='" + input2
                    + "' and lname='" + input3 + "')," + input4 + "," + input5 + ",'" + input6 + "',";

            if (!input7.isEmpty())
                query += input7 + ",";

            query += input8 + ");";

            esql.executeUpdate(query);
            System.out.println("Success!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end bookRoom

    public static void assignHouseCleaningToRoom(DBProject esql) {
        // Given Staff SSN, HotelID, roomNo Assign the staff to the room
        try {
            String query = "INSERT INTO Assigned VALUES(";
            System.out.print("\tEnter asgID:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("asgID CANNOT be empty!");
                System.out.print("\tEnter asgID again:");
                input = in.readLine();
            }
            query += input + ",";

            System.out.print("\tEnter staffID:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("staffID CANNOT be empty!");
                System.out.print("\tEnter staffID again:");
                input = in.readLine();
            }
            query += input + ",";

            System.out.print("\tEnter hotelID:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("hotelID CANNOT be empty!");
                System.out.print("\tEnter hotelID again:");
                input = in.readLine();
            }
            query += input + ",";

            System.out.print("\tEnter roomNo:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("roomNo CANNOT be empty!");
                System.out.print("\tEnter roomNo again:");
                input = in.readLine();
            }
            query += input + ");";

            esql.executeUpdate(query);
            System.out.println("Success!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end assignHouseCleaningToRoom

    public static void repairRequest(DBProject esql) {
        // Given a hotelID, Staff SSN, roomNo, repairID , date create a repair request in the DB
        try {
            String query = "INSERT INTO Request VALUES(";
            System.out.print("\tEnter reqID:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("reqID CANNOT be empty!");
                System.out.print("\tEnter reqID again:");
                input = in.readLine();
            }
            query += input + ",";

            System.out.print("\tEnter managerID:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("managerID CANNOT be empty!");
                System.out.print("\tEnter managerID again:");
                input = in.readLine();
            }
            query += input + ",";

            System.out.print("\tEnter repairID:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("repairID CANNOT be empty!");
                System.out.print("\tEnter repairID again:");
                input = in.readLine();
            }
            query += input + ",'";

            System.out.print("\tEnter requestDate:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("requestDate CANNOT be empty!");
                System.out.print("\tEnter requestDate again:");
                input = in.readLine();
            }
            query += input + "'";

            System.out.print("\tEnter description:");
            input = in.readLine();
            if (!input.isEmpty())
                query += ",'" + input + "'";

            query += ");";

            esql.executeUpdate(query);
            System.out.println("Success!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end repairRequest

    public static void numberOfAvailableRooms(DBProject esql) {
        // Given a hotelID, get the count of rooms available
        try {
            String query =
                    "select a.anum-b.bnum available from\n" + "(select count(*) anum from room where room.hotelid='";
            System.out.print("\tEnter hotelID:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a hotelID!");
                System.out.print("\tEnter hotelID again:");
                input = in.readLine();
            }
            query += input + "') a, (select count(*) bnum from booking where booking.hotelid='" + input + "') b;";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end numberOfAvailableRooms

    public static void numberOfBookedRooms(DBProject esql) {
        // Given a hotelID, get the count of rooms booked
        try {
            String query = "select count(*) booked from booking where booking.hotelid='";
            System.out.print("\tEnter hotelID:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a hotelID!");
                System.out.print("\tEnter hotelID again:");
                input = in.readLine();
            }
            query += input + "';";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end numberOfBookedRooms

    public static void listHotelRoomBookingsForAWeek(DBProject esql) {
        // Given a hotelID, date - list all the rooms available for a week(including the input date)
        try {
            String query = "select roomno from room where hotelid='";
            System.out.print("\tEnter hotelID:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a hotelID!");
                System.out.print("\tEnter hotelID again:");
                input = in.readLine();
            }
            query += input + "' and roomno not in(select roomno from booking where booking.hotelid='" + input
                    + "'and bookingdate>='";

            System.out.print("\tEnter date(DD/MM/YYYY):");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a date!");
                System.out.print("\tEnter date again(DD/MM/YYYY):");
                input = in.readLine();
            }
            query += input + "' and bookingdate<date'" + input + "'+integer'7');";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end listHotelRoomBookingsForAWeek

    public static void topKHighestRoomPriceForADateRange(DBProject esql) {
        // List Top K Rooms with the highest price for a given date range
        try {
            String query = "select price from booking where bookingdate>='";
            System.out.print("\tEnter date from(DD/MM/YYYY):");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a start date!");
                System.out.print("\tEnter start date again:");
                input = in.readLine();
            }
            query += input + "' and bookingdate<='";

            System.out.print("\tEnter to(DD/MM/YYYY):");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter an end date!");
                System.out.print("\tEnter end date again(DD/MM/YYYY):");
                input = in.readLine();
            }
            query += input + "' order by price desc limit ";

            System.out.print("\tEnter top k:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter top k!");
                System.out.print("\tEnter top k again:");
                input = in.readLine();
            }
            query += input + ";";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end topKHighestRoomPriceForADateRange

    public static void topKHighestPriceBookingsForACustomer(DBProject esql) {
        // Given a customer Name, List Top K highest booking price for a customer
        try {
            String query = "select price from booking b,customer c where c.fname='";
            System.out.print("\tEnter fName:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a fName!");
                System.out.print("\tEnter fName again:");
                input = in.readLine();
            }
            query += input + "' and c.lname='";

            System.out.print("\tEnter lName:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a lName!");
                System.out.print("\tEnter lName again:");
                input = in.readLine();
            }
            query += input + "' and b.customer=c.customerid order by price desc limit ";

            System.out.print("\tEnter top k:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter top k!");
                System.out.print("\tEnter top k again:");
                input = in.readLine();
            }
            query += input + ";";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end topKHighestPriceBookingsForACustomer

    public static void totalCostForCustomer(DBProject esql) {
        // Given a hotelID, customer Name and date range get the total cost incurred by the customer
        try {
            String query = "select sum(price) from booking b,customer c where b.hotelid='";
            System.out.print("\tEnter hotelID:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a hotelID!");
                System.out.print("\tEnter hotelID again:");
                input = in.readLine();
            }
            query += input + "' and c.fname='";

            System.out.print("\tEnter fName:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a fName!");
                System.out.print("\tEnter fName again:");
                input = in.readLine();
            }
            query += input + "' and c.lname='";

            System.out.print("\tEnter lName:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a lName!");
                System.out.print("\tEnter lName again:");
                input = in.readLine();
            }
            query += input + "' and b.customer=c.customerid and b.bookingdate>='";

            System.out.print("\tEnter date from(DD/MM/YYYY):");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a start date!");
                System.out.print("\tEnter date from again(DD/MM/YYYY):");
                input = in.readLine();
            }
            query += input + "' and\n" + "b.bookingdate<='";

            System.out.print("\tEnter to(DD/MM/YYYY):");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter an end date!");
                System.out.print("\tEnter end date again(DD/MM/YYYY):");
                input = in.readLine();
            }
            query += input + "';";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end totalCostForCustomer

    public static void listRepairsMade(DBProject esql) {
        // Given a Maintenance company name list all the repairs along with repairType, hotelID and roomNo
        try {
            String query = "select rid,hotelid,roomno,repairtype from repair r,maintenancecompany m where m.name='";
            System.out.print("\tEnter company name:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a company name!");
                System.out.print("\tEnter company name again:");
                input = in.readLine();
            }
            query += input + "' and r.mcompany=m.cmpid;";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end listRepairsMade

    public static void topKMaintenanceCompany(DBProject esql) {
        // List Top K Maintenance Company Names based on total repair count (descending order)
        try {
            String query = "select name " + "from repair r,maintenancecompany m " + "where r.mcompany=m.cmpid "
                    + "group by name " + "order by count(*) desc " + "limit ";
            System.out.print("\tEnter top k:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter top k!");
                System.out.print("\tEnter top k again:");
                input = in.readLine();
            }
            query += input + ";";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end topKMaintenanceCompany

    public static void numberOfRepairsForEachRoomPerYear(DBProject esql) {
        // Given a hotelID, roomNo, get the count of repairs per year
        try {
            String query = "select extract(year from repairdate),count(*) from repair where hotelid='";
            System.out.print("\tEnter hotelID:");
            String input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a hotelID!");
                System.out.print("\tEnter hotelID again:");
                input = in.readLine();
            }
            query += input + "' and roomno='";

            System.out.print("\tEnter roomNo:");
            input = in.readLine();
            while (input.isEmpty()) {
                System.out.println("Please enter a roomNo!");
                System.out.print("\tEnter roomNo again:");
                input = in.readLine();
            }
            query += input + "' group by extract(year from repairdate);";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//end listRepairsMade

}//end DBProject
