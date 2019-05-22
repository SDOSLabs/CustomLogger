package es.sdos.samplescustomlogger;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import es.sdos.customlogger.CustomLog;
import es.sdos.customlogger.LogExtensionsKt;
import es.sdos.customlogger.LogType;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JavaActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);
        setUpCustomLog();
        writeInfoIntoLog();
        registerTiming();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CustomLog.getInstance().writeData(LogType.INFO.INSTANCE, "permission was granted");

            } else {
                CustomLog.getInstance().writeData(LogType.INFO.INSTANCE, "permission denied");
            }

        }
    }

    private void writeInfoIntoLog() {
        List<String> elements = new ArrayList<>();
        elements.add("Hola");
        elements.add("Adiós");
        Integer nullValue = null;
        CustomLog.getInstance().writeData(ManoloType.INSTANCE, "Prueba de tipo Manolo: Test info");
        CustomLog.getInstance().writeData(PepeType.INSTANCE, "Prueba de tipo Pepe: Test info");
        CustomLog.getInstance().writeData(CustomType.INSTANCE, "Prueba de tipo Custom: Test info");
        CustomLog.getInstance().writeData(" - ", CustomType.INSTANCE, 1, "2", 3L);
        CustomLog.getInstance().writeData(LogType.EXCEPTION.INSTANCE, "Test error");
        CustomLog.getInstance().writeData(LogType.EXCEPTION.INSTANCE, nullValue);
        CustomLog.getInstance().writeData(LogType.EXCEPTION.INSTANCE, elements);
        CustomLog.getInstance().writeData(new JavaCustomType(), "Mensaje de prueba de Java type");
    }

    private void setUpCustomLog() {
        //- Ruta: prueba/sample/DDMM_java.tra
        new CustomLog.Builder()
                .withMainContainerFolderName("prueba")
                .withLogFilesFolder("sample")
                .withFileName("java")
                .withDaysToCleanLog(8)
                .withTimeToWriteIntoLog(1000)
                .build(this, new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        LogExtensionsKt.requestExternalStoragePermission(JavaActivity.this, 100);
                        return null;
                    }
                });
    }

    private void registerTiming() {
        CustomLog.getInstance().startTiming("Hola", "Empieza la fiesta");
        new CountDownTimer(3000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                CustomLog.getInstance().addSplitToTiming("Yeeep: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                CustomLog.getInstance().endTiming();
            }
        }.start();
    }
}
