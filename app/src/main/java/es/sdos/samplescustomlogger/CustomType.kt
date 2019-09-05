package es.sdos.samplescustomlogger

import es.sdos.customlogger.log.LogType

object CustomType : LogType.FeatureLog() {
    override var name: String = "Customsito"
}

object PepeType : LogType.FeatureLog() {
    override var name: String = "Pepito"
}

object ManoloType : LogType.FeatureLog() {
    override val name: String = "Manolo"
}