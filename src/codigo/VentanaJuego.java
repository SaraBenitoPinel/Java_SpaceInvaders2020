/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

/**
 *
 * @author jorgecisneros
 */
public class VentanaJuego extends javax.swing.JFrame {

    //LO QUE QUEREMOS QUE MIDA EL ANCHO Y EL LARGO DE LA PANTALLA
    static int ANCHOPANTALLA = 800;
    static int ALTOPANTALLA = 600;
    int filasMarcianos = 5;
    int columnasMarcianos = 10;
    int contador = 0;
    BufferedImage buffer = null;

    //HILO DE EJECUCION NUEVO QUE SE ENCARGA DE REFRESCAR EL CONTENIDO DE LA PANTALLA 
    //(BUCLE DE ANIMACION DEL JUEGO)
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override //SOBRESCRIBIR
        public void actionPerformed(ActionEvent e) {
            //CODIGO DE LA ANIMACION
            bucleDelJuego();
        }
    });
    Marciano miMarciano = new Marciano(ANCHOPANTALLA);
    Nave minave = new Nave();
    Disparo midisparo = new Disparo();
    //el array de dos dimensiones que guarda la lista de marcianos
    Marciano[][] listaMarcianos = new Marciano[filasMarcianos][columnasMarcianos];
    //dirección en la que se mueve el grupo de marcianos
    boolean direccionMarcianos = true;

    private void pintaMarcianos(Graphics2D _g2) {
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnasMarcianos; j++) {
                listaMarcianos[i][j].mueve(direccionMarcianos);
                if (listaMarcianos[i][j].posX == ANCHOPANTALLA - listaMarcianos[i][j].imagen1.getWidth(null) || listaMarcianos[i][j].posX == 0) {
                    direccionMarcianos = !direccionMarcianos;
                    for (int k = 0; k < filasMarcianos; k++) {
                        for (int m = 0; m < columnasMarcianos; m++) {
                            listaMarcianos[k][m].posY += listaMarcianos[k][m].imagen1.getHeight(null);
                        }
                    }
                }
                if (contador < 50) {
                    _g2.drawImage(listaMarcianos[i][j].imagen1, listaMarcianos[i][j].posX, listaMarcianos[i][j].posY, null);
                } else if (contador < 100) {
                    _g2.drawImage(listaMarcianos[i][j].imagen2, listaMarcianos[i][j].posX, listaMarcianos[i][j].posY, null);
                } else {
                    contador = 0;
                }
            }
        }
    }

    private void bucleDelJuego() {
        //ESTE METODO GOBIERNA EL REDIBUJADO DE LOS OBJETOS EN EL JPANEL1
        //PRIMERO BORRO TODO LO QUE HAY EN EL BUFFER
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);
        /////////////////////////////////////////
        contador++;
        pintaMarcianos(g2);
        //DIBUJO DE GOLPE TODO EL BUFFER SOBRE EL JPANEL
        if (contador < 50) {
            g2.drawImage(miMarciano.imagen1, 10, 10, null);
        } else if (contador < 100) {
            g2.drawImage(miMarciano.imagen2, 10, 10, null);
        } else {
            contador = 0;
        }
        //DIBUJO LA NAVE
        g2.drawImage(minave.imagen, minave.posX, minave.posY, null);
        g2.drawImage(midisparo.imagen, midisparo.posX, midisparo.posY, null);
        minave.mueve();
        midisparo.mueve();
        chequeaColision();
        ///////////////////////////////////////////
        g2 = (Graphics2D) VentanaJuego.getGraphics();
        g2.drawImage(buffer, 0, 0, null);

    }
    //CHEQUEA SI UN DISPARO Y UN MARCIANO COLISIONAN

    private void chequeaColision() {
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();
        //calculo el rectangulo que contiene al disparo
        rectanguloDisparo.setFrame(midisparo.posX, midisparo.posY, midisparo.imagen.getHeight(null), midisparo.imagen.getWidth(null));
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnasMarcianos; j++) {
                rectanguloMarciano.setFrame(listaMarcianos[i][j].posX, listaMarcianos[i][j].posY,
                        listaMarcianos[i][j].imagen1.getWidth(null), listaMarcianos[i][j].imagen1.getHeight(null));
                if (rectanguloDisparo.intersects(rectanguloMarciano)) {
                    //SI ENTRA AQUI ES PORQUE HAN CHOCADO UN MARCIANO Y EL DISPARO
                    listaMarcianos[i][j].posY = 2000;
                    midisparo.posY = -2000;
                }
            }
        }
    }

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        buffer = (BufferedImage) VentanaJuego.createImage(ANCHOPANTALLA, ALTOPANTALLA);
        buffer.createGraphics();
        //ARRANCA EL TEMPORIZADOR PARA QUE EMPIECE EL JUEGO
        temporizador.start();
        minave.posX = ANCHOPANTALLA / 2 - minave.imagen.getWidth(this) / 2;
        minave.posY = ALTOPANTALLA - 100;
        //creamos el array de marcianos
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnasMarcianos; j++) {
                listaMarcianos[i][j] = new Marciano(ANCHOPANTALLA);
                listaMarcianos[i][j].posX = j * (15 + listaMarcianos[i][j].imagen1.getWidth(null));
                listaMarcianos[i][j].posY = i * (10 + listaMarcianos[i][j].imagen1.getHeight(null));
            }
        }
        midisparo.posY = 2000;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        VentanaJuego = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout VentanaJuegoLayout = new javax.swing.GroupLayout(VentanaJuego);
        VentanaJuego.setLayout(VentanaJuegoLayout);
        VentanaJuegoLayout.setHorizontalGroup(
            VentanaJuegoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 625, Short.MAX_VALUE)
        );
        VentanaJuegoLayout.setVerticalGroup(
            VentanaJuegoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(VentanaJuego, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(VentanaJuego, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                minave.setPulsadoIzquierda(true);
                break;
            case KeyEvent.VK_RIGHT:
                minave.setPulsadoDerecha(true);
                break;
            case KeyEvent.VK_SPACE:
                midisparo.posicionDisparo(minave);
                break;
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                minave.setPulsadoIzquierda(false);
                break;
            case KeyEvent.VK_RIGHT:
                minave.setPulsadoDerecha(false);
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
    private javax.swing.JPanel VentanaJuego;
    // End of variables declaration//GEN-END:variables
}
