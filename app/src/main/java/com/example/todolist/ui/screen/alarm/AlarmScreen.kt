package com.example.todolist.ui.screen.alarm

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.TaskCategory
import kotlin.math.roundToInt

@Composable
fun AlarmScreen(
    title: String,
    description: String?,
    category: String?,
    priority: String?,
    onDismiss: () -> Unit
) {
    val backgroundBrush = getPriorityBackground(priority)
    val categoryIcon = getCategoryIcon(category)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(24.dp)
    ) {
        // Center Content: Pulse and Icon
        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            PulseAnimation()
            RingingIconAnimation(icon = categoryIcon)
        }

        // Top Content: Text
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (!description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom Content: Swipe to Dismiss
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            SwipeToDismissButton(onDismiss = onDismiss)
        }
    }
}

@Composable
fun getPriorityBackground(priority: String?): Brush {
    val priorityLevel = try {
        if (priority != null) PriorityLevel.valueOf(priority) else PriorityLevel.LOW
    } catch (e: Exception) {
        PriorityLevel.LOW
    }

    return when (priorityLevel) {
        PriorityLevel.HIGH -> Brush.verticalGradient(
            colors = listOf(Color(0xFFB71C1C), Color(0xFFD32F2F)) // Deep Red
        )
        PriorityLevel.MEDIUM -> Brush.verticalGradient(
            colors = listOf(Color(0xFFF57C00), Color(0xFFFFA000)) // Orange/Amber
        )
        PriorityLevel.LOW -> Brush.verticalGradient(
            colors = listOf(Color(0xFF455A64), Color(0xFF607D8B)) // Blue Grey
        )
    }
}

@Composable
fun getCategoryIcon(category: String?): ImageVector {
    val taskCategory = try {
        if (category != null) TaskCategory.valueOf(category) else TaskCategory.PERSONAL
    } catch (e: Exception) {
        TaskCategory.PERSONAL
    }

    return when (taskCategory) {
        TaskCategory.WORK -> Icons.Default.Work
        TaskCategory.STUDY -> Icons.Default.School
        TaskCategory.PERSONAL -> Icons.Default.Person
    }
}

@Composable
fun PulseAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .size(150.dp)
            .scale(scale)
            .alpha(alpha)
            .background(Color.White.copy(alpha = 0.3f), CircleShape)
    )
}

@Composable
fun RingingIconAnimation(icon: ImageVector) {
    val infiniteTransition = rememberInfiniteTransition(label = "bell")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bellRotation"
    )

    Icon(
        imageVector = icon,
        contentDescription = "Alarm Icon",
        tint = Color.White,
        modifier = Modifier
            .size(100.dp)
            .rotate(rotation)
    )
}

@Composable
fun SwipeToDismissButton(onDismiss: () -> Unit) {
    val width = 300.dp
    val height = 60.dp
    val buttonSize = 56.dp
    val padding = 2.dp
    
    val swipeDistance = with(LocalDensity.current) { (width - buttonSize - padding * 2).toPx() }
    
    var offsetX by remember { mutableFloatStateOf(0f) }
    
    val draggableState = rememberDraggableState { delta ->
        val newValue = offsetX + delta
        offsetX = newValue.coerceIn(0f, swipeDistance)
    }

    LaunchedEffect(offsetX) {
        if (offsetX >= swipeDistance * 0.9f) {
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(percent = 50))
            .padding(padding),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Slide to Dismiss >>>",
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.SemiBold
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .size(buttonSize)
                .background(Color.White, CircleShape)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                         if (offsetX < swipeDistance * 0.9f) {
                             offsetX = 0f
                         }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}
