import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaSocio {
    private String nombre;
    private String descripcion;

    public CategoriaSocio(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void devolverDescripcionCategoria (String nombre) {
        Connection connection = DataBaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM categorias_socios WHERE nombre = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String descripcion = resultSet.getString("descripcion");
                    System.out.println("Descripción: " + descripcion);
                } else {
                    System.out.println("No se encontró ninguna descripción para esa categoria." +
                            " Verifique que los datos ingresados sean correctos.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar devolver la descripción:" + e.getMessage());
        }
    }
}
