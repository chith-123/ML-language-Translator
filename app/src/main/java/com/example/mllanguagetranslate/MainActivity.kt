package com.example.mllanguagetranslate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mllanguagetranslate.ui.theme.MLlanguagetranslateTheme
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MLlanguagetranslateTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TranslateScreen()
                }
            }
        }
    }
}

@Composable
fun TranslateScreen() {
    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedLang by remember { mutableStateOf("hi") } // default Hindi

    val languageMap = mapOf(
        "Hindi" to "hi",
        "Kannada" to "kn",
        "Tamil" to "ta",
        "Spanish" to "es",
        "French" to "fr"
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Input Text Field
        BasicTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray)
                .padding(12.dp),
            decorationBox = { innerTextField ->
                if (inputText.isEmpty()) Text("Enter text to translate...", color = Color.Gray)
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language Dropdown
        Box {
            Button(onClick = { expanded = true }) {
                Text(languageMap.entries.firstOrNull { it.value == selectedLang }?.key ?: "Select Language")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                languageMap.forEach { (langName, langCode) ->
                    DropdownMenuItem(
                        text = { Text(langName) },
                        onClick = {
                            selectedLang = langCode
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Translate Button
        Button(onClick = {
            if (inputText.isBlank()) {
                Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
            } else {
                translateText(inputText, selectedLang) {
                    translatedText = it
                }
            }
        }) {
            Text("Translate")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Translated Text:", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = translatedText)
    }
}

// Translation Logic
fun translateText(input: String, targetLang: String, onResult: (String) -> Unit) {
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(targetLang)
        .build()

    val translator = Translation.getClient(options)

    translator.downloadModelIfNeeded()
        .addOnSuccessListener {
            translator.translate(input)
                .addOnSuccessListener { translated ->
                    onResult(translated)
                }
                .addOnFailureListener {
                    onResult("Translation failed")
                }
        }
        .addOnFailureListener {
            onResult("Model download failed")
        }
}
