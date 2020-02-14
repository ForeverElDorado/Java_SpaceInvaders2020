/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * WRITTEN AND DIRECTED BY ALVARO GARCIA HERRERO D:
 */
public class VentanaJuego extends javax.swing.JFrame {

    static int ANCHO_PANTALLA = 800;
    static int ALTO_PANTALLA = 600;

    int filasMarcianos = 5;
    int columnaMarcianos = 10;
    int contador = 0;

    BufferedImage buffer = null;
    //buffer para guardar las imágenes de todos los marcianos
    BufferedImage plantilla = null;

    Image[] imagenes = new Image[30];

    //Bucle de animacion del juego en este caso es un hilo de ejecucion
    //que se encarga de refescar el contenido de la pantalla
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            bucleDelJuego();
        }
    });

    Marciano miMarciano = new Marciano(ANCHO_PANTALLA);
    //estructura del marciano
    Marciano[][] listaMarcianos = new Marciano[filasMarcianos][columnaMarcianos];

    boolean direccionMarciano = true;
    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();

    //Nos va a permitir tener mas de un disparo en pantalla.
    ArrayList<Disparo> listaDisparos = new ArrayList();
    //Array de las explosiones....
    ArrayList<Explosion> listaExplosiones = new ArrayList();
    /////////////////////////////////////////////

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
        try {
            plantilla = ImageIO.read(getClass().getResource("/imagenes/invaders2.png"));
        } catch (IOException ex) {

        }
        //cargo las 30 imágenes del spritesheet en el array de bufferedimages
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                imagenes[i * 4 + j] = plantilla
                        .getSubimage(j * 64, i * 64, 64, 64)
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            }
        }

        imagenes[20] = plantilla.getSubimage(0, 320, 66, 32); //sprite de la nave
        imagenes[21] = plantilla.getSubimage(66, 320, 64, 32);
        imagenes[23] = plantilla.getSubimage(255, 320, 32, 32);//explosion parteB
        imagenes[22] = plantilla.getSubimage(255, 289, 32, 32);//explosion parteA

        setSize(ANCHO_PANTALLA, ALTO_PANTALLA);
        //Crea una imagen de mismo alto y ancho que el lienzo
        buffer = (BufferedImage) jPanel1.createImage(ANCHO_PANTALLA, ALTO_PANTALLA);
        buffer.createGraphics();

        temporizador.start();

        miNave.imagen = imagenes[20];

        miNave.posX = ANCHO_PANTALLA / 2 - miNave.imagen.getWidth(this) / 2;
        miNave.posY = ALTO_PANTALLA - 100;

        //Creamos array, es decir, el conjunto de marcianos de el mapa.
        //AQUI SE HACEN LA ESTRUCTURA PARA OTROS NIVELES.
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnaMarcianos; j++) {
                listaMarcianos[i][j] = new Marciano(ANCHO_PANTALLA);
                listaMarcianos[i][j].imagen1 = imagenes[2 * i];
                listaMarcianos[i][j].imagen2 = imagenes[3 * i + 1];
                listaMarcianos[i][j].posX = j * (15 + listaMarcianos[i][j].imagen1.getWidth(null));
                listaMarcianos[i][j].posY = i * (10 + listaMarcianos[i][j].imagen1.getHeight(null));
                //El numero 15/10 hace referencia a los pixeles
            }
        }
        //Asi evitamso que aparezca arriba y se cargue a un marciano al empezar.
        miDisparo.posY = -2000;
    }

    //Creams el metodo que va a colocarles
    private void pintaMarcianos(Graphics2D _g2) {
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnaMarcianos; j++) {
                listaMarcianos[i][j].mueve(direccionMarciano);
                if (contador < 50) {
                    _g2.drawImage(listaMarcianos[i][j].imagen1, listaMarcianos[i][j].posX, listaMarcianos[i][j].posY, null);
                } else if (contador < 100) {
                    _g2.drawImage(listaMarcianos[i][j].imagen2, listaMarcianos[i][j].posX, listaMarcianos[i][j].posY, null);
                } else {
                    contador = 0;
                }

                if (listaMarcianos[i][j].posX == ANCHO_PANTALLA - listaMarcianos[i][j].imagen1.getWidth(null) || listaMarcianos[i][j].posX == 0) {
                    direccionMarciano = !direccionMarciano;
                    for (int k = 0; k < filasMarcianos; k++) {
                        for (int m = 0; m < columnaMarcianos; m++) {
                            listaMarcianos[k][m].posY += listaMarcianos[k][m].imagen1.getHeight(null);
                        }
                    }
                }
            }
        }
    }

    private void pintaExplosiones(Graphics2D g2) {
        //pinta todas las explosiones 
        Explosion explosionAux;
        for (int i = 0; i < listaExplosiones.size(); i++) {
            explosionAux = listaExplosiones.get(i);
            explosionAux.tiempoDeVida--;
            if (explosionAux.tiempoDeVida > 25) {
                g2.drawImage(explosionAux.imagen1,
                        explosionAux.posX,
                        explosionAux.posY, null);

            } else {
                g2.drawImage(explosionAux.imagen2,
                        explosionAux.posX,
                        explosionAux.posY, null);
            }
            //si el tiempo de vida de la explosión es menor o igual a 0 la elimino
            if (explosionAux.tiempoDeVida <= 0) {
                listaExplosiones.remove(i);
            }
        }
    }

    private void pintaDisparo(Graphics2D g2) {

        //pinta todos los disparos 
        Disparo disparoAux;
        for (int i = 0; i < listaDisparos.size(); i++) {
            disparoAux = listaDisparos.get(i);
            disparoAux.mueve();
            if (disparoAux.posY < 0) {
                listaDisparos.remove(i);
            } else {
                g2.drawImage(disparoAux.imagen, disparoAux.posX, disparoAux.posY, null);
            }
        }
    }

    private void bucleDelJuego() {
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ANCHO_PANTALLA, ALTO_PANTALLA);

        contador++;
        pintaMarcianos(g2);
        ////////////////////////////////////////////////////////
        //dibujo la nave
        g2.drawImage(miNave.imagen, miNave.posX, miNave.posY, null);
        pintaDisparo(g2);
        pintaExplosiones(g2);
        miNave.mueve();
        chequeaColision();
        /////////////////////////////////////////////////////////
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);

    }

    public void chequeaColision() {
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();

        for (int k = 0; k < listaDisparos.size(); k++) {
            //calculo el rectángulo que contiene al disparo
            rectanguloDisparo.setFrame(listaDisparos.get(k).posX,
                    listaDisparos.get(k).posY,
                    listaDisparos.get(k).imagen.getWidth(null),
                    listaDisparos.get(k).imagen.getHeight(null));

            for (int i = 0; i < filasMarcianos; i++) {
                for (int j = 0; j < columnaMarcianos; j++) {
                    //calculo el rectángulo corresponmdiente al marciano que estoy comprobando
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].posX,
                            listaMarcianos[i][j].posY,
                            listaMarcianos[i][j].imagen1.getWidth(null),
                            listaMarcianos[i][j].imagen1.getHeight(null)
                    );
                    if (rectanguloDisparo.intersects(rectanguloMarciano)) {

                        //si entra aquí es porque han chocado un marciano y el disparo
                        Explosion e = new Explosion();
                        e.posX = listaMarcianos[i][j].posX;
                        e.posY = listaMarcianos[i][j].posY;
                        e.imagen1 = imagenes[23];
                        e.imagen2 = imagenes[22];
                        listaExplosiones.add(e);
                        //Sonido Explosion
                        e.sonidoExplosion.start();
                        listaMarcianos[i][j].posY = 2000;
                        listaDisparos.remove(k);

                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 740, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // Tecla Presionada
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzquierda(true);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDerecha(true);
                break;
            case KeyEvent.VK_SPACE:
                Disparo d = new Disparo();
                d.posicionDisparo(miNave);
                d.sonidoDisparo.start();
                //Agregamos el disparo
                listaDisparos.add(d);
                break;

        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        // Otro
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzquierda(false);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDerecha(false);
                break;
        }
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
