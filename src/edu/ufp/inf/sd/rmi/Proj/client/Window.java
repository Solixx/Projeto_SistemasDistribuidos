package edu.ufp.inf.sd.rmi.Proj.client;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

class Window extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;

    Window(Game game, ObserverRI observer) throws InterruptedException, RemoteException {
        Sprite.loadImages();
        Sprite.setMaxLoopStatus();

        add(game);

        setTitle("bomberman");
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addKeyListener(new Sender(observer));
    }
}
