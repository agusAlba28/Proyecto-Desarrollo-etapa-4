import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static DataBaseConnection instance;
    private Connection connection;
    private DataBaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/proyectodesarrollo",
                    "user_agus", "Agus2003");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static DataBaseConnection getInstance(){
        if (instance == null ){
            instance = new DataBaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Reconectar si la conexión se cerró
                String url = "jdbc:postgresql://localhost:5432/proyectodesarrollo";
                String user = "user_agus";
                String password = "Agus2003" ;
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la conexión: " + e.getMessage());
        }
        return connection;
    }
    /*public Connection getConnection() {
        return connection;
    }*/
}
