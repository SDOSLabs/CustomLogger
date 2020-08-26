package es.sdos.samplescustomlogger

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import es.sdos.customlogger.log.CustomLog
import es.sdos.customlogger.log.LogType

class MainActivity : AppCompatActivity() {

    val REQUEST_STORAGE_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpCustomLog()
        writeInfoIntoLog()
        registerTiming()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CustomLog.instance.writeData(LogType.INFO, "permission was granted")

                } else {
                    CustomLog.instance.writeData(LogType.INFO, "permission denied")
                }
                return
            }
        }
    }

    private fun setUpCustomLog() {
        //- Ruta: prueba/sample/DDMM_kotlin.tra
        CustomLog.Builder()
            .withMainContainerFolderName("prueba")
            .withLogFilesFolder("sample")
            .withFileName("kotlin")
            .withDaysToCleanLog(8)
            .withTimeToWriteIntoLog(1000)
            .build(CustomApplication.instance)
    }

    private fun writeInfoIntoLog() {
        val nullValue = null
        CustomLog.instance.writeData(ManoloType, "Prueba de tipo Manolo: Test info")
        CustomLog.instance.writeData(PepeType, "Prueba de tipo Pepe: Test info")
        CustomLog.instance.writeData(CustomType, "Prueba de tipo Custom: Test info")
        CustomLog.instance.writeData(" - ", CustomType, 1, "2", 3L)
        CustomLog.instance.writeData(LogType.EXCEPTION, "Test error")
        CustomLog.instance.writeData(LogType.EXCEPTION, nullValue)
        CustomLog.instance.writeData(LogType.EXCEPTION, listOf("Hola", "Adiós"))
        CustomLog.instance.writeData(JavaCustomType(), "Prueba de tipo Java: Test info")
        CustomLog.instance.startTiming("Prueba timing", "Uno")
        CustomLog.instance.endTiming()
    }

    private fun registerTiming() {
        CustomLog.instance.startTiming("Hola", "Empieza la fiesta")
        object : CountDownTimer(3000, 500) {

            override fun onTick(millisUntilFinished: Long) {
                CustomLog.instance.addSplitToTiming("Yeeep: $millisUntilFinished")
            }

            override fun onFinish() {
                CustomLog.instance.endTiming()
            }
        }.start()
    }
}
