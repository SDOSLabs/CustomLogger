package es.sdos.customlogger

import java.text.SimpleDateFormat
import java.util.*
val DEFAULT_TIME_TO_WRITE_INTO_LOG = 3000L
val DEFAULT_MAIN_CONTAINER_FOLDER_NAME = "/debug"
val DEFAULT_LOG_FILES_FOLDER = "/log"
val DEFAULT_FILE_NAME = "_trazas.tra"
val DEFAULT_DAYS_TO_CLEAN_LOG = 7
val STARTER_DATE_CHAR = "["
val END_DATE_CHAR = "] "
val LINE_SEPARATOR = "line.separator"
val DATE_FORMAT_TO_TIME_INTO_LOG_FILE = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
val DATE_FORMAT_TO_NAMING_LOG_FILE = SimpleDateFormat("MMdd", Locale.getDefault())