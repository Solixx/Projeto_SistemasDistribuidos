package rmi.cliente;

import rmi.server.DigLibFactoryRI;
import rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMICliente {
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private DigLibFactoryRI digLibFactoryRI;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi._01_helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            RMICliente hwc=new RMICliente(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService();
        }
    }

    public RMICliente(String args[]) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(RMICliente.class.getName()).log(Level.SEVERE, null, e);
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

            menuAuth();

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ping");


        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int menuAuth() throws RemoteException {
        System.out.println("Menu:");
        System.out.println("1 - Register");
        System.out.println("2 - Login");

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
                    System.out.print("\nPassword: ");
                    Spassword = new Scanner(System.in);

                    username = Susername.nextLine();
                    password = Spassword.nextLine();
                }

                break;
            case 2:
                System.out.println("Login");

                System.out.print("Username: ");
                Scanner S2username = new Scanner(System.in);
                System.out.print("\nPassword: ");
                Scanner S2password = new Scanner(System.in);

                username = S2username.nextLine();
                password = S2password.nextLine();

                this.digLibFactoryRI.login(username, password);

                break;
            default:
                System.out.println("Invalid option");
                menuAuth();
                break;
        }

        return 0;
    }
}
