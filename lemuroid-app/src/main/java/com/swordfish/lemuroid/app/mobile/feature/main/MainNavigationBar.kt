package com.swordfish.lemuroid.app.mobile.feature.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

data class ButtonData(
    val text: String,
    val icon: ImageVector,
)

@Composable
fun MainNavigationBar(
    currentRoute: MainRoute?,
    navController: NavHostController,
) {
    AnimatedVisibility(
        visible = currentRoute?.showBottomNavigation != false,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        LemuroidNavigationBar(
            currentRoute = currentRoute,
            navController = navController,
        )
    }
}

@Composable
private fun LemuroidNavigationBar(
    currentRoute: MainRoute?,
    navController: NavHostController,
) {

    val routes = MainNavigationRoutes.entries

    val selectedIndex = routes.indexOfFirst {
        currentRoute?.root == it.route
    }.coerceAtLeast(0)

    val barColor = Color(0xFFF4F0E5)
    val activeColor = Color(0xFFF6D96B)
    val textColor = Color(0xFF505050)

    val circleRadius = 34.dp

    var barSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    //
    // ITEM POSITION
    //
    val offsetStep = remember(barSize) {
        if (routes.isEmpty()) {
            0f
        } else {
            barSize.width.toFloat() / (routes.size * 2)
        }
    }

    val targetOffset =
        offsetStep + selectedIndex * 2 * offsetStep

    val circleRadiusPx =
        LocalDensity.current.run {
            circleRadius.toPx().toInt()
        }

    //
    // ANIMATION
    //
    val transition = updateTransition(
        targetState = targetOffset,
        label = "navbar_transition",
    )

    val animation = spring<Float>(
        dampingRatio = 0.82f,
        stiffness = Spring.StiffnessMediumLow,
    )

    //
    // CUTOUT POSITION
    //
    val cutoutOffset by transition.animateFloat(
        transitionSpec = {
            if (initialState == 0f) {
                snap()
            } else {
                animation
            }
        },
        label = "cutout_offset",
    ) {
        it
    }

    //
    // FLOATING BUTTON POSITION
    //
    val circleOffset by transition.animateIntOffset(
        transitionSpec = {
            if (initialState == 0f) {
                snap()
            } else {
                spring(
                    dampingRatio = 0.82f,
                    stiffness = Spring.StiffnessMediumLow,
                )
            }
        },
        label = "circle_offset",
    ) {

        IntOffset(
            x = (it - circleRadiusPx).toInt(),
            y = -(circleRadiusPx / 2),
        )
    }

    //
    // CUSTOM BAR SHAPE
    //
    val barShape = remember(cutoutOffset) {
        BarShape(
            offset = cutoutOffset,
            circleRadius = circleRadius,
            cornerRadius = 28.dp,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp,
            )
            .height(112.dp),
    ) {

        //
        // FLOATING ACTIVE BUTTON
        //
        Circle(
            modifier = Modifier
                .offset { circleOffset }
                .align(Alignment.TopStart)
                .zIndex(2f)
                .shadow(
                    elevation = 18.dp,
                    shape = CircleShape,
                    clip = false,
                ),
            color = activeColor,
            radius = circleRadius,
            button = ButtonData(
                text = "",
                icon = routes[selectedIndex].selectedIcon,
            ),
            iconColor = Color(0xFF2D2D2D),
        )

        //
        // NAVIGATION BAR
        //
        Row(
            modifier = Modifier
                .padding(top = 22.dp)
                .onSizeChanged {
                    barSize = it
                }
                .shadow(
                    elevation = 12.dp,
                    shape = barShape,
                    clip = false,
                )
                .clip(barShape)
                .background(barColor)
                .fillMaxWidth()
                .height(84.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            routes.forEachIndexed { _, destination ->

                val isSelected =
                    currentRoute?.root == destination.route

                val iconDrawable =
                    if (isSelected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {

                            navController.navigate(destination.route.route) {

                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }

                                launchSingleTop = true
                                restoreState = false
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {

                        //
                        // HIDE ICON WHEN SELECTED
                        //
                        Icon(
                            imageVector = iconDrawable,
                            contentDescription = stringResource(destination.titleId),
                            tint =
                                if (isSelected) {
                                    Color.Transparent
                                } else {
                                    textColor
                                },
                            modifier = Modifier
                                .size(24.dp)
                                .alpha(
                                    if (isSelected) 0f else 1f
                                ),
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp),
                        )

                        Text(
                            text = stringResource(destination.titleId),
                            color = textColor,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

//
// FLOATING ACTIVE CIRCLE
//
@Composable
private fun Circle(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    radius: Dp,
    button: ButtonData,
    iconColor: Color,
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(radius * 2)
            .clip(CircleShape)
            .background(color),
    ) {

        AnimatedContent(
            targetState = button.icon,
            label = "Bottom bar circle icon",
        ) { targetIcon ->

            Icon(
                imageVector = targetIcon,
                contentDescription = button.text,
                tint = iconColor,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

//
// CUSTOM BAR SHAPE
//
private class BarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val cornerRadius: Dp,
    private val circleGap: Dp = 5.dp,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {

        return Outline.Generic(
            getPath(size, density),
        )
    }

    private fun getPath(
        size: Size,
        density: Density,
    ): Path {

        val cutoutCenterX = offset

        val cutoutRadius =
            density.run {
                (circleRadius + circleGap).toPx()
            }

        val cornerRadiusPx =
            density.run {
                cornerRadius.toPx()
            }

        val cornerDiameter = cornerRadiusPx * 2

        return Path().apply {

            val cutoutEdgeOffset = cutoutRadius * 1.5f

            val cutoutLeftX =
                cutoutCenterX - cutoutEdgeOffset

            val cutoutRightX =
                cutoutCenterX + cutoutEdgeOffset

            //
            // BOTTOM LEFT
            //
            moveTo(
                x = 0f,
                y = size.height,
            )

            //
            // TOP LEFT
            //
            if (cutoutLeftX > 0) {

                val realLeftCornerDiameter =
                    if (cutoutLeftX >= cornerRadiusPx) {
                        cornerDiameter
                    } else {
                        cutoutLeftX * 2
                    }

                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = 0f,
                        right = realLeftCornerDiameter,
                        bottom = realLeftCornerDiameter,
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )
            }

            //
            // BEFORE CUTOUT
            //
            lineTo(cutoutLeftX, 0f)

            //
            // CURVE DOWN
            //
            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutCenterX,
                y3 = cutoutRadius,
            )

            //
            // CURVE UP
            //
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = 0f,
                x3 = cutoutRightX,
                y3 = 0f,
            )

            //
            // TOP RIGHT
            //
            if (cutoutRightX < size.width) {

                val realRightCornerDiameter =
                    if (cutoutRightX <= size.width - cornerRadiusPx) {
                        cornerDiameter
                    } else {
                        (size.width - cutoutRightX) * 2
                    }

                arcTo(
                    rect = Rect(
                        left = size.width - realRightCornerDiameter,
                        top = 0f,
                        right = size.width,
                        bottom = realRightCornerDiameter,
                    ),
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )
            }

            //
            // BOTTOM RIGHT
            //
            lineTo(
                x = size.width,
                y = size.height,
            )

            close()
        }
    }
}
