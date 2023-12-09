package com.nemo.veloon.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text

@Composable
fun HugeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Center,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        fontSize = 28.sp,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        fontWeight = FontWeight.Bold,
    )
}

private class PreviewProvider : @Composable PreviewParameterProvider<String> {
    override val values: Sequence<String>
        get() = sequenceOf("Hello World", "Hello World Hello World Hello World Hello World")
}

@Preview
@Composable
private fun Preview(@PreviewParameter(PreviewProvider::class) text: String) {
    HugeText(text = text)
}