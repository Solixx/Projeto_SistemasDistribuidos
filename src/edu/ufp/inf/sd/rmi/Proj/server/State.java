/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: UFP </p>
 * @author Rui Moreira
 * @version 2.0
 */
package edu.ufp.inf.sd.rmi.Proj.server;

import java.io.Serializable;

/**
 * 
 * @author rui
 */
public class State implements Serializable {
    private String msg;
    private int id;

    /**
     * 
     * @param id
     * @param m 
     */
    public State(int id, String m) {
        this.id = id;
        this.msg = m;
    }

    public State(String m) {
        this.msg = m;
    }

    /**
     * 
     * @return 
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @return 
     */
    public String getInfo(){
        return this.msg;
    }

    /**
     * 
     * @param m 
     */
    public void setInfo(String m){
        this.msg = m;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setId(int id) {
        this.id = id;
    }
}
