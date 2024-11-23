public class Login {
    private boolean exitoso;
    private Usuario usuario;

    public Login(boolean exitoso, Usuario usuario) {
        this.exitoso = exitoso;
        this.usuario = usuario;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
