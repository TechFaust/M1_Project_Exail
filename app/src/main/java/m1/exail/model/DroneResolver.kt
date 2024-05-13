package m1.exail.model

import m1.exail.controller.controllerInstance
import java.io.IOException
import java.net.*
import java.util.*

private fun getNetworkBroadcastAddress(): List<InetAddress> {
    val list = mutableListOf<InetAddress>()
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
                list.add(broadcast)
            }
        }
    }
    return list
}

fun getDroneAddress(): List<InetAddress> {
    val list = mutableListOf<InetAddress>()
    var receiver: DatagramSocket? = null
    try {
        val broadcastAddress = getNetworkBroadcastAddress()
        if (broadcastAddress.isEmpty()) {
            throw RuntimeException("Aucunes interfaces réseau utilisables")
        }

        val emitter = DatagramSocket()
        val emitted = DatagramPacket(ByteArray(0), 0)
        emitted.port = 42001
        emitter.broadcast = true
        for(addr in broadcastAddress) {
            emitted.address = addr
            emitter.send(emitted)
        }
        emitter.close()

        receiver = DatagramSocket(42000)
        receiver.soTimeout = 10000 // timeout de 10 secondes
        val buffer = ByteArray(1024)
        val received = DatagramPacket(buffer, buffer.size)
        while(true) {
            receiver.receive(received)
            val address = received.address
            if(address.isReachable(1000)) {
                if(list.contains(address)) continue
                list.add(address)
                controllerInstance?.droneIpUpdate(list)
            }
        }

    } catch (e: SocketTimeoutException) { // on arrive au bout du timeout
        receiver?.close()
        if (list.isEmpty()) { // si on n'a pas trouvé de drone
            throw RuntimeException("Drone did not respond in time")
        } else { // sinon on retourne la liste
            return list
        }
    } catch (e: SocketException) { // erreur lors de la création du socket
        println("Error: " + e.message)
        throw RuntimeException("Could not open Socket on port 42000")
    }  catch (e: UnknownHostException) { // erreur lors de la résolution de l'adresse
        println("Error: " + e.message)
        throw RuntimeException("Could not find broadcast address")
    } catch (e: IOException) { // erreur lors de la réception du paquet
        println("Error: " + e.message)
        throw RuntimeException("Error while receiving packet")
    }
}
