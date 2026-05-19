package com.swordfish.lemuroid.app.mobile.feature.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

data class ButtonData(
    val text: String,
    val icon: ImageVector,
)

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

@Composable
fun MainNavigationBar(
    currentRoute: MainRoute?,
    navController: NavHostController,
) {

    AnimatedVisibility(
        visible = true,
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

    var expanded by remember {
        mutableStateOf(false)
    }

    val routes = listOf(
        BottomNavItem(
            route = MainRoute.HOME.route,
            title = "Home",
            icon = MainNavigationRoutes.HOME.selectedIcon,
        ),
        BottomNavItem(
            route = MainRoute.SETTINGS.route,
            title = "Settings",
            icon = MainNavigationRoutes.SETTINGS.selectedIcon,
        ),
    )

    val barColor = Color(0xFFF4F0E5)

    val activeColor = Color(0xFFF6D96B)

    val activeTextColor = Color(0xFF1F1F1F)

    val inactiveTextColor = Color(0xFF777777)

    val circleRadius = 34.dp

    //
    // IMPORTANT
    //
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
    ) {

        //
        // TAP OUTSIDE CLOSE
        //
        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                    ) {

                        expanded = false
                    },
            )
        }

        //
        // BOTTOM BAR
        //
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp,
                )
                .shadow(
                    elevation = 12.dp,
                    shape = BarShape(
                        offset = 500f,
                        circleRadius = circleRadius,
                        cornerRadius = 28.dp,
                    ),
                    clip = false,
                )
                .clip(
                    BarShape(
                        offset = 500f,
                        circleRadius = circleRadius,
                        cornerRadius = 28.dp,
                    ),
                )
                .background(barColor)
                .fillMaxWidth()
                .height(84.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            //
            // HOME
            //
            BottomItem(
                modifier = Modifier.weight(1f),
                destination = routes[0],
                isSelected = currentRoute?.route == routes[0].route,
                activeTextColor = activeTextColor,
                inactiveTextColor = inactiveTextColor,
                navController = navController,
                onCloseFab = {
                    expanded = false
                },
            )

            //
            // SPACE FOR FAB
            //
            Spacer(
                modifier = Modifier.width(100.dp),
            )

            //
            // SETTINGS
            //
            BottomItem(
                modifier = Modifier.weight(1f),
                destination = routes[1],
                isSelected = currentRoute?.route == routes[1].route,
                activeTextColor = activeTextColor,
                inactiveTextColor = inactiveTextColor,
                navController = navController,
                onCloseFab = {
                    expanded = false
                },
            )
        }

        //
        // FAB
        //
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)

                //
                // FIX REAL FLOATING POSITION
                //
                .offset {
                    IntOffset(
                        x = 0,
                        y = -180,
                    )
                }
                .zIndex(100f),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                //
                // MENU
                //
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 14.dp),
                    ) {
                        SmallFab(
                            text = "Camera",
                            icon = Icons.Default.CameraAlt,
                            onClick = {

                                expanded = false
                            },
                        )

                        SmallFab(
                            text = "Gallery",
                            icon = Icons.Default.Collections,
                            onClick = {

                                expanded = false
                            },
                        )

                        SmallFab(
                            text = "Files",
                            icon = Icons.Default.Description,
                            onClick = {

                                expanded = false
                            },
                        )
                    }
                }

                //
                // MAIN FAB
                //
                Circle(
                    modifier = Modifier
                        .shadow(
                            elevation = 18.dp,
                            shape = CircleShape,
                            clip = false,
                        )
                        .clickable {

                            expanded = !expanded
                        },
                    color = activeColor,
                    radius = circleRadius,
                    button = ButtonData(
                        text = "",
                        icon = Icons.Default.Add,
                    ),
                    iconColor = Color(0xFF2D2D2D),
                )
            }
        }
    }
}

@Composable
private fun BottomItem(
    modifier: Modifier = Modifier,
    destination: BottomNavItem,
    isSelected: Boolean,
    activeTextColor: Color,
    inactiveTextColor: Color,
    navController: NavHostController,
    onCloseFab: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                onCloseFab()
                navController.navigate(destination.route) {
                    popUpTo(
                        navController.graph.findStartDestination().id
                    ) {
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
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.title,
                tint =
                    if (isSelected) {
                        activeTextColor
                    } else {
                        inactiveTextColor
                    },
                modifier = Modifier
                    .size(24.dp)
                    .alpha(
                        if (isSelected) 1f else 0.7f
                    ),
            )
            Spacer(
                modifier = Modifier.height(6.dp),
            )
            Text(
                text = destination.title,
                color =
                    if (isSelected) {
                        activeTextColor
                    } else {
                        inactiveTextColor
                    },
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight =
                        if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SmallFab(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
            )
            .clip(CircleShape)
            .background(Color.White)
            .clickable {

                onClick()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp,
            ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.Black,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = text,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

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
        val cutoutCenterX = size.width / 2f

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

            moveTo(
                x = 0f,
                y = size.height,
            )

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

            lineTo(cutoutLeftX, 0f)

            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutCenterX,
                y3 = cutoutRadius,
            )

            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = 0f,
                x3 = cutoutRightX,
                y3 = 0f,
            )

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

            lineTo(
                x = size.width,
                y = size.height,
            )

            close()
        }
    }
}
