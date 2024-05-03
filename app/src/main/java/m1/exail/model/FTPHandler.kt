package m1.exail.model

import java.net.InetAddress
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile

class File(private val name: String, private val isDirectory: Boolean){
    override fun toString(): String {
        return if(isDirectory) "$name (dir)" else name
    }
}

class FTPHandler(address: InetAddress, port: Int = 21) {
    private val client = FTPClient()

    init {
        client.connect(address, port)
        client.login("anonymous", "")
        println("Connected to ${address.hostAddress}")
    }

    fun listFiles(path: String = "/"): List<FTPFile> {
        return client.listFiles(path).toList()
    }
}