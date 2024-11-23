import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);


    public static void main (String[] args) {
        int opcion;
        Usuario usuarioNuevo = null;
        Usuario usuarioLogin = null;
        Login login = null;
        Actividad actividad = new Actividad();
        TipoActividad tipoActividad = new TipoActividad();
        Perfil perfil = new Perfil();
        Espacio espacio = new Espacio();
        Pago pago = new Pago();
        Auditoria auditoria = new Auditoria();
        Funcionalidad funcionalidad = new Funcionalidad();

        System.out.println("Bienvenido al sistema de ASUR.");
        System.out.println("1. Registrarse como usuario del sistema.");
        System.out.println("2. Login al sistema.");
        opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                usuarioNuevo = new Usuario();
                usuarioNuevo.registrarUsuario();
                break;
            case 2:
                usuarioLogin = new Usuario();
                login = usuarioLogin.loginUsuario();
                if (login.isExitoso()) {
                    usuarioLogin = login.getUsuario();
                } else {
                    System.out.println("Login fallido. Inténtelo nuevamente.");
                }
                break;
            default:
                System.out.println("Opción no válida. Inténtelo de nuevo.");
        }
        if (usuarioLogin != null && login.isExitoso()) {
            if (usuarioLogin.getTipoUsuario() == TipoUsuario.Administrador) {
                do {
                    mostrarMenuPorPerfil(1);
                    opcion = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcion) {
                        case 1:
                            usuarioLogin.registrarUsuario();
                            break;
                        case 2:
                            usuarioLogin.modificarUsuario(usuarioLogin);
                            break;
                        case 3:
                            usuarioLogin.eliminarUsuario(usuarioLogin);
                            break;
                        case 4:
                            List<Usuario> usuarios = usuarioLogin.listarUsuarios();
                            if (usuarios.isEmpty()) {
                                System.out.println("No hay usuarios registrados.");
                            } else {
                                System.out.println("Lista de usuarios:");
                                for (Usuario usuario : usuarios) {
                                    System.out.println(usuario.toString());
                                }
                            }
                            System.out.println();
                            break;
                        case 5:
                            actividad.ingresarActividad(usuarioLogin);
                            break;
                        case 6:
                            actividad.modificarActividad(usuarioLogin);
                            break;
                        case 7:
                            actividad.eliminarActividad(usuarioLogin);
                            break;
                        case 8:
                            tipoActividad.ingresarTipoActividad(usuarioLogin);
                            break;
                        case 9:
                            tipoActividad.modificarTipoActividad(usuarioLogin);
                            break;
                        case 10:
                            tipoActividad.eliminarTipoActividad(usuarioLogin);
                            break;
                        case 11:
                            perfil.ingresarPerfil(usuarioLogin);
                            break;
                        case 12:
                            perfil.modificarPerfil(usuarioLogin);
                            break;
                        case 13:
                            perfil.eliminarPerfil(usuarioLogin);
                            break;
                        case 14:
                            List<Perfil> perfiles = perfil.listarPerfiles();
                            if (perfiles.isEmpty()) {
                                System.out.println("No hay perfiles registrados.");
                            } else {
                                System.out.println("Lista de perfiles:");
                                for (Perfil perfil1 : perfiles) {
                                    System.out.println(perfil1.toString());
                                }
                            }
                            System.out.println();
                            break;
                        case 15:
                            funcionalidad.altaFuncionalidad(usuarioLogin);
                            break;
                        case 16:
                            funcionalidad.modificarFuncionalidad(usuarioLogin);
                            break;
                        case 17:
                            funcionalidad.eliminarFuncionalidad(usuarioLogin);
                            break;
                        case 18:
                            List<Funcionalidad> funcionalidades = funcionalidad.listarFuncionalidades();
                            if (funcionalidades.isEmpty()) {
                                System.out.println("No hay funcionalidades registradas.");
                            } else {
                                System.out.println("Lista de funcionalidades:");
                                for (Funcionalidad funcion : funcionalidades) {
                                    System.out.println(funcion.toString());
                                }
                            }
                            System.out.println();
                            break;
                        case 19:
                            espacio.ingresarEspacio(usuarioLogin);
                            break;
                        case 20:
                            espacio.modificarEspacio(usuarioLogin);
                            break;
                        case 21:
                            espacio.eliminarEspacio(usuarioLogin);
                            break;
                        case 22:
                            pago.ingresarPago(usuarioLogin);
                            break;
                        case 23:
                            pago.modificarPago(usuarioLogin);
                            break;
                        case 24:
                            funcionalidad.listarFuncionalidades();
                            System.out.println("Ingrese ID de la Funcionalidad a registrar: ");
                            int resultado = scanner.nextInt();
                            scanner.nextLine();
                            auditoria.registrarOperacion(usuarioLogin.getId(), resultado);
                            break;
                        case 25:
                            System.out.println("Ingrese fecha de inicio de búsqueda (formato dd-mm-yyyy): ");
                            String fechaInicioStr = scanner.nextLine();
                            System.out.println("Ingrese fecha de fin de búsqueda (formato dd-mm-yyyy): ");
                            String fechaFinStr = scanner.nextLine();

                            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");

                            try {
                                Date fechaInicio = (Date) formatoFecha.parse(fechaInicioStr);
                                Date fechaFin = (Date) formatoFecha.parse(fechaFinStr);
                                System.out.println("Fecha de inicio de búsqueda: " + fechaInicio);
                                System.out.println("Fecha de fin de búsqueda: " + fechaFin);
                                auditoria.generarReporte(fechaInicio, fechaFin);
                            } catch (Exception e) {
                                System.out.println("Error al parsear la fecha: " + e.getMessage());
                            }
                        case 0:
                            System.out.println("Saliendo...");
                            break;
                        default:
                            System.out.println("Opción no válida. Intente de nuevo.");
                    }

                }while (opcion != 0);
            } else {
                do {
                    mostrarMenuPorPerfil(2);
                    opcion = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcion) {
                        case 1:
                            usuarioLogin.modificarDatosPropios(usuarioLogin);
                            break;
                        case 2:
                            actividad.listarActividades();
                            System.out.println("Ingrese ID de la actividad a inscribirse: ");
                            int resultado = scanner.nextInt();
                            scanner.nextLine();
                            actividad.inscribirseActividad(usuarioLogin, actividad.obtenerActividadPorId(resultado));
                            break;
                        case 3:
                            actividad.cancelarInscripcion(usuarioLogin);
                            break;
                        case 4:
                            espacio.reservarEspacio(usuarioLogin);
                            break;
                        case 5:
                            espacio.cancelarResera(usuarioLogin);
                            break;
                        case 0:
                            System.out.println("Saliendo...");
                            break;
                        default:
                            System.out.println("Opción no válida. Intente de nuevo.");
                    }
                } while (opcion != 0);
            }
        }
    }

    public static void mostrarMenuPorPerfil(int idPerfil) {
        int contador = 1;
        try {
            List<String> funcionalidades = obtenerFuncionalidadesPorPerfil(idPerfil);
            if (funcionalidades.isEmpty()) {
                System.out.println("No hay funcionalidades disponibles para este perfil.");
                return;
            }

            System.out.println("Funcionalidades disponibles para su perfil:");
            for (String funcion: funcionalidades) {
                System.out.println(contador + ". " + funcion);
                contador++;
            }
            System.out.println("0. Salir.");
        } catch (SQLException e) {
            System.err.println("Error al obtener las funcionalidades: " + e.getMessage());
        }
    }

    public static List<String> obtenerFuncionalidadesPorPerfil(int idPerfil) throws SQLException {
        List<String> funcionalidades = new ArrayList<>();
        String sql =  "SELECT f.id_funcionalidad, f.nombre "
                + "FROM funcionalidades f "
                + "INNER JOIN perfil_funcionalidad pf ON f.id_funcionalidad = pf.id_funcionalidad "
                + "WHERE pf.id_perfil = ? "
                + "ORDER BY f.id_funcionalidad ASC";

        Connection connection = DataBaseConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPerfil);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    funcionalidades.add(rs.getString("nombre"));
                }
            }
        }
        return funcionalidades;
    }

}