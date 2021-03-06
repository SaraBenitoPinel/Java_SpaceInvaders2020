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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 *
 * @author Sara Benito
 */
public class VentanaJuego extends javax.swing.JFrame {

    //LO QUE QUEREMOS QUE MIDA EL ANCHO Y EL LARGO DE LA PANTALLA
    static int ANCHOPANTALLA = 800;
    static int ALTOPANTALLA = 700;
    int filasMarcianos = 5;
    int columnasMarcianos = 10;
    int contador = 0;
    BufferedImage buffer = null;
    //buffer para guardar las imágenes de todos los marcianos
    BufferedImage plantilla = null;
    Image[] imagenes = new Image[30];

    Marciano marciano = new Marciano(ANCHOPANTALLA);//inicializo el marciano
    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();
    ArrayList<Disparo> listaDisparos = new ArrayList();
    ArrayList<Explosion> listaExplosiones = new ArrayList();
    //EL ARRAY DE DOS DIMENSIONES QUE GUARDA LA LISTA DE MARCIANOS
    Marciano[][] listaMarcianos = new Marciano[filasMarcianos][columnasMarcianos];
    //DIRECCION EN LA QUE SE MUEVE EL GRUPO DE MARCIANOS
    boolean direccionMarcianos = true;

    //HILO DE EJECUCION NUEVO QUE SE ENCARGA DE REFRESCAR EL CONTENIDO DE LA PANTALLA 
    //(BUCLE DE ANIMACION DEL JUEGO)
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override //SOBRESCRIBIR
        public void actionPerformed(ActionEvent e) {
            //CODIGO DE LA ANIMACION
            bucleDelJuego();
        }
    });

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {

        initComponents();

        try {
            plantilla = ImageIO.read(getClass().getResource("/imagenes/invaders2.png"));
        } catch (IOException ex) {
        }
        //CARGO LAS 30 IMAGENES DEL SPRITESHEET EN EL ARRAY DE BUFFEREDIMAGES
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                imagenes[i * 4 + j] = plantilla
                        .getSubimage(j * 64, i * 64, 64, 64)
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH);

            }
        }
        imagenes[20] = plantilla.getSubimage(0, 320, 66, 32); //SPRITE DE LA NAVE
        imagenes[21] = plantilla.getSubimage(66, 320, 64, 32);
        imagenes[22] = plantilla.getSubimage(255, 320, 32, 32); //EXPLOSION PARTE B
        imagenes[23] = plantilla.getSubimage(255, 289, 32, 32); //EXPLOSION PARTE A
        imagenes[24] = plantilla.getSubimage(194, 320, 64, 32).getScaledInstance(32, 32, Image.SCALE_SMOOTH); //EXPLOSION PARTE C

        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        VentanaJuego.setSize(ANCHOPANTALLA, ALTOPANTALLA);
        buffer = (BufferedImage) VentanaJuego.createImage(ANCHOPANTALLA, ALTOPANTALLA);//INICIALIZO EL BUFFER
        buffer.createGraphics();

        temporizador.start();//ARRANCO EL TEMPORIZADOR
        miNave.imagen = imagenes[20];
        miNave.posX = ANCHOPANTALLA / 2 - miNave.imagen.getWidth(this) / 2;
        miNave.posY = ALTOPANTALLA - 100;
        //CREAMOS EL ARRAY DE MARCIANOS
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnasMarcianos; j++) {
                listaMarcianos[i][j] = new Marciano(ANCHOPANTALLA);
                listaMarcianos[i][j].imagen1 = imagenes[2 * i];
                listaMarcianos[i][j].imagen2 = imagenes[2 * 1 + 1];
                listaMarcianos[i][j].posX = j * (15 + listaMarcianos[i][j].imagen1.getWidth(null));
                listaMarcianos[i][j].posY = i * (10 + listaMarcianos[i][j].imagen1.getHeight(null));
            }
        }
        miDisparo.posY = -2000;
    }

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

    private void pintaDisparos(Graphics2D g2) {
        //PINTA TODOS LOS DISPAROS 
        Disparo disparoAux;
        for (int i = 0; i < listaDisparos.size(); i++) {
            disparoAux = listaDisparos.get(i);
            disparoAux.mueve();
            if (disparoAux.posY < 0) {
                listaDisparos.remove(i);
            }
            g2.drawImage(disparoAux.imagen, disparoAux.posX, disparoAux.posY, null);
        }
    }

    private void pintaExplosiones(Graphics2D g2) {
        //PINTA TODAS LAS EXPLOSIONES
        Explosion explosionAux;
        for (int i = 0; i < listaExplosiones.size(); i++) {
            explosionAux = listaExplosiones.get(i);
            explosionAux.tiempoDeVida--;
            if (explosionAux.tiempoDeVida > 25) {
                g2.drawImage(explosionAux.imagen1, explosionAux.posX, explosionAux.posY, null);
            } else if (explosionAux.tiempoDeVida >10) {
                g2.drawImage(explosionAux.imagen2, explosionAux.posX, explosionAux.posY, null);
            }
            if (explosionAux.tiempoDeVida > 5) {
                g2.drawImage(explosionAux.imagen3, explosionAux.posX, explosionAux.posY, null);
            }
            //SI EL TIEMPO DE VIDA DE LA EXPLOSION ES MENOR O IGUAL A 0 LA ELIMINO
            if (explosionAux.tiempoDeVida <= 0) {
                listaExplosiones.remove(i);
            }
        }
    }


private void bucleDelJuego() {
        //ESTE METODO GOBIERNA EL REDIBUJADO DE LOS OBJETOS EN EL JPANEL1
        //PRIMERO BORRO TODO LO QUE HAY EN EL BUFFER
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();//borro todo lo que ahi en el buffer

        g2.setColor(Color.BLACK);//doy el color negro a la pantalla
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);
        ///////////////////////////////////////////////////
        contador++;
        pintaMarcianos(g2);
        //DIBUJO LA NAVE
        g2.drawImage(miNave.imagen, miNave.posX, miNave.posY, null);
        pintaDisparos(g2);
        pintaExplosiones(g2);
        miNave.mueve();
        chequeaColision();
        ///////////////////////////////////////////////////
        g2 = (Graphics2D) VentanaJuego.getGraphics();//DIBUJO DE GOLPE EL BUFFER SOBRE EL JPANEL
        g2.drawImage(buffer, 0, 0, null);

    }

    //CHEQUEA SI UN DISPARO Y UN MARCIANO COLISIONAN
    private void chequeaColision() {
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();

        for (int k = 0; k < listaDisparos.size(); k++) {
            //CALCULO EL RECTANGULO QUE CONTIENE AL DISPARO
            rectanguloDisparo.setFrame(listaDisparos.get(k).posX,
                    listaDisparos.get(k).posY,
                    listaDisparos.get(k).imagen.getWidth(null),
                    listaDisparos.get(k).imagen.getHeight(null));

            for (int i = 0; i < filasMarcianos; i++) {
                for (int j = 0; j < columnasMarcianos; j++) {
                    //CALCULO EL RECTANGULO CORRESPONDIENTE AL MARCIANO QUE ESTOY COMPROBANDO
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].posX,
                            listaMarcianos[i][j].posY,
                            listaMarcianos[i][j].imagen1.getWidth(null),
                            listaMarcianos[i][j].imagen1.getHeight(null)
                    );
                    if (rectanguloDisparo.intersects(rectanguloMarciano)) {
                        //SI ENTRA AQUI ES PORQUE HAN CHOCADO UN MARCIANO Y EL DISPARO
                        Explosion e = new Explosion();
                        e.posX = listaMarcianos[i][j].posX;
                        e.posY = listaMarcianos[i][j].posY;
                        e.imagen1 = imagenes[23];
                        e.imagen2 = imagenes[22];
                        e.imagen3 = imagenes[24];
                        listaExplosiones.add(e);
                        e.sonidoExplosion.start();//SUENA EL SONIDO
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
            .addGap(0, 420, Short.MAX_VALUE)
        );
        VentanaJuegoLayout.setVerticalGroup(
            VentanaJuegoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(VentanaJuego, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                miNave.setPulsadoIzquierda(true);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDerecha(true);
                break;
            case KeyEvent.VK_SPACE:
                Disparo d = new Disparo();
                d.sonidoDisparo.start();
                d.posicionaDisparo(miNave);
                //AGREGAMOS EL DISPARO A LA LISTA DE DISPAROS
                listaDisparos.add(d);
                break;
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
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
            java.util.logging.Logger.getLogger(VentanaJuego

.class  


.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego

.class  


.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego

.class  


.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego

.class  


.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
