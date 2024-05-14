package m1.exail.view

import m1.exail.controller.Controller
import m1.exail.view.elements.FileUi
import m1.exail.view.elements.FolderUi
import org.apache.commons.net.ftp.FTPFile
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*

class FileExplorer(private val caller: MainFrame) : JPanel() {
    private val pathDisplay: JLabel = JLabel("Répertoire courant : /", JLabel.CENTER)
    private val fileDisplay: JPanel = JPanel()
    private val uploadButton: JButton = JButton("Téléverser un fichier")
    private val newFolderButton: JButton = JButton("Créer un nouveau dossier")
    private val uploadPanel: JPanel = JPanel()

    var path: String = "/"
        set(value) {
            field = if(value.endsWith("/")) value else "${value}/"
            pathDisplay.text = "Répertoire courant : $field"  // Update the path display text
            caller.pcs.firePropertyChange(Controller.FTP_PATH_CHANGED, null, field)  // Notify the controller
        }

    init {
        layout = BorderLayout(10, 10)
        fileDisplay.layout = FlowLayout(FlowLayout.CENTER, 10, 10)
        add(fileDisplay, BorderLayout.CENTER)
        fileDisplay.add(JLabel("Aucun drone connecté."), JLabel.CENTER)
        uploadPanel.layout = FlowLayout(FlowLayout.RIGHT, 10, 10)
        uploadPanel.add(uploadButton)
        uploadButton.addActionListener {
            val chooser = JFileChooser()
            chooser.dialogTitle = "Choisissez un fichier à téléverser"
            chooser.showOpenDialog(this)
            val file = chooser.selectedFile
            if(file != null) {
                val remote = path + file.name
                uploadFile(file.absolutePath, remote)
            }
        }
        uploadPanel.add(newFolderButton)
        newFolderButton.addActionListener {
            val folderName = JOptionPane.showInputDialog(this, "Nom du nouveau dossier :")
            if(folderName != null) {
                val remote = path + folderName
                caller.pcs.firePropertyChange(Controller.FTP_CREATE_FOLDER, null, remote)
            }
        }
    }

    fun setFileList(incomingFiles: List<FTPFile>) {
        removeAll()
        add(pathDisplay, BorderLayout.NORTH)

        fileDisplay.removeAll()
        fileDisplay.layout = FlowLayout(FlowLayout.LEFT, 10, 10)

        val dirs = incomingFiles.filter { it.isDirectory }.sortedBy { it.name }
        val files = incomingFiles.filter { !it.isDirectory }.sortedBy { it.name }

        fileDisplay.add(FolderUi(this, ".."))  // Add the parent directory
        fileDisplay.add(FolderUi(this, "."))  // Add the current directory

        for (d in dirs) fileDisplay.add(FolderUi(this, d.name))  // Add all the directories
        for (f in files) fileDisplay.add(FileUi(this, f.name))  // Add all the files

        add(fileDisplay, BorderLayout.CENTER)

        add(uploadPanel, BorderLayout.SOUTH)
    }

    fun downloadFile(remote: String, local: String) {
        caller.pcs.firePropertyChange(Controller.FTP_FILE_DOWNLOAD, remote, local)
    }

    fun uploadFile(local: String, remote: String) {
        caller.pcs.firePropertyChange(Controller.FTP_FILE_UPLOAD, local, remote)
    }
}