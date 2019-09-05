package es.sdos.samplescustomlogger;

import es.sdos.customlogger.log.LogType;
import org.jetbrains.annotations.NotNull;

public class JavaCustomType extends LogType.FeatureLog {
    @NotNull
    @Override
    public String getName() {
        return "Java";
    }
}
