package com.example.invader

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.invader.ui.theme.InvaderTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    setContent {
      InvaderTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Invader(Deck())
        }
      }
    }
  }
}

@Composable
fun Invader(deck: Deck) {
  val discardCard = remember { mutableStateOf(Card.EMPTY) }
  val ravageCard = remember { mutableStateOf(Card.EMPTY) }
  val exploreCard = remember { mutableStateOf(Card.EMPTY) }
  val buildingCard = remember { mutableStateOf(Card.EMPTY) }

  val activity = LocalContext.current as Activity
  activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier.fillMaxSize()
  ) {
    Discard(card = discardCard.value)
    Ravage(card = ravageCard.value)
    Building(card = buildingCard.value)
    Explore(card = exploreCard.value, onClick = {
      if (exploreCard.value == Card.EMPTY) {
        exploreCard.value = deck.next()
      } else if (exploreCard.value != Card.FINISH) {
        discardCard.value = ravageCard.value
        ravageCard.value = buildingCard.value
        buildingCard.value = exploreCard.value
        exploreCard.value = Card.EMPTY
      }
    })
  }
}

@Composable
fun Building(card: Card) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    DynamicDisplay(card = card)
    Text(text = "Bauen")
  }
}

@Composable
fun Ravage(card: Card) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    DynamicDisplay(card = card)
    Text(text = "Wüten")
  }
}

@Composable
fun Explore(card: Card, onClick: () -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    DynamicDisplay(card = card)
    Text(text = "Erkunden")
  }
}

@Composable
fun Discard(card: Card) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(180.dp)) {
    Column(modifier = Modifier.rotate(-90f)) {
      DynamicDisplay(card = card)
    }
    Text("Ablage")
  }
}

@Composable
fun DynamicDisplay(card: Card) {
  when (card) {
    Card.EMPTY -> Empty()
    Card.SWAMP -> Swamp()
    Card.COAST -> Coast()
    Card.DESERT -> Desert()
    Card.JUNGLE -> Jungle()
    Card.FINISH -> Finish()
    Card.DESERT_JUNGLE -> DesertJungle()
    Card.DESERT_SWAMP -> DesertSwamp()
    Card.MOUNTAIN -> Mountain()
    Card.MOUNTAIN_DESERT -> MountainDesert()
    Card.MOUNTAIN_JUNGLE -> MountainJungle()
    Card.MOUNTAIN_SWAMP -> MountainSwamp()
    Card.SWAMP_JUNGLE -> SwampJungle()
  }
}

@Preview
@Composable
fun Swamp() {
  SingleDisplayCard(color = CardColor.SWAMP.color, text = "Sumpf")
}

@Preview
@Composable
fun Mountain() {
  SingleDisplayCard(color = CardColor.MOUNTAIN.color, text = "Berg")
}

@Preview
@Composable
fun Desert() {
  SingleDisplayCard(color = CardColor.DESERT.color, text = "Wüste")
}

@Preview
@Composable
fun Jungle() {
  SingleDisplayCard(color = CardColor.JUNGLE.color, text = "Dschungel")
}

@Preview
@Composable
fun Coast() {
  SingleDisplayCard(color = CardColor.COAST.color, text = "Küste")
}

@Preview
@Composable
fun Finish() {
  SingleDisplayCard(color = CardColor.FINISH.color, text = "Gewonnen")
}

@Preview
@Composable
fun Empty() {
  SingleDisplayCard(color = CardColor.FINISH.color, text = "Leer")
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
    modifier = Modifier.size(width = 100.dp, height = 180.dp),
    colors = CardDefaults.cardColors(
      containerColor = color1
    )
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .height(90.dp)
        .fillMaxSize()
        .background(color1)
    ) {
      Text(text1)
    }
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .fillMaxSize()
        .background(color2)
    ) {
      Text(text2)
    }
  }
}

@Composable
fun SingleDisplayCard(color: Color, text: String) {
  Card(
    border = BorderStroke(2.dp, Color.Black),
    colors = CardDefaults.cardColors(
      containerColor = color
    ),
    modifier = Modifier.size(width = 100.dp, height = 180.dp),
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxSize()
    ) {
      Text(text)
    }
  }
}

@Preview
@Composable
fun InvaderPreview() {
  Invader(Deck())
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
  private val secondColors = mutableListOf(Card.SWAMP, Card.JUNGLE, Card.MOUNTAIN, Card.DESERT, Card.COAST)
  private val thirdColors = mutableListOf(Card.MOUNTAIN_DESERT, Card.SWAMP_JUNGLE, Card.DESERT_JUNGLE, Card.MOUNTAIN_JUNGLE, Card.DESERT_JUNGLE, Card.DESERT_SWAMP, Card.MOUNTAIN_SWAMP)

  init {
    firstColors.shuffle()
    secondColors.shuffle()
    thirdColors.shuffle()
  }

  fun next(): Card {
    if (firstColors.size > 0)
      return firstColors.removeFirst()
    if (secondColors.size > 0)
      return secondColors.removeFirst()
    if (thirdColors.size > 0)
      return thirdColors.removeFirst()
    return Card.FINISH
  }
}