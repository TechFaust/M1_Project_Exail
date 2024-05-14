package m1.exail.model

import m1.exail.controller.Controller
import java.net.InetAddress
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.io.File


class FTPHandler(private val address: InetAddress, port: Int = 21) : PropertyChangeListener {
    private val pcs = PropertyChangeSupport(this)

    fun addEventListener(listener: PropertyChangeListener) {
        pcs.addPropertyChangeListener(listener)
    }

    fun removeEventListener(listener: PropertyChangeListener) {
        pcs.removePropertyChangeListener(listener)
    }

    private val client = FTPClient()

    init {
        client.connect(address, port)
        client.login("anonymous", "")
        client.keepAlive = true
        println("Connected to ${address.hostAddress}")
    }

    private fun listFiles(path: String = "/"): List<FTPFile> {
        return client.listFiles(path).toList()
    }


    override fun propertyChange(evt: PropertyChangeEvent?) {
        println("FTPHandler received event: ${evt?.propertyName} from ${evt?.source}")
        when (evt?.propertyName) {
            Controller.FTP_PATH_CHANGED -> {
                val files = listFiles(evt.newValue as String)
                pcs.firePropertyChange(Controller.FTP_FILE_LIST_CHANGED, null, files)
            }
            Controller.FTP_FILE_DOWNLOAD -> {
                val remote = evt.oldValue as String
                val local = evt.newValue as String
                println("Downloading \"$remote\" to \"$local\"")
                val target = File(local)
                client.retrieveFile(remote, target.outputStream())
            }
            Controller.FTP_FILE_UPLOAD -> {
                val local = evt.oldValue as String
                val remote = evt.newValue as String
                val target = File(local)
                client.storeFile(remote, target.inputStream())
            }
            Controller.FTP_CREATE_FOLDER -> {
                val remote = evt.newValue as String
                println("Creating folder \"$remote\"")
                client.changeWorkingDirectory("${remote.substringBeforeLast('/')}/")
                if(!client.makeDirectory(remote.substringAfterLast('/'))){
                    println("Failed to create folder")
                }
            }
            Controller.CONNECTION_SPEED_REQUEST -> {
                val speed = getSpeed(address, 20000)
                pcs.firePropertyChange(Controller.CONNECTION_SPEED_CHANGED, null, speed)
            }
        }
    }
}