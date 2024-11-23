import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TipoActividad {
    private int id;
    private String nombre;
    private String descripcion;

    private static final Scanner scanner = new Scanner(System.in);

    public TipoActividad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public TipoActividad() {
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void ingresarTipoActividad (Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            System.out.println("Ingrese nombre del tipo de actividad: ");
            String nombre = scanner.nextLine();
            System.out.println("Ingrese descripción del tipo de actividad: ");
            String descripcion = scanner.nextLine();

           /* if (nombre.isEmpty() || descripcion.isEmpty()) {
                System.out.println("El nombre y la descripción no pueden estar vacíos.");
                return;
            }  para validar que el nombre o la descripcion no esten vacios */

            Connection connection = DataBaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO tipos_actividades (nombre, descripcion) VALUES (?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombre);
                statement.setString(2,descripcion);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error al intentar ingresar un tipo de actividad " + e.getMessage());
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    public void modificarTipoActividad (Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            listarTiposActividades();
            System.out.println("Ingrese id del tipo de actividad a modificar: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Ingrese nueva descripción: ");
            String descripcion = scanner.nextLine();

           /* if (descripcion.isEmpty()) {
                System.out.println("La descripción no puede estar vacía.");
                return;
            } */

            Connection connection = DataBaseConnection.getInstance().getConnection();
            String sql = "UPDATE tipos_actividades SET descripcion = ? WHERE id_tipoact = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, descripcion);
                statement.setInt(2, id);

                int filasModificadas = statement.executeUpdate();
                if (filasModificadas > 0) {
                    System.out.println("Tipo de actividad modificada con éxito.");
                } else {
                    System.out.println("No se encontró ningún tipo de actividad con el ID especificado.");
                }
            } catch (SQLException e) {
                System.out.println("Error al intentar modificar el tipo de actividad: " + e.getMessage());
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    public List<TipoActividad> listarTiposActividades() {
        List<TipoActividad> tipoActividades = new ArrayList<>();
        String sql = "SELECT id_tipoact , nombre FROM tipos_actividades";
        Connection connection = DataBaseConnection.getInstance().getConnection();
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id_tipoact");
                String nombre = resultSet.getString("nombre");

                TipoActividad tipoActividad = new TipoActividad(id, nombre);
                tipoActividades.add(tipoActividad);
            }
        }catch (SQLException e) {
            System.out.println("Error al listar los tipos de actividades: " + e.getMessage());
        }
        return tipoActividades;
    }

    public void eliminarTipoActividad (Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            listarTiposActividades();
            System.out.println("Ingrese el id del tipo de actividad a eliminar: ");
            int id= scanner.nextInt();
            scanner.nextLine();

            System.out.println("¿Está seguro de que desea eliminar el tipo de actividad con ID " + id + "? (Si/No)");
            String confirmacion = scanner.nextLine();
            if (confirmacion.equalsIgnoreCase("Si")) {

                Connection connection = DataBaseConnection.getInstance().getConnection();
                String sql = "DELETE FROM tipos_actividades WHERE id_tipoact = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, id);

                    int filasEliminadas = statement.executeUpdate();
                    if (filasEliminadas > 0) {
                        System.out.println("Tipo de actividad con ID " + id + " eliminado con éxito.");
                    } else {
                        System.out.println("No se encontró ningún tipo de actividad con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar eliminar el tipo de actividad: " + e.getMessage());
                }
            }else {
                System.out.println("Operación cancelada.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }
}
