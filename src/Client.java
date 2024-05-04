import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

class Window extends JFrame {
   private static final long serialVersionUID = 1L;

   Window() {
      Sprite.loadImages();
      Sprite.setMaxLoopStatus();
      
      add(new Game(Const.COL*Const.SIZE_SPRITE_MAP, Const.LIN*Const.SIZE_SPRITE_MAP));
      setTitle("bomberman");
      pack();
      setVisible(true);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      addKeyListener(new Sender());
   }
}