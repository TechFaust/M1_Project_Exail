package m1.exail.model

import org.json.JSONObject
import java.io.File
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.util.zip.ZipFile


private fun WINDOWS_getSpeed(ip: InetAddress, port: Int): Pair<Double, Double> {
    val processName = "app/src/main/resources/iperf3/iperf3.exe"
    if(!File(processName).exists()) {
        println("Downloading iperf3 for Windows")
        val urlGitHub = "https://github.com/ar51an/iperf3-win-builds/releases/download/3.16/iperf-3.16-win64.zip"
        // télécharger et extraire le zip
        val stream = URL.of(URI(urlGitHub), null).openStream()
        val zip = File.createTempFile("iperf3", ".zip")
        zip.writeBytes(stream.readAllBytes())
        stream.close()
        val zipFile = ZipFile(zip)
        for(entry in zipFile.entries()){
            val file = File("app/src/main/resources/iperf3/${entry.name}")
            file.writeBytes(zipFile.getInputStream(entry).readAllBytes())
        }
        zip.delete()
    }

    return iperf3ProcessHandler(ProcessBuilder(processName, "-c", ip.hostAddress, "-p", port.toString(), "-J"))
}

private fun MACOS_getSpeed(ip: InetAddress, port: Int): Pair<Double, Double> {
    val iperfFinder = ProcessBuilder("which", "iperf3").start()
    if(iperfFinder.exitValue() != 0){
        val proc = ProcessBuilder("brew", "install", "iperf3").start()
        proc.waitFor()
    }
    return iperf3ProcessHandler(ProcessBuilder("iperf3", "-c", ip.hostAddress, "-p", port.toString(), "-J"))
}

private fun LINUX_getSpeed(ip: InetAddress, port: Int): Pair<Double, Double> {
    val iperfFinder = ProcessBuilder("which", "iperf3").start()
    if(iperfFinder.exitValue() != 0){
        var proc = ProcessBuilder("sudo", "apt", "update").start()
        proc.waitFor()
        proc = ProcessBuilder("sudo", "apt", "install", "iperf3").start()
        proc.waitFor()

    }
    return iperf3ProcessHandler(ProcessBuilder("iperf3", "-c", ip.hostAddress, "-p", port.toString(), "-J"))
}

private fun iperf3ProcessHandler(process: ProcessBuilder): Pair<Double, Double> {
    val iperf = process.start()
    val output = iperf.inputStream.bufferedReader().readText()
    println("output: $output")

    val end = JSONObject(output).getJSONObject("end")
    val downloadSpeed = end.getJSONObject("sum_received").getDouble("bits_per_second")
    val uploadSpeed = end.getJSONObject("sum_sent").getDouble("bits_per_second")

    return Pair(downloadSpeed, uploadSpeed)
}

fun getSpeed(ip: InetAddress, port: Int): Pair<Double, Double> {
    val os = System.getProperty("os.name")
    println("OS: $os")

    if(os.contains("Windows"))
        return WINDOWS_getSpeed(ip, port)

    if(os.contains("Linux"))
        return LINUX_getSpeed(ip, port)

    if(os.contains("Mac"))
        return MACOS_getSpeed(ip, port)

    throw RuntimeException("Unsupported OS: $os")
}