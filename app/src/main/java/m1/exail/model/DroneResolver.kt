package m1.exail.model

import java.io.IOException
import java.net.*
import java.util.*

private fun getNetworkBroadcastAddress(): InetAddress {
    // récupère la liste des interfaces réseau
    val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
    for (netInterface in interfaces) {
        // ignore les interfaces de loopback et celles qui ne sont pas activées
        if (netInterface.isLoopback || !netInterface.isUp) continue

        val interfaceAddresses = netInterface.interfaceAddresses
        for (interfaceAddress in interfaceAddresses) {
            val broadcast = interfaceAddress.broadcast
            // vérifie qu'on a une adresse de broadcast
            if (broadcast != null) {
                return broadcast
            }
        }
    }
    throw UnknownHostException("No broadcast address found")
}

public fun getDroneAddress(): InetAddress {
    try {
        // crée un socket UDP
        val socket = DatagramSocket(42000)
        socket.broadcast = true

        // crée un paquet vide
        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)

        // envoie le paquet sur l'adresse de broadcast
        val broadcastAddress: InetAddress = getNetworkBroadcastAddress()
        packet.address = broadcastAddress
        packet.port = 42001
        socket.send(packet)


        // met le socket en mode écoute
        socket.soTimeout = 10000

        // attend de recevoir un paquet
        socket.receive(packet)
        println("Received packet from: " + packet.address)
        return packet.address
    } catch (e: SocketException) { // erreur lors de la création du socket
        println("Error: " + e.message)
        throw RuntimeException("Could not open Socket on port 42000")
    } catch (e: SocketTimeoutException) { // le drone n'a pas répondu à temps
        throw RuntimeException("Drone did not respond in time")
    } catch (e: java.rmi.UnknownHostException) {
        println("Error: " + e.message)
        throw RuntimeException("Could not find broadcast address")
    } catch (e: IOException) { // erreur lors de la réception du paquet
        println("Error: " + e.message)
        throw RuntimeException("Error while receiving packet")
    }
}
