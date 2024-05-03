package m1.exail.controller

import java.net.InetAddress

class Controller {
    companion object {
        const val DRONE_SEARCH = "droneSearch"  // event de recherche de l'adresse IP du drone
        const val DRONE_IP_CHANGED = "droneIpChanged"  // event de changement d'adresse IP du drone
        const val DRONE_CONNECTION = "droneConnection"  // event de connexion/déconnexion du drone

        const val CONNECTION_SPEED_REQUEST = "connectionSpeedRequest"  // event de demande de débit de connexion
        const val CONNECTION_SPEED_CHANGED = "connectionSpeedChanged"  // event de changement de débit de connexion

        const val FTP_CONNECTION = "ftpConnection"  // event de connexion/déconnexion au serveur FTP

        const val FTP_PATH_CHANGED = "ftpPathChanged"  // event de changement de répertoire FTP
        const val FTP_FILE_LIST_CHANGED = "ftpFileListChanged"  // event de changement de liste de fichiers FTP

        const val FTP_FILE_DOWNLOAD = "ftpFileDownload"  // event de téléchargement de fichier FTP
        const val FTP_FILE_DOWNLOADED = "ftpFileDownloaded"  // event de fin de téléchargement de fichier FTP
        const val FTP_FILE_UPLOAD = "ftpFileUpload"  // event de téléversement de fichier FTP
        const val FTP_FILE_UPLOADED = "ftpFileUploaded"  // event de fin de téléversement de fichier FTP
    }
}