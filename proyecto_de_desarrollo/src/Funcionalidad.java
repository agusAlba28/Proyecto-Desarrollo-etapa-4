import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Funcionalidad {
    private int id;
    private String nombre;
    private String descripcion;
    private boolean estado;

    private static final Scanner scanner = new Scanner(System.in);

    public Funcionalidad(int id, String nombre, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }

    public Funcionalidad() {
    }

    public void altaFuncionalidad (Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            System.out.println("Ingrese nombre de la funcionalidad: ");
            String nombre = scanner.nextLine();
            if (nombre == null || nombre.trim().isEmpty()) {
                System.out.println("El campo 'Nombre' no puede estar vacío.");  //verificar que los campos no esten vacíos
                return;
            }
            if (!esNombreFuncionalidadUnico(nombre)) {
                System.out.println("La funcionalidad con este nombre ya existe.");
                return;
            }


            System.out.println("Ingrese descripción de la funcionalidad: ");
            String descripcion = scanner.nextLine();
            if (descripcion == null || descripcion.trim().isEmpty()) {
                System.out.println("El campo 'Descripción' no puede estar vacío."); //verificar que los campos no esten vacíos
                return;
            }

            System.out.println("¿Seguro que desea realizar el alta de la funcionalidad? (si/no)");
            String resultado = scanner.nextLine();

            if (resultado.equals("si")) {
                Connection connection = DataBaseConnection.getInstance().getConnection();
                String verificarSql = "SELECT COUNT(*) FROM funcionalidades WHERE nombre = ?";
                String insertarSql = "INSERT INTO funcionalidades (nombre, estado, descripcion) VALUES (?, false, ?)";

                try (PreparedStatement verificarStmt = connection.prepareStatement(verificarSql);
                     PreparedStatement insertarStmt = connection.prepareStatement(insertarSql)) {

                    verificarStmt.setString(1, nombre); // Verifica si ya existe la funcionalidad
                    ResultSet resultSet = verificarStmt.executeQuery();

                    resultSet.next();
                    int conteo = resultSet.getInt(1);

                    if (conteo > 0) {
                        System.out.println("La funcionalidad con este nombre ya existe.");
                    } else {
                        insertarStmt.setString(1, nombre);  // Insertar la nueva funcionalidad
                        insertarStmt.setString(2, descripcion);
                        int filasAfectadas = insertarStmt.executeUpdate();

                        if (filasAfectadas > 0) {
                            System.out.println("Funcionalidad ingresada con éxito.");
                        } else {
                            System.out.println("No se pudo ingresar la funcionalidad.");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar ingresar la funcionalidad: " + e.getMessage());
                }
            } else {
                System.out.println("La operación se ha cancelado.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    private boolean esNombreFuncionalidadUnico(String nombre) {
        String sql = "SELECT COUNT(*) FROM funcionalidades WHERE nombre = ?";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Si no hay registros, el nombre es único
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar unicidad del nombre de la funcionalidad: " + e.getMessage());
        }
        return false;
    }

    public void modificarFuncionalidad ( Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            List<Funcionalidad> funcionalidades = listarFuncionalidades();
            if (funcionalidades.isEmpty()) {
                System.out.println("No hay funcionalidades registradas.");
            } else {
                System.out.println("Lista de funcionalidades:");
                for (Funcionalidad funcion : funcionalidades) {
                    System.out.println(funcion.toString());
                }
            }
            System.out.println();
            System.out.println("Ingrese el id de la funcionalidad a modificar: ");
            int idModificar = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Ingrese nueva descripción: ");
            String descripcionNueva = scanner.nextLine();
            Connection connection = DataBaseConnection.getInstance().getConnection();
            String sql = "UPDATE funcionalidades SET descripcion = ? WHERE id_funcionalidad = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, descripcionNueva);
                statement.setInt(2, idModificar);

                int filasModificadas = statement.executeUpdate();
                if (filasModificadas > 0) {
                    System.out.println("La operación fue realizada con éxito");
                } else {
                    System.out.println("No se encontró ningúna funcionalidad con el id especificado");
                }
            } catch (SQLException e) {
                System.out.println("Error al intentar modificar la funcionalidad: " + e.getMessage());
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }



    public void eliminarFuncionalidad (Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            List<Funcionalidad> funcionalidades = listarFuncionalidades();
            if (funcionalidades.isEmpty()) {
                System.out.println("No hay funcionalidades registradas.");
            } else {
                System.out.println("Lista de funcionalidades:");
                for (Funcionalidad funcion : funcionalidades) {
                    System.out.println(funcion.toString());
                }
            }
            System.out.println();
            System.out.println("Ingrese el id de la funcionalidad para cambiar el estado: ");
            int idModificar = scanner.nextInt();
            scanner.nextLine();
            Boolean estadoActual = obtenerEstadoFuncionalidad(idModificar);

            if (estadoActual) {
                estadoActual = false;
            } else {
                estadoActual = true;
            }

            System.out.println("¿Seguro que desea realizar la modificacion del estado? (si/no)");
            String resultado = scanner.nextLine();

            if (resultado.equals("si")) {
                Connection connection = DataBaseConnection.getInstance().getConnection();
                String sql = "UPDATE funcionalidades SET estado = ? WHERE id_funcionalidad = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setBoolean(1, estadoActual);
                    statement.setInt(2, idModificar);

                    int filasModificadas = statement.executeUpdate();
                    if (filasModificadas > 0) {
                        System.out.println("Estado de la funcionalidad modificado con éxito.");
                    } else {
                        System.out.println("No se encontró ningúna funcionalidad con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar modificar el estado de la funcionalidad: " + e.getMessage());
                }

            } else {
                System.out.println("La operación se ha cancelado.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios");
        }
    }

    private Boolean obtenerEstadoFuncionalidad(int idFuncionalidad) {
        Boolean estado = null;
        String sql = "SELECT estado FROM funcionalidades WHERE id_funcionalidad = ?";

        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idFuncionalidad);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                estado = resultSet.getBoolean("estado");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el estado de la funcionalidad: " + e.getMessage());
        }
        return estado;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + ", Nombre: " + this.nombre + ", Estado: " + this.estado;
    }

    public List<Funcionalidad> listarFuncionalidades () {
        List<Funcionalidad> funcionalidades = new ArrayList<>();
        String sql = "SELECT id_funcionalidad, nombre, estado FROM funcionalidades";
        Connection connection = DataBaseConnection.getInstance().getConnection();
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id_funcionalidad");
                String nombre = resultSet.getString("nombre");
                Boolean estado = resultSet.getBoolean("estado");

                Funcionalidad funcionalidad = new Funcionalidad(id, nombre, estado);
                funcionalidades.add(funcionalidad);
            }
        }catch (SQLException e) {
            System.out.println("Error al listar las funcionalidades: " + e.getMessage());
        }
        return funcionalidades;
    }
}