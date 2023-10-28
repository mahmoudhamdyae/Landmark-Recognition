package com.mahmoudhamdyae.landmarkrecognition.ui

import android.content.Intent
import android.net.Uri
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.mahmoudhamdyae.landmarkrecognition.data.TfLiteLandmarkClassifier
import com.mahmoudhamdyae.landmarkrecognition.domain.Classification


@Composable
fun LandmarkRecognitionScreen() {
    val context = LocalContext.current

    var classifications by remember {
        mutableStateOf(emptyList<Classification>())
    }
    val analyzer = remember {
        LandmarkImageAnalyzer(
            classifier = TfLiteLandmarkClassifier(
                context = context
            ),
            onResults = {
                classifications = it
            }
        )
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CameraPreview(controller, Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            classifications.forEach {
                Text(
                    text = "${it.name} (${((it.score) * 100).toInt()}%)",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                        .clickable {
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=${it.name}"))
                            context.startActivity(browserIntent)
                        }
                )
            }
        }

        // 321 dp * 321 dp Box

        val stroke = Stroke(
            width = 8.dp.value,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24.dp.value, 24.dp.value), 0.dp.value)
        )
        val boxColor = MaterialTheme.colorScheme.primary

        Box(
            modifier = Modifier
                .size(321.dp)
                .align(Alignment.Center)
                .drawBehind {
                    drawRoundRect(
                        color = boxColor,
                        style = stroke,
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                    )
                }
        )
    }
}