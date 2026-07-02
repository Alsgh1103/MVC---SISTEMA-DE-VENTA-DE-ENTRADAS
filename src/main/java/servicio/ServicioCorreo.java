/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
/**
 *
 * @author alex_
 */
public class ServicioCorreo {
    private static final String REMITENTE = "alorgohe@gmail.com"; 
    private static final String CLAVE = "zyjtznlkhwrktttw"; 
    
    public static void enviarCodigo(String destinatario, String codigoVerificacion) {
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, CLAVE);
            }
        });
        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(REMITENTE));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));

            mensaje.setSubject("Código de Verificación - Sistema de Entradas");
            mensaje.setText("Hola,\n\nTu código de verificación de 4 dígitos es: " 
                            + codigoVerificacion + "\n\nIngrésalo en el sistema para completar tu registro.");
            Transport.send(mensaje);
            System.out.println("¡Correo enviado exitosamente a: " + destinatario + "!");
            
        } catch (MessagingException e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
