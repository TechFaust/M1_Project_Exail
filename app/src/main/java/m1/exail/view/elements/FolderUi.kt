package m1.exail.view.elements

import m1.exail.view.MainFrame
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

val folderIcon: BufferedImage = ImageIO.read(File("Folder-Icon.png"))

class FolderUi(private val parent: MainFrame, private val name: String) : JPanel() {
    private class CustomMouse(private val caller: FolderUi): MouseAdapter(){
        override fun mouseEntered(e: MouseEvent?) {
            caller.hover(true)
        }

        override fun mouseExited(e: MouseEvent?) {
            caller.hover(false)
        }

        override fun mouseClicked(e: MouseEvent?) {
            if((e?.clickCount ?: 0) == 2 && (e?.button ?: 0) == MouseEvent.BUTTON1){
                when (caller.name) {
                    ".." -> caller.parent.ftpPath = caller.parent.ftpPath?.substringBeforeLast("/") ?: "/"
                    "." -> return
                    else -> caller.parent.ftpPath += "/" + caller.name
                }
            }
        }
    }

    init {
        layout = GridLayout(2, 1)
        add(JLabel(ImageIcon(folderIcon)))
        add(JLabel(name))
        addMouseListener(CustomMouse(this))
    }

    fun hover(isHovered: Boolean){
        // Change the background color when the mouse is over the folder
        background = if(isHovered) Color.LIGHT_GRAY else null
    }
}