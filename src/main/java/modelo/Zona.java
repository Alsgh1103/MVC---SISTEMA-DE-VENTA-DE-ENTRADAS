/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;

public class Zona {
    private String nombre;
    private int capacidadTotal;
    private int capacidadDisponible;
    private int precio;
    private ArrayList<Entrada> entradas;

    public Zona(int id, String name, int price, int capacity) {
        this.nombre = name;
        this.precio = price;
        this.capacidadTotal = capacity;
        this.capacidadDisponible = capacity;
        this.entradas = new ArrayList<>();
    }

    public boolean generarEntradas() {
        entradas.clear();
        for (int i = 1; i <= capacidadTotal; i++) {
            entradas.add(new Entrada(i, "DISPONIBLE"));
        }
        return true;
    }

    public Entrada[] mostrarEntrada() {
        return entradas.toArray(new Entrada[0]);
    }

    public Entrada[] venderEntrada(int numero) {
        ArrayList<Entrada> vendidas = new ArrayList<>();
        for (Entrada e : entradas) {
            if (e.getNumero() == numero && e.vender()) {
                vendidas.add(e);
                capacidadDisponible--;
                break;
            }
        }
        return vendidas.toArray(new Entrada[0]);
    }

    public boolean verificarDisponibilidad(int cantidadEntradas) {
        return cantidadEntradas <= capacidadDisponible;
    }

    public boolean reducirCapacidad(int cantidadComprada) {
        if (verificarDisponibilidad(cantidadComprada)) {
            capacidadDisponible -= cantidadComprada;
            return true;
        } else {
            return false;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public int getCapacidadTotal() {
        return capacidadTotal;
    }

    public int getCapacidadDisponible() {
        return capacidadDisponible;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public void setCapacidadTotal(int capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
    }

    public void setCapacidadDisponible(int capacidadDisponible) {
        this.capacidadDisponible = capacidadDisponible;
    }
}
