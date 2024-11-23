import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Pago {
    private int id;
    private String tipoUsuario;
    private String cuota;
    private String actividad;
    private String salon;
    private Date fecha;
    private double monto;
    private String formaPago;
    private Usuario usuario;

    public Pago() {
    }

    public Pago(int id, String tipoUsuario, String pagaCuota, String pagaActividad, String pagaSalon,
                double monto, Date fecha, String formaPago) {
        this.id = id;
        this.tipoUsuario = tipoUsuario;
        this.cuota = pagaCuota;
        this.actividad = pagaActividad;
        this.salon = pagaSalon;
        this.monto = monto;
        this.fecha = fecha;
        this.formaPago = formaPago;
    }

    private static final Scanner scanner = new Scanner(System.in);


    public void ingresarPago(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            List<Usuario> usuarios = usuario.listarUsuarios();
            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
                return;
            } else {
                System.out.println("Lista de usuarios:");
                for (Usuario u : usuarios) {
                    System.out.println("ID: " + u.getId() + ", Nombre: " + u.getNombre() + ", Tipo: " + u.getTipoUsuario());
                }
            }
            System.out.println("Ingrese ID del usuario que realizara el pago: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            Usuario usuarioSeleccionado = usuario.obtenerUsuarioPorId(id);

            if (usuarioSeleccionado == null) {
                System.out.println("Usuario no encontrado.");
                return;
            }
            if (usuarioSeleccionado.getTipoUsuario() == TipoUsuario.Socio) {
                System.out.println("¿El usuario paga cuota?: (Si/No)");
                String pagaCuota = scanner.nextLine();
                TipoUsuario tipoSocio = TipoUsuario.Socio;

                if (pagaCuota.equalsIgnoreCase("si")) {
                    System.out.println("Ingrese costo de la cuota: ");
                    double costoCuota = scanner.nextDouble();
                    scanner.nextLine();
                    if (costoCuota == 0) {
                        System.out.println("El monto no puede ser vacío");
                        return;
                    }
                    System.out.println("Ingrese fecha del cobro: (formato dd-MM-yyyy)");
                    String fechaCuotaString = scanner.nextLine();
                    LocalDate fechaCuota = LocalDate.parse(fechaCuotaString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

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
                    System.out.println("¿Seguro que desea realizar el ingreso del pago? (si/no)");
                    String resultado = scanner.nextLine();
                    if (resultado.equals("si")) {
                        registrarPago(id, TipoUsuario.Socio, "si", " ", " ", costoCuota, fechaCuota, formaPago);
                        System.out.println("Pago de cuota registrado exitosamente.");
                        return;
                    } else {
                        System.out.println("Ingreso de pago de cuota cancelado.");
                    }
                }
            }   //opcion para no socio, o socio que no paga cuota
            System.out.println("Seleccione una opción: ");
            System.out.println("1. Inscripción a actividad.");
            System.out.println("2. Reserva de salón");
            int opcion = scanner.nextInt();
            scanner.nextLine();

            String inscripcion = " ";
            String reserva = " ";
            if (opcion == 1) {
                inscripcion = "Si";
            } else if (opcion == 2) {
                reserva = "Si";
            } else {
                System.out.println("Opción no válida.");
                return;
            }

            System.out.println("Ingrese el monto del pago: ");
            double monto = scanner.nextDouble();
            scanner.nextLine(); // Limpiar el buffer

            System.out.println("Ingrese la fecha del pago (formato dd-MM-yyyy): ");
            String fechaPagoStr = scanner.nextLine();
            LocalDate fechaPago = LocalDate.parse(fechaPagoStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            System.out.println("Seleccione la forma de pago: ");
            System.out.println("1. Efectivo");
            System.out.println("2. Transferencia");
            System.out.println("3. Débito");
            System.out.println("4. Crédito");
            opcion = scanner.nextInt();
            scanner.nextLine();
            String formaDeCobro = " ";
            switch (opcion) {
                case 1:
                    formaDeCobro = "Efectivo";
                    break;
                case 2:
                    formaDeCobro = "Transferencia";
                    break;
                case 3:
                    formaDeCobro = "Débito";
                    break;
                case 4:
                    formaDeCobro = "Crédito";
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
            System.out.println("¿Seguro que desea realizar el ingreso del pago? (si/no)");
            String resultado = scanner.nextLine();

            if (resultado.equals("si")) {
                registrarPago(id, usuarioSeleccionado.getTipoUsuario(), "no", inscripcion, reserva, monto, fechaPago, formaDeCobro);
                System.out.println("Pago de inscripción/reserva registrado exitosamente.");
            } else {
                System.out.println("Ingreso de pago asociado a actividad/reserva cancelado.");
            }

        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    private void registrarPago(int usuarioId, TipoUsuario tipoUsuario, String cuota, String inscripcion, String reserva,
                               double monto, LocalDate fecha, String formaPago) {
        String sql = "INSERT INTO pagos (usuario_id, tipo_usuario, cuota, inscripcion_actividad, reserva_salon, monto, fecha, forma_pago) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, usuarioId);
            statement.setString(2, tipoUsuario.name());
            statement.setString(3, cuota);
            statement.setString(4, inscripcion);
            statement.setString(5, reserva);
            statement.setDouble(6, monto);
            statement.setDate(7, java.sql.Date.valueOf(fecha));
            statement.setString(8, formaPago);
            int filasModificadas = statement.executeUpdate();
            if (filasModificadas > 0) {
                System.out.println("Pago registrado con éxito.");
            } else {
                System.out.println("Error al registrar el pago.");
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar registrar el pago: " + e.getMessage());
        }
    }

    public void modificarPago(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.Administrador) {
            List<Pago> pagos = listarPagos();
            if (pagos.isEmpty()) {
                System.out.println("No hay pagos registrados.");
            } else {
                System.out.println("Lista de Pagos:");
                for (Pago pago1 : pagos) {
                    System.out.println(pago1.toString());
                }
            }
            System.out.println();
            System.out.println("Ingrese ID del usuario a seleccionar: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            Usuario usuarioSeleccionado = usuario.obtenerUsuarioPorId(id);
            if (usuarioSeleccionado == null) {
                System.out.println("Usuario no encontrado.");
                return;
            }

            if (!verificarPagoExistente(id)) {
                System.out.println("No se encontró un pago registrado para este usuario.");
                return;
            }
            Integer pagoId = buscarPagoIdPorUsuario(id);
            if (pagoId == null) {
                System.out.println("Error al obtener el ID del pago.");
                return;
            }

            if (usuarioSeleccionado.getTipoUsuario() == TipoUsuario.Socio) {
                System.out.println("¿El usuario paga cuota? (Si/No): ");
                String pagaCuota = scanner.nextLine();

                if (pagaCuota.equalsIgnoreCase("si")) {
                    // Modificar detalles de la cuota
                    System.out.println("Ingrese el nuevo costo de la cuota: ");
                    double nuevoCostoCuota = scanner.nextDouble();
                    scanner.nextLine();
                    if (nuevoCostoCuota == 0) {
                        System.out.println("El monto no puede ser vacío");
                        return;
                    }

                    System.out.println("Ingrese la nueva fecha del cobro (formato dd-MM-yyyy): ");
                    String fechaCuotaString = scanner.nextLine();
                    LocalDate nuevaFechaCuota = LocalDate.parse(fechaCuotaString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    System.out.println("Ingrese la nueva forma de cobro (Efectivo, Transferencia, Crédito, Débito): ");
                    String nuevaFormaCuota = scanner.nextLine();

                    System.out.println("Confirma la modificación? (Si/No)");   // Confirmación de cambios
                    String confirmacion = scanner.nextLine();
                    if (confirmacion.equalsIgnoreCase("si")) {
                        actualizarPago(pagoId, "si", "", "", nuevoCostoCuota, nuevaFechaCuota, nuevaFormaCuota);
                        System.out.println("Pago de cuota modificado exitosamente.");
                    } else {
                        System.out.println("Modificación cancelada.");
                    }
                    return;
                }
            }
            System.out.println("Seleccione una opción: ");
            System.out.println("1. Inscripción a actividad");
            System.out.println("2. Reserva de salón");
            int opcion = scanner.nextInt();
            scanner.nextLine();

            String inscripcion = " ";
            String reserva = " ";
            if (opcion == 1) {
                inscripcion = "Si";
            } else if (opcion == 2) {
                reserva = "Si";
            } else {
                System.out.println("Opción no válida.");
                return;
            }
            System.out.println("Ingrese el nuevo monto cobrado: ");
            double nuevoMonto = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("Ingrese la nueva fecha de cobro (formato dd-MM-yyyy): ");
            String fechaPagoStr = scanner.nextLine();
            LocalDate nuevaFechaPago = LocalDate.parse(fechaPagoStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            System.out.println("Ingrese la nueva forma de cobro (efectivo, tarjeta, transferencia): ");
            String nuevaFormaDeCobro = scanner.nextLine();

            System.out.println("Confirma la modificación? (Si/No)");
            String confirmacion = scanner.nextLine();
            if (confirmacion.equalsIgnoreCase("si")) {
                actualizarPago(pagoId, " ", inscripcion, reserva, nuevoMonto, nuevaFechaPago, nuevaFormaDeCobro);
                System.out.println("Pago de inscripción/reserva modificado exitosamente.");
            } else {
                System.out.println("Modificación cancelada.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    private void actualizarPago(int pagoId, String cuota, String inscripcion, String reserva,
                                double monto, LocalDate fecha, String formaPago) {
        String sql = "UPDATE pagos SET cuota = ?, inscripcion_actividad = ?, reserva_salon = ?, monto = ?, fecha = ?, forma_pago = ? " +
                "WHERE id_pago = ?";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cuota);
            statement.setString(2, inscripcion);
            statement.setString(3, reserva);
            statement.setDouble(4, monto);
            statement.setDate(5, java.sql.Date.valueOf(fecha));
            statement.setString(6, formaPago);
            statement.setInt(7, pagoId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al intentar actualizar el pago: " + e.getMessage());
        }
    }

    private boolean verificarPagoExistente(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM pagos WHERE usuario_id = ?";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Devuelve true si hay al menos un pago registrado
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar verificar el pago: " + e.getMessage());
        }
        return false;
    }

    private Integer buscarPagoIdPorUsuario(int usuarioId) {
        String sql = "SELECT id FROM pagos WHERE usuario_id = ?";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id"); // Devuelve el ID del pago
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar obtener el ID del pago: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return "ID del usuario: " + this.id + ", Tipo Usuario: " + this.tipoUsuario+ ", Cuota? "+ this.cuota +", Actividad? " +
                this.actividad + ", Salon? " + this.salon + ", Monto: "+ this.monto + ", Fecha: " + this.fecha + ", Forma de Pago: " + this.formaPago;
    }

   public List<Pago> listarPagos () {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.tipo_usuario, p.cuota, p.inscripcion_actividad, p.reserva_salon, p.monto, p.fecha, p.forma_pago "
                + "FROM usuarios u "
                + "INNER JOIN pagos p ON u.id_usuario = p.usuario_id";
        Connection connection = DataBaseConnection.getInstance().getConnection();
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int idUsuario = resultSet.getInt("id_usuario");
                String tipoUsuario = resultSet.getString("tipo_usuario");

                String pagaCuota = resultSet.getString("cuota");
                String pagaActividad = resultSet.getString("inscripcion_actividad");
                String pagaSalon = resultSet.getString("reserva_salon");
                double monto = resultSet.getDouble("monto");
                Date fecha = resultSet.getDate("fecha");
                String formaPago = resultSet.getString("forma_pago");

                Pago pago = new Pago(idUsuario, tipoUsuario, pagaCuota, pagaActividad, pagaSalon, monto, fecha, formaPago);
                pagos.add(pago);   // Agregar a la lista
            }
        }catch (SQLException e) {
            System.out.println("Error al listar los pagos: " + e.getMessage());
        }
        return pagos;
    }

}
