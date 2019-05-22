package es.sdos.customlogger

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.LinkedHashMap

class CustomLog {

    class Builder {
        private var timeToWriteIntoLog = DEFAULT_TIME_TO_WRITE_INTO_LOG
            set(value) {
                field = if (value <= 0) DEFAULT_TIME_TO_WRITE_INTO_LOG else value
            }

        private var mainContainerFolderName: String = DEFAULT_MAIN_CONTAINER_FOLDER_NAME
            set(value) {
                field = formatFolder(value, DEFAULT_MAIN_CONTAINER_FOLDER_NAME)
            }

        private var logFilesFolder = DEFAULT_LOG_FILES_FOLDER
            set(value) {
                field = formatFolder(value, DEFAULT_MAIN_CONTAINER_FOLDER_NAME)
            }

        private var fileName = DEFAULT_FILE_NAME
            set(value) {
                field = formatFileName(value, DEFAULT_FILE_NAME)
            }

        private var daysToCleanLog = DEFAULT_DAYS_TO_CLEAN_LOG
            set(value) {
                field = if (value <= 0) DEFAULT_DAYS_TO_CLEAN_LOG else value
            }

        fun withMainContainerFolderName(mainContainerFolderName: String): Builder {
            this.mainContainerFolderName = mainContainerFolderName
            return this
        }

        fun withLogFilesFolder(logFilesFolder: String): Builder {
            this.logFilesFolder = logFilesFolder
            return this
        }

        fun withFileName(fileName: String): Builder {
            this.fileName = fileName
            return this
        }

        fun withDaysToCleanLog(daysToCleanLog: Int): Builder {
            this.daysToCleanLog = daysToCleanLog
            return this
        }

        fun withTimeToWriteIntoLog(timeToWriteIntoLog: Long): Builder {
            this.timeToWriteIntoLog = timeToWriteIntoLog
            return this
        }

        fun build(context: Context, executeIfExternalStoragePermissionNotGranted: () -> Unit) : CustomLog {
            return CustomLog.init(context,
                this.mainContainerFolderName,
                this.logFilesFolder,
                this.fileName,
                this.daysToCleanLog,
                this.timeToWriteIntoLog,
                executeIfExternalStoragePermissionNotGranted)
        }
    }

    companion object {
        //region Members
        @JvmStatic
        @Volatile
        var instance: CustomLog = CustomLog()
            private set

        private var timingLogger: CustomTimingLogger? = null

        private var customExceptionHandler: Thread.UncaughtExceptionHandler? = null

        private var timeToWriteIntoLog = DEFAULT_TIME_TO_WRITE_INTO_LOG
        private var mainContainerFolderName: String? = DEFAULT_MAIN_CONTAINER_FOLDER_NAME
        private var logFilesFolder = DEFAULT_LOG_FILES_FOLDER
        private var fileName = DEFAULT_FILE_NAME
        private var daysToCleanLog = DEFAULT_DAYS_TO_CLEAN_LOG

        private var handler = Handler()

        private var typeWithInfoMap: LinkedHashMap<LogType, MutableList<String?>> = LinkedHashMap()
        //endregion

        //region Private Methods
        private fun init(context: Context?,
                         mainContainerFolderName: String = DEFAULT_MAIN_CONTAINER_FOLDER_NAME,
                         logFilesFolder: String = DEFAULT_LOG_FILES_FOLDER,
                         fileName: String = DEFAULT_FILE_NAME,
                         daysToCleanLog: Int = DEFAULT_DAYS_TO_CLEAN_LOG,
                         timeToWriteIntoLog: Long = DEFAULT_TIME_TO_WRITE_INTO_LOG,
                         executeIfExternalStoragePermissionNotGranted: () -> Unit) : CustomLog {
            customExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
            timingLogger = CustomTimingLogger(null, null)
            this.daysToCleanLog = daysToCleanLog
            setUpLocationParams(mainContainerFolderName, logFilesFolder, fileName, timeToWriteIntoLog)
            if (context != null &&
                !context.hasExternalStoragePermission()) {
                Log.e(CustomLog::class.java.simpleName, "Storage permission not granted")
                executeIfExternalStoragePermissionNotGranted()

            } else {
                needResetLog()
            }
            return CustomLog()
        }

        private fun needResetLog() {
            val pathToFile: String = Environment.getExternalStorageDirectory().path +
                    mainContainerFolderName + logFilesFolder
            val file = File(pathToFile)

            if (file.list() == null) {
                return
            }

            val filesNameList = ArrayList(Arrays.asList<String>(*file.list()))
            filesNameList.sort()

            val filteredFiles = ArrayList<String>()
            for (fileName in filesNameList) {
                if (fileName.contains(fileName)) {
                    filteredFiles.add(fileName)
                }
            }

            deleteLogFileIfIsNeeded(daysToCleanLog, filteredFiles)
        }

        private fun deleteLogFileIfIsNeeded(daysToCleanLog: Int, filteredFiles: ArrayList<String>) {
            if (!filteredFiles.isEmpty()) {
                for (i in filteredFiles.indices) {
                    val candidateFileToDelete = filteredFiles[i]
                    try {
                        //- Obtenemos el dia y mes del fichero
                        val day = Integer.parseInt(candidateFileToDelete.substring(2, 4))
                        val month = Integer.parseInt(candidateFileToDelete.substring(0, 2))

                        //- Configuramos la fecha del fichero
                        val oldFileCalendar = Calendar.getInstance()
                        //- Se le resta 1 al mes ya que el Calendar va de 0 a 11.
                        oldFileCalendar.set(Calendar.MONTH, month - 1)
                        oldFileCalendar.set(Calendar.DAY_OF_MONTH, day)

                        //- Cogemos la fecha actual
                        val currentDate = Calendar.getInstance()

                        //- Restamos los timeStamps para obtener los días
                        val different = currentDate.timeInMillis - oldFileCalendar.timeInMillis
                        val secondsInMilli: Long = 1000
                        val minutesInMilli = secondsInMilli * 60
                        val hoursInMilli = minutesInMilli * 60
                        val daysInMilli = hoursInMilli * 24

                        val numOfDays = Math.abs(different / daysInMilli)

                        //- Borramos los ficheros que no estén comprendidos en el número máximo de días a almacenar
                        if (daysToCleanLog in 1..(numOfDays - 1)) {
                            deleteNotNeededFiles(candidateFileToDelete)
                        }

                    } catch (e: Exception) {
                        Log.e(CustomLog::class.java.simpleName, e.message)
                    }

                }
            }
        }

        private fun deleteNotNeededFiles(nameOfFile: String) {
            val pathToFile: String = Environment.getExternalStorageDirectory().path +
                    mainContainerFolderName + logFilesFolder + File.separator + nameOfFile
            val file = File(pathToFile)
            if (file.exists()) {
                file.delete()
            }
        }

        private fun setUpLocationParams(mainContainerFolderName: String = DEFAULT_MAIN_CONTAINER_FOLDER_NAME,
                                        logFilesFolder: String = DEFAULT_LOG_FILES_FOLDER,
                                        fileName: String = DEFAULT_FILE_NAME,
                                        timeToWriteIntoLog: Long = DEFAULT_TIME_TO_WRITE_INTO_LOG) {
            this.mainContainerFolderName = mainContainerFolderName
            this.logFilesFolder = logFilesFolder
            this.fileName = fileName
            this.timeToWriteIntoLog = timeToWriteIntoLog
        }

        private fun formatFolder(name: String?, default: String): String {
            return if (TextUtils.isEmpty(name)) default else "/$name"
        }

        private fun formatFileName(name: String?, default: String): String {
            return if (TextUtils.isEmpty(name)) default else "_$name.tra"
        }
        //endregion
    }

    //region Public Methods
    fun startTiming(tag: String, label: String) {
        timingLogger?.reset(tag, label)
    }

    fun addSplitToTiming(label: String) {
        timingLogger?.addSplit(label)
    }

    fun endTiming() {
        writeData(LogType.TIMING, timingLogger?.dumpToLog())
    }

    fun writeData(type: LogType, objectToBeLogged: Any?) {
        writeData(type, objectToBeLogged.toString())
    }

    fun writeData(separator: String = " - ", type: LogType, vararg values: Any) {
        val strings = values.apply { toString() }
        writeData(type, strings.joinToString(separator))
    }

    fun <T : LogType> writeData(type: T, message: String?) {
        handler.removeCallbacksAndMessages(null)
        val messageListByType: MutableList<String?> = if (typeWithInfoMap[type] != null) typeWithInfoMap[type]!! else mutableListOf()
        messageListByType.add(message)
        typeWithInfoMap[type] = messageListByType
        handler.postDelayed({
            Thread.setDefaultUncaughtExceptionHandler { t, e ->
                writeInfoIntoLogFile(type, e.message)
                writePendingData()
                customExceptionHandler?.uncaughtException(t, e)
            }
            writePendingData()
            Thread.setDefaultUncaughtExceptionHandler(customExceptionHandler)
        }, timeToWriteIntoLog)
    }
    //endregion

    //region Private Methods
    private fun writePendingData() {
        typeWithInfoMap.forEach {
            writeInfoIntoLogFile(it.key, it.value)
        }
        typeWithInfoMap.clear()
    }

    private fun <T : LogType> writeInfoIntoLogFile(type: T, messageList: List<String?>): Boolean {
        var res = false
        try {
            messageList.forEach {
                writeInfoIntoLogFile(type, it)
                Log.d(CustomLog::class.java.simpleName, getNullStringFromNull(it))
            }
            res = true

        } catch (e: Exception) {
            Log.e(CustomLog::class.java.simpleName, e.message)
        }
        return res
    }

    private fun <T : LogType> writeInfoIntoLogFile(type: T, message: String?): Boolean {
        var res = false
        try {
            val now = Date()
            val appDirectoryPath = Environment.getExternalStorageDirectory().toString() + mainContainerFolderName
            val appDirectory = File(appDirectoryPath)
            val logDirectory = File(appDirectoryPath + logFilesFolder)
            val logFile = File(logDirectory, DATE_FORMAT_TO_NAMING_LOG_FILE.format(now) + fileName)
            createDirectoryIfNotExist(appDirectory)
            createDirectoryIfNotExist(logDirectory)
            val fileWriter = FileWriter(logFile, true)
            val headerLog = getHeaderLogByType(type)
            if (!headerLog.isEmpty()) {
                fileWriter.append(headerLog)
                    .appendLineSeparator(false)
            }
            fileWriter.wrapperMessageWithDate(message, DATE_FORMAT_TO_TIME_INTO_LOG_FILE)
                .appendLineSeparator(true)
            fileWriter.close()
            res = true
            Log.d(CustomLog::class.java.simpleName, getNullStringFromNull(message))

        } catch (e: Exception) {
            Log.e(CustomLog::class.java.simpleName, e.message)
        }
        return res
    }

    private fun <T : LogType> getHeaderLogByType(type: T) : String {
        return "/***** ${type.name} *****/"
    }

    private fun createDirectoryIfNotExist(directory: File) {
        if (!directory.exists()) {
            directory.mkdir()
        }
    }
    private fun getNullStringFromNull(value: Any?) : String {
        return value?.toString() ?: "null"
    }
    //endregion
}