package m1.exail.view

import m1.exail.controller.Controller
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridLayout
import java.lang.Exception
import java.net.InetAddress
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.SwingWorker
import javax.swing.border.LineBorder
import kotlin.math.floor

private class ComboItem(val address: InetAddress){
    override fun toString(): String {
        return address.hostAddress
    }
}

private class DroneSelector(private val parent: TopBar, adresses: List<ComboItem>): JComboBox<ComboItem>() {
    init {
        for (adr in adresses){
            addItem(adr)
        }
        addActionListener { parent.selectedDrone = (selectedItem as ComboItem?)?.address }
        selectedItem = adresses.firstOrNull()
    }
}

private class Search(private val caller: TopBar) : SwingWorker<Void, Void>() {
    override fun doInBackground(): Void? {
        caller.searchButton.text = "Recherche en cours..."
        caller.searchButton.isEnabled = false
        caller.caller.pcs.firePropertyChange(Controller.DRONE_SEARCH, null, null)
        return null
    }

    override fun done() {
        caller.searchButton.text = "Chercher un drone sur le réseau"
        caller.searchButton.isEnabled = true
    }
}


class TopBar(val caller: MainFrame ) : JPanel() {
    private val addressLabel: JLabel = JLabel("", SwingConstants.LEFT)
    private val informationLabel: JLabel = JLabel("", SwingConstants.CENTER)

    val searchButton: JButton = JButton("Chercher un drone sur le réseau")
    private val connectionButton: JButton = JButton("Se connecter au drone")
    private val deconnectionButton: JButton = JButton("Se déconnecter du drone")

    private var droneSelector: DroneSelector = DroneSelector(this, emptyList())
    var selectedDrone: InetAddress? = null

    init {
        layout = GridLayout(1,3, 20, 20)
        border = LineBorder(java.awt.Color.BLACK)

        searchButton.addActionListener {
            Search(this).execute()
        }

        connectionButton.addActionListener {
            caller.pcs.firePropertyChange(Controller.FTP_CONNECTION, null, selectedDrone)
        }

        deconnectionButton.addActionListener {
            caller.pcs.firePropertyChange(Controller.FTP_CONNECTION, null, null)
        }

        add(addressLabel)
        add(informationLabel)
        add(searchButton)
    }

    fun setDroneStatus(exception: Exception){
        removeAll()

        add(JLabel(("")))  // case vide
        add(informationLabel)
        add(searchButton, BorderLayout.EAST)

        informationLabel.text = "Erreur : ${exception.message}"
        informationLabel.foreground = java.awt.Color.RED
        informationLabel.font = Font(informationLabel.font.fontName, Font.BOLD, informationLabel.font.size)

        revalidate()
    }

    fun setDroneStatus(adresses: List<InetAddress>){
        if(adresses.isEmpty())
            return setDroneStatus(Exception("Aucun drone trouvé sur le réseau"))

        removeAll()

        add(addressLabel)
        droneSelector = DroneSelector(this, adresses.map { ComboItem(it) })
        add(droneSelector)
        add(connectionButton)

        addressLabel.text = "  ${adresses.size} drones trouvés sur le réseau"

        revalidate()
    }

    fun setDroneStatus(address: InetAddress){
        removeAll()

        add(addressLabel)
        add(informationLabel)
        add(deconnectionButton)

        addressLabel.text = "  Adresse du drone : ${address.hostAddress}"
        informationLabel.text = "Connecté au drone"
        informationLabel.foreground = java.awt.Color.GREEN
        informationLabel.font = Font(informationLabel.font.fontName, Font.BOLD, informationLabel.font.size)

        revalidate()
    }

    private fun scale(value: Double): String {
        return when {
            value < 1_000 -> "${floor(value)} B/s"
            value < 1_000_000 -> "${floor(value / 1_000)} KB/s"
            value < 1_000_000_000 -> "${floor(value / 1_000_000)} MB/s"
            else -> "${floor(value / 1_000_000_000)} GB/s"
        }
    }

    fun setDroneStatus(upload: Double, download: Double){
        informationLabel.text = "Débit de connexion : ${scale(upload)} (upload) / ${scale(download)} (download)"
        informationLabel.foreground = java.awt.Color.BLACK
    }
}