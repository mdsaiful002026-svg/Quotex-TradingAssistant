package com.example.quotexassistant

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

object GeminiHelper {

    private const val API_KEY = "YOUR_GEMINI_API_KEY" // Replace with your actual Gemini API Key here
    private const val MODEL_NAME = "gemini-1.5-flash"

    fun analyzeChart(bitmap: Bitmap, onResult: (String) -> Unit) {
        Thread {
            try {
                // Convert Bitmap to Base64
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()
                val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

                val urlString = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$API_KEY"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doOutput = true

                // Construct JSON Request Payload
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().put(
                        JSONObject().put("parts", JSONArray()
                            .put(JSONObject().put("text", "Analyze this Quotex trading chart candlestick pattern and give a quick signal (CALL or PUT) with brief reason."))
                            .put(JSONObject().put("inline_data", JSONObject()
                                .put("mime_type", "image/jpeg")
                                .put("data", base64Image)
                            ))
                        )
                    ))
                }

                connection.outputStream.use { os ->
                    val input = jsonBody.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(responseString)
                    val candidates = jsonResponse.getJSONArray("candidates")
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    val textResult = parts.getJSONObject(0).getString("text")

                    onResult(textResult)
                } else {
                    onResult("API Error: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult("Analysis Failed: ${e.message}")
            }
        }.start()
    }
}
