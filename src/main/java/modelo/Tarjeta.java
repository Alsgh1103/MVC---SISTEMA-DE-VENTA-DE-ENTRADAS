/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Modelo de Tarjeta de pago.
 *
 * REGLAS DE NEGOCIO encapsuladas aquí:
 *   - Formato del número: 13-19 dígitos numéricos.
 *   - Límite de entradas: máximo 4 por tarjeta.
 *   - Validación de CVV: debe coincidir con el registrado.
 *
 * @author alex_
 */
public class Tarjeta {

    // ---------------------------------------------------------------
    // EMISORES RECONOCIDOS
    // ---------------------------------------------------------------

    /**
     * Emisores de tarjeta soportados por el sistema.
     * Se usa para determinar el descuento aplicable por concierto.
     */
    public enum Emisor {
        VISA, MASTERCARD, DINERS, AMERICAN_EXPRESS, DESCONOCIDO
    }
    private String numero;
    private String nombre;
    private String fecha;
    private int CVV;
    private int cantidadComprada;
    private Emisor emisor;

    // ---------------------------------------------------------------
    // Constructor interno — úsese el método de fábrica `crear()` para
    // construir objetos con validación completa de campos en String.
    // ---------------------------------------------------------------
    public Tarjeta(String numero, String nombre, String fecha, int CVV) {
        this.numero = numero;
        this.nombre = nombre;
        this.fecha = fecha;
        this.CVV = CVV;
        this.cantidadComprada = 0;
        this.emisor = detectarEmisor(numero); // se detecta automáticamente al construir
    }

    // ---------------------------------------------------------------
    // MÉTODO DE FÁBRICA — recibe todos los campos como String y
    // ejecuta las mismas validaciones que antes vivían en FrmTarjeta.
    // Lanza IllegalArgumentException con los mensajes originales exactos.
    // ---------------------------------------------------------------
    /**
     * Crea y valida una Tarjeta a partir de entradas de texto crudas.
     *
     * @param numero      Número de tarjeta (sin espacios).
     * @param nombre      Nombre del titular.
     * @param fecha       Fecha de vencimiento.
     * @param cvvStr      CVV como cadena de texto (será convertido a int).
     * @return            Nueva instancia de Tarjeta si todos los datos son válidos.
     * @throws IllegalArgumentException si alguna validación falla.
     */
    public static Tarjeta crear(String numero, String nombre, String fecha, String cvvStr) {
        // V1 — Validación: ningún campo puede estar vacío
        if (numero == null || numero.isEmpty() ||
            nombre == null || nombre.isEmpty() ||
            fecha  == null || fecha.isEmpty()  ||
            cvvStr == null || cvvStr.isEmpty()) {
            throw new IllegalArgumentException("Por favor, complete todos los campos.");
        }

        // V2 — Validación: número de tarjeta debe ser 13-19 dígitos numéricos
        if (!numero.matches("\\d{13,19}")) {
            throw new IllegalArgumentException(
                "El número de tarjeta debe tener entre 13 y 19 dígitos numéricos.");
        }

        // NUEVA Validación de fecha: formato YYYY-MM-DD
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Formato de fecha inválido. Por favor use el formato YYYY-MM-DD.");
        }

        // NUEVA Validación de fecha real y expiración
        LocalDate fechaVencimiento;
        try {
            fechaVencimiento = LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha seleccionada no existe en el calendario.");
        }

        if (fechaVencimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La tarjeta ya ha expirado.");
        }

        // NUEVA Validación de CVV: entre 3 y 4 dígitos numéricos
        if (!cvvStr.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("El CVV debe contener entre 3 y 4 dígitos numéricos.");
        }

        // V3 — Validación: CVV debe ser estrictamente numérico
        int cvv;
        try {
            cvv = Integer.parseInt(cvvStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El CVV debe ser únicamente numérico.");
        }

        return new Tarjeta(numero, nombre, fecha, cvv);
    }

    // ---------------------------------------------------------------
    // DETECCIÓN DE EMISOR (método estático — usable sin instanciar)
    // ---------------------------------------------------------------

    /**
     * Detecta el emisor a partir de los primeros dígitos del número (IIN/BIN).
     * Puede llamarse en tiempo real mientras el usuario escribe.
     *
     * Criterios estándar:
     *   VISA             → empieza con 4, 13 o 16 dígitos
     *   MASTERCARD       → 51-55 (16 dígitos) ó rango 2221-2720 (16 dígitos)
     *   AMERICAN EXPRESS → empieza con 34 ó 37, 15 dígitos
     *   DINERS           → empieza con 300-305, 36 ó 38, 14 dígitos
     *
     * @param numero Número de tarjeta (solo dígitos, puede ser parcial).
     * @return Emisor detectado, o DESCONOCIDO si no coincide con ningún patrón.
     */
    public static Emisor detectarEmisor(String numero) {
        if (numero == null || numero.isEmpty()) return Emisor.DESCONOCIDO;
        // VISA: empieza con 4, 13 o 16 dígitos
        if (numero.matches("4\\d{12}(?:\\d{3})?"))          return Emisor.VISA;
        // MASTERCARD: 51-55 (16 dig) o rango BIN 2221-2720 (16 dig)
        if (numero.matches("5[1-5]\\d{14}") ||
            numero.matches("2(?:2[2-9][1-9]|[3-6]\\d{2}|7[01]\\d|720)\\d{12}"))
                                                              return Emisor.MASTERCARD;
        // AMERICAN EXPRESS: empieza con 34 o 37, 15 dígitos
        if (numero.matches("3[47]\\d{13}"))                  return Emisor.AMERICAN_EXPRESS;
        // DINERS: empieza con 300-305, 36 o 38, 14 dígitos
        if (numero.matches("3(?:0[0-5]|[68])\\d{11,12}"))    return Emisor.DINERS;
        return Emisor.DESCONOCIDO;
    }

    // ---------------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------------
    public String getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public int getCVV() {
        return CVV;
    }

    public int getCantidadComprada() {
        return cantidadComprada;
    }

    public Emisor getEmisor() {
        return emisor;
    }

    // ---------------------------------------------------------------
    // REGLAS DE NEGOCIO
    // ---------------------------------------------------------------

    /** Retorna true si aún puede comprar la cantidad solicitada (límite: 4). */
    public boolean puedeComprar(int cantidadNueva) {
        return (this.cantidadComprada + cantidadNueva) <= 4;
    }

    /** Retorna true si el CVV ingresado coincide con el registrado. */
    public boolean validarTarjeta(int cvvIngresado) {
        return cvvIngresado == CVV;
    }

    /**
     * Intenta registrar la compra validando límite de entradas y CVV.
     * Lanza IllegalArgumentException con mensaje descriptivo si alguna
     * regla de negocio no se cumple (en lugar de retornar false silenciosamente).
     *
     * @param cantidadNueva   Entradas que se quieren comprar.
     * @param cvvIngresado    CVV introducido por el usuario en la vista.
     * @throws IllegalArgumentException si se supera el límite o el CVV es incorrecto.
     */
    public void registrarCompra(int cantidadNueva, int cvvIngresado) {
        // T1 — Límite de 4 entradas por tarjeta
        if (!puedeComprar(cantidadNueva)) {
            throw new IllegalArgumentException(
                "Ha alcanzado el límite de 4 entradas por tarjeta.");
        }
        // T2 — CVV incorrecto
        if (!validarTarjeta(cvvIngresado)) {
            throw new IllegalArgumentException(
                "El CVV ingresado no coincide con la tarjeta.");
        }
        cantidadComprada += cantidadNueva;
    }
}
