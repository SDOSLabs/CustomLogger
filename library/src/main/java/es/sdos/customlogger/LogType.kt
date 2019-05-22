package es.sdos.customlogger

sealed class LogType {
    abstract val name : String

    object INFO : LogType() {
        override var name: String = "Info"
    }

    object EXCEPTION : LogType() {
        override val name: String = "Exception"
    }

    object TIMING : LogType() {
        override val name: String = "Timing"
    }

    abstract class FeatureLog : LogType()
}