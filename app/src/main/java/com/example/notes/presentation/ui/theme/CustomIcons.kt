package com.example.notes.presentation.ui.theme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
object CustomIcons {
    val Add_a_photo: ImageVector
        get() {
            if (_Add_a_photo != null) return _Add_a_photo!!

            _Add_a_photo = ImageVector.Builder(
                name = "Add_a_photo",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                    moveTo(120f, 840f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(40f, 760f)
                    verticalLineToRelative(-480f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(120f, 200f)
                    horizontalLineToRelative(126f)
                    lineToRelative(74f, -80f)
                    horizontalLineToRelative(240f)
                    verticalLineToRelative(80f)
                    horizontalLineTo(355f)
                    lineToRelative(-73f, 80f)
                    horizontalLineTo(120f)
                    verticalLineToRelative(480f)
                    horizontalLineToRelative(640f)
                    verticalLineToRelative(-360f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(360f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(760f, 840f)
                    close()
                    moveToRelative(640f, -560f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(-80f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(80f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(80f)
                    horizontalLineToRelative(-80f)
                    verticalLineToRelative(80f)
                    close()
                    moveTo(440f, 700f)
                    quadToRelative(75f, 0f, 127.5f, -52.5f)
                    reflectiveQuadTo(620f, 520f)
                    reflectiveQuadToRelative(-52.5f, -127.5f)
                    reflectiveQuadTo(440f, 340f)
                    reflectiveQuadToRelative(-127.5f, 52.5f)
                    reflectiveQuadTo(260f, 520f)
                    reflectiveQuadToRelative(52.5f, 127.5f)
                    reflectiveQuadTo(440f, 700f)
                    moveToRelative(0f, -80f)
                    quadToRelative(-42f, 0f, -71f, -29f)
                    reflectiveQuadToRelative(-29f, -71f)
                    reflectiveQuadToRelative(29f, -71f)
                    reflectiveQuadToRelative(71f, -29f)
                    reflectiveQuadToRelative(71f, 29f)
                    reflectiveQuadToRelative(29f, 71f)
                    reflectiveQuadToRelative(-29f, 71f)
                    reflectiveQuadToRelative(-71f, 29f)
                }
            }.build()

            return _Add_a_photo!!
        }

    private var _Add_a_photo: ImageVector? = null


}