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
import javax.swing.JLabel
import javax.swing.JPanel

private val folderIcon: BufferedImage = ImageIO.read(File("app/src/main/resources/Folder-Icon.png"))
private val scaledFolderIcon = folderIcon.getScaledInstance(50, 50, BufferedImage.SCALE_SMOOTH)

private class CustomMouseFolder(private val caller: FolderUi): MouseAdapter(){
    override fun mouseEntered(e: MouseEvent?) { caller.hover(true) }
    override fun mouseExited(e: MouseEvent?) { caller.hover(false) }
    override fun mouseClicked(e: MouseEvent?) {
        if((e?.clickCount ?: 0) == 2 && (e?.button ?: 0) == MouseEvent.BUTTON1){
            when (caller.fileName) {
                ".." -> {
                    var path = caller.parent.path
                    if(path == "/") return  // Si on est déjà à la racine, on ne fait rien
                    path = path.substring(0, path.length - 1)  // Enlève le dernier "/"
                    path = path.substringBeforeLast("/")  // Enlève le dernier dossier
                    caller.parent.path = "$path/"  // Ajoute le nouveau chemin
                }
                "." -> caller.parent.path = caller.parent.path  // force une actualisation
                else -> caller.parent.path += "${caller.fileName}/"
            }
        }
    }
}

class FolderUi(val parent: FileExplorer, val fileName: String) : JPanel() {

    init {
        layout = GridLayout(2, 1)
        add(JLabel(ImageIcon(scaledFolderIcon)))
        add(JLabel(fileName))
        addMouseListener(CustomMouseFolder(this))
    }

    fun hover(isHovered: Boolean){
        // Change the background color when the mouse is over the folder
        background = if(isHovered) Color.LIGHT_GRAY else null
    }
}