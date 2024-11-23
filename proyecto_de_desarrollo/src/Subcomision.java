import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Subcomision {
    private String nombre;
    private String descripcion;

    public Subcomision(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void devolverDescripcionSubcomision (String nombre) {
        Connection connection = DataBaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM subcomisiones WHERE nombre = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String descripcion = resultSet.getString("descripcion");
                    System.out.println("Descripción: " + descripcion);
                } else {
                    System.out.println("No se encontraron resultados para esa subcomisión. " +
                            "Verifique que los datos ingresados sean correctos.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar devolver la descripción:" + e.getMessage());
        }
    }
}
