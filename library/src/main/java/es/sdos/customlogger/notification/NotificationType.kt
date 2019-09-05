package es.sdos.customlogger.notification

import android.support.annotation.StringRes
import es.sdos.customlogger.R

/**
 * Sacada de LeakCanary: https://github.com/square/leakcanary
 */
enum class NotificationType(@StringRes val nameResId: Int, val importance: Int) {
    CUSTOMLOG_LOW(
        R.string.customlog__notification_channel_low,
        IMPORTANCE_LOW
    ),
    CUSTOMLOG_RESULT(
        R.string.customlog__notification_channel_result,
        IMPORTANCE_DEFAULT
    );
}

private const val IMPORTANCE_LOW = 2
private const val IMPORTANCE_DEFAULT = 3