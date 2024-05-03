package m1.exail.view

import m1.exail.view.elements.FileUi
import m1.exail.view.elements.FolderUi
import org.apache.commons.net.ftp.FTPFile
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel

class FileExplorer(private val parent: MainFrame) : JPanel() {
    private val pathDisplay: JLabel = JLabel("Répertoire courant : /", JLabel.CENTER)
    init {
        layout = FlowLayout(FlowLayout.CENTER, 10, 10)
        add(JLabel("Aucun drone connecté."), JLabel.CENTER)
    }

    fun setPathDisplayText(path: String) {
        pathDisplay.text = "Répertoire courant : $path"
    }

    fun setFileList(incomingFiles: List<FTPFile>) {
        removeAll()
        add(pathDisplay)
        val dirs = incomingFiles.filter { it.isDirectory }.sortedBy { it.name }
        val files = incomingFiles.filter { !it.isDirectory }.sortedBy { it.name }
        for(f in dirs){
            add(FolderUi(parent, f.name))
        }
        for(f in files){
            add(FileUi(parent, f.name))
        }
        revalidate()
    }
}