package com.interimi.interimi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.interimi.interimi.R

// Importar la fuente "Cinzel" (descárgala y agrégala a res/font/)
val RomanFont = FontFamily(
    Font(R.font.cinzel_regular) // Asegúrate de tener este archivo en res/font
)

private val RomanLightColorScheme = lightColorScheme(
    primary = Color(0xFF8B5E3B), // Bronce romano
    onPrimary = Color.White,
    secondary = Color(0xFF6D4C41), // Tono bronce oscuro
    onSecondary = Color.White,
    background = Color(0xFFFFF8E1), // Mármol crema
    onBackground = Color.Black,
    surface = Color(0xFFF5DEB3), // Mármol cálido
    onSurface = Color.Black
)

private val RomanDarkColorScheme = darkColorScheme(
    primary = Color(0xFFC4A484), // Oro viejo
    onPrimary = Color.Black,
    secondary = Color(0xFF8B5E3B), // Bronce viejo
    onSecondary = Color.White,
    background = Color(0xFF1C1C1C), // Mármol oscuro
    onBackground = Color.White,
    surface = Color(0xFF2C2C2C),
    onSurface = Color.White
)


// 🏛️ Tipografía con estética romana
val RomanTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = RomanFont,
        fontSize = 18.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RomanFont,
        fontSize = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RomanFont,
        fontSize = 16.sp
    )
)

@Composable
fun InterimiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) RomanDarkColorScheme else RomanLightColorScheme
        }
        darkTheme -> RomanDarkColorScheme
        else -> RomanLightColorScheme
    }

    // 🔹 Sobreescribimos el estilo global de los botones
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF8B5E3B), // Bronce romano
        contentColor = Color.White // Texto en blanco
    )

    val buttonShape = MaterialTheme.shapes.small

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RomanTypography,
        shapes = Shapes(
            small = buttonShape,
            medium = buttonShape,
            large = buttonShape
        ),
        content = content
    )
}
