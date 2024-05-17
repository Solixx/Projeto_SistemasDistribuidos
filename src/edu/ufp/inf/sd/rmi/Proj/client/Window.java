package edu.ufp.inf.sd.rmi.Proj.client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

class Window extends JFrame {
    private static final long serialVersionUID = 1L;

    Window(Game game, ObserverRI observer) {
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
