package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.DigLibFactoryRI;
import edu.ufp.inf.sd.rmi.Proj.server.DigLibSessionRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIClient {
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private DigLibFactoryRI digLibFactoryRI;


    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.edu.ufp.inf.sd.rmi._01_helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            RMIClient hwc=new RMIClient(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService();
        }
    }

    public RMIClient(String args[]) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy MAIL_TO_ADDR rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR lookup service @ {0}", serviceUrl);

                //============ Get proxy MAIL_TO_ADDR HelloWorld service ============
                digLibFactoryRI = (DigLibFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return (Remote) digLibFactoryRI;
    }

    private void playService() {
        try {
            //============ Call HelloWorld remote service ==========

            DigLibSessionRI thisSession = null;

            thisSession = menuAuth();

            System.out.println("Session: " + thisSession);

            menuSalas(thisSession);


            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Client Start");


        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DigLibSessionRI menuAuth() throws RemoteException {
        System.out.println("Menu:");
        System.out.println("1 - Register");
        System.out.println("2 - Login");
        System.out.println("0 - Exit");

        Scanner scanner = new Scanner(System.in);

        String username, password;

        switch (scanner.nextInt()) {
            case 1:
                System.out.println("=Register=");
                System.out.print("Username: ");
                Scanner Susername = new Scanner(System.in);
                System.out.print("\nPassword: ");
                Scanner Spassword = new Scanner(System.in);

                username = Susername.nextLine();
                password = Spassword.nextLine();

                while(!this.digLibFactoryRI.register(username, password)) {
                    System.out.println("Username already exists");
                    System.out.print("Username: ");
                    Susername = new Scanner(System.in);
                    username = Susername.nextLine();
                    System.out.print("\nPassword: ");
                    Spassword = new Scanner(System.in);

                    password = Spassword.nextLine();
                }

                return menuAuth();
            case 2:
                System.out.println("Login");

                System.out.print("Username: ");
                Scanner S2username = new Scanner(System.in);
                username = S2username.nextLine();
                System.out.print("\nPassword: ");
                Scanner S2password = new Scanner(System.in);


                password = S2password.nextLine();

                return this.digLibFactoryRI.login(username, password);
            default:
                System.out.println("Invalid option");
                System.exit(0);
                return menuAuth();
        }
    }

    private void waitingRoom(User user) throws RemoteException {
        Game game = user.game;
        System.out.println("À espera de jogadores");

        System.out.println(user.game);

        System.out.println("CurrP: " + game.players.size());
        System.out.println("CurrP: " + game.maxPlayers);
        while (game.players.size() < game.maxPlayers){

        }

        new Window(game, game.findObserver(user.getId()));
    }

    public void menuSalas(DigLibSessionRI thisSession) throws RemoteException {
        System.out.println("Menu:");
        System.out.println("1 - Ver Salas");
        System.out.println("2 - Criar Sala");
        System.out.println("3 - Entrar numa Sala");
        System.out.println("0 - Logout");

        Scanner scanner = new Scanner(System.in);

        switch (scanner.nextInt()){
            case 1:
                System.out.println("=Lista Salas=");

                ArrayList<Game> s = thisSession.listSalas();

                for (Game sala : s) {
                    System.out.println("salaClient: " + sala.getId() + " MaxPlayers: " + sala.maxPlayers + "CurrentPlayers: " + sala.users.size());
                }

                System.out.println("1 - Entrar numa Sala");
                System.out.println("0 - Voltar");

                Scanner scanner2 = new Scanner(System.in);

                if (scanner2.nextInt() == 1) {
                    System.out.println("Insira o numero da sala");
                    Scanner numeroSala = new Scanner(System.in);
                    if(thisSession.joinSala(numeroSala.nextInt())){
                        System.out.println("Entrou");
                    } else{
                        System.out.println("Erro ao entrar na sala");
                    }
                }
                menuSalas(thisSession);
            case 2:
                System.out.println("=Criar Sala=");

                System.out.println("Numero Maximo de Jogadores");
                Scanner scanner3 = new Scanner(System.in);

                int numPlayer = scanner3.nextInt();

                if(thisSession.criarSala(numPlayer)){
                    System.out.println("Entrou");
                    waitingRoom(thisSession.getUser());
                } else{
                    System.out.println("Erro ao entrar na sala");
                }

                menuSalas(thisSession);
            case 3:
                System.out.println("=Escolher sala=");

                System.out.println("Insira o numero da sala");
                Scanner numeroSala = new Scanner(System.in);
                thisSession.joinSala(numeroSala.nextInt());
                System.out.println("Entrou");

                menuSalas(thisSession);
            default:
                thisSession.logout();

                menuSalas(menuAuth());
        }
    }
}
