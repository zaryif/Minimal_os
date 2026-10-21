package com.zaryif.minimalos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.R

/**
 * Apple San Francisco typography implementation.
 * Follows Apple HIG typography guidelines:
 * - Negative tracking on display & headlines (tighter kerning at large sizes)
 * - Proportional numerals for time & dates
 * - Positive letter-spacing on small caps / caption labels
 */
val AppleSanFranciscoFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_regular, FontWeight.Medium),
    Font(R.font.inter_regular, FontWeight.SemiBold),
    Font(R.font.inter_regular, FontWeight.Bold)
)

val MinimalTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 72.sp,
        lineHeight = 78.sp,
        letterSpacing = (-2.5).sp,
        color = Color.White
    ),
    headlineLarge = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.6).sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.4).sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.1).sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        color = Color(0xFFA1A1AA)
    ),
    bodySmall = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        color = Color(0xFFA1A1AA)
    ),
    labelLarge = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp,
        color = Color.White
    ),
    labelSmall = TextStyle(
        fontFamily = AppleSanFranciscoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp,
        color = Color(0xFFA1A1AA)
    )
)
