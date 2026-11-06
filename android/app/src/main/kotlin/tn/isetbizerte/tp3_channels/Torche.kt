package tn.isetbizerte.tp3_channels

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class Torche(context: Context) : MethodChannel.MethodCallHandler {

    private val cameraManager = context.getSystemService(CameraManager::class.java)

    // Identifiant de la première caméra qui possède un flash (null s'il n'y en a pas)
    private val idFlash: String? by lazy {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "allumer" -> {
                val etat = call.argument<Boolean>("etat")        // paramètre envoyé par Dart
                if (etat == null) {
                    result.error("ARGUMENT_MANQUANT", "Il faut envoyer un booléen « etat »", null)
                    return
                }
                try {
                    val id = idFlash
                    if (id == null) {
                        result.error("PAS_DE_FLASH", "Cet appareil n'a pas de flash", null)
                        return
                    }
                    cameraManager.setTorchMode(id, etat)
                    result.success(null)
                } catch (e: CameraAccessException) {
                    result.error("CAMERA_INDISPONIBLE", e.message, null)
                }
            }
            else -> result.notImplemented()
        }
    }
}