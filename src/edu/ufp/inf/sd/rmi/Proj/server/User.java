package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.Client;
import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 *
 * @author rmoreira
 */
public class User implements Serializable {

    static int currId = 1;
    private int id;
    private String uname;
    private String pword;
    private ObserverRI observer;
    private Client cliente;

    public User(String uname, String pword) {
        this.id = currId++;
        this.uname = uname;
        this.pword = pword;
    }

    @Override
    public String toString() {
        return "User{" + "uname=" + uname + ", pword=" + pword + '}';
    }

    /**
     * @return the uname
     */
    public String getUname() {
        return uname;
    }

    /**
     * @param uname the uname to set
     */
    public void setUname(String uname) {
        this.uname = uname;
    }

    /**
     * @return the pword
     */
    public String getPword() {
        return pword;
    }

    /**
     * @param pword the pword to set
     */
    public void setPword(String pword) {
        this.pword = pword;
    }

    public int getId() {
        return id;
    }

    public ObserverRI getObserver() {
        return observer;
    }

    public void setObserver(ObserverRI observer) throws RemoteException {
        this.observer = observer;
    }


}
