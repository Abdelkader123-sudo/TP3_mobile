package tn.isetbizerte.tp3_channels

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class Vibreur(context: Context) : MethodChannel.MethodCallHandler {

    // Android 12+ : VibratorManager ; avant : Vibrator directement
    private val vibrator: Vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }

    // Motif = [pause, vibration, pause, vibration, ...] en millisecondes
    private val motifs = mapOf(
        "court" to longArrayOf(0, 100),
        "long" to longArrayOf(0, 600),
        "sos" to longArrayOf(0, 100, 100, 100, 100, 100, 300, 400, 100, 400, 100, 400,
            300, 100, 100, 100, 100, 100)
    )

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "vibrer" -> {
                val nom = call.argument<String>("motif")      // paramètre envoyé par Dart
                val motif = motifs[nom]
                if (motif == null) {
                    result.error("MOTIF_INCONNU", "Motif « $nom » inconnu. Choix : ${motifs.keys}", null)
                    return
                }
                if (!vibrator.hasVibrator()) {
                    result.error("PAS_DE_VIBREUR", "Cet appareil ne peut pas vibrer", null)
                    return
                }
                vibrator.vibrate(VibrationEffect.createWaveform(motif, -1))   // -1 = ne pas répéter
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }
}