package com.example.kizilasistan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var speechReady = false
    private var onText: ((String) -> Unit)? = null
    private var onState: ((String, Boolean) -> Unit)? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startListening() else onState?.invoke("Mikrofon izni verilmedi.", false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { onState?.invoke("Seni dinliyorum…", false) }
                    override fun onBeginningOfSpeech() { onState?.invoke("Dinliyorum…", false) }
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() { onState?.invoke("Yanıt hazırlanıyor…", true) }
                    override fun onError(error: Int) { onState?.invoke("Ses algılanamadı. Tekrar deneyebilirsin.", false) }
                    override fun onResults(results: Bundle?) {
                        val phrase = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                        if (!phrase.isNullOrBlank()) onText?.invoke(phrase)
                        else onState?.invoke("Seni anlayamadım; tekrar deneyebilirsin.", false)
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
        setContent {
            var transcript by remember { mutableStateOf("Merhaba. Ben Kızıl Asistan. Konuşmak için mikrofona dokun.") }
            var status by remember { mutableStateOf("Hazır") }
            var speaking by remember { mutableStateOf(false) }
            var listening by remember { mutableStateOf(false) }
            onText = { heard ->
                transcript = "Sen: $heard\n\nKızıl Asistan: Bu prototipte sesini algılıyorum. Yapay zekâ yanıt servisi bir sonraki aşamada bağlanacak."
                status = "Yanıt veriyorum…"
                speaking = true
                speak("Seni duydum. Şu anda prototip modundayım. Yapay zekâ bağlantısı eklendiğinde sorularını yanıtlayacağım.")
            }
            onState = { message, isSpeaking -> status = message; speaking = isSpeaking; listening = message.contains("dinliyorum", true) }
            MaterialTheme(colorScheme = darkColorScheme()) {
                AssistantScreen(
                    transcript = transcript,
                    status = status,
                    speaking = speaking,
                    listening = listening,
                    onMic = {
                        if (listening) stopListening()
                        else if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) startListening()
                        else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    onStop = { stopListening(); tts?.stop(); speaking = false; status = "Hazır" }
                )
            }
        }
    }

    private fun startListening() {
        val sr = recognizer ?: run { onState?.invoke("Bu cihazda konuşma tanıma kullanılamıyor.", false); return }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        try { sr.startListening(intent) } catch (_: Exception) { onState?.invoke("Mikrofon başlatılamadı.", false) }
    }

    private fun stopListening() { recognizer?.cancel(); onState?.invoke("Hazır", false) }

    private fun speak(text: String) {
        if (speechReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kizil-asistan-utterance")
        else { onState?.invoke("Ses motoru hazırlanıyor…", false) }
    }

    override fun onInit(status: Int) {
        speechReady = status == TextToSpeech.SUCCESS
        if (speechReady) tts?.language = Locale("tr", "TR")
    }

    override fun onDestroy() {
        recognizer?.destroy()
        tts?.stop(); tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
private fun AssistantScreen(
    transcript: String,
    status: String,
    speaking: Boolean,
    listening: Boolean,
    onMic: () -> Unit,
    onStop: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "avatar")
    val mouthScale by transition.animateFloat(
        initialValue = 1f, targetValue = if (speaking) 1.10f else 1.015f,
        animationSpec = infiniteRepeatable(tween(if (speaking) 240 else 1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "mouth"
    )
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF21172C), Color(0xFF10121D))))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("KIZIL ASİSTAN", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Spacer(Modifier.height(26.dp))
                Box(
                    modifier = Modifier.size(236.dp).background(
                        Brush.linearGradient(listOf(Color(0xFFB96B54), Color(0xFF6E3C58), Color(0xFF34415F))), CircleShape
                    ), contentAlignment = Alignment.Center
                ) {
                    // Placeholder avatar illustration; replace with the approved portrait asset in the next build.
                    Text("👩🏻‍🦰", fontSize = 112.sp, modifier = Modifier.scale(mouthScale))
                }
                Spacer(Modifier.height(18.dp))
                Text("Kızıl Asistan", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
                Text(status, color = Color(0xFFC8BBD5), fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(color = Color(0xFF292538), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(transcript, color = Color(0xFFF1EDF5), fontSize = 14.sp, lineHeight = 21.sp, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Start)
                }
                Spacer(Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilledIconButton(onClick = onMic, modifier = Modifier.size(72.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFB85F68))) {
                        Icon(if (listening) Icons.Default.Stop else Icons.Default.Mic, contentDescription = "Mikrofon", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    OutlinedButton(onClick = onStop) { Text("Durdur") }
                }
                Spacer(Modifier.height(12.dp))
                Text("Sesli prototip • Türkçe", color = Color(0xFF988DA8), fontSize = 12.sp)
            }
        }
    }
}
