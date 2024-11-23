import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Actividad {

    private String nombre;
    private int id;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime hora;
    private double costo;
    private TipoActividad tipoActividad;
    private Espacio espacio;
    private Boolean estado;
    private Boolean inscripcion;

    private Date fecha_inscripcion;

    private static final Scanner scanner = new Scanner(System.in);

    public Actividad() {
        this.tipoActividad = new TipoActividad();
    }

    public Actividad(String nombre, String descripcion, LocalDate fecha, TipoActividad tipoActividad, Espacio espacio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.tipoActividad = tipoActividad;
        this.espacio = espacio;
    }

    public Actividad( int id, String nombre, LocalDate fecha, double costo, TipoActividad tipoActividad,  boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.costo = costo;
        this.tipoActividad = tipoActividad;
        this.estado = estado;
    }

    public Actividad( int id, String nombre, LocalDate fecha, LocalTime hora, double costo,Boolean inscripcion, Date fecha_inscripcion, TipoActividad tipoActividad, Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.costo = costo;
        this.tipoActividad = tipoActividad;
        this.estado = estado;
        this.inscripcion = inscripcion;
        this.fecha_inscripcion = fecha_inscripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void ingresarActividad(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            System.out.println("Ingrese nombre de la actividad: ");
            String nombre = scanner.nextLine();
            System.out.println("Ingrese descripción/objetivo de la actividad: ");
            String descripcion = scanner.nextLine();
            System.out.println("Seleccione el tipo de actividad: (nombre)");
            System.out.println("Tipos de actividades disponibles: ");
            for (TipoActividad tipo : tipoActividad.listarTiposActividades()) {
                System.out.println("ID: " + tipo.getId() + ", Nombre: " + tipo.getNombre());
            }
            String tipoactividad = scanner.nextLine();

            System.out.println("Ingrese fecha de la actividad (formato dd-MM-yyyy): ");
            LocalDate fecha = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            System.out.println("Ingrese hora de la actividad (formato HH:mm): ");
            LocalTime hora = LocalTime.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("HH:mm"));

            System.out.println("Ingrese duración de la atividad: (en horas)");
            int duracion = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Ingrese lugar de la actividad: (nombre)");
            //listarespacios (metodo de espacios)
            String lugar = scanner.nextLine();
            String lugarDisponible =verificarDisponibilidadLugar(fecha, hora, duracion);
            if (lugarDisponible == null) {
                System.out.println("No hay espacios disponibles en la fecha y hora seleccionada.");
                return;
            }
            System.out.println("¿Requiere inscripción? (True/False): ");
            Boolean requiereInscripcion = scanner.nextBoolean();

            String aperturaInscripcion= null;
            if (requiereInscripcion) {
                System.out.println("Ingrese la fecha de apertura de inscripción: (formato dd-mm-yyyy)");
                aperturaInscripcion = scanner.nextLine();
            }
            System.out.println("Ingrese costo de la actividad: (pesos)");
            double costo = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("Seleccione la forma de pago: ");
            System.out.println("1. Efectivo");
            System.out.println("2. Transferencia");
            System.out.println("3. Débito");
            System.out.println("4. Crédito");
            int opcion = scanner.nextInt();
            scanner.nextLine();
            String formaPago = " ";
            switch (opcion) {
                case 1:
                    formaPago = "Efectivo";
                    break;
                case 2:
                    formaPago = "Transferencia";
                    break;
                case 3:
                    formaPago = "Débito";
                    break;
                case 4:
                    formaPago = "Crédito";
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
            String observaciones = null;
            System.out.println("Ingrese observaciones si desea: ");
            observaciones = scanner.nextLine();
            System.out.println("¿Seguro que desea ingresar la actividad? (Si/No)");
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("si")) {
                String sqlInsertActividad = "INSERT INTO actividades (nombre, fecha_act, hora_act, lugar, duracion, " +
                        "costo, descripcion, observacion, requiere_inscripcion, fecha_apertura_ins," +
                        "forma_pago, id_usuario, tipo_actividad, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, false)";

                try (Connection connection = DataBaseConnection.getInstance().getConnection();
                     PreparedStatement statement = connection.prepareStatement(sqlInsertActividad)) {

                    statement.setString(1, nombre);
                    statement.setDate(2, java.sql.Date.valueOf(fecha));
                    statement.setTime(3, java.sql.Time.valueOf(hora));
                    statement.setString(4,lugar);
                    statement.setInt(5, duracion);
                    statement.setDouble(6,costo);
                    statement.setString(7, descripcion);
                    statement.setString(8, observaciones);
                    statement.setBoolean(9, requiereInscripcion);
                    statement.setString(10, aperturaInscripcion);
                    statement.setString(11, formaPago);
                    statement.setInt(12, usuario.getId());
                    statement.setString(13, tipoactividad);

                    int filasAfectadas = statement.executeUpdate();
                    if (filasAfectadas > 0) {
                        System.out.println("Actividad registrada con éxito y lugar reservado.");
                    } else {
                        System.out.println("Error al registrar la actividad.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar registrar la actividad: " + e.getMessage());
                }
            } else {
                System.out.println("Ingreso de actividad cancelado.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    public void modificarActividad(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            listarActividades();
            System.out.println("Ingrese el ID de la actividad a modificar: ");
            int idModificar = scanner.nextInt();
            scanner.nextLine();
            Actividad actividad = obtenerActividadPorId(idModificar);
            if (actividad == null) {
                System.out.println("No se encontró la actividad con el ID especificado.");
                return;
            }

            LocalDate fechaActual = LocalDate.now();
            if (actividad.getFecha().isBefore(fechaActual)) {
                System.out.println("No se pueden modificar actividades que ya han comenzado o finalizado.");
                return ;
            }
            System.out.println("Modificar fecha de la actividad (formato dd-MM-yyyy): ");
            String nuevaFechaStr = scanner.nextLine();
            LocalDate nuevaFecha = LocalDate.parse(nuevaFechaStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            System.out.println("Modificar costo de la actividad: ");
            double nuevoCosto = scanner.nextDouble();
            scanner.nextLine(); // Consumir la línea

            System.out.println("Modificar tipo de actividad: ");
            String nuevoTipoNombre = scanner.nextLine();
            TipoActividad nuevoTipoActividad = obtenerTipoActividadPorNombre(nuevoTipoNombre);

            System.out.println("Modificar estado de la actividad (true/false): ");
            boolean nuevoEstado = scanner.nextBoolean();

            System.out.println("¿Desea confirmar las modificaciones? (Si/No)");    // Confirmación
            String confirmacion = scanner.next();

            if (confirmacion.equalsIgnoreCase("si")) {
                String sql = "UPDATE actividades SET fecha_act = ?, costo = ?, tipo_actividad_id = ?, estado = ? " +
                        "WHERE cod_actividad = ?";
                try (Connection connection = DataBaseConnection.getInstance().getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setDate(1, Date.valueOf(nuevaFecha));
                    statement.setDouble(2, nuevoCosto);
                    statement.setInt(3, nuevoTipoActividad.getId());
                    statement.setBoolean(4, nuevoEstado);
                    statement.setInt(5, idModificar);

                    int filasModificadas = statement.executeUpdate();
                    if (filasModificadas > 0) {
                        System.out.println("Actividad modificada con éxito.");
                        listarActividades();
                    } else {
                        System.out.println("No se encontró la actividad para modificar.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al modificar la actividad: " + e.getMessage());
                }
            } else {
                System.out.println("Modificación de actividad cancelada.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios. ");
        }
    }

    public void eliminarActividad ( Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            listarActividades();
            System.out.println("Ingrese ID de la actividad a eliminar: ");
            int idEliminar = scanner.nextInt();
            scanner.nextLine();

            Boolean estadoActual = obtenerEstadoActividad(idEliminar);
            if (estadoActual) {
                estadoActual = false;
            } else {
                estadoActual = true;
            }

            System.out.println("¿Seguro que desea realizar la modificacion del estado? (si/no)");
            String resultado = scanner.nextLine();
            if (resultado.equalsIgnoreCase("si")) {
                Connection connection = DataBaseConnection.getInstance().getConnection();
                String sql = "UPDATE actividades SET estado = ? WHERE cod_actividad = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setBoolean(1, estadoActual);
                    statement.setInt(2, idEliminar);
                    int filasModificadas = statement.executeUpdate();
                    if (filasModificadas > 0) {
                        System.out.println("Estado de la actividad modificado con éxito.");
                    } else {
                        System.out.println("No se encontró ningúna actividad con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar modificar el estado de la actividad: " + e.getMessage());
                }
            } else {
                System.out.println("Eliminación de actividad cancelado.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }


    private Boolean obtenerEstadoActividad (int id){
        Boolean estado = null;
        String sql = "SELECT estado FROM actividades WHERE cod_actividad = ?";

        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                estado = resultSet.getBoolean("estado");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el estado de la actividad: " + e.getMessage());
        }
        return estado;
    }

    private String verificarDisponibilidadLugar(LocalDate fecha, LocalTime hora, int duracion) {
        String lugarDisponible = null;
        String sqlConsultaLugares = "SELECT nombre FROM salones WHERE nombre NOT IN ( " +
                " SELECT lugar FROM actividades WHERE fecha_act = ? AND ( " +
               " (hora_act BETWEEN ? AND ?) OR (? BETWEEN hora_act AND hora_act + (duracion || ' minute')::INTERVAL))) LIMIT 1 ";

            try (Connection connection = DataBaseConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement(sqlConsultaLugares)) {
                LocalTime horaFin = hora.plusMinutes(duracion);

                statement.setDate(1, java.sql.Date.valueOf(fecha));
                statement.setTime(2, java.sql.Time.valueOf(hora));
                statement.setTime(3,java.sql.Time.valueOf(horaFin));
                statement.setTime(4,java.sql.Time.valueOf(hora));

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    lugarDisponible = resultSet.getString("nombre");
                }
            } catch (SQLException e) {
                System.out.println("Error al verificar disponibilidad del lugar: " + e.getMessage());
            }
            return lugarDisponible;
        }

        public TipoActividad obtenerTipoActividadPorNombre(String nombre) {
            String sql = "SELECT id_tipoact, nombre FROM tipos_actividades WHERE nombre = ?";

            try (Connection connection = DataBaseConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombre);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Obtener los datos del tipo de actividad
                        int idTipoActividad = resultSet.getInt("id_tipoact");
                        String tipoActividadNombre = resultSet.getString("nombre");

                        return new TipoActividad(idTipoActividad, tipoActividadNombre);   // Crear y retornar el objeto TipoActividad
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener el tipo de actividad por nombre: " + e.getMessage());
            }
            return null; // Si no se encuentra, retorna null
        }

        public Actividad obtenerActividadPorId(int idActividad) {
            String sql = "SELECT a.cod_actividad, a.nombre, a.fecha_act, a.costo, a.estado, " +
                    "t.id_tipoact, t.nombre AS tipo_nombre " +
                    "FROM actividades a " +
                    "JOIN tipos_actividades t ON a.cod_actividad = t.id_tipoact " +
                    "WHERE a.cod_actividad = ?";

            try (Connection connection = DataBaseConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, idActividad);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("cod_actividad");
                        String nombre = resultSet.getString("nombre");
                        LocalDate fecha = resultSet.getDate("fecha_act").toLocalDate();
                        double costo = resultSet.getDouble("costo");
                        boolean estado = resultSet.getBoolean("estado");

                        int tipoActividadId = resultSet.getInt("id_tipoact");
                        String tipoNombre = resultSet.getString("tipo_nombre");
                        TipoActividad tipoActividad = new TipoActividad(tipoActividadId, tipoNombre);

                        return new Actividad(id, nombre, fecha, costo, tipoActividad, estado);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener la actividad por ID: " + e.getMessage());
            }
            return null; // Si no se encuentra, retorna null
        }

        public List<Actividad> listarActividades() {
            List<Actividad> actividades = new ArrayList<>();
            String sql = "SELECT a.cod_actividad, a.nombre AS actividad_nombre, a.fecha_act, a.costo, " +
                    "t.id_tipoact, t.nombre AS tipo_nombre, a.estado " +
                    "FROM actividades a " +
                    "JOIN tipos_actividades t ON a.tipo_actividad = t.id_tipoact";
            Connection connection = DataBaseConnection.getInstance().getConnection();
            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("cod_actividad");
                    String nombre = resultSet.getString("actividad_nombre");
                    LocalDate fecha = resultSet.getDate("fecha_act").toLocalDate();
                    double costo = resultSet.getDouble("costo");

                    int tipoActividadId = resultSet.getInt("id_tipoact");
                    String tipoActividadNombre = resultSet.getString("tipo_nombre");
                    TipoActividad tipoActividad = new TipoActividad(tipoActividadId, tipoActividadNombre);

                    Boolean estado = resultSet.getBoolean("estado");



                    Actividad actividad = new Actividad(id, nombre, fecha, costo, tipoActividad, estado);
                    actividades.add(actividad);
                }
            }catch (SQLException e) {
                System.out.println("Error al listar actividades: " + e.getMessage());
            }
            return actividades;
        }

        public void inscribirseActividad (Usuario usuario, Actividad actividad) {
            String sql = "INSERT INTO inscripciones (usuario_id, actividad_id) VALUES (?, ?)";
            Connection connection = DataBaseConnection.getInstance().getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, usuario.getId());
                preparedStatement.setInt(2, actividad.id);
                int filasAfectadas = preparedStatement.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Inscripción realizada con éxito.");
                } else {
                    System.out.println("No se pudo realizar la inscripción.");
                }
            } catch (SQLException e) {
                System.out.println("Error al inscribir usuario: " + e.getMessage());
            }
        }
        public List<Actividad> listarActividadesConInscripcion() {
            List<Actividad> actividades = new ArrayList<>();
            String sql = "SELECT cod_actividad, nombre, fecha_act, hora_act, costo, requiere_inscripcion, fecha_apertura_ins, tipo_actividad, estado FROM actividades WHERE estado = TRUE AND requiere_inscripcion = TRUE";
            Connection connection = DataBaseConnection.getInstance().getConnection();

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("cod_actividad");
                    String nombre = resultSet.getString("nombre");
                    LocalDate fecha = resultSet.getDate("fecha_act").toLocalDate();
                    java.sql.Time sqlTime = resultSet.getTime("hora_act");
                    java.time.LocalTime localTime = sqlTime.toLocalTime();
                    double costo = resultSet.getDouble("costo");
                    Boolean plazoInscripcion = resultSet.getBoolean("requiere_inscripcion");
                    Date fechaApertura = resultSet.getDate("fecha_apertura_ins");
                    String tipo = resultSet.getString("tipo_actividad");
                    Boolean estado = resultSet.getBoolean("estado");

                    TipoActividad tipoActividad = obtenerTipoActividadPorNombre(tipo);

                    Actividad actividad = new Actividad(id, nombre, fecha, localTime, costo, plazoInscripcion, fechaApertura, tipoActividad, estado);
                    actividades.add(actividad);
                }
            } catch (SQLException e) {
                System.out.println("Error al listar las actividades con inscripción: " + e.getMessage());
            }
            return actividades;
        }


        public void mostrarDetallesActividad(int idActividad) {
            String sql = "SELECT cod_actividad, nombre, fecha_act, hora_act, lugar, duracion, costo, descripcion, requiere_inscripcion, fecha_apertura_ins, tipo_actividad, estado FROM actividades WHERE cod_actividad = ?";
            Connection connection = DataBaseConnection.getInstance().getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, idActividad);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String nombre = resultSet.getString("nombre");
                    Date fecha = resultSet.getDate("fecha_act");
                    java.sql.Time sqlTime = resultSet.getTime("hora_act");
                    java.time.LocalTime localTime = sqlTime.toLocalTime();
                    String lugar = resultSet.getString("lugar");
                    int duracion = resultSet.getInt("duracion");
                    double costo = resultSet.getDouble("costo");
                    String descripcion = resultSet.getString("descripcion");
                    Date plazoInscripcion = resultSet.getDate("fecha_apertura_ins");
                    String tipo = resultSet.getString("tipo_actividad");
                    String estado = resultSet.getString("estado");

                    System.out.println("Detalles de la Actividad:");
                    System.out.println("Nombre: " + nombre);
                    System.out.println("Tipo de actividad: " + tipo);
                    System.out.println("Descripción: " + descripcion);
                    System.out.println("Lugar de la actividad: " + lugar);
                    System.out.println("Fecha y hora de la actividad: " + fecha + ", " + localTime);
                    System.out.println("Duración de la actividad: " + duracion + " horas.");
                    System.out.println("Fecha inicial para las inscripciones: " + plazoInscripcion);
                    System.out.println("Costo: " + costo);
                    System.out.println("Estado: " + estado);
                }
            } catch (SQLException e) {
                System.out.println("Error al mostrar los detalles de la actividad: " + e.getMessage());
            } /*finally {
            // Cierre de la conexión si no usas un pool de conexiones
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }*/
        }

        public void cancelarInscripcion(Usuario usuario) {
            System.out.println("Actividades a las que está inscripto: ");
            listarActividadesInscritas(usuario);
            System.out.println("Ingrese el código de la actividad en la que desea cancelar su inscripción: ");
            int cod = scanner.nextInt();
            scanner.nextLine();
            System.out.println("¿Estás seguro de que deseas cancelar tu inscripción en la actividad elegida? (Si/No)");
            String confirmacion = scanner.nextLine();

            Actividad actividad = obtenerActividadPorId(cod);
            if (actividad == null) {
                System.out.println("La actividad seleccionada no existe.");
                return; // Salir del método
            }

            if (confirmacion.equalsIgnoreCase("si")) {
                String sql = "DELETE FROM inscripciones WHERE id_usuario = ? AND cod_actividad = ?";
                Connection connection = DataBaseConnection.getInstance().getConnection();

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, usuario.getId());
                    preparedStatement.setInt(2, actividad.id);
                    int filasAfectadas = preparedStatement.executeUpdate();

                    if (filasAfectadas > 0) {
                        System.out.println("Inscripción cancelada con éxito.");
                    } else {
                        System.out.println("No se pudo cancelar la inscripción. Es posible que no estuvieras inscrito en esta actividad.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al cancelar la inscripción: " + e.getMessage());
                }
            } else {
                System.out.println("Proceso cancelado.");
            }
        }

        public List<Actividad> listarActividadesInscritas(Usuario usuario) {
            List<Actividad> actividadesInscritas = new ArrayList<>();
            String sql = "SELECT a.cod_actividad, a.nombre, a.fecha_act, a.hora_act, a.costo, a.requiere_inscripcion, a.fecha_apertura_ins, a.tipo_actividad, a.estado "
                    + "FROM actividades a "
                    + "JOIN inscripciones i ON a.cod_actividad = i.cod_actividad "
                    + "WHERE i.id_usuario = ? AND a.estado = TRUE";
            Connection connection = DataBaseConnection.getInstance().getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, usuario.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("cod_actividad");
                    String nombre = resultSet.getString("nombre");
                    LocalDate fecha = resultSet.getDate("fecha_act").toLocalDate();
                    java.sql.Time sqlTime = resultSet.getTime("hora_act");
                    java.time.LocalTime hora = sqlTime.toLocalTime();
                    double costo = resultSet.getDouble("costo");
                    Boolean plazoInscripcion = resultSet.getBoolean("requiere_inscripcion");
                    Date fechaApertura = resultSet.getDate("fecha_apertura_ins");
                    String tipo = resultSet.getString("tipo_actividad");
                    TipoActividad tipoActividad = obtenerTipoActividadPorNombre(tipo);
                    Boolean estado = resultSet.getBoolean("estado");

                    Actividad actividad = new Actividad(id, nombre, fecha, hora , costo, plazoInscripcion, fechaApertura, tipoActividad, estado);
                    actividadesInscritas.add(actividad);
                }
            } catch (SQLException e) {
                System.out.println("Error al listar las actividades inscritas: " + e.getMessage());
            }
            return actividadesInscritas;
        }
    }
