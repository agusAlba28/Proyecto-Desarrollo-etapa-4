public enum TipoUsuario {
    Socio,
    NoSocio,
    Administrador;

    public static TipoUsuario fromString(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de usuario no puede estar vacío o nulo.");
        }
        switch (tipo.toLowerCase()) {
            case "socio":
                return Socio;
            case "nosocio":
                return NoSocio;
            case "administrador":
                return Administrador;
            default:
                throw new IllegalArgumentException("Tipo de usuario no válido: " + tipo);
        }
    }
}
