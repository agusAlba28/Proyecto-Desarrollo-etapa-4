import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
public class Usuario {
    private String nombre;
    private int id;
    private String apellido;
    private String tipoDocumento;
    private String numeroDocumento;
    private Date fechaNacimiento;
    private String domicilio;
    private List<String> telefonos;
    private String email;
    private String contrasena;
    private TipoUsuario tipoUsuario;
    private CategoriaSocio categoriaSocio;
    private boolean dificultadAuditiva;
    private boolean manejaLenguajeDeSenas;
    private Subcomision subcomision;
    private String estado;
    private static final Scanner scanner = new Scanner(System.in);

    public Usuario(String nombre, String apellido, String tipoDocumento, String numeroDocumento, Date fechaNacimiento, String domicilio, List<String> telefonos, String email, String contrasena, TipoUsuario tipoUsuario, CategoriaSocio categoriaSocio, boolean dificultadAuditiva, boolean manejaLenguajeDeSenas, Subcomision subcomision) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.domicilio = domicilio;
        this.telefonos = telefonos;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
        this.categoriaSocio = categoriaSocio;
        this.dificultadAuditiva = dificultadAuditiva;
        this.manejaLenguajeDeSenas = manejaLenguajeDeSenas;
        this.subcomision = subcomision;
    }

    public Usuario() {
    }

    public Usuario(int id, String nombre, String apellido, String tipoDocumento, String numeroDocumento, TipoUsuario tipoUsuario, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.tipoUsuario = tipoUsuario;
        this.estado = estado;
    }

    public Usuario(String email, String contrasena, TipoUsuario tipoUsuario) {
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    public Usuario(int id, String nombre, String apellido, TipoUsuario tipoUsuario, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoUsuario = tipoUsuario;
        this.estado = estado;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public int getId() {
        return id;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void registrarUsuario() {
        System.out.println("Ingrese nombre: ");
        String nombrePersona = scanner.nextLine();
        if (!verificarNombreApellio(nombrePersona)) {
            System.out.println("Formato del campo Nombre incorrecto.");
            return;
        }
        System.out.println("Ingrese apellido: ");
        String apellidoPersona = scanner.nextLine();
        if (!verificarNombreApellio(apellidoPersona)) {
            System.out.println("Formato del campo Apellido incorrecto.");
            return;
        }
        System.out.println("Ingrese tipo de documento:");
        System.out.println("1. CI");
        System.out.println("2. DNI");
        System.out.println("3. Pasaporte");
        System.out.println("4. Otro");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        switch (opcion){
            case 1:
                tipoDocumento = "CI";
                break;
            case 2:
                tipoDocumento = "DNI";
                break;
            case 3:
                tipoDocumento = "Pasaporte";
                break;
            case 4:
                tipoDocumento = "Otro";
                break;
            default:
                System.out.println("Opción no válida");
        }
        System.out.println("Ingrese numero de documento: ");
        String numeroDocumentoPersona = scanner.nextLine();
        if (!validarDocumento(numeroDocumentoPersona)) {
            System.out.println("Formato del campo Numero de Documento incorrecto.");
            return;
        }
        System.out.println("Ingrese fecha de nacimiento (dd-MM-yyyy): ");
        String fechaNacimientoPersona = scanner.nextLine();

        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = null;

        try {
            fechaNacimiento = formato.parse(fechaNacimientoPersona);  // Intentar convertir el String en Date
        } catch (ParseException e) {
            System.out.println("Formato de fecha incorrecto. ");
            return;
        }
        java.sql.Date fechaNacimientoDate = null;
        if (fechaNacimiento != null) {
            // Convertir de java.util.Date a java.sql.Date solo si la conversión fue exitosa
            fechaNacimientoDate = new java.sql.Date(fechaNacimiento.getTime());
        }

        System.out.println("Ingrese domicilio: ");
        String domicilioPersona = scanner.nextLine();
        System.out.println("Ingrese telefono de contacto: ");
        String telefonoPersona = scanner.nextLine();
        if (!validarTelefono(telefonoPersona)) {
            System.out.println("Formato del campo Telefono incorrecto.");
            return;
        }
        System.out.println("Ingrese email: ");
        String emailPersona = scanner.nextLine();
        if (!validarCorreoGmail(emailPersona)) {
            System.out.println("Formato del campo Email incorrecto.");
            return;
        }
        if (!esCorreoUnico(emailPersona)) {
            System.out.println("El correo ingresado ya está registrado. Intente con otro correo.");
            return;
        }
        System.out.println("Ingrese contraseña: ");
        String contrasenaPersona = scanner.nextLine();
        if (!validarContrasena(contrasenaPersona)) {
            System.out.println("Formato del campo Constraseña incorrecto. Mínimo 8 caracteres, incluido algun dígito y letra.");
            return;
        }
        System.out.println("Ingrese tipo de usuario: ");
        System.out.println("1. Socio");
        System.out.println("2. No Socio");
        System.out.println("3. Administrador");
        opcion = scanner.nextInt();
        scanner.nextLine();
        TipoUsuario tipoUsuarioPersona = null;

        switch (opcion) {
            case 1:
                tipoUsuarioPersona = TipoUsuario.Socio;
                break;
            case 2:
                tipoUsuarioPersona = TipoUsuario.NoSocio;
                break;
            case 3:
                tipoUsuarioPersona = TipoUsuario.Administrador;
                break;
            default:
                System.out.println("Opción no válida, intente de nuevo.");
        }

        String categoriaPersona = " ";
        boolean dificultadAuditivaPersona = false;
        boolean lenguajeDeSenasPersona = false;
        boolean participaSubcomisionPersona = false;
        String subcomisionQueParticipaPersona = " ";

        if (tipoUsuarioPersona == TipoUsuario.Socio) {
            System.out.println("Ingrese categoria de socio: ");
            categoriaPersona = scanner.nextLine(); // validar que el texto sea una categoria valida (equals)
            System.out.println("Ingrese dificultad auditiva: (true o false / si o no) ");
            dificultadAuditivaPersona = scanner.nextBoolean();
            System.out.println("Ingrese si maneja lenguaje de señas: (true o false / si o no");
            lenguajeDeSenasPersona = scanner.nextBoolean();
            System.out.println("Ingrese si particia en subcomision: (true o false / si o no");
            participaSubcomisionPersona = scanner.nextBoolean();
            scanner.nextLine();
            if (participaSubcomisionPersona) {
                System.out.println("Seleccione a que subcomision pertenece: ");
                subcomisionQueParticipaPersona = scanner.nextLine(); //validar que sea una subcomision (equals)
            }
        }

        Connection connection = DataBaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO usuarios (documento, nombre, apellido, correo, fec_nacimiento, tipo_documento," +
                "contraseña, tipo_usuario, categoria_socio, uso_lenguaseña, dif_auditiva, participa_subcomision,estado_solicitud, estado, nombre_subcomision) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, numeroDocumentoPersona);
            statement.setString(2,nombrePersona);
            statement.setString(3,apellidoPersona);
            statement.setString(4,emailPersona);
            statement.setDate(5, fechaNacimientoDate);
            statement.setString(6, tipoDocumento);
            statement.setString(7, contrasenaPersona);
            statement.setString(8, String.valueOf(tipoUsuarioPersona));
            statement.setString(9, categoriaPersona);
            statement.setBoolean(10,lenguajeDeSenasPersona);
            statement.setBoolean(11, dificultadAuditivaPersona);
            statement.setBoolean(12, participaSubcomisionPersona);
            statement.setString(13,"Pendiente");
            statement.setString(14, null);
            statement.setString(15, subcomisionQueParticipaPersona);
            statement.executeUpdate();

            System.out.println("Su solicitud de registro fue exitosa y está pendiente de validación.");
        } catch (SQLException e) {
            System.out.println("Error al intentar registrar usuario: " + e.getMessage());
        }
    }

    private boolean esCorreoUnico(String correo) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, correo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Si no hay registros con el correo, es único
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar unicidad del correo: " + e.getMessage());
        }
        return false;
    }

    public boolean verificarNombreApellio (String nombre) {
        if (nombre == null || nombre.trim().length() <= 2) {
            return false;
        }
        return nombre.matches("[a-zA-Z]+");  // Verificar que contenga solo letras (mayúsculas o minúsculas)
    }

    public boolean validarDocumento(String documento) {
        if (documento == null || documento.length() != 11) {  // Verificar que no sea null y tenga exactamente 11 caracteres
            return false;
        }
        return documento.matches("\\d\\.\\d{3}\\.\\d{3}-\\d");  // Verificar que cumpla con el formato x.xxx.xxx-x
    }

    public boolean validarTelefono (String telefono) {
        if (telefono == null) {
            System.out.println("El número de teléfono no puede ser nulo.");
            return false;
        }
        return telefono.matches("\\d{8,9}");
    }

    public boolean validarCorreoGmail(String correo) {
        if (correo == null) {
            System.out.println("El correo no puede ser nulo.");
            return false;
        }
        return correo.endsWith("@gmail.com");   // Verificar si el correo termina con "@gmail.com"
    }

    public boolean validarContrasena(String contrasena) {
        if (contrasena == null) {
            System.out.println("La contraseña no puede ser nula.");
            return false;
        }
        String regex = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
        return contrasena.matches(regex); // Verificar si la contraseña cumple con la expresión regular
    }

    public void modificarUsuario(Usuario usuario) {
        if (usuario.tipoUsuario == TipoUsuario.Administrador) {
            List<Usuario> usuarios = listarUsuarios();
            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
            } else {
                System.out.println("Lista de usuarios:");
                for (Usuario user : usuarios) {
                    System.out.println(user.toString());
                }
            }
            System.out.println();
            System.out.println("Ingrese el id del usuario a modificar: ");
            int id = scanner.nextInt();
            System.out.println("¿Qué modificación desea realizar en el tipo de usuario?");
            System.out.println("1. Cambiar de Socio a No Socio");
            System.out.println("2. Cambiar de No Socio a Socio");
            int opcion = scanner.nextInt();
            scanner.nextLine();

            TipoUsuario nuevoTipoUsuario = null;
            String nuevaCategoriaSocio = null; //ver el tipo de dato
            boolean nuevaDificultadAuditiva = false;
            boolean nuevoManejaLenguajeDeSenas = false;
            boolean nuevaSubcomisionParticipa = false;
            String nombreSubcomision = null; //ver el tipo de dato

            if (opcion == 1) {
                nuevoTipoUsuario = TipoUsuario.NoSocio;
            } else if (opcion == 2) {
                nuevoTipoUsuario = TipoUsuario.Socio;
                System.out.println("Ingrese la categoría del socio:");
                nuevaCategoriaSocio = scanner.next();  //ver la clase Categoria socio
                System.out.println("¿Tiene dificultad auditiva? (true/false): ");
                nuevaDificultadAuditiva = scanner.nextBoolean();
                scanner.nextLine();
                System.out.println("¿Maneja lenguaje de señas? (true/false): ");
                nuevoManejaLenguajeDeSenas = scanner.nextBoolean();
                scanner.nextLine();
                System.out.println("¿Participa en una subcomisión? (true/false) (si/no)");
                nuevaSubcomisionParticipa = scanner.nextBoolean();
                scanner.nextLine();
                if (nuevaSubcomisionParticipa) {
                    System.out.println("Ingrese la subcomisión (si aplica): ");
                    nombreSubcomision = scanner.nextLine();  //ver la clase subcomision
                }
            } else {
                System.out.println("Opción no válida.");
                return;
            }
            System.out.println("Confirma la modificación? (Si/No)");
            String confirmacion = scanner.nextLine();

            if(confirmacion.equalsIgnoreCase("si")) {
                Connection connection = DataBaseConnection.getInstance().getConnection();
                String sql = "UPDATE usuarios SET tipo_usuario = ?, categoria_socio = ?, dif_auditiva = ?," +
                        "uso_lenguaseña = ?, participa_subcomision = ?, nombre_subcomision = ? WHERE id_usuario = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, nuevoTipoUsuario.toString());
                    statement.setString(2, nuevaCategoriaSocio);
                    statement.setBoolean(3, nuevaDificultadAuditiva);
                    statement.setBoolean(4, nuevoManejaLenguajeDeSenas);
                    statement.setBoolean(5, nuevaSubcomisionParticipa);
                    statement.setString(6, nombreSubcomision);
                    statement.setInt(7, id);

                    int filasModificadas = statement.executeUpdate();
                    if (filasModificadas > 0) {
                        System.out.println("Tipo de usuario modificado con éxito.");
                    } else {
                        System.out.println("No se encontró ningún usuario con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar modificar el tipo de usuario: " + e.getMessage());
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    public void eliminarUsuario(Usuario usuario) {
        if (usuario.tipoUsuario == TipoUsuario.Administrador) {
            List<Usuario> usuarios = listarUsuarios();
            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
            } else {
                System.out.println("Lista de usuarios:");
                for (Usuario user : usuarios) {
                    System.out.println(user.toString());
                }
            }
            System.out.println("Ingrese el id del usuario a eliminar: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            Usuario user = obtenerUsuarioPorId(id);
            String estado = null;
            if (user.estado.equalsIgnoreCase("activo")) {
                estado = "Inactivo";
            } else if (user.estado.equalsIgnoreCase("Inactivo")) {
                estado = "Activo";
            }

            System.out.println("¿Seguro que desea realizar la modificacion del estado? (si/no)");
            String resultado = scanner.nextLine();

            if (resultado.equals("si")) {
                Connection connection = DataBaseConnection.getInstance().getConnection();
                String sql = "UPDATE usuarios SET estado = ? WHERE id_usuario = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, estado);
                    statement.setInt(2, id);
                    int filasEliminadas = statement.executeUpdate();

                    if (filasEliminadas > 0) {
                        System.out.println("Cambio de estado del usuario con éxito.");
                    } else {
                        System.out.println("No se encontró ningún usuario con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar eliminar el usuario: " + e.getMessage());
                }
            } else {
                System.out.println("La operación se ha cancelado.");
            }
        } else {
            System.out.println("No tiene los permisos necesarios.");
        }
    }

    @Override
    public String toString() {
        return "ID: " + this.id + ", Nombre Completo: " + this.nombre + " " + this.apellido + ", Tipo de usuario: " +this.tipoUsuario + ", Estado: " + this.estado;
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, apellido, tipo_documento, documento, tipo_usuario, estado FROM usuarios";
        Connection connection = DataBaseConnection.getInstance().getConnection();
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id_usuario");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                String tipoDocumento = resultSet.getString("tipo_documento");
                String numeroDocumento = resultSet.getString("documento");

                TipoUsuario tipoUsuario = TipoUsuario.fromString(resultSet.getString("tipo_usuario"));
                String estado = resultSet.getString("estado");

                Usuario usuario = new Usuario(id,nombre, apellido, tipoDocumento, numeroDocumento, tipoUsuario, estado);
                usuarios.add(usuario);
            }

        }catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public Login loginUsuario() {
        System.out.println("Ingrese email: ");
        String emailUsuario = scanner.nextLine();
        System.out.println("Ingrese contraseña: ");
        String contrasenaUsuario = scanner.nextLine();
        Usuario usuarioActual = new Usuario();

        Connection connection = DataBaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, emailUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String contrasenaGuardada = resultSet.getString("contraseña");
                    String estadoSolicitud = resultSet.getString("estado_solicitud");
                    String estadoUsuario = resultSet.getString("estado");
                    String tipoUsuarioString = resultSet.getString("tipo_usuario");

                    TipoUsuario tipoUsuario = TipoUsuario.fromString(tipoUsuarioString);

                    if (!"activo".equalsIgnoreCase(estadoUsuario)) {
                        System.out.println("El usuario no está activo. Contacte al administrador.");
                        return new Login(false, null);
                    }

                    if (contrasenaUsuario.equals(contrasenaGuardada) ) {
                        if ("aprobado".equals(estadoSolicitud)) {
                            usuarioActual = new Usuario(emailUsuario, contrasenaUsuario, tipoUsuario);
                            System.out.println("Inicio de sesión exitoso. Bienvenido");
                            System.out.println();
                            return new Login(true, usuarioActual);

                        } else {
                            System.out.println("El usuario no está aprobado.");
                        }
                    } else {
                        System.out.println("Contraseña incorrecta.");
                    }
                } else {
                    System.out.println("El email ingresado no está registrado.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Login(false, null);
    }

    public void modificarDatosPropios(Usuario usuario) {
        if (usuario.tipoUsuario == TipoUsuario.Socio || usuario.tipoUsuario == TipoUsuario.NoSocio) {
            if (usuario.estado.equals("Activo")) {

                int id = obtenerIdPorCorreo(usuario.email);

                // Si el ID no es válido (por ejemplo, no se encontró), salimos del método
                if (id == -1) {
                    System.out.println("No se encontró el usuario.");
                    return;
                }

                System.out.println("¿Qué dato desea modificar?");
                System.out.println("1. Nombre");
                System.out.println("2. Apellido");
                System.out.println("3. Contraseña");
                System.out.println("4. Fecha de Nacimiento");
                int opcion = scanner.nextInt();
                scanner.nextLine();

                String nuevoNombre = null;
                String nuevoApellido = null;
                String nuevaContrasena = null;
                SimpleDateFormat  fecha= new SimpleDateFormat("dd/MM/yyyy");
                Date nuevaFechaNacimiento = null;

                switch (opcion) {
                    case 1:
                        System.out.println("Ingrese el nuevo nombre: ");
                        nuevoNombre = scanner.nextLine();
                        break;
                    case 2:
                        System.out.println("Ingrese el nuevo apellido: ");
                        nuevoApellido = scanner.nextLine();
                        break;
                    case 3:
                        System.out.println("Ingrese la nueva contraseña: ");
                        nuevaContrasena = scanner.nextLine();
                        break;
                    case 4:
                        System.out.println("Ingrese la nueva fecha de nacimiento (formato dd-mm-yyyy): ");
                        String fechaInput = scanner.nextLine();
                        try {
                            nuevaFechaNacimiento = fecha.parse(fechaInput); // Convertir la cadena a Date
                        } catch (IllegalArgumentException e) {
                            System.out.println("Fecha inválida. El formato debe ser dd-mm-yyyy.");
                            return;
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        return;
                }

                Connection connection = DataBaseConnection.getInstance().getConnection();
                StringBuilder sql = new StringBuilder("UPDATE usuarios SET ");

                boolean hasChanges = false;
                if (nuevoNombre != null) {
                    sql.append("nombre = ?, ");
                    hasChanges = true;
                }
                if (nuevoApellido != null) {
                    sql.append("apellido = ?, ");
                    hasChanges = true;
                }
                if (nuevaContrasena != null) {
                    sql.append("contraseña = ?, ");
                    hasChanges = true;
                }
                if (nuevaFechaNacimiento != null) {
                    sql.append("fecha_nacimiento = ?, ");
                    hasChanges = true;
                }
                if (hasChanges) {
                    sql.delete(sql.length() - 2, sql.length());
                } else {
                    System.out.println("No se seleccionó ninguna opción válida para modificar.");
                    return;
                }
                sql.append(" WHERE id = ?");

                try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                    int index = 1;
                    if (nuevoNombre != null) {
                        statement.setString(index++, nuevoNombre);
                    }
                    if (nuevoApellido != null) {
                        statement.setString(index++, nuevoApellido);
                    }
                    if (nuevaContrasena != null) {
                        statement.setString(index++, nuevaContrasena);
                    }
                    if (nuevaFechaNacimiento != null) {
                        statement.setDate(index++, (java.sql.Date) nuevaFechaNacimiento);
                    }

                    statement.setInt(index, id);

                    int filasModificadas = statement.executeUpdate();
                    if (filasModificadas > 0) {
                        System.out.println("Datos modificados con éxito.");
                    } else {
                        System.out.println("No se encontró ningún usuario con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al intentar modificar los datos: " + e.getMessage());
                }
            } else {
                System.out.println("Su estado de usuario no está activo.");
            }
        }
    }

    public int obtenerIdPorCorreo(String email) {
        int idUsuario = -1; // Valor predeterminado si no se encuentra el usuario
        String sql = "SELECT id FROM usuarios WHERE correo = ?";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email); // Establecer el parámetro de nombre
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                idUsuario = resultSet.getInt("id"); // Obtener el id del resultado
            } else {
                System.out.println("No se encontró un usuario con el nombre proporcionado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar obtener el ID del usuario: " + e.getMessage());
        }

        return idUsuario;
    }

    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT id_usuario, nombre, apellido, tipo_usuario, estado FROM usuarios WHERE id_usuario = ?";
        try (Connection connection = DataBaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                TipoUsuario tipoUsuario = TipoUsuario.valueOf(resultSet.getString("tipo_usuario"));
                String estado = resultSet.getString("estado");
                return new Usuario(id, nombre, apellido, tipoUsuario ,estado);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }
}
