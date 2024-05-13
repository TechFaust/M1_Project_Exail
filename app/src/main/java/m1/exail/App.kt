package m1.exail

import m1.exail.controller.Controller
import m1.exail.view.MainFrame

fun main() {
    val ctrl = Controller()
    val ui = MainFrame()

    ctrl.addUi(ui)
}