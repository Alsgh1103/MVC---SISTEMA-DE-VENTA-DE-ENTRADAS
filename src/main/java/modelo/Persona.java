/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author alex_
 */
public abstract class Persona {
    private String dni;
    private String nombres;
    private String apellidos;
    private String contrasena;
    private String correo;
    private boolean enSesion; 
    
    public Persona(String dni, String nombres, String apellidos, String correo, String contrasena){
        this.dni        = dni;
        this.nombres    = nombres;
        this.apellidos  = apellidos;
        this.correo     = correo;
        this.contrasena = contrasena;
        this.enSesion   = false;
    }
    
    public String getDni       (){return dni;}
    public String getNombres   (){return nombres;}
    public String getApellidos (){return apellidos;}
    public String getContrasena(){return contrasena;}
    public String getCorreo    (){return correo;}
    public boolean isEnSesion  (){return enSesion;}
    
    public boolean validarContrasena(String pass){return pass.equals(contrasena);}
    public void setNombres(String nombres)       {this.nombres = nombres;}
    public void setApellidos(String apellidos)   {this.apellidos = apellidos;}
    public void setCorreo (String correo)        {this.correo = correo;}
    public void setContrasena(String contrasena) {this.contrasena = contrasena;}
    
    public boolean iniciarSesion(String contrasenaIngresada, String correoIngresado){
        if (this.contrasena.equals(contrasenaIngresada) && this.correo.equals(correoIngresado)){
            this.enSesion = true;
            return true;
        }
        return false;
    }
    public void cerrarSesion(){this.enSesion = false;}

    public boolean registrarTarjeta() { return false; }
    public boolean eliminarTarjeta() { return false; }
    public boolean anularVenta() { return false; }
}
