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
public class Marciano {

    public Image imagen1 = null;
    public Image imagen2 = null;

    public int posX = 0;
    public int posY = 0;
    

    private int anchoPantalla;
    public int vida = 50;

    public Marciano(int _anchPantalla) {
        anchoPantalla = _anchPantalla;
       
    }
    public void mueve(boolean direccion){
        if(direccion){//Mueve a los marcianitos jeje con true
            posX++;
        }
        else{//con false
            posX--;
        }
        
    }
}
