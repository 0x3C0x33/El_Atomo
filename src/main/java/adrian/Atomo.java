package adrian;

import java.io.Serializable;

public class Atomo implements Serializable{
    private String nombre;
    private int numeroAtomico;
    private double masaAtomica;
    
    public Atomo() {
    }

    public Atomo(String nombre, int numeroAtomico, double masaAtomica) {
        this.nombre = nombre;
        this.numeroAtomico = numeroAtomico;
        this.masaAtomica = masaAtomica;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getNumeroAtomico() {
        return numeroAtomico;
    }
    
    public void setNumeroAtomico(int numeroAtomico) {
        this.numeroAtomico = numeroAtomico;
    }
    
    public double getMasaAtomica() {
        return masaAtomica;
    }
    
    public void setMasaAtomica(double masaAtomica) {
        this.masaAtomica = masaAtomica;
    }
}
