import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Auditoria {
    private Date fechaHora;
    private String terminal;
    private Usuario usuario;
    private String operacion;

    public void registrarOperacion(int idUsuario, int idFuncionalidad ) {
        String verificarSql = "SELECT COUNT(*) FROM auditoria WHERE id_usuario = ? AND id_funcionalidad = ?";
        String sql = "INSERT INTO auditoria (id_usuario, fecha_hora, id_funcionalidad) VALUES (?, CURRENT_TIMESTAMP, ?)";

        Connection connection = DataBaseConnection.getInstance().getConnection();
        try (PreparedStatement verificarStatement = connection.prepareStatement(verificarSql)) {   // Verificar si ya existe un registro de auditoría para este usuario y funcionalidad
            verificarStatement.setInt(1, idUsuario);
            verificarStatement.setInt(2, idFuncionalidad);

            ResultSet resultSet = verificarStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {      // Solo insertar si no hay registros previos
                try (PreparedStatement insertarStatement = connection.prepareStatement(sql)) {
                    insertarStatement.setInt(1, idUsuario);
                    insertarStatement.setInt(2, idFuncionalidad);

                    insertarStatement.executeUpdate();
                    System.out.println("Operación registrada en la auditoría.");
                }
            } else {
                System.out.println("Operación ya fue auditada anteriormente.");
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar operación en auditoría: " + e.getMessage());
        }
    }

    public void generarReporte (Date fechaInicio, Date fechaFin) {
        String sql = "SELECT u.nombre, u.apellido, f.nombre_funcionalidad AS funcionalidad_nombre, a.fecha_hora " +
                "FROM auditoria a " +
                "JOIN usuarios u ON a.id_usuario = u.id_usuario " +
                "JOIN funcionalidades f ON a.id_funcionalidad = f.id_funcionalidad " +
                "WHERE a.fecha_hora BETWEEN ? AND ? " +
                "ORDER BY a.fecha_hora DESC";

        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime()));
            statement.setTimestamp(2, new java.sql.Timestamp(fechaFin.getTime()));

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Reporte de Auditoría:");
                System.out.println("----------------------");

                boolean hayRegistros = false;

                while (resultSet.next()) {
                    hayRegistros = true;
                    String nombre = resultSet.getString("nombre");
                    String apellido = resultSet.getString("apellido");
                    String funcionalidad = resultSet.getString("funcionalidad_nombre");
                    String fechaHora = resultSet.getTimestamp("fecha_hora").toString();

                    System.out.println("El usuario " + nombre + " " + apellido +
                            " realizó la operación " + funcionalidad + " " +
                            " el día " + fechaHora);
                    System.out.println("----------------------");
                }
                if (!hayRegistros) {
                    System.out.println("No se encontraron registros en el período especificado. Seleccione una fecha válida.");
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al obtener el registro de auditoría: " + e.getMessage());
        }
    }
}
