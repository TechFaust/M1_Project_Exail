package m1.exail.view

import m1.exail.controller.Controller
import org.apache.commons.net.ftp.FTPFile
import java.awt.BorderLayout
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import javax.swing.JFrame
import kotlin.properties.Delegates

class MainFrame : JFrame(), PropertyChangeListener {
    val pcs = PropertyChangeSupport(this)

    var ftpPath: String? by Delegates.observable(null) { _, _, newValue ->
        fileExplorer.setPathDisplayText(newValue ?: "/")
    }

    private var droneIp: String? by Delegates.observable(null) { _, _, newValue ->
        topBar.setDroneStatus(newValue, null, null)
    }


    private val topBar = TopBar(this)
    private val fileExplorer = FileExplorer(this)

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

    fun downloadFile(origin: String, target: String) {
        pcs.firePropertyChange(Controller.FTP_FILE_DOWNLOAD, origin, target)
    }

    fun uploadFile(origin: String, target: String) {
        pcs.firePropertyChange(Controller.FTP_FILE_UPLOAD, origin, target)
    }

    override fun propertyChange(evt: PropertyChangeEvent?) {
        println("MainFrame received event: ${evt?.propertyName}")
        when (evt?.propertyName) {
            Controller.DRONE_IP_CHANGED -> {
                droneIp = evt.newValue as String
            }
            Controller.CONNECTION_SPEED_CHANGED -> {
                topBar.setInformationLabelText(evt.newValue as Double)
            }
            Controller.FTP_FILE_LIST_CHANGED -> {
                // Warning de "unchecked cast", à voir si on peut régler ça, mais ça devrait fonctionner
                fileExplorer.setFileList(evt.newValue as List<FTPFile>)
            }
        }
    }
}