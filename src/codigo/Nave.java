/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author aghsk
 */
public class Nave {

    Image imagen = null;
    public int posX = 0;
    public int posY = 0;
    private boolean pulsadoIzquierda = false;

    public boolean isPulsadoIzquierda() {
        return pulsadoIzquierda;
    }

    public void setPulsadoIzquierda(boolean pulsadoIzquierda) {
        this.pulsadoDerecha = false;
        this.pulsadoIzquierda = pulsadoIzquierda;

    }
    ///////////////////////////////////////////
    ///////////////////////////////////////////
    private boolean pulsadoDerecha = false;

    public boolean isPulsadoDerecha() {
        return pulsadoDerecha;
    }

    public void setPulsadoDerecha(boolean pulsadoDerecha) {
        this.pulsadoDerecha = pulsadoDerecha;
        this.pulsadoIzquierda = false;

    }

    public Nave() {

    }

    public void mueve() {
        if (pulsadoIzquierda && posX > 0) {
            posX -= 3;
        }
        if (pulsadoDerecha && posX < VentanaJuego.ANCHO_PANTALLA - imagen.getWidth(null)) {
            posX += 3;
        }
    }
}
