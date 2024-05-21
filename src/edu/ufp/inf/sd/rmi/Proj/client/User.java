package edu.ufp.inf.sd.rmi.Proj.client;

import java.io.Serializable;
import java.util.Arrays;

//class PlayerData {
//    boolean logged, alive;
//    int x, y; //coordenada atual
//    int numberOfBombs;
//    int userID;
//
//    PlayerData(int x, int y) {
//        this.x = x;
//        this.y = y;
//        this.logged = false;
//        this.alive = false;
//        this.numberOfBombs = 1; // para 2 bombas, é preciso tratar cada bomba em uma thread diferente
//    }
//
//    public void setUserID(int id){
//        this.userID = id;
//    }
//}

public class User implements Serializable {

    static int currID = 1;
    private int id;
    final static int rateStatusUpdate = 115;

    private Coordinate spawn[] = new Coordinate[Const.QTY_PLAYERS];
    //public PlayerData player[] = new PlayerData[Const.QTY_PLAYERS];
    private boolean alive[] = new boolean[Const.QTY_PLAYERS];
    public Coordinate map[][] = new Coordinate[Const.LIN][Const.COL];


    private String uname;
    private String pword;

    public Game game;
    public ObserverRI observer;

    public User(String uname, String pword) {
        this.id = currID++;
        this.uname = uname;
        this.pword = pword;
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

//    boolean loggedIsFull() {
//        for (int i = 0; i < Const.QTY_PLAYERS; i++)
//            if (!player[i].logged)
//                return false;
//        return true;
//    }

    public void joinGame(Game game){
        this.game = game;
        for (int i = 0; i < Const.LIN; i++)
            for (int j = 0; j < Const.COL; j++)
                map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, game.map[i][j].img);

        //situação (vivo ou morto) inicial de todos os jogadores
        for (int i = 0; i < game.maxPlayers; i++)
            this.alive[i] = game.playerData[i].alive;

        //coordenadas inicias de todos os jogadores
        for (int i = 0; i < Const.QTY_PLAYERS; i++)
            this.spawn[i] = new Coordinate(game.playerData[i].x, game.playerData[i].y);
    }

//    void setMap() {
//        for (int i = 0; i < Const.LIN; i++)
//            for (int j = 0; j < Const.COL; j++)
//                map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, "block");
//
//        // paredes fixas das bordas
//        for (int j = 1; j < Const.COL - 1; j++) {
//            map[0][j].img = "wall-center";
//            map[Const.LIN - 1][j].img = "wall-center";
//        }
//        for (int i = 1; i < Const.LIN - 1; i++) {
//            map[i][0].img = "wall-center";
//            map[i][Const.COL - 1].img = "wall-center";
//        }
//        map[0][0].img = "wall-up-left";
//        map[0][Const.COL - 1].img = "wall-up-right";
//        map[Const.LIN - 1][0].img = "wall-down-left";
//        map[Const.LIN - 1][Const.COL - 1].img = "wall-down-right";
//
//        // paredes fixas centrais
//        for (int i = 2; i < Const.LIN - 2; i++)
//            for (int j = 2; j < Const.COL - 2; j++)
//                if (i % 2 == 0 && j % 2 == 0)
//                    map[i][j].img = "wall-center";
//
//        // arredores do spawn
//        map[1][1].img = "floor-1";
//        map[1][2].img = "floor-1";
//        map[2][1].img = "floor-1";
//        map[Const.LIN - 2][Const.COL - 2].img = "floor-1";
//        map[Const.LIN - 3][Const.COL - 2].img = "floor-1";
//        map[Const.LIN - 2][Const.COL - 3].img = "floor-1";
//        map[Const.LIN - 2][1].img = "floor-1";
//        map[Const.LIN - 3][1].img = "floor-1";
//        map[Const.LIN - 2][2].img = "floor-1";
//        map[1][Const.COL - 2].img = "floor-1";
//        map[2][Const.COL - 2].img = "floor-1";
//        map[1][Const.COL - 3].img = "floor-1";
//    }

//    void setPlayerData() {
//        player[0] = new PlayerData(
//                map[1][1].x - Const.VAR_X_SPRITES,
//                map[1][1].y - Const.VAR_Y_SPRITES
//        );
//
//        player[1] = new PlayerData(
//                map[Const.LIN - 2][Const.COL - 2].x - Const.VAR_X_SPRITES,
//                map[Const.LIN - 2][Const.COL - 2].y - Const.VAR_Y_SPRITES
//        );
//        player[2] = new PlayerData(
//                map[Const.LIN - 2][1].x - Const.VAR_X_SPRITES,
//                map[Const.LIN - 2][1].y - Const.VAR_Y_SPRITES
//        );
//        player[3] = new PlayerData(
//                map[1][Const.COL - 2].x - Const.VAR_X_SPRITES,
//                map[1][Const.COL - 2].y - Const.VAR_Y_SPRITES
//        );
//    }

    public int getId() {
        return id;
    }

    public ObserverRI getObserver() {
        return observer;
    }

    public void setObserver(ObserverRI observer) {
        this.observer = observer;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                //", player=" + Arrays.toString(player) +
                ", map=" + Arrays.toString(map) +
                ", uname='" + uname + '\'' +
                ", pword='" + pword + '\'' +
                ", game=" + game +
                ", observer=" + observer +
                '}';
    }

    public Coordinate[] getSpawn() {
        return spawn;
    }

    public void setSpawn(Coordinate[] spawn) {
        this.spawn = spawn;
    }

    public boolean[] getAlive() {
        return alive;
    }

    public void setAlive(boolean[] alive) {
        this.alive = alive;
    }
}
