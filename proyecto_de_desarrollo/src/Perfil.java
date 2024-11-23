import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Perfil {
    private int id;

    private String nombre;
    private String descripcion;
    private Boolean estado;

    private static final Scanner scanner = new Scanner(System.in);
    public Perfil(String nombre, String descripcion, boolean estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Perfil() {
    }

    public Perfil(int id, String nombre, Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }


    public String getNombre() {
        return nombre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void ingresarPerfil(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            System.out.println("Ingrese el nombre del perfil: ");
            String nombre = scanner.nextLine();
            if (nombre == null || nombre.trim().isEmpty()) {
                System.out.println("El campo 'Nombre' no puede estar vacío.");  //verificar que los campos no esten vacíos
                return;
            }
            if (!esNombrePerfilUnico(nombre)) {
                System.out.println("El perfil con este nombre ya existe.");
                return;
            }

            System.out.println("Ingrese la descripción del perfil: ");
            String descripcion = scanner.nextLine();
            if (descripcion == null || descripcion.trim().isEmpty()) {
                System.out.println("El campo 'Descripción' no puede estar vacío."); //verificar que los campos no esten vacíos
                return;
            }

            System.out.println("¿Seguro que desea realizar el alta del perfil? (si/no)");
            String resultado = scanner.nextLine();

            if (resultado.equals("si")) {
                Connection connection = DataBaseConnection.getInstance().getConnection();
                String sql = "INSERT INTO perfiles (nombre_perfil, estado, descripcion) VALUES (?, false, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, nombre);
                    statement.setString(2, descripcion);

                    int filasAfectadas = statement.executeUpdate();
                    if (filasAfectadas > 0) {
                        System.out.println("Perfil ingresado correctamente con estado inactivo.");
                    } else {
                        System.out.println("No se pudo ingresar el perfil.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al ingresar el perfil: " + e.getMessage());
                }
            } else {
                System.out.println("La operación se ha cancelado.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    private boolean esNombrePerfilUnico(String nombre) {
        String sql = "SELECT COUNT(*) FROM perfiles WHERE nombre_perfil = ?";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Si no hay registros, el nombre es único
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar unicidad del nombre del perfil: " + e.getMessage());
        }
        return false;
    }

    public void modificarPerfil(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            listarPerfiles();
            System.out.println("Ingrese el id del perfil a modificar: ");
            int idModificar = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Ingrese la nueva descripción: ");
            String descripcionModificar = scanner.nextLine();

            Connection connection = DataBaseConnection.getInstance().getConnection();
            String sql = "UPDATE perfiles SET descripcion = ? WHERE id_perfil = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, descripcionModificar);
                statement.setInt(2, idModificar);

                int filasModificadas = statement.executeUpdate();
                if (filasModificadas > 0) {
                    System.out.println("Descripción modificada con éxito.");
                } else {
                    System.out.println("No se encontró ningún perfil con el ID especificado.");
                }
            } catch (SQLException e) {
                System.out.println("Error al intentar modificar la descripción: " + e.getMessage());
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    public void eliminarPerfil (Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            listarPerfiles();
            System.out.println("Ingrese el id del perfil a eliminar: ");
            int idEliminar = scanner.nextInt();
            scanner.nextLine();

            Boolean estadoActual = obtenerEstadoPerfil(idEliminar);

            if (estadoActual) {
                estadoActual = false;
            } else {
                estadoActual = true;
            }

            System.out.println("¿Seguro que desea realizar la modificacion del estado? (si/no)");
            String resultado = scanner.nextLine();

            if (resultado.equals("si")) {
                Connection connection = DataBaseConnection.getInstance().getConnection();
                String sql = "UPDATE perfiles SET estado = ? WHERE id_perfil = ? ";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setBoolean(1, estadoActual);
                    statement.setInt(2, idEliminar);

                    int filasEliminadas = statement.executeUpdate();

                    if (filasEliminadas > 0) {
                        System.out.println("Perfil eliminado con éxito.");
                    } else {
                        System.out.println("No se encontró ningún perfil con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar eliminar el perfil: " + e.getMessage());
                }
            } else {
                System.out.println("La operación se ha cancelado.");
            }
        }else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    private Boolean obtenerEstadoPerfil(int idPerfil) {
        Boolean estado = null;
        String sql = "SELECT estado FROM perfiles WHERE id_perfil = ?";

        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPerfil);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                estado = resultSet.getBoolean("estado");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el estado del perfil: " + e.getMessage());
        }
        return estado;
    }


    @Override
    public String toString() {
        return "ID: " + this.id + ", Nombre Perfil: " + this.nombre + ", Estado: " + this.estado;
    }
    public List<Perfil> listarPerfiles() {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT id_perfil, nombre_perfil, estado FROM perfiles";
        Connection connection = DataBaseConnection.getInstance().getConnection();
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id_perfil");
                String nombre = resultSet.getString("nombre_perfil");
                Boolean estado = resultSet.getBoolean("estado");

                Perfil perfil = new Perfil(id, nombre, estado);
                perfiles.add(perfil);
            }
        }catch (SQLException e) {
            System.out.println("Error al listar los perfiles: " + e.getMessage());
        }
        return perfiles;
    }
}
