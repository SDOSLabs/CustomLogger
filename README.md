# CustomLog

Librería que permite escribir un log personalizado en un fichero con el objetivo de ser posteriormente estudiado para posible corrección de errores.


## Dependencia

```java
implementation 'es.sdos.android:customlogger:1.0.0'
```

## Inicialización
Haremos uso del builder CustomLog.Builder(), el cuál puede configurar los siguientes parámetros:
 - Contexto. 
 - Nombre de la carpeta donde se almacenará la carpeta que contiene los logs. Esta carpeta estará en el raíz del dispositivo. Método:  *.withMainContainerFolderName("prueba")*, **por defecto**: "debug"
 - Nombre de la carpeta que contendrá los ficheros de logs. Método: *.withLogFilesFolder("sample")*, **por defecto**: "log".
 - Nombre del fichero de trazas, este nombre será: "DDMM_nombreDelFicheroEstablecido.tra". Método:  *.withFileName("test")*, **por defecto**: "trazas".
 - Días necesarios para limpiar el log. Método: *.withDaysToCleanLog(8)*, **por defecto**: 7.
 - Tiempo (**en milisegundos**) para lanzar el proceso de escritura en el log en segundo plano. Método: *.withTimeToWriteIntoLog(1500)*, **por defecto**: 3 segundos.

Posteriormente llamaremos al método *build* que recibe dos parámetros:
- Contexto. 
- Método que se ejecutará si los permisos de almacenamiento no están aceptados.

No es necesario añadir "/" puesto que ya se añade automáticamente.

### Kotlin
```kotlin
//- Ruta: prueba/sample/DDMM_kotlin.tra  
CustomLog.Builder()  
    .withMainContainerFolderName("prueba")  
    .withLogFilesFolder("sample")  
    .withFileName("kotlin")  
    .withDaysToCleanLog(8)  
    .withTimeToWriteIntoLog(1000)  
    .build(this) {  
  requestExternalStoragePermission(REQUEST_STORAGE_PERMISSION)  
}
```
### Java
```java
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
```

## Representar mensajes en el Log
El método necesario para pintar registros en el log es el siguiente:

### Kotlin
```kotlin
var nullValue = null  
CustomLog.instance.writeData(ManoloType, "Prueba de tipo Manolo: Test info")  
CustomLog.instance.writeData(PepeType, "Prueba de tipo Pepe: Test info")  
CustomLog.instance.writeData(CustomType, "Prueba de tipo Custom: Test info")  
CustomLog.instance.writeData(" - ", CustomType, 1, "2", 3L) //- Puede recibir múltiples tipos 
CustomLog.instance.writeData(LogType.EXCEPTION, "Test error")  
CustomLog.instance.writeData(LogType.EXCEPTION, nullValue)  
CustomLog.instance.writeData(LogType.EXCEPTION, listOf("Hola", "Adiós")) 
```

### Java 
```java
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
```

Fichero después de las escrituras anteriores:

    /***** Manolo *****/  
    [22/05/2019 11:42:14] Prueba de tipo Manolo: Test info
    
    /***** Pepito *****/  
    [22/05/2019 11:42:14] Prueba de tipo Pepe: Test info
    
    /***** Customsito *****/  
    [22/05/2019 11:42:14] Prueba de tipo Custom: Test info
    
    /***** Customsito *****/  
    [22/05/2019 11:42:14] 1 - 2 - 3
    
    /***** Exception *****/  
    [22/05/2019 11:42:14] Test error
    
    /***** Exception *****/  
    [22/05/2019 11:42:14] null
    
    /***** Exception *****/  
    [22/05/2019 11:42:14] [Hola, Adiós]

    /***** Java *****/  
    [22/05/2019 11:42:14] Prueba de tipo Java: Test info

## Tipos de mensajes
Se pueden representar valores con tres etiquetas por defecto: *LogType.EXCEPTION*, *LogType.INFO* y *LogType.TIMING*.
Si es necesario añadir más tipos se deberá extender de la clase LogType.FeatureLog y se debe establecer un valor en la variable "name". Ésta será la que se utilice para pintar la cabecera de los logs bajo este tipo personalizado.
Ejemplo de cabecera: "/***** Custom Type *****/"
La forma de crear más tipos es la siguiente:

### Kotlin (Fichero CustomType.kt)
```kotlin
import es.sdos.customlogger.LogType  
  
object CustomType : LogType.FeatureLog() {  
    override var name: String = "Customsito"  
}  
  
object PepeType : LogType.FeatureLog() {  
    override var name: String = "Pepito"  
}  
  
object ManoloType : LogType.FeatureLog() {  
    override val name: String = "Manolo"  
}
```
### Java (Clase JavaCustomType.java)
```java
import es.sdos.customlogger.LogType;  
import org.jetbrains.annotations.NotNull;  
  
public class JavaCustomType extends LogType.FeatureLog {  
    @NotNull  
    @Override  public String getName() {  
        return "Java";  
    }  
}
```
## Creación de contadores de tiempo
Será posible crear contadores de tiempo para ver cuánto tarda una ejecución específica. De este modo podremos saber cuánto tardan nuestros mappers, métodos, bucles, etc.
Para ello se debe:
 1. Inicializar el contador.
```kotlin
CustomLog.instance.startTiming("Hola", "Empieza la fiesta")
```
```java
CustomLog.getInstance().startTiming("Hola", "Empieza la fiesta");
```
 2. Si lo necesitamos, podemos añadir contadores intermedios antes de la finalización del contador.
```kotlin
CustomLog.instance.addSplitToTiming("Yeeep 1")
//- Ejecutamos más código
CustomLog.instance.addSplitToTiming("Yeeep 2")
```
```java
CustomLog.getInstance().addSplitToTiming("Yeeep 1");
//- Ejecutamos más código
CustomLog.getInstance().addSplitToTiming("Yeeep 2");
```
 3. Finalizar el contador.
```kotlin
CustomLog.instance.endTiming()
```
```java
CustomLog.getInstance().endTiming();
```
### Ejemplo con un CountDownTimer
```kotlin
CustomLog.instance.startTiming("Hola", "Empieza la fiesta")  
object : CountDownTimer(3000, 500) {  
  
    override fun onTick(millisUntilFinished: Long) {  
        CustomLog.instance.addSplitToTiming("Yeeep: $millisUntilFinished")  
    }  
  
    override fun onFinish() {  
        CustomLog.instance.endTiming()  
    }  
}.start()
```

```java
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
```

Resultado en el log:
    /***** Timing *****/ 
    [22/05/2019 12:02:32] Start: Hola: Empieza la fiesta: 32 ms, Yeeep: 2968 
    Hola: Empieza la fiesta: 500 ms, Yeeep: 2468 
    Hola: Empieza la fiesta: 523 ms, Yeeep: 1945 
    Hola: Empieza la fiesta: 501 ms, Yeeep: 1444 
    Hola: Empieza la fiesta: 501 ms, Yeeep: 943 
    Hola: Empieza la fiesta: end, 2057 ms