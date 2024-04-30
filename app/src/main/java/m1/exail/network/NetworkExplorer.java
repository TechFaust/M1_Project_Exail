package m1.exail.network;

import java.io.IOException;
import java.net.*;
import java.rmi.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class NetworkExplorer {
    public static void main(String[] args) {
        NetworkExplorer explorer = new NetworkExplorer();
        try{
            byte[] address = explorer.getNetworkBroadcast().getAddress();
            int[] correctAddress = new int[4];
            for(int i = 0; i < 4; i++){
                correctAddress[i] = address[i] + (address[i] < 0 ? 256 : 0);
            }
            System.out.println("Broadcast Address : " + correctAddress[0] + "." + correctAddress[1] + "." + correctAddress[2] + "." + correctAddress[3]);
        } catch (UnknownHostException | SocketException e){
            System.out.println("Error: " + e.getMessage());
        }

        try {
            InetAddress droneAddress = explorer.SearchDroneOnNetwork();
            System.out.println("Drone found at: " + droneAddress);
        } catch (RuntimeException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private InetAddress getNetworkBroadcast() throws UnknownHostException, SocketException {
        // récupère la liste des interfaces réseau
        List<NetworkInterface> interfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
        for(NetworkInterface netInterface : interfaceList){
            // vérifie si l'interface n'est pas une interface de loop back ou si elle n'est pas activée
            if(netInterface.isLoopback() || !netInterface.isUp()) continue;

            // récupère la liste des adresses de l'interface
            List<InterfaceAddress> addresses = netInterface.getInterfaceAddresses();
            for(InterfaceAddress address : addresses){
                // vérifie qu'on a une adresse de broadcast
                if(address.getBroadcast() == null) continue;

                return address.getBroadcast();
            }
        }
        throw new UnknownHostException("No broadcast address found");
    }

    public InetAddress SearchDroneOnNetwork(){
        try {
            // crée un socket UDP
            DatagramSocket socket = new DatagramSocket(42000);
            socket.setBroadcast(true);

            // crée un paquet vide
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // envoie le paquet sur l'adresse de broadcast
            InetAddress broadcastAddress = getNetworkBroadcast();
            packet.setAddress(broadcastAddress);
            packet.setPort(42001);
            socket.send(packet);


            // met le socket en mode écoute
            socket.setSoTimeout(10000);

            // attend de recevoir un paquet
            socket.receive(packet);
            System.out.println("Received packet from: " + packet.getAddress());
            return packet.getAddress();
            
        } catch (SocketException e) { // erreur lors de la création du socket
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException("Could not open Socket on port 42000");
        } catch (SocketTimeoutException e) { // le drone n'a pas répondu à temps
            throw new RuntimeException("Drone did not respond in time");
        } catch(UnknownHostException e){
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException("Could not find broadcast address");
        } catch (IOException e) { // erreur lors de la réception du paquet
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException("Error while receiving packet");
        }
    }
}
