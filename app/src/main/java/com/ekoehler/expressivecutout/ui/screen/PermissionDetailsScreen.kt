package com.ekoehler.expressivecutout.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ekoehler.expressivecutout.R

/**
 * One permission, explained: what it is, and the specific things the app does with it. The copy
 * lives here rather than in strings.xml so a permission is described next to the manifest entry it
 * documents.
 */
private data class PermissionDoc(
    val icon: ImageVector,
    val title: String,
    /** The manifest identifier, shown small and monospaced so it can be matched to the manifest. */
    val manifestName: String,
    val summary: String,
    val uses: List<String>,
    /** Only asked for on some Android versions, or only when a given feature is used. */
    val optional: Boolean = false,
)

/**
 * Every permission the app declares, in the order the user meets them during setup, ending with
 * the quiet ones — network state and package visibility — that are granted automatically and never
 * prompt. This list is the whole manifest — if an entry is added there, it belongs here too.
 */
@Composable
private fun permissionDocs(): List<PermissionDoc> = listOf(
    PermissionDoc(
        icon = Icons.Rounded.Notifications,
        title = stringResource(R.string.permission_doc_0),
        manifestName = "BIND_NOTIFICATION_LISTENER_SERVICE",
        summary = stringResource(R.string.permission_doc_1),
        uses = listOf(
            stringResource(R.string.permission_doc_2),
            stringResource(R.string.permission_doc_3),
            stringResource(R.string.permission_doc_4),
            stringResource(R.string.permission_doc_5),
            stringResource(R.string.permission_doc_6),
        ),
    ),
    PermissionDoc(
        icon = Icons.Rounded.Vibration,
        title = stringResource(R.string.permission_doc_7),
        manifestName = "VIBRATE",
        summary = stringResource(R.string.permission_doc_8),
        uses = listOf(
            stringResource(R.string.permission_doc_9),
            stringResource(R.string.permission_doc_10)
        )
    ),
    PermissionDoc(
        icon = Icons.Rounded.Layers,
        title = stringResource(R.string.permission_doc_11),
        manifestName = "BIND_ACCESSIBILITY_SERVICE",
        summary = stringResource(R.string.permission_doc_12),
        uses = listOf(
            stringResource(R.string.permission_doc_13),
            stringResource(R.string.permission_doc_14),
            stringResource(R.string.permission_doc_15),
            stringResource(R.string.permission_doc_16),
        ),
    ),
    PermissionDoc(
        icon = Icons.Rounded.BatterySaver,
        title = stringResource(R.string.permission_doc_17),
        manifestName = "REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
        summary = stringResource(R.string.permission_doc_18),
        uses = listOf(
            stringResource(R.string.permission_doc_19),
        ),
        optional = true,
    ),
    PermissionDoc(
        icon = Icons.Rounded.NotificationsActive,
        title = stringResource(R.string.permission_doc_20),
        manifestName = "POST_NOTIFICATIONS",
        summary = stringResource(R.string.permission_doc_21),
        uses = listOf(
            stringResource(R.string.permission_doc_22),
        ),
        optional = true,
    ),
    PermissionDoc(
        icon = Icons.Rounded.VolumeUp,
        title = stringResource(R.string.permission_doc_23),
        manifestName = "MODIFY_AUDIO_SETTINGS",
        summary = stringResource(R.string.permission_doc_24),
        uses = listOf(
            stringResource(R.string.permission_doc_25),
            stringResource(R.string.permission_doc_26),
        ),
    ),
    PermissionDoc(
        icon = Icons.Rounded.Call,
        title = stringResource(R.string.permission_doc_27),
        manifestName = "READ_PHONE_STATE",
        summary = stringResource(R.string.permission_doc_28),
        uses = listOf(
            stringResource(R.string.permission_doc_29),
        ),
        optional = true,
    ),
    PermissionDoc(
        icon = Icons.Rounded.Terminal,
        title = stringResource(R.string.permission_doc_30),
        manifestName = "moe.shizuku.manager.permission.API_V23",
        summary = stringResource(R.string.permission_doc_31),
        uses = listOf(
            stringResource(R.string.permission_doc_32),
            stringResource(R.string.permission_doc_33),
            stringResource(R.string.permission_doc_34),
        ),
        optional = true,
    ),
    PermissionDoc(
        icon = Icons.Rounded.Wifi,
        title = stringResource(R.string.permission_doc_35),
        manifestName = "ACCESS_NETWORK_STATE",
        summary = stringResource(R.string.permission_doc_36),
        uses = listOf(
            stringResource(R.string.permission_doc_37),
        ),
    ),
    PermissionDoc(
        icon = Icons.Rounded.WifiTethering,
        title = stringResource(R.string.permission_doc_38),
        manifestName = "ACCESS_WIFI_STATE",
        summary = stringResource(R.string.permission_doc_39),
        uses = listOf(
            stringResource(R.string.permission_doc_40),
        ),
    ),
    PermissionDoc(
        icon = Icons.Rounded.Apps,
        title = stringResource(R.string.permission_doc_41),
        manifestName = "<queries>",
        summary = stringResource(R.string.permission_doc_42),
        uses = listOf(
            stringResource(R.string.permission_doc_43),
            stringResource(R.string.permission_doc_44),
            stringResource(R.string.permission_doc_45),
        ),
    ),
)

/**
 * "Permission details": one card per declared permission — what it is, then the specific things the
 * app does with it. Reached from the permissions card in Profile.
 */
@Composable
fun PermissionDetailsScreen(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IntroCard()

        permissionDocs().forEach { doc -> PermissionDocCard(doc) }

        FooterCard()
    }
}

/** The promise the rest of the screen backs up: on-device only, no network, no tracking. */
@Composable
private fun IntroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.permission_details_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * One permission explained at length: what it is for, and what the app deliberately does not do
 * with it.
 */
@Composable
private fun PermissionDocCard(doc: PermissionDoc) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Title row: icon, name, and the optional badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = doc.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = doc.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (doc.optional) {
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ) {
                        Text(
                            text = stringResource(R.string.permission_details_optional),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            // The manifest permission itself
            Text(
                text = doc.manifestName,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )

            // Plain-language summary
            Text(
                text = doc.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )

            // What the app uses it for
            BulletGroup(
                icon = Icons.Rounded.Check,
                label = stringResource(R.string.permission_details_uses),
                accent = MaterialTheme.colorScheme.primary,
                items = doc.uses,
            )
        }
    }
}

/** One labelled group of bullets, matching the changelog's release groups. */
@Composable
private fun BulletGroup(
    icon: ImageVector,
    label: String,
    accent: Color,
    items: List<String>,
) {
    if (items.isEmpty()) return

    Column(
        modifier = Modifier.padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = accent,
            )
        }
        items.forEach { item ->
            Row {
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(5.dp)
                        .background(accent, CircleShape),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

/** Closes the screen by pointing at the source, so none of the above has to be taken on trust. */
@Composable
private fun FooterCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.permission_details_footer_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.permission_details_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
