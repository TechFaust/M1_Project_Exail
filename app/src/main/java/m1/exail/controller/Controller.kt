package m1.exail.controller

import m1.exail.model.FTPHandler
import m1.exail.model.getDroneAddress
import m1.exail.view.MainFrame

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.net.InetAddress

var controllerInstance: Controller? = null

class Controller: PropertyChangeListener {
    companion object {
        const val DRONE_SEARCH = "droneSearch"  // event de recherche de l'adresse IP du drone
        const val DRONE_IP_CHANGED = "droneIpChanged"  // event de changement d'adresse IP du drone

        const val CONNECTION_SPEED_REQUEST = "connectionSpeedRequest"  // event de demande de débit de connexion
        const val CONNECTION_SPEED_CHANGED = "connectionSpeedChanged"  // event de changement de débit de connexion

        const val FTP_CONNECTION = "ftpConnection"  // event de connexion/déconnexion au serveur FTP
        const val FTP_CONNECTED = "ftpConnected"  // event de connexion au serveur FTP

        const val FTP_PATH_CHANGED = "ftpPathChanged"  // event de changement de répertoire FTP
        const val FTP_FILE_LIST_CHANGED = "ftpFileListChanged"  // event de changement de liste de fichiers FTP

        const val FTP_FILE_DOWNLOAD = "ftpFileDownload"  // event de téléchargement de fichier FTP
        const val FTP_FILE_DOWNLOADED = "ftpFileDownloaded"  // event de fin de téléchargement de fichier FTP
        const val FTP_FILE_UPLOAD = "ftpFileUpload"  // event de téléversement de fichier FTP
        const val FTP_FILE_UPLOADED = "ftpFileUploaded"  // event de fin de téléversement de fichier FTP
    }

    init {
        controllerInstance = this
    }

    fun droneIpUpdate(newAddresses: List<InetAddress>){
        pcs.firePropertyChange(DRONE_IP_CHANGED, null, newAddresses)
    }

    private var ftpHandler: FTPHandler? = null

    private val pcs = PropertyChangeSupport(this)

    fun addEventListener(listener: PropertyChangeListener) {
        pcs.addPropertyChangeListener(listener)
    }

    fun removeEventListener(listener: PropertyChangeListener) {
        pcs.removePropertyChangeListener(listener)
    }

    private val uis = mutableListOf<MainFrame>()

    fun addUi(ui: MainFrame) {
        ui.addEventListener(this)
        addEventListener(ui)
        if(ftpHandler != null) {
            ui.addEventListener(ftpHandler!!)
            ftpHandler?.addEventListener(ui)
        }
        uis.add(ui)
    }

    fun removeUi(ui: MainFrame) {
        ui.removeEventListener(this)
        removeEventListener(ui)
        if(ftpHandler != null) {
            ui.removeEventListener(ftpHandler!!)
            ftpHandler?.removeEventListener(ui)
        }
        uis.remove(ui)
    }

    override fun propertyChange(evt: PropertyChangeEvent?) {
        println("Controller received event: ${evt?.propertyName} from ${evt?.source}")
        when (evt?.propertyName) {
            DRONE_SEARCH -> {
                try {
                    getDroneAddress()
                } catch (e: Exception) {
                    pcs.firePropertyChange(DRONE_IP_CHANGED, null, e)
                }
            }
            FTP_CONNECTION -> {
                if(evt.newValue == null){
                    ftpHandler?.removeEventListener(this)
                    for(ui in uis) {
                        ftpHandler?.removeEventListener(ui)
                        ui.removeEventListener(ftpHandler!!)
                    }
                    ftpHandler = null
                    pcs.firePropertyChange(FTP_CONNECTED, null, null)
                } else {
                    ftpHandler = FTPHandler(evt.newValue as InetAddress)
                    ftpHandler?.addEventListener(this)
                    for(ui in uis) {
                        ftpHandler?.addEventListener(ui)
                        ui.addEventListener(ftpHandler!!)
                    }
                    pcs.firePropertyChange(FTP_CONNECTED, null, evt.newValue as InetAddress)
                }
            }
        }
    }
}