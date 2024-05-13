package m1.exail.view

import m1.exail.controller.Controller
import org.apache.commons.net.ftp.FTPFile
import java.awt.BorderLayout
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.lang.Exception
import java.net.InetAddress
import javax.swing.JFrame

class MainFrame : JFrame(), PropertyChangeListener {
    val pcs = PropertyChangeSupport(this)

    fun addEventListener(listener: PropertyChangeListener) {
        pcs.addPropertyChangeListener(listener)
    }

    fun removeEventListener(listener: PropertyChangeListener) {
        pcs.removePropertyChangeListener(listener)
    }

    private val topBar = TopBar(this)
    private var fileExplorer = FileExplorer(this)

    init {
        title = "Exail Drone Communication Software"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(800, 600)
        isVisible = true
        layout = BorderLayout()
        add(topBar, BorderLayout.NORTH)
        add(fileExplorer, BorderLayout.CENTER)
        revalidate()
    }

    override fun propertyChange(evt: PropertyChangeEvent?) {
        println("MainFrame received event: ${evt?.propertyName} from ${evt?.source}")
        when (evt?.propertyName) {
            Controller.DRONE_IP_CHANGED -> {
                if(evt.newValue is Exception){
                    topBar.setDroneStatus(evt.newValue as Exception)
                } else {
                    // Warning de "unchecked cast", à voir si on peut arranger ça, mais ça devrait fonctionner
                    topBar.setDroneStatus(evt.newValue as List<InetAddress>)
                }
            }
            Controller.FTP_CONNECTED -> {
                if(evt.newValue == null){
                    topBar.setDroneStatus(Exception(""))
                    remove(fileExplorer)
                    fileExplorer = FileExplorer(this)
                    add(fileExplorer, BorderLayout.CENTER)
                } else {
                    topBar.setDroneStatus(evt.newValue as InetAddress)
                    fileExplorer.setFileList(emptyList())
                    fileExplorer.path = "/"
                }
            }
            Controller.CONNECTION_SPEED_CHANGED -> {
                val uploadSpeed = (evt.newValue as Pair<*, *>).first as Double
                val downloadSpeed = (evt.newValue as Pair<*, *>).second as Double
                topBar.setDroneStatus(uploadSpeed, downloadSpeed)
            }
            Controller.FTP_FILE_LIST_CHANGED -> {
                assert(evt.newValue is List<*>)
                assert((evt.newValue as List<*>).all { it is FTPFile })
                fileExplorer.setFileList(evt.newValue as List<FTPFile>)
                pcs.firePropertyChange(Controller.CONNECTION_SPEED_REQUEST, null, null)
            }
        }
    }
}