package codigo;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Sara Benito
 */
public class Disparo {

    Image imagen = null;
    public int posX = 0;
    public int posY = 0;
    Clip sonidoDisparo; //SONIDOS DE TIPO .wap

    public Disparo() {
        try {
            //imagen = ImageIO.read(getClass().getResource("/imagenes/disparo.png"));
            sonidoDisparo = AudioSystem.getClip();
            sonidoDisparo.open(AudioSystem.getAudioInputStream(getClass().getResource("/sonidos/laser.wav")));
        } catch (LineUnavailableException ex) {
        } catch (IOException ex) {
        } catch (UnsupportedAudioFileException ex) {
        }
    }

    public void mueve() {
        posY -= 5;

    }
    public void posicionaDisparo(Nave _nave) {
        posX = _nave.posX
                + _nave.imagen.getWidth(null) / 2
                - imagen.getWidth(null) / 2;
        posY = _nave.posY - _nave.imagen.getHeight(null) / 2;

    }

}
