package com.adrija.sportstalentscout.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adrija.sportstalentscout.data.models.Badge
import com.adrija.sportstalentscout.ui.theme.AccentGold
import com.adrija.sportstalentscout.ui.theme.SportsTalentScoutTheme

@Composable
fun BadgeItem(
    badge: Badge,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (badge.isEarned) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isEarned) AccentGold.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                color = if (badge.isEarned) AccentGold else MaterialTheme.colorScheme.outline
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (badge.isEarned) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isEarned) AccentGold else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!badge.isEarned && badge.progress > 0f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = badge.progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${(badge.progress * 100).toInt()}% complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BadgeItemPreview() {
    SportsTalentScoutTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BadgeItem(
                badge = Badge(
                    id = "1",
                    title = "First Test",
                    description = "Complete your first fitness test",
                    isEarned = true
                )
            )

            BadgeItem(
                badge = Badge(
                    id = "2",
                    title = "Speed Demon",
                    description = "Run 100m under 12 seconds",
                    isEarned = false,
                    progress = 0.7f
                )
            )
        }
    }
}
