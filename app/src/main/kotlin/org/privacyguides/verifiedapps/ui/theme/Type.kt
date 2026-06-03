@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.privacyguides.verifiedapps.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AppFontFamily = FontFamily.SansSerif

private fun emphasized(base: TextStyle, weight: FontWeight = FontWeight.Bold): TextStyle =
    base.copy(fontWeight = weight)

private val DisplayLarge = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 57.sp,
    lineHeight = 64.sp,
    letterSpacing = (-0.25).sp,
)
private val DisplayMedium = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 45.sp,
    lineHeight = 52.sp,
    letterSpacing = 0.sp,
)
private val DisplaySmall = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = 0.sp,
)
private val HeadlineLarge = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = 0.sp,
)
private val HeadlineMedium = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = 0.sp,
)
private val HeadlineSmall = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp,
)
private val TitleLarge = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp,
)
private val TitleMedium = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.15.sp,
)
private val TitleSmall = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp,
)
private val BodyLarge = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp,
)
private val BodyMedium = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp,
)
private val BodySmall = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp,
)
private val LabelLarge = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp,
)
private val LabelMedium = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp,
)
private val LabelSmall = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp,
)

val Typography = Typography(
    displayLarge = DisplayLarge,
    displayLargeEmphasized = emphasized(DisplayLarge, FontWeight.Medium),
    displayMedium = DisplayMedium,
    displayMediumEmphasized = emphasized(DisplayMedium, FontWeight.Medium),
    displaySmall = DisplaySmall,
    displaySmallEmphasized = emphasized(DisplaySmall, FontWeight.Medium),
    headlineLarge = HeadlineLarge,
    headlineLargeEmphasized = emphasized(HeadlineLarge),
    headlineMedium = HeadlineMedium,
    headlineMediumEmphasized = emphasized(HeadlineMedium),
    headlineSmall = HeadlineSmall,
    headlineSmallEmphasized = emphasized(HeadlineSmall),
    titleLarge = TitleLarge,
    titleLargeEmphasized = emphasized(TitleLarge),
    titleMedium = TitleMedium,
    titleMediumEmphasized = emphasized(TitleMedium, FontWeight.SemiBold),
    titleSmall = TitleSmall,
    titleSmallEmphasized = emphasized(TitleSmall, FontWeight.SemiBold),
    bodyLarge = BodyLarge,
    bodyLargeEmphasized = emphasized(BodyLarge, FontWeight.Medium),
    bodyMedium = BodyMedium,
    bodyMediumEmphasized = emphasized(BodyMedium, FontWeight.Medium),
    bodySmall = BodySmall,
    bodySmallEmphasized = emphasized(BodySmall, FontWeight.Medium),
    labelLarge = LabelLarge,
    labelLargeEmphasized = emphasized(LabelLarge),
    labelMedium = LabelMedium,
    labelMediumEmphasized = emphasized(LabelMedium),
    labelSmall = LabelSmall,
    labelSmallEmphasized = emphasized(LabelSmall),
)
