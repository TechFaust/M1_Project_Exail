package m1.exail.view

import m1.exail.controller.Controller
import java.awt.BorderLayout
import java.net.InetAddress
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.LineBorder

class TopBar(private val parent: MainFrame ) : JPanel() {
    private val addressLabel: JLabel = JLabel("  Adresse du drone : XXX.XXX.XXX.XXX", SwingConstants.LEFT)
    private val informationLabel: JLabel = JLabel("0.0 Mbps / xxx ms", SwingConstants.CENTER)
    private val connectionButton: JButton = JButton("Chercher & Connecter un drone")

    init {
        layout = BorderLayout(20,20)
        border= LineBorder(java.awt.Color.BLACK)

        connectionButton.addActionListener {
            parent.pcs.firePropertyChange(Controller.DRONE_SEARCH, null, null)
        }

        add(addressLabel, BorderLayout.WEST)
        add(informationLabel, BorderLayout.CENTER)
        add(connectionButton, BorderLayout.EAST)
    }

    fun setAddressLabelText(address: InetAddress) {
        addressLabel.text = "  Adresse du drone : ${address.hostAddress}"
    }

    fun setInformationLabelText(speed: Double) {
        informationLabel.text = "Débit de connexion : $speed Mbps"
    }

    fun setDroneStatus(ipAddress: String?, connectionSpeed: Double?, ping: Long?){
        if(ipAddress != null){
            addressLabel.text = "  Adresse du drone : $ipAddress"
        }else{
            addressLabel.text = "  Drone non trouvé"
        }
        if(connectionSpeed != null){
            informationLabel.text = "$connectionSpeed Mbps / $ping ms"
        } else {
            informationLabel.text = ""
        }
    }
}