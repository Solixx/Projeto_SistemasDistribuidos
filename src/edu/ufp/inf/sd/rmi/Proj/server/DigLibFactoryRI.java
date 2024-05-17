package edu.ufp.inf.sd.rmi.Proj.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DigLibFactoryRI extends Remote{

    boolean register(String usernamne, String pwd) throws RemoteException;

    DigLibSessionRI login(String user, String pwd) throws RemoteException;
}
