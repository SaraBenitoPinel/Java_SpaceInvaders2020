
package codigo;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Sara Benito
 */
public class Disparo {
    Image imagen = null;
    public int posX = 0;
    public int posY = 0;

    
    public Disparo(){
            try{
                imagen=ImageIO.read(getClass().getResource("/imagenes/disparo.png"));
            }
            catch(Exception e){
            }
    }
    
    public void mueve(){
         posY -= 5;
            
    }

    public void posicionaDisparo( Nave _nave){
        posX = _nave.posX 
                +_nave.imagen.getWidth(null)/2 
                - imagen.getWidth(null)/2;
        posY = _nave.posY - _nave.imagen.getHeight(null)/2;
        
    }
    
    
}




