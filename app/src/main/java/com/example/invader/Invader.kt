package com.example.invader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlin.math.max
import kotlin.math.min


@Composable
fun Invader(
  resetDeck: () -> Unit,
  counter: Int,
  discardCard: Card,
  ravageCard: Card,
  exploreCard: Card,
  buildingCard: Card,
  immigrationCard: Card,
  exploreClick: () -> Unit,
  revealed: Boolean,
  nationConfig: NationConfig,
  setNationConfig: (NationConfig) -> Unit
) {
  val openNewGameDialog = remember { mutableStateOf(false) }
  val openNationDialog = remember { mutableStateOf(false) }

  val openNationDialogFunc = {
    openNationDialog.value = true
  }

  val openNewGameDialogFunc = {
    openNewGameDialog.value = true
  }

  when {
    openNewGameDialog.value -> {
      AlertDialog(onDismissRequest = { openNewGameDialog.value = false }, onConfirmation = {
        openNewGameDialog.value = false
        resetDeck()
      }, dialogTitle = stringResource(R.string.new_game), dialogText = stringResource(R.string.new_game_dialog), icon = Icons.Default.Warning
      )
    }

    openNationDialog.value -> {
      NationDialog(
        onDismissRequest = { openNationDialog.value = false },
        onConfirmation = { nc: NationConfig ->
          openNationDialog.value = false
          setNationConfig(nc)
        },
        currentConfig = nationConfig
      )
    }
  }
  Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
    CardDisplay(
      discardCard = discardCard,
      ravageCard = ravageCard,
      buildingCard = buildingCard,
      exploreCard = exploreCard,
      counter = counter,
      exploreClick = exploreClick,
      revealed = revealed,
      immigrationCard = immigrationCard,
      nationConfig = nationConfig
    )
    Bottom(nationConfig, openNationDialogFunc, openNewGameDialogFunc)
  }
}

@Preview(
  device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun CardDisplayPreview() {
  CardDisplay(Card.EMPTY, Card.EMPTY, Card.EMPTY, Card.EMPTY, Card.JUNGLE, 12, {}, false, NationConfig(Nation.England, 3))
}

@Composable
fun CardDisplay(
  discardCard: Card,
  ravageCard: Card,
  buildingCard: Card,
  immigrationCard: Card,
  exploreCard: Card,
  counter: Int,
  exploreClick: () -> Unit,
  revealed: Boolean,
  nationConfig: NationConfig
) {
  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
      Box(
        modifier = Modifier.width(100.dp),

        ) { Text(text = stringResource(R.string.cards) + counter.toString()) }
    }
    Row(
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Discard(card = discardCard)
      if (nationConfig.nation != Nation.England || nationConfig.level < 3)
        Splitter(color = MaterialTheme.colorScheme.primary)
      if (nationConfig.nation == Nation.England && nationConfig.level >= 3)
        Immigration(card = immigrationCard)
      Ravage(card = ravageCard)
      if (nationConfig.nation != Nation.England || nationConfig.level < 3)
        Splitter(color = MaterialTheme.colorScheme.primary)
      Building(card = buildingCard)
      if (nationConfig.nation != Nation.England || nationConfig.level < 3)
        Splitter(color = MaterialTheme.colorScheme.primary)
      Explore(card = exploreCard, onClick = exploreClick, revealed)
    }
  }
}

@Composable
@Preview(
  device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
fun BottomPreview() {
  Bottom(NationConfig(Nation.Brandenburg, 2), {}, {})
}

@Composable
fun Bottom(
  nationConfig: NationConfig,
  openNationDialog: () -> Unit,
  openNewGameDialog: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 5.dp),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      NationDisplay(nationConfig)
      Spacer(modifier = Modifier.width(20.dp))
      Button(onClick = { openNationDialog() }) {
        Icon(Icons.Filled.Settings, contentDescription = "Nation Settings")
      }
    }
    Button(onClick = { openNewGameDialog() }) {
      Text(stringResource(R.string.neues_spiel))
    }
  }
}

@Composable
@Preview(
  device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
fun NationDisplayPreview() {
  NationDisplay(NationConfig(Nation.Brandenburg, 1))
}

@Composable
fun NationDisplay(nationConfig: NationConfig) {
  Row(horizontalArrangement = Arrangement.SpaceBetween) {
    Text(stringResource(nationConfig.nation.descId))
    Row(modifier = Modifier.padding(start = 20.dp)) {
      Text(stringResource(R.string.level) + ":")
      Text(nationConfig.level.toString())
    }
  }
}

@Composable
fun NationDialog(
  onDismissRequest: () -> Unit,
  onConfirmation: (NationConfig) -> Unit,
  currentConfig: NationConfig
) {
  val nation = remember { mutableStateOf(currentConfig.nation) }
  val level = remember { mutableIntStateOf(currentConfig.level) }

  AlertDialog(
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = Modifier.fillMaxWidth(.8f),
    title = {
      Text(text = stringResource(R.string.nationen_einstellungen))
    },
    text = {
      Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
          Button(
            onClick = { nation.value = Nation.None }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (nation.value == Nation.None) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)
          ) {
            Text(stringResource(Nation.None.descId))
          }
          Button(
            onClick = { nation.value = Nation.Brandenburg }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (nation.value == Nation.Brandenburg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)
          ) {
            Text(stringResource(Nation.Brandenburg.descId))
          }
          Button(
            onClick = { nation.value = Nation.England }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (nation.value == Nation.England) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)
          ) {
            Text(stringResource(Nation.England.descId))
          }
          Button(
            onClick = { nation.value = Nation.Schweden }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (nation.value == Nation.Schweden) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)
          ) {
            Text(stringResource(Nation.Schweden.descId))
          }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
          Text("Level")
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { level.intValue = max(1, level.intValue - 1) }) {
              Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "-1")
            }
            Text(text = "${level.intValue}")
            IconButton(onClick = { level.intValue = min(level.intValue + 1, 6) }) {
              Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "+1")
            }
          }
        }
      }

    }, onDismissRequest = {
      onDismissRequest()
    }, confirmButton = {
      TextButton(onClick = {
        onConfirmation(NationConfig(nation.value, level.intValue))
      }) {
        Text(stringResource(R.string.confirm))
      }
    }, dismissButton = {
      TextButton(onClick = {
        onDismissRequest()
      }) {
        Text(stringResource(R.string.abort))
      }
    })
}


@Composable
fun AlertDialog(
  onDismissRequest: () -> Unit,
  onConfirmation: () -> Unit,
  dialogTitle: String,
  dialogText: String,
  icon: ImageVector,
) {
  AlertDialog(icon = {
    Icon(icon, contentDescription = "Example Icon")
  }, title = {
    Text(text = dialogTitle)
  }, text = {
    Text(text = dialogText)
  }, onDismissRequest = {
    onDismissRequest()
  }, confirmButton = {
    TextButton(onClick = {
      onConfirmation()
    }) {
      Text(stringResource(R.string.confirm))
    }
  }, dismissButton = {
    TextButton(onClick = {
      onDismissRequest()
    }) {
      Text(stringResource(R.string.abort))
    }
  })
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
fun Immigration(card: Card) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    DynamicDisplay(card = card)
    Text(text = stringResource(R.string.immigration))
  }
}


@Composable
fun Building(card: Card) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    DynamicDisplay(card = card)
    Text(text = stringResource(R.string.building))
  }
}

@Composable
fun Ravage(card: Card) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    DynamicDisplay(card = card)
    Text(text = stringResource(R.string.ravage))
  }
}

@Composable
fun Explore(card: Card, onClick: () -> Unit, revealed: Boolean) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(modifier = Modifier.clickable(onClick = onClick)) {
      if (revealed) {
        DynamicDisplay(card = card)
      } else {
        Empty(card.gen)
      }
    }
    Text(text = stringResource(R.string.explore))
  }
}

@Composable
fun Discard(card: Card) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(180.dp)) {
    Column(modifier = Modifier.rotate(-90f)) {
      DynamicDisplay(card = card)
    }
    Text(stringResource(R.string.ablage))
  }
}

@Composable
fun DynamicDisplay(card: Card) {
  when (card) {
    Card.EMPTY -> Empty(card.gen)
    Card.SWAMP -> Swamp(card.gen)
    Card.COAST -> Coast(card.gen)
    Card.DESERT -> Desert(card.gen)
    Card.JUNGLE -> Jungle(card.gen)
    Card.SWAMP_NATION -> Swamp(card.gen, nation = true)
    Card.MOUNTAIN_NATION -> Mountain(card.gen, nation = true)
    Card.DESERT_NATION -> Desert(card.gen, nation = true)
    Card.JUNGLE_NATION -> Jungle(card.gen, nation = true)
    Card.FINISH -> Finish()
    Card.DESERT_JUNGLE -> DesertJungle()
    Card.DESERT_SWAMP -> DesertSwamp()
    Card.MOUNTAIN -> Mountain(card.gen)
    Card.MOUNTAIN_DESERT -> MountainDesert()
    Card.MOUNTAIN_JUNGLE -> MountainJungle()
    Card.MOUNTAIN_SWAMP -> MountainSwamp()
    Card.SWAMP_JUNGLE -> SwampJungle()
  }
}

@Preview
@Composable
fun Swamp(gen: Int = 1, nation: Boolean = false) {
  SingleDisplayCard(color = CardColor.SWAMP.color, text = stringResource(R.string.sumpf), generation = gen, nation = nation)
}

@Preview
@Composable
fun Mountain(gen: Int = 2, nation: Boolean = false) {
  SingleDisplayCard(color = CardColor.MOUNTAIN.color, text = stringResource(R.string.berg), generation = gen, nation = nation)
}

@Preview
@Composable
fun Desert(gen: Int = 1, nation: Boolean = false) {
  SingleDisplayCard(color = CardColor.DESERT.color, text = stringResource(R.string.desert), generation = gen, nation = nation)
}

@Preview
@Composable
fun Jungle(gen: Int = 1, nation: Boolean = false) {
  SingleDisplayCard(color = CardColor.JUNGLE.color, text = stringResource(R.string.dschungel), generation = gen, nation = nation)
}

@Preview
@Composable
fun Coast(gen: Int = 1) {
  SingleDisplayCard(color = CardColor.COAST.color, text = stringResource(R.string.coast), generation = gen)
}

@Preview
@Composable
fun Finish() {
  SingleDisplayCard(color = CardColor.FINISH.color, text = stringResource(R.string.gewonnen))
}

@Preview
@Composable
fun Empty(gen: Int = 2) {
  Card(
    border = BorderStroke(2.dp, Color.Black), modifier = Modifier.size(width = 120.dp, height = 200.dp)
  ) {
    Box(contentAlignment = Alignment.TopCenter) {
      Image(
        painter = painterResource(id = R.drawable.invasoren), contentDescription = stringResource(R.string.empty), contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize()
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
  DoubleDisplayCard(color1 = CardColor.DESERT.color, color2 = CardColor.JUNGLE.color, text1 = stringResource(R.string.desert), text2 = stringResource(R.string.dschungel))
}

@Preview
@Composable
fun DesertSwamp() {
  DoubleDisplayCard(color1 = CardColor.DESERT.color, color2 = CardColor.SWAMP.color, text1 = stringResource(R.string.desert), text2 = stringResource(R.string.sumpf))
}

@Preview
@Composable
fun MountainDesert() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.DESERT.color, text1 = stringResource(R.string.berg), text2 = stringResource(R.string.desert))
}

@Preview
@Composable
fun MountainJungle() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.JUNGLE.color, text1 = stringResource(R.string.berg), text2 = stringResource(R.string.dschungel))
}


@Preview
@Composable
fun MountainSwamp() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.SWAMP.color, text1 = stringResource(R.string.berg), text2 = stringResource(R.string.sumpf))
}

@Preview
@Composable
fun SwampJungle() {
  DoubleDisplayCard(color1 = CardColor.SWAMP.color, color2 = CardColor.JUNGLE.color, text1 = stringResource(R.string.sumpf), text2 = stringResource(R.string.dschungel))
}


@Composable
fun DoubleDisplayCard(color1: Color, color2: Color, text1: String, text2: String) {
  Card(
    border = BorderStroke(2.dp, Color.Black), modifier = Modifier.size(width = 120.dp, height = 200.dp), colors = CardDefaults.cardColors(
      containerColor = color1
    )
  ) {
    Box(contentAlignment = Alignment.Center) {
      Column {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .weight(1f, true)
            .fillMaxWidth()
            .background(color1)
        ) {
          Text(text1)
        }
        Column(
          horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
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
fun SingleDisplayCard(color: Color, text: String, generation: Int? = null, nation: Boolean = false) {
  Card(
    border = BorderStroke(2.dp, Color.Black),
    colors = CardDefaults.cardColors(
      containerColor = color
    ),
    modifier = Modifier.size(width = 120.dp, height = 200.dp),
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.height(50.dp)
      ) {
        if (generation != null) Text(text = if (generation == 1) "I" else "II")
      }
      Text(text)
      if (nation) Box(modifier = Modifier.height(50.dp), contentAlignment = Alignment.Center) {
        Image(
          painter = painterResource(id = R.drawable.nation),
          contentDescription = "Empty",
          contentScale = ContentScale.FillHeight,
          modifier = Modifier
            .padding(5.dp)
            .size(20.dp, 20.dp)
            .blur(5.dp, BlurredEdgeTreatment.Unbounded),
          colorFilter = ColorFilter.tint(Color.White),

          )
        Image(
          painter = painterResource(id = R.drawable.nation), contentDescription = "Empty", contentScale = ContentScale.FillHeight, modifier = Modifier
            .padding(5.dp)
            .size(20.dp, 20.dp)
        )
      }
      else Spacer(modifier = Modifier.height(50.dp))
    }
  }
}


enum class CardColor(val color: Color) {
  SWAMP(Color(0, 200, 200)), COAST(Color(55, 100, 178)), MOUNTAIN(Color(100, 100, 100)), DESERT(Color(196, 194, 98)), JUNGLE(Color(14, 81, 7)), FINISH(Color(250, 250, 250)),
}

enum class Card(val gen: Int) {
  SWAMP(1), JUNGLE(1), MOUNTAIN(1), DESERT(1), COAST(2), SWAMP_NATION(2), JUNGLE_NATION(2), MOUNTAIN_NATION(2), DESERT_NATION(2), MOUNTAIN_DESERT(3), SWAMP_JUNGLE(3), DESERT_JUNGLE(3), MOUNTAIN_JUNGLE(
    3
  ),
  DESERT_SWAMP(3), MOUNTAIN_SWAMP(3), FINISH(0), EMPTY(0),
}

enum class Nation(val descId: Int) {
  Brandenburg(R.string.brandenburg), England(R.string.england), Schweden(R.string.schweden), None(R.string.none);
}

data class NationConfig(val nation: Nation, val level: Int)

class Deck(val nationConfig: NationConfig) {
  private val firstColors = mutableListOf(Card.SWAMP, Card.JUNGLE, Card.MOUNTAIN, Card.DESERT)
  private val secondColors = mutableListOf(Card.SWAMP_NATION, Card.JUNGLE_NATION, Card.DESERT_NATION, Card.COAST, Card.MOUNTAIN_NATION)
  private val thirdColors = mutableListOf(Card.MOUNTAIN_DESERT, Card.SWAMP_JUNGLE, Card.DESERT_JUNGLE, Card.MOUNTAIN_JUNGLE, Card.DESERT_SWAMP, Card.MOUNTAIN_SWAMP)

  private val deck: MutableList<Card> = emptyList<Card>().toMutableList()

  init {
    firstColors.shuffle()
    secondColors.shuffle()
    thirdColors.shuffle()

    firstColors.removeAt(0)
    secondColors.removeAt(0)
    thirdColors.removeAt(0)

    when {
      nationConfig.nation == Nation.Brandenburg && nationConfig.level > 1 -> {
        val removed = thirdColors.removeAt(0)
        firstColors.add(removed)
        if (nationConfig.level >= 3) {
          firstColors.removeAt(0)
        }
        if (nationConfig.level >= 4) {
          secondColors.removeAt(0)
        }
        if (nationConfig.level >= 5) {
          firstColors.removeAt(0)
        }
        if (nationConfig.level >= 6) {
          firstColors.removeAt(0)
        }

        deck.addAll(firstColors)
        deck.addAll(secondColors)
        deck.addAll(thirdColors)
      }

      else -> {
        deck.addAll(firstColors)
        deck.addAll(secondColors)
        deck.addAll(thirdColors)
      }
    }

    deck.add(Card.FINISH)
  }

  val size get() = deck.size

  fun next(): Card {
    return deck.removeAt(0)
  }

}
