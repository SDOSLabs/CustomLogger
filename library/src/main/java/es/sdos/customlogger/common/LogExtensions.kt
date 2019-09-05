package es.sdos.customlogger.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*

fun Writer.wrapperMessageWithDate(message: String?, dateFormat: SimpleDateFormat) : Writer {
    return this.append(STARTER_DATE_CHAR)
        .append(dateFormat.format(Date()))
        .append(END_DATE_CHAR)
        .append(message)
}

fun Writer.appendLineSeparator(needNewLine: Boolean) : Writer {
    if (needNewLine) {
        this.append("\n")
    }
    return this.append(System.getProperty(LINE_SEPARATOR))
}

fun Activity.requestExternalStoragePermission(resultCode: Int) {
    ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        resultCode)
}

fun Fragment.requestExternalStoragePermission(resultCode: Int) {
    this.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        resultCode)
}

fun Context.hasExternalStoragePermission(): Boolean {
    return ContextCompat.checkSelfPermission(this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}


