package m1.exail.view.elements

import m1.exail.view.FileExplorer
import java.awt.Color
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists

private val fileIcon: BufferedImage = ImageIO.read(File("app/src/main/resources/File-Icon.png"))
private val scaledFileIcon = fileIcon.getScaledInstance(50, 50, BufferedImage.SCALE_SMOOTH)

private class CustomMouseFile(private val caller: FileUi): MouseAdapter(){
    override fun mouseEntered(e: MouseEvent?) { caller.hover(true) }
    override fun mouseExited(e: MouseEvent?) { caller.hover(false) }
    override fun mouseClicked(e: MouseEvent?) {
        if((e?.clickCount ?: 0) == 2 && (e?.button ?: 0) == MouseEvent.BUTTON1){
            val origin = caller.caller.path + caller.fileName

            val chooser = JFileChooser()
            chooser.dialogTitle = "Choisissez un emplacement pour le télécharger ${caller.fileName}"
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            chooser.showSaveDialog(caller.caller)

            if(chooser.selectedFile == null) return  // si on a annulé, on ne fait rien

            val remote = Path(chooser.selectedFile.toString(), caller.fileName)
            if(!remote.exists())
                remote.createFile()

            caller.caller.downloadFile(origin, remote.toString())
        }
    }
}

class FileUi(val caller: FileExplorer, val fileName: String) : JPanel() {
    init {
        layout = GridLayout(2, 1)
        add(JLabel(ImageIcon(scaledFileIcon)))
        add(JLabel(fileName))
        addMouseListener(CustomMouseFile(this))
    }

    fun hover(isHovered: Boolean){
        // Change the background color when the mouse is over the folder
        background = if(isHovered) Color.LIGHT_GRAY else null
    }
}