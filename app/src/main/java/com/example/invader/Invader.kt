package com.example.invader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath


@Composable
fun Invader(
  resetDeck: () -> Unit,
  counter: Int,
  discardCard: Pair<Card, Int>,
  ravageCard: Pair<Card, Int>,
  exploreCard: Pair<Card, Int>,
  buildingCard: Pair<Card, Int>,
  exploreClick: () -> Unit
) {
  val openDialog = remember { mutableStateOf(false) }

  when {
    openDialog.value -> {
      AlertDialog(
        onDismissRequest = { openDialog.value = false },
        onConfirmation = {
          openDialog.value = false
          resetDeck()
        },
        dialogTitle = "Neues Spiel",
        dialogText = "Es wird neu gemischt und kann nicht mehr rückgängig gemacht werden.",
        icon = Icons.Default.Warning
      )
    }
  }
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier.fillMaxSize()
  ) {
    Discard(card = discardCard)
    Splitter(color = MaterialTheme.colorScheme.primary)
    Ravage(card = ravageCard)
    Splitter(color = MaterialTheme.colorScheme.primary)
    Building(card = buildingCard)
    Splitter(color = MaterialTheme.colorScheme.primary)
    Column {
      Box(
        modifier = Modifier
          .weight(1f, true)
          .width(120.dp),
        contentAlignment = Alignment.BottomCenter,
      ) {
        Text(text = "$counter")
      }
      Explore(card = exploreCard, onClick = exploreClick)
      Column(
        modifier = Modifier.weight(1f, true),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Button(
          onClick = { openDialog.value = true }
        ) {
          Text("Neues Spiel")
        }
      }
    }
  }
}


@Composable
fun AlertDialog(
  onDismissRequest: () -> Unit,
  onConfirmation: () -> Unit,
  dialogTitle: String,
  dialogText: String,
  icon: ImageVector,
) {
  AlertDialog(
    icon = {
      Icon(icon, contentDescription = "Example Icon")
    },
    title = {
      Text(text = dialogTitle)
    },
    text = {
      Text(text = dialogText)
    },
    onDismissRequest = {
      onDismissRequest()
    },
    confirmButton = {
      TextButton(
        onClick = {
          onConfirmation()
        }
      ) {
        Text("Bestätigen")
      }
    },
    dismissButton = {
      TextButton(
        onClick = {
          onDismissRequest()
        }
      ) {
        Text("Abbrechen")
      }
    }
  )
}

@Preview
@Composable
fun Splitter(width: Dp = 20.dp, color: Color = Color.Black) {
  Column() {
    Box(modifier = Modifier
      .drawWithCache {
        val h = size.height
        val w = size.width
        val start = (h - 2 * w) / 2
        val r = RoundedPolygon(vertices = floatArrayOf(w, 1f * start, 0f, 0.5f * h, w, w + w + start, w, w + w + start - 0.1f * w, 0.11f * w, 0.5f * h, w, start + 0.1f * w))
        val roundedPolygonPath = r
          .toPath()
          .asComposePath()
        onDrawBehind { drawPath(roundedPolygonPath, color = color) }
      }
      .width(width)
      .height(width * 2))
    Text(text = "")
  }
}


@Composable
fun Building(card: Pair<Card, Int>) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    DynamicDisplay(card = card)
    Text(text = "Bauen")
  }
}

@Composable
fun Ravage(card: Pair<Card, Int>) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    DynamicDisplay(card = card)
    Text(text = "Wüten")
  }
}

@Composable
fun Explore(card: Pair<Card, Int>, onClick: () -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(modifier = Modifier.clickable(onClick = onClick)) {
      DynamicDisplay(card = card)
    }
    Text(text = "Erkunden")
  }
}

@Composable
fun Discard(card: Pair<Card, Int>) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(180.dp)) {
    Column(modifier = Modifier.rotate(-90f)) {
      DynamicDisplay(card = card)
    }
    Text("Ablage")
  }
}

@Composable
fun DynamicDisplay(card: Pair<Card, Int>) {
  when (card.first) {
    Card.EMPTY -> Empty(card.second)
    Card.SWAMP -> Swamp(card.second)
    Card.COAST -> Coast(card.second)
    Card.DESERT -> Desert(card.second)
    Card.JUNGLE -> Jungle(card.second)
    Card.FINISH -> Finish()
    Card.DESERT_JUNGLE -> DesertJungle()
    Card.DESERT_SWAMP -> DesertSwamp()
    Card.MOUNTAIN -> Mountain(card.second)
    Card.MOUNTAIN_DESERT -> MountainDesert()
    Card.MOUNTAIN_JUNGLE -> MountainJungle()
    Card.MOUNTAIN_SWAMP -> MountainSwamp()
    Card.SWAMP_JUNGLE -> SwampJungle()
  }
}

@Preview
@Composable
fun Swamp(gen: Int = 1) {
  SingleDisplayCard(color = CardColor.SWAMP.color, text = "Sumpf", generation = gen)
}

@Preview
@Composable
fun Mountain(gen: Int = 2) {
  SingleDisplayCard(color = CardColor.MOUNTAIN.color, text = "Berg", generation = gen)
}

@Preview
@Composable
fun Desert(gen: Int = 1) {
  SingleDisplayCard(color = CardColor.DESERT.color, text = "Wüste", generation = gen)
}

@Preview
@Composable
fun Jungle(gen: Int = 1) {
  SingleDisplayCard(color = CardColor.JUNGLE.color, text = "Dschungel", generation = gen)
}

@Preview
@Composable
fun Coast(gen: Int = 1) {
  SingleDisplayCard(color = CardColor.COAST.color, text = "Küste", generation = gen)
}

@Preview
@Composable
fun Finish() {
  SingleDisplayCard(color = CardColor.FINISH.color, text = "Gewonnen")
}

@Preview
@Composable
fun Empty(gen: Int = 2) {
  Card(
    border = BorderStroke(2.dp, Color.Black),
    modifier = Modifier
      .size(width = 120.dp, height = 200.dp)
  ) {
    Box(contentAlignment = Alignment.TopCenter) {
      Image(
        painter = painterResource(id = R.drawable.invasoren),
        contentDescription = "Empty",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
      )
      Column() {
        Spacer(modifier = Modifier.weight(1f, true))
        Box(modifier = Modifier.weight(7f, true)) {
          when (gen) {
            1 -> Text("I", color = Color(201, 49, 42), fontWeight = FontWeight(700))
            2 -> Text("II", color = Color(201, 49, 42), fontWeight = FontWeight(700))
            3 -> Text("III", color = Color(201, 49, 42), fontWeight = FontWeight(700))
            else -> Text("")
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun DesertJungle() {
  DoubleDisplayCard(color1 = CardColor.DESERT.color, color2 = CardColor.JUNGLE.color, text1 = "Wüste", text2 = "Dschungel")
}

@Preview
@Composable
fun DesertSwamp() {
  DoubleDisplayCard(color1 = CardColor.DESERT.color, color2 = CardColor.SWAMP.color, text1 = "Wüste", text2 = "Sumpf")
}

@Preview
@Composable
fun MountainDesert() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.DESERT.color, text1 = "Berg", text2 = "Wüste")
}

@Preview
@Composable
fun MountainJungle() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.JUNGLE.color, text1 = "Berg", text2 = "Dschungel")
}


@Preview
@Composable
fun MountainSwamp() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.SWAMP.color, text1 = "Berg", text2 = "Sumpf")
}

@Preview
@Composable
fun SwampJungle() {
  DoubleDisplayCard(color1 = CardColor.SWAMP.color, color2 = CardColor.JUNGLE.color, text1 = "Sumpf", text2 = "Dschungel")
}


@Composable
fun DoubleDisplayCard(color1: Color, color2: Color, text1: String, text2: String) {
  Card(
    border = BorderStroke(2.dp, Color.Black),
    modifier = Modifier.size(width = 120.dp, height = 200.dp),
    colors = CardDefaults.cardColors(
      containerColor = color1
    )
  ) {
    Box(contentAlignment = Alignment.Center) {
      Column {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .weight(1f, true)
            .fillMaxWidth()
            .background(color1)
        ) {
          Text(text1)
        }
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .weight(1f, true)
            .fillMaxWidth()
            .background(color2)
        ) {
          Text(text2)
        }
      }
      Text(text = "III")
    }
  }
}

@Composable
fun SingleDisplayCard(color: Color, text: String, generation: Int? = null) {
  Card(
    border = BorderStroke(2.dp, Color.Black),
    colors = CardDefaults.cardColors(
      containerColor = color
    ),
    modifier = Modifier.size(width = 120.dp, height = 200.dp),
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxSize()
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(50.dp)
      ) {
        if (generation != null)
          Text(text = if (generation == 1) "I" else "II")
      }
      Text(text)
      Spacer(modifier = Modifier.height(50.dp))
    }
  }
}


enum class CardColor(val color: Color) {
  SWAMP(Color(0, 200, 200)),
  COAST(Color(55, 100, 178)),
  MOUNTAIN(Color(100, 100, 100)),
  DESERT(Color(196, 194, 98)),
  JUNGLE(Color(14, 81, 7)),
  FINISH(Color(250, 250, 250)),
}

enum class Card {
  COAST,
  SWAMP,
  JUNGLE,
  MOUNTAIN,
  DESERT,
  MOUNTAIN_DESERT,
  SWAMP_JUNGLE,
  DESERT_JUNGLE,
  MOUNTAIN_JUNGLE,
  DESERT_SWAMP,
  MOUNTAIN_SWAMP,
  FINISH,
  EMPTY,
}

class Deck {
  private val firstColors = mutableListOf(Card.SWAMP, Card.JUNGLE, Card.MOUNTAIN, Card.DESERT)
  private val secondColors = mutableListOf(Card.SWAMP, Card.JUNGLE, Card.DESERT, Card.COAST, Card.MOUNTAIN)
  private val thirdColors = mutableListOf(Card.MOUNTAIN_DESERT, Card.SWAMP_JUNGLE, Card.DESERT_JUNGLE, Card.MOUNTAIN_JUNGLE, Card.DESERT_SWAMP, Card.MOUNTAIN_SWAMP)

  init {
    firstColors.shuffle()
    secondColors.shuffle()
    thirdColors.shuffle()
  }

  fun next(): Pair<Card, Int> {
    if (firstColors.size > 0)
      return firstColors.removeFirst() to 1
    if (secondColors.size > 0)
      return secondColors.removeFirst() to 2
    if (thirdColors.size > 0)
      return thirdColors.removeFirst() to 3
    return Card.FINISH to 0
  }

  fun nextGen(): Int {
    if (firstColors.size > 0)
      return 1
    if (secondColors.size > 0)
      return 2
    if (thirdColors.size > 0)
      return 3
    return 0
  }
}
