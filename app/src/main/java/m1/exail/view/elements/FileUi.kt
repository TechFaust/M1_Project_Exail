package m1.exail.view.elements


import m1.exail.view.MainFrame
import java.awt.Color
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*

private val fileIcon: BufferedImage = ImageIO.read(File("File-Icon.png"))

class FileUi(private val parent: MainFrame, private val name: String) : JPanel() {
    private class CustomMouse(private val caller: FileUi): MouseAdapter(){
        override fun mouseEntered(e: MouseEvent?) {
            caller.hover(true)
        }

        override fun mouseExited(e: MouseEvent?) {
            caller.hover(false)
        }

        override fun mouseClicked(e: MouseEvent?) {
            if((e?.clickCount ?: 0) == 2 && (e?.button ?: 0) == MouseEvent.BUTTON1){
                val filePath = caller.parent.ftpPath + "/" + caller.name
                val chooser = JFileChooser()
                chooser.dialogTitle = "Choisissez un emplacement pour le télécharger ${caller.name}"
                chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                chooser.showSaveDialog(caller.parent)
                caller.parent.downloadFile(filePath, chooser.selectedFile?.absolutePath + "/" + caller.name)
            }
        }
    }

    init {
        layout = GridLayout(2, 1)
        add(JLabel(ImageIcon(fileIcon)))
        add(JLabel(name))
        addMouseListener(CustomMouse(this))
    }

    fun hover(isHovered: Boolean){
        // Change the background color when the mouse is over the folder
        background = if(isHovered) Color.LIGHT_GRAY else null
    }
}