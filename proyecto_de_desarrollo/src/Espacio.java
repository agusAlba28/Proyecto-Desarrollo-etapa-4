import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Espacio {
    private String nombre;
    private int id;
    private String descripcion;
    private int capacidadMaxima;
    private double tarifaSocio;
    private double tarifaNoSocio;
    private boolean estado;
    private String ubicacion;
    private static final Scanner scanner = new Scanner(System.in);

    public Espacio( int id, String nombre, String descripcion, int capacidadMaxima, double tarifaSocio, double tarifaNoSocio, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidadMaxima = capacidadMaxima;
        this.tarifaSocio = tarifaSocio;
        this.tarifaNoSocio = tarifaNoSocio;
        this.estado = estado;
    }

    public Espacio(int id, String nombre,  int capacidadMaxima) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
    }

    public Espacio() {
    }

    public void ingresarEspacio(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            System.out.println("Ingrese los datos para registrar un nuevo espacio:");
            System.out.println("Ingrese nombre del espacio (Gimnasio, cancha, parrillero, salon Estrada):");
            String nombre = scanner.nextLine();
            System.out.println("Ingrese descripción: ");
            String descripcion = scanner.nextLine();
            System.out.print("Ingrese capacidad Máxima (en personas): ");
            int capacidadMaxima = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Ingrese precio de reserva para Socio: ");
            double precioSocio = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Ingrese precio de reserva para No Socio: ");
            double precioNoSocio = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Ingrese fecha de vigencia de precios (dd-mm-yyyy): ");
            String fechaString = scanner.nextLine();
            LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            System.out.print("Ingrese observaciones (puede dejar en blanco): ");
            String observaciones = scanner.nextLine();

            System.out.println("¿Confirma la creación del espacio? (Si/No)");
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("si")) {
                String sql = "INSERT INTO salones (nombre, descripcion, capacidad_max, tarifa_socio, tarifa_nosocio, fecha_vigencia_precio, observaciones, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                Connection connection = DataBaseConnection.getInstance().getConnection();
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, nombre);
                    preparedStatement.setString(2, descripcion);
                    preparedStatement.setInt(3, capacidadMaxima);
                    preparedStatement.setDouble(4, precioSocio);
                    preparedStatement.setDouble(5, precioNoSocio);
                    preparedStatement.setDate(6, Date.valueOf(fecha));
                    preparedStatement.setString(7, observaciones.isEmpty() ? null : observaciones); // Guardar como null si está vacío
                    preparedStatement.setBoolean(8, false); // Inactivo por defecto

                    int filasAfectadas = preparedStatement.executeUpdate();
                    if (filasAfectadas > 0) {
                        System.out.println("Espacio registrado exitosamente como inactivo.");
                    } else {
                        System.out.println("No se pudo registrar el espacio.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al registrar el espacio: " + e.getMessage());
                }

            } else {
                System.out.println("Ingreso de espacio cancelado.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    public void modificarEspacio(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            listarEspacios();
            System.out.println("Ingrese ID del salón a modificar: ");
            int idModificar = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Ingrese los nuevos datos (dejar en blanco para mantener el valor actual):");
            System.out.print("Ingrese nombre: ");
            String nuevoNombre = scanner.nextLine();
            System.out.print("Ingrese descripción: ");
            String nuevaDescripcion = scanner.nextLine();
            System.out.print("Ingrese capacidad Máxima: ");
            int capacidadStr = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Ingrese precio para Socio: ");
            double precioSocioStr = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Ingrese precio para No Socio: ");
            double precioNoSocioStr = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Ingrese fecha de Vigencia de Precio (dd-mm-yyyy): ");
            String fechaStr = scanner.nextLine();
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            System.out.print("Ingrese observaciones: ");
            String observaciones = scanner.nextLine();
            System.out.print("Ingrese estado (true/false) (si/no): ");
            Boolean estado = scanner.nextBoolean();
            scanner.nextLine();

            System.out.println("¿Confirmar las modificaciones? (Si/No)");
            String confirmacion = scanner.nextLine();
            if (confirmacion.equalsIgnoreCase("si")) {
                String sql = "UPDATE salones SET nombre = ?, descripcion = ?, capacidad_max = ?, tarifa_socio = ?, tarifa_nosocio = ?, fecha_vigencia_precio = ?, observaciones = ?, estado = ? WHERE id_recurso = ?";
                Connection connection = DataBaseConnection.getInstance().getConnection();

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, nuevoNombre);
                    preparedStatement.setString(2, nuevaDescripcion);
                    preparedStatement.setInt(3,  capacidadStr);
                    preparedStatement.setDouble(4, precioSocioStr);
                    preparedStatement.setDouble(5, precioNoSocioStr);
                    preparedStatement.setDate(6, Date.valueOf(fecha));
                    preparedStatement.setString(7, observaciones);
                    preparedStatement.setBoolean(8, estado);
                    preparedStatement.setInt(9, idModificar);

                    int filasAfectadas = preparedStatement.executeUpdate();
                    if (filasAfectadas > 0) {
                        System.out.println("Modificaciones guardadas con éxito.");
                    } else {
                        System.out.println("No se pudo actualizar el espacio.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al actualizar el espacio: " + e.getMessage());
                }
            } else {
                System.out.println("Modificación del salón cancelado.");
            }

        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    public List<Espacio> listarEspacios() {
        List<Espacio> espacios = new ArrayList<>();
        String sql = "SELECT id_recurso, nombre, descripcion, capacidad_max, tarifa_socio, tarifa_nosocio, estado FROM salones";
        Connection connection = DataBaseConnection.getInstance().getConnection();
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id_recurso");
                String nombre = resultSet.getString("nombre");
                String descripcion = resultSet.getString("descripcion");
                int capacidadMax = resultSet.getInt("capacidad_max");
                double tarifaSocio = resultSet.getDouble("tarifa_socio");
                double tarifaNoSocio = resultSet.getDouble("tarifa_nosocio");
                boolean estado = resultSet.getBoolean("estado");

                Espacio espacio = new Espacio(id, nombre, descripcion, capacidadMax, tarifaSocio, tarifaNoSocio, estado);
                espacios.add(espacio);
            }
        }catch (SQLException e) {
            System.out.println("Error al listar salones: " + e.getMessage());
        }
        return espacios;
    }

    public void eliminarEspacio(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            listarEspacios();
            System.out.println("Ingrese ID del espacio a eliminar: ");
            int idEliminar = scanner.nextInt();
            scanner.nextLine();

            Boolean estadoActual = obtenerEstadoEspacio(idEliminar);
            if (estadoActual) {
                estadoActual = false;
            } else {
                estadoActual = true;
            }

            System.out.println("¿Seguro que desea realizar la modificacion del estado? (si/no)");
            String resultado = scanner.nextLine();
            if (resultado.equalsIgnoreCase("si")) {
                Connection connection = DataBaseConnection.getInstance().getConnection();
                String sql = "UPDATE salones SET estado = ? WHERE id_recurso = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setBoolean(1, estadoActual);
                    statement.setInt(2, idEliminar);

                    int filasModificadas = statement.executeUpdate();
                    if (filasModificadas > 0) {
                        System.out.println("Estado del espacio modificado con éxito.");
                    } else {
                        System.out.println("No se encontró ningún espacio con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar modificar el estado del espacio: " + e.getMessage());
                }
            } else {
                System.out.println("Eliminación de espacio cancelado.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    public void reservarEspacio(Usuario usuario) {
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }
        System.out.println("Ingrese la fecha de la reserva (formato dd-MM-yyyy): ");
        String fechaReservaStr = scanner.nextLine();
        LocalDate fechaReserva = LocalDate.parse(fechaReservaStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        System.out.println("Ingrese hora de la reserva (formato HH:mm): ");
        String horaReservaStr = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime horaReserva = null;
        try {
            horaReserva = LocalTime.parse(horaReservaStr, formatter);
            System.out.println("Hora de reserva convertida: " + horaReserva);
        } catch (Exception e) {
            System.out.println("Formato de hora incorrecto. Intenta nuevamente.");
        }

        System.out.println("Ingrese la duración de la reservación en horas: ");
        double duracionReserva = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Ingrese la cantidad de personas: ");
        int cantidadPersonas = scanner.nextInt();
        scanner.nextLine();

        List<Espacio> lugaresDisponibles = verificarDisponibilidad(fechaReserva, horaReserva, duracionReserva, cantidadPersonas);
        if (lugaresDisponibles == null) {
            System.out.println("No hay espacios disponibles para la fecha y cantidad de personas ingresadas.");
            return;
        }
        Espacio espacioReserva = elegirLugar(lugaresDisponibles);
        double importeAbonar = calcularTarifa(usuario,espacioReserva);

        LocalDate fechaVtoSena = calcularFechaVtoSena(fechaReserva); // Fecha Vto de Seña - 5 días hábiles previos


        System.out.println("Ingrese el importe de la seña a pagar: ");  // Importe de seña
        double importeSena = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Ingrese la fecha de pago de la seña (formato dd-MM-yyyy): "); // Fecha de pago de seña
        String fechaPagoSenaStr = scanner.nextLine();
        LocalDate fechaPagoSena = LocalDate.parse(fechaPagoSenaStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));


        System.out.println("Ingrese el importe de seña pagado: "); // Importe de seña pagado
        double importeSenaPagado = scanner.nextDouble();
        scanner.nextLine();

        double saldoPendiente = importeAbonar - importeSenaPagado; // Saldo pendiente

        // Confirmación y registro en la base de datos
        System.out.println("Confirma la reserva? (Si/No)");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("si")) {
            LocalDateTime fechaConfirmacion = LocalDateTime.now();
            registrarReserva(espacioReserva.nombre, fechaReserva, horaReserva, duracionReserva, cantidadPersonas,
                    usuario.getNumeroDocumento(), importeAbonar, importeSenaPagado, fechaPagoSena, fechaVtoSena, usuario.getId(), espacioReserva.id);
            System.out.println("Reserva registrada exitosamente.");
        } else {
            System.out.println("Reserva cancelada.");
        }
    }

    public Espacio elegirLugar(List<Espacio> lugaresDisponibles) {
        System.out.println("Lugares disponibles:");
        for (Espacio lugar : lugaresDisponibles) {
            System.out.println("- " + lugar.nombre + " (Capacidad: " + lugar.capacidadMaxima + ")");
        }

        System.out.print("Ingrese el nombre del lugar que desea reservar: ");
        String nombreLugar = scanner.nextLine();

        for (Espacio lugar : lugaresDisponibles) {
            if (lugar.nombre.equalsIgnoreCase(nombreLugar)) {
                System.out.println("Ha seleccionado el lugar: " + lugar.nombre);
                return lugar;
            }
        }
        System.out.println("El lugar ingresado no está disponible o no existe en la lista.");
        return null;
    }

    public double calcularTarifa(Usuario usuario, Espacio espacio) {
        double tarifa;
        if (usuario.getTipoUsuario() == TipoUsuario.Socio) {
            tarifa = espacio.tarifaSocio; // Tarifa para socios
        } else {
            tarifa = espacio.tarifaNoSocio; // Tarifa para no socios
        }
        return tarifa;
    }

    public void registrarReserva(String nombreRecurso, LocalDate fechaActividad, LocalTime horaActividad, double duracion, int cantidadPersonas,
                                 String datoContacto, double montoTotal, double sena, LocalDate fechaPagoSena, LocalDate fechaVencimientoSena,
                                 int idUsuario, int idRecurso) {
        String sql = "INSERT INTO reservas (nombre_recurso, fecha_act, hora_act, duracion, cant_personas, dato_contacto, monto_total, seña, " +
                "fecha_pago_seña, fecha_vencimiento_seña, id_usuario, id_recurso) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nombreRecurso);  // nombre_recurso
            statement.setDate(2, java.sql.Date.valueOf(fechaActividad));  // fecha_act
            statement.setTime(3, java.sql.Time.valueOf(horaActividad));  // hora_act
            statement.setDouble(4, duracion);  // duracion
            statement.setInt(5, cantidadPersonas);  // cant_personas
            statement.setString(6, datoContacto);  // dato_contacto
            statement.setDouble(7, montoTotal);  // monto_total
            statement.setDouble(8, sena);  // seña

            if (fechaPagoSena != null) {
                statement.setDate(9, java.sql.Date.valueOf(fechaPagoSena));
            } else {
                statement.setNull(9, java.sql.Types.DATE);
            }

            statement.setDate(10, java.sql.Date.valueOf(fechaVencimientoSena));
            statement.setInt(11, idUsuario);
            statement.setInt(12, idRecurso);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Reserva registrada exitosamente.");
            } else {
                System.out.println("No se pudo registrar la reserva.");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar la reserva: " + e.getMessage());
        }
    }

    // Método que calcula la fecha de vencimiento de la seña, 5 días hábiles antes de la actividad
    private LocalDate calcularFechaVtoSena(LocalDate fechaActividad) {
        int diasRestantes = 5;
        LocalDate fechaVto = fechaActividad.minusDays(1);
        while (diasRestantes > 0) {
            fechaVto = fechaVto.minusDays(1);
            DayOfWeek DayOfWeek = null;
            if (!(fechaVto.getDayOfWeek() == DayOfWeek.SATURDAY || fechaVto.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                diasRestantes--;
            }
        }
        return fechaVto;
    }

    public List<Espacio> verificarDisponibilidad(LocalDate fecha, LocalTime horaInicio, double duracion, int cantidadPersonas) {
        LocalTime horaFin = horaInicio.plusHours((long) duracion); // Calcula la hora de finalización de la actividad

        List<Espacio> lugaresDisponibles = new ArrayList<>(); // Lista para almacenar los lugares disponibles que cumplen con los requisitos

        String sql = "SELECT * FROM salones s " +
                "WHERE s.capacidad_max >= ? " +
                "AND NOT EXISTS (" +
                "  SELECT 1 FROM reservas r " +
                "  WHERE r.id_recurso = s.id_recurso " +
                "  AND r.fecha_act = ? " + // Misma fecha
                "  AND ((r.hora_act BETWEEN ? AND ?) " +
                "       OR (TIME_ADD(r.hora_act, INTERVAL r.duracion HOUR) BETWEEN ? AND ?)" +
                "       OR (? BETWEEN r.hora_act AND TIME_ADD(r.hora_act, INTERVAL r.duracion HOUR)) )" +
                ")";

        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Setea los parámetros de la consulta
            statement.setInt(1, cantidadPersonas);  // Capacidad mínima requerida
            statement.setDate(2, Date.valueOf(fecha));  // Fecha específica de la actividad
            statement.setTime(3, Time.valueOf(horaInicio));  // Hora de inicio
            statement.setTime(4, Time.valueOf(horaFin));  // Hora de fin
            statement.setTime(5, Time.valueOf(horaInicio));
            statement.setTime(6, Time.valueOf(horaFin));
            statement.setTime(7, Time.valueOf(horaInicio));
            ResultSet resultSet = statement.executeQuery();

            // Procesa los resultados
            while (resultSet.next()) {  // Crear un objeto espacio con los datos del resultado de la consulta
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                int capacidad = resultSet.getInt("capacidad");
                Espacio lugar = new Espacio(id, nombre, capacidad);
                lugaresDisponibles.add(lugar);  // Añadir el lugar a la lista
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar disponibilidad de espacios: " + e.getMessage());
        }
        return lugaresDisponibles;
    }

    private boolean obtenerEstadoEspacio (int id) {
        Boolean estado = null;
        String sql = "SELECT estado FROM salones WHERE id_recurso = ?";

        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                estado = resultSet.getBoolean("estado");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el estado del espacio: " + e.getMessage());
        }
        return estado;
    }

    public void cancelarResera(Usuario usuario) {
        try {
            // Consulta para obtener todas las reservas del usuario
            String query = "SELECT r.id_reserva,, r.nombre_recurso, r.fecha_act, r.hora_act, r.duracion, r.cant_persona, " +
                    "r.monto_total, r.seña, r.fecha_pago_seña, r.fecha_vencimiento_seña, s.nombre AS lugar " +
                    "FROM reservas r JOIN salones s ON r.espacio_id = s.id_recurso " +
                    "WHERE r.id_usuario = ?";

            Connection connection = DataBaseConnection.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, usuario.getId());
            ResultSet resultSet = statement.executeQuery();


            System.out.println("Reservas del usuario " + usuario.getNombre() +" "+ usuario.getApellido() + ":"); // Mostrar reservas del usuario
            while (resultSet.next()) {
                int reservaId = resultSet.getInt("id_reserva");
                Date fechaActividad = resultSet.getDate("fecha_act");
                Time horaActividad = resultSet.getTime("hora_act");
                int duracion = resultSet.getInt("duracion");
                int cantidadPersonas = resultSet.getInt("cant_persona");
                double montoTotal = resultSet.getDouble("monto_total");
                double sena = resultSet.getDouble("seña");
                Date fechaPagoSena = resultSet.getDate("fecha_pago_seña");
                Date fechaVencimientoSena = resultSet.getDate("fecha_vencimiento_seña");
                String lugar = resultSet.getString("lugar");

                double saldoPendiente = montoTotal - sena;  // Calcular saldo pendiente

                // Mostrar detalles de la reserva
                System.out.println("Reserva ID: " + reservaId);
                System.out.println("Fecha y Hora de Actividad: " + fechaActividad + " " + horaActividad);
                System.out.println("Duración: " + duracion + " horas");
                System.out.println("Cantidad de Personas: " + cantidadPersonas);
                System.out.println("Monto total: $" + montoTotal);
                System.out.println("Importe de Seña Pagado: $" + sena);
                System.out.println("Fecha de pago de Seña: " + fechaPagoSena);
                System.out.println("Fecha de vencimiento de seña: " + fechaVencimientoSena);
                System.out.println("Saldo Pendiente: $" + saldoPendiente);
                System.out.println("Lugar a Reservar: " + lugar);
                System.out.println("-----------------------------------");
            }

            System.out.print("Ingrese el ID de la reserva que desea cancelar: ");  // Pedir el ID de la reserva a cancelar
            int reservaId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("¿Está seguro que desea cancelar la reserva con ID " + reservaId + "? (Si/No): "); // Confirmar cancelación
            String confirmacion = scanner.nextLine();
            if (confirmacion.equalsIgnoreCase("si")) {
                eliminarReserva(reservaId);
                System.out.println("La reserva ha sido cancelada y eliminada con éxito.");
            } else {
                System.out.println("Cancelación de la reserva abortada.");
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar los detalles de las reservas: " + e.getMessage());
        }
    }

    private void eliminarReserva(int reservaId) throws SQLException {
        String deleteQuery = "DELETE FROM reservas WHERE id_reserva = ?";
        Connection connection = DataBaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(deleteQuery);
        statement.setInt(1, reservaId);
        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Reserva eliminada exitosamente.");
        } else {
            System.out.println("No se encontró la reserva con el ID especificado.");
        }
    }
}
