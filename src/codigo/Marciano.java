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
 * @author Sarita
 */
public class Marciano {

    public Image imagen1 = null;
    public Image imagen2 = null;
    private int anchoPantalla;
    public int vida = 50;

    public Marciano(int _anchoPantalla) {
        anchoPantalla = _anchoPantalla;
        try {
            imagen1 = ImageIO.read(getClass().getResource("/imagenes/marcianito1.png"));
            imagen2 = ImageIO.read(getClass().getResource("/imagenes/marcianito2.png"));
        } catch (Exception e) {

        }
    }
}
