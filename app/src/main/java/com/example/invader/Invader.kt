package com.example.invader

import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
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

/**
 * Composable that shows the invader deck in its current state and lets the user manipulate that.
 *
 * @param resetDeck handler to reset the deck
 * @param discardCard all cards that sit on top of the discard pile
 * @param ravageCard all cards that sit on top of the ravage pile
 * @param exploreCard all cards that sit on top of the explore pile
 * @param buildingCard all cards that sit on top of the building pile
 * @param immigrationCard all cards that sit on top of the immigration pile
 * @param exploreClick handler of the click event on top of the explore pile
 * @param revealed True of the top card of the explore pile is revealed
 * @param nationConfig configuration of the nation setup that controls visible and useable things
 * @param setNationConfig handler that changes the nation config
 * @param addDiscardCard handler that adds card to the discard pile
 * @param addRavageCard handler that adds card to the ravage pile
 * @param addBuildingCard handler that adds card to the building pile
 * @param addExploreCard handler that adds card to the explore pile
 * @param addImmigrationCard handler that adds card to the immigration pile
 * @param russiaHiddenCards list of cards that are on top of the russia pile
 * @param russiaRevealed True if the top card on the russia pile is revealed
 * @param russiaOnClick handler of the click event of the russia pile
 */
@Composable
fun Invader(
  resetDeck: () -> Unit,
  discardCard: List<Card>,
  ravageCard: List<Card>,
  exploreCard: List<Card>,
  buildingCard: List<Card>,
  immigrationCard: List<Card>,
  exploreClick: () -> Unit,
  revealed: Boolean,
  nationConfig: NationConfig,
  setNationConfig: (NationConfig) -> Unit,
  addDiscardCard: (Card) -> Unit,
  addRavageCard: (Card) -> Unit,
  addBuildingCard: (Card) -> Unit,
  addExploreCard: (Card) -> Unit,
  addImmigrationCard: (Card) -> Unit,
  russiaHiddenCards: List<Card>,
  russiaRevealed: Boolean,
  russiaOnClick: () -> Unit,
) {
  val openNationDialog = remember { mutableStateOf(false) }

  val openNationDialogFunc = {
    openNationDialog.value = true
  }

  val currentView = LocalView.current
  DisposableEffect(Unit) {
    currentView.keepScreenOn = true
    onDispose { currentView.keepScreenOn = false }
  }

  when {
    openNationDialog.value -> {
      NationDialog(onDismissRequest = { openNationDialog.value = false }, onConfirmation = { nc: NationConfig ->
        openNationDialog.value = false
        setNationConfig(nc)
        resetDeck()
      }, currentConfig = nationConfig
      )
    }
  }
  Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
    CardDisplay(
      discardCard = discardCard,
      ravageCard = ravageCard,
      buildingCard = buildingCard,
      immigrationCard = immigrationCard,
      exploreCard = exploreCard,
      exploreClick = exploreClick,
      revealed = revealed,
      nationConfig = nationConfig,
      addDiscardCard = addDiscardCard,
      addRavageCard = addRavageCard,
      addBuildingCard = addBuildingCard,
      addImmigrationCard = addImmigrationCard,
      addExploreCard = addExploreCard,
      openNationDialog = openNationDialogFunc,
      russiaHiddenCards = russiaHiddenCards,
      russiaRevealed = russiaRevealed,
      russiaOnClick = russiaOnClick,
    )
  }
}

/**
 * preview of [CardDisplay]
 */
@Preview(
  device = "spec:width=411dp,height=700dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun CardDisplayPreview() {
  CardDisplay(
    listOf(Card.EMPTY),
    listOf(Card.EMPTY),
    listOf(Card.EMPTY),
    listOf(Card.EMPTY),
    listOf(Card.JUNGLE),
    {},
    false,
    NationConfig(Nation.Brandenburg, 3),
    {},
    {},
    {},
    {},
    {},
    {},
    listOf(Card.EMPTY),
    false,
    {},
  )
}

/**
 * Component that shows the invader card piles
 * @param discardCard all cards that sit on top of the discard pile
 * @param ravageCard all cards that sit on top of the ravage pile
 * @param exploreCard all cards that sit on top of the explore pile
 * @param buildingCard all cards that sit on top of the building pile
 * @param immigrationCard all cards that sit on top of the immigration pile
 * @param exploreClick handler of the click event on top of the explore pile
 * @param revealed True of the top card of the explore pile is revealed
 * @param nationConfig configuration of the nation setup that controls visible and useable things
 * @param addDiscardCard handler that adds card to the discard pile
 * @param addRavageCard handler that adds card to the ravage pile
 * @param addBuildingCard handler that adds card to the building pile
 * @param addExploreCard handler that adds card to the explore pile
 * @param addImmigrationCard handler that adds card to the immigration pile
 * @param russiaHiddenCards list of cards that are on top of the russia pile
 * @param russiaRevealed True if the top card on the russia pile is revealed
 * @param russiaOnClick handler of the click event of the russia pile
 */
@Composable
fun CardDisplay(
  discardCard: List<Card>,
  ravageCard: List<Card>,
  buildingCard: List<Card>,
  immigrationCard: List<Card>,
  exploreCard: List<Card>,
  exploreClick: () -> Unit,
  revealed: Boolean,
  nationConfig: NationConfig,
  addDiscardCard: (Card) -> Unit,
  addRavageCard: (Card) -> Unit,
  addBuildingCard: (Card) -> Unit,
  addImmigrationCard: (Card) -> Unit,
  addExploreCard: (Card) -> Unit,
  openNationDialog: () -> Unit,
  russiaHiddenCards: List<Card>,
  russiaRevealed: Boolean,
  russiaOnClick: () -> Unit,
) {

  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    Row {
      Discard(cards = discardCard, addCard = addDiscardCard, nationConfig = nationConfig, openNationDialog = openNationDialog)
      Row(
        horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
      ) {
        if (nationConfig.nation != Nation.England || nationConfig.level < 3) Splitter(color = MaterialTheme.colorScheme.primary, onClick = exploreClick)
        if (nationConfig.nation == Nation.England && nationConfig.level >= 4 || nationConfig.nation == Nation.England && nationConfig.level == 3 && (discardCard.isEmpty() || discardCard.last().gen <= 1))
          Immigration(
            cards = immigrationCard,
            addCard = addImmigrationCard
          )
        Ravage(cards = ravageCard, addCard = addRavageCard)
        if (nationConfig.nation != Nation.England || nationConfig.level < 3) Splitter(color = MaterialTheme.colorScheme.primary, onClick = exploreClick)
        Building(
          cards = buildingCard,
          addCard = addBuildingCard
        )
        if (nationConfig.nation != Nation.England || nationConfig.level < 3) Splitter(color = MaterialTheme.colorScheme.primary, onClick = exploreClick)
        Explore(cards = exploreCard, onClick = exploreClick, revealed, addExploreCard)
        if (nationConfig.nation == Nation.Russland && nationConfig.level >= 5) {
          RussiaDeck(cards = russiaHiddenCards, onClick = russiaOnClick, revealed = russiaRevealed)
        }
      }
    }
  }
}

/**
 * Preview of [Bottom]
 */
@Composable
@Preview(
  device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
fun BottomPreview() {
  Bottom(NationConfig(Nation.Brandenburg, 2)) {}
}

/**
 * Composable with nation config
 * @param nationConfig current nation config
 * @param openNationDialog handler to open the nation config dialog
 */
@Composable
fun Bottom(
  nationConfig: NationConfig,
  openNationDialog: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 5.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      NationDisplay(nationConfig)
      Spacer(modifier = Modifier.width(20.dp))
    }
    Button(onClick = { openNationDialog() }) {
      Text(stringResource(R.string.neues_spiel))
    }
  }
}

/**
 * Preview for [NationDisplay]
 */
@Composable
@Preview(
  device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
fun NationDisplayPreview() {
  NationDisplay(NationConfig(Nation.Brandenburg, 1))
}

/**
 * Composable that shows the current nation config
 * @param nationConfig current nation config
 */
@Composable
fun NationDisplay(nationConfig: NationConfig) {
  Column(horizontalAlignment = Alignment.Start) {
    Text(stringResource(nationConfig.nation.descId))
    Row {
      Text(stringResource(R.string.level) + ":")
      Text(nationConfig.level.toString())
    }
  }
}

/**
 * Dialog that lets the user choose a nation config
 * @param onDismissRequest handler on dismissing the dialog
 * @param onConfirmation handler on confirming the dialog
 * @param currentConfig initial config of the dialog
 */
@Composable
fun NationDialog(
  onDismissRequest: () -> Unit,
  onConfirmation: (NationConfig) -> Unit,
  currentConfig: NationConfig,
) {
  val nation = remember { mutableStateOf(currentConfig.nation) }
  val level = remember { mutableIntStateOf(currentConfig.level) }

  AlertDialog(properties = DialogProperties(usePlatformDefaultWidth = false), modifier = Modifier.fillMaxWidth(.8f), title = {
    Text(text = stringResource(R.string.neues_spiel))
  }, text = {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 200.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(Nation.entries.toList()) { n ->
          Button(
            onClick = { nation.value = n },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (nation.value == n) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)
          ) {
            Text(stringResource(n.descId))
          }
        }
      }
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
          .fillMaxHeight()
          .width(100.dp)
      ) {
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

/**
 * This is a arrow that is used to separate the piles of cards
 */
@Preview
@Composable
fun Splitter(width: Dp = 20.dp, color: Color = Color.Black, onClick: () -> Unit = {}) {
  Column(
    modifier = Modifier
      .height(200.dp)
      .clickable(onClick = onClick), verticalArrangement = Arrangement.Center
  ) {
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
      .height(width * 2)
    )
    Text(text = "")
  }
}

/**
 * Droppable Composable for a card
 * @param addCard on drop handler of a card
 * @param content content of the droppable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardDroppable(addCard: (Card) -> Unit, content: @Composable (() -> Unit)) {
  Box(
    modifier = Modifier.dragAndDropTarget(
      shouldStartDragAndDrop = { event ->
        event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
      }, target =
      object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
          val data = event.toAndroidDragEvent().clipData.getItemAt(0).text
          val addedCard = Card.valueOf(data.toString())
          addCard(addedCard)
          Log.d(data.toString(), data.toString())
          return true
        }
      }
    )
  ) {
    content()
  }
}

/**
 * Composable that is a pile of cards
 * @param cards list of cards with the first card being the bottom card
 */
@Composable
fun CardStack(cards: List<Card>) {
  Box() {
    DynamicDisplay(Card.EMPTY, draggable = false)
    Column(verticalArrangement = Arrangement.spacedBy(-(185).dp)) {
      for ((index, card) in cards.withIndex()) {
        key(card) {
          Box(modifier = Modifier.padding(start = (15 * index).dp)) {
            DynamicDisplay(card = card)
          }
        }
      }
    }
  }
}

/**
 * Pile of immigration cards
 * @param cards list of cards with bottom one being the first card
 * @param addCard handler that is called when a card is added to this pile
 */
@Composable
fun Immigration(cards: List<Card>, addCard: (Card) -> Unit) {
  CardDroppable(addCard) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = stringResource(R.string.cards) + cards.size.toString())
      CardStack(cards)
      Text(text = stringResource(R.string.immigration))
    }
  }
}

/**
 * Pile of building cards
 * @param cards list of cards where the first card is the bottom one
 * @param addCard handler that is called when a card is added to this pile
 */
@Composable
fun Building(cards: List<Card>, addCard: (Card) -> Unit) {
  CardDroppable(addCard) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(text = stringResource(R.string.cards) + cards.size.toString())
      CardStack(cards)
      Text(text = stringResource(R.string.building))
    }
  }
}

/**
 * Pile of ravage cards
 * @param cards list of cards where the first card is the bottom card
 * @param addCard handler to add a card to the pile
 */
@Composable
fun Ravage(cards: List<Card>, addCard: (Card) -> Unit) {
  CardDroppable(addCard) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(text = stringResource(R.string.cards) + cards.size.toString())
      CardStack(cards)
      Text(text = stringResource(R.string.ravage))
    }
  }
}

/**
 * Pile of explore cards
 *
 * @param cards list of cards where the first card is the bottom card
 * @param onClick handler of the on click of the pile
 * @param revealed if the top card of the pile is revealed
 * @param addCard handler to add card to pile
 */
@Composable
fun Explore(cards: List<Card>, onClick: () -> Unit, revealed: Boolean, addCard: (Card) -> Unit) {
  CardDroppable(addCard) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(text = stringResource(R.string.cards) + cards.size.toString())
      Box(modifier = Modifier.clickable(onClick = onClick)) {
        DynamicDisplay(Card.FINISH, draggable = false)
        if (revealed) {
          Box {
            for (card in cards) {
              DynamicDisplay(card = card)
            }
          }
        } else {
          if (cards.isNotEmpty()) {
            DynamicDisplay(Card.EMPTY, cards.last().gen, false)
          }
        }
      }
      Text(text = stringResource(R.string.explore))
    }
  }
}

/**
 * The pile of cards for russia
 *
 * @param cards pile of cards where the first one is the bottom card of the deck
 * @param onClick handler of the onclick event
 * @param revealed True if the first card is revealed
 */
@Composable
fun RussiaDeck(cards: List<Card>, onClick: () -> Unit, revealed: Boolean) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(text = stringResource(R.string.cards) + cards.size.toString())
    Box(modifier = Modifier.clickable(onClick = onClick)) {
      DynamicDisplay(Card.EMPTY, draggable = false)
      if (revealed) {
        Box {
          for (card in cards) {
            DynamicDisplay(card = card)
          }
        }
      } else {
        if (cards.isNotEmpty()) {
          DynamicDisplay(Card.EMPTY, cards.last().gen, false)
        }
      }
    }
    Text(text = stringResource(R.string.russland))
  }
}

/**
 * Pile of discard cards
 *
 * @param cards pile of cards where the first one is the bottom card
 * @param addCard handler to add card to the pile
 * @param nationConfig current nation config
 * @param openNationDialog handler to open the nation config dialog
 */
@Composable
fun Discard(cards: List<Card>, addCard: (Card) -> Unit, nationConfig: NationConfig, openNationDialog: () -> Unit) {
  CardDroppable(addCard) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top, modifier = Modifier.width(200.dp)
    ) {
      Text(text = stringResource(R.string.cards) + cards.size.toString())
      Box(
        modifier = Modifier
          .requiredWidth(200.dp)
          .requiredHeight(120.dp)
      ) {
        Box(
          modifier = Modifier
            .rotate(-90f)
            .offset(y = 40.dp)
        ) {
          DynamicDisplay(Card.EMPTY, draggable = false)
          for (card in cards) {
            DynamicDisplay(card = card)
          }
        }
      }
      Column(modifier = Modifier.offset(y = (0).dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.ablage))
        Spacer(modifier = Modifier.height(5.dp))
        Bottom(nationConfig = nationConfig, openNationDialog = openNationDialog)
      }
    }
  }
}

/**
 * Display of a card
 * @param card card to be displayed
 * @param gen invader generation if there is any
 * @param draggable if the card is draggable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DynamicDisplay(card: Card, gen: Int? = null, draggable: Boolean = true) {
  Box(
    modifier = Modifier
      .then(if (draggable)
        Modifier.dragAndDropSource {
          detectTapGestures(onPress = {
            startTransfer(
              DragAndDropTransferData(
                ClipData.newPlainText(
                  card.toString(), card.toString()
                )
              )
            )
          })
        } else Modifier)
      .requiredHeight(200.dp)
      .requiredWidth(120.dp)
  ) {
    when (card) {
      Card.EMPTY -> Empty(gen)
      Card.SWAMP -> Swamp(1)
      Card.DESERT -> Desert(1)
      Card.JUNGLE -> Jungle(1)
      Card.MOUNTAIN -> Mountain(1)
      Card.COAST -> Coast(2)
      Card.SWAMP_NATION -> Swamp(2, nation = true)
      Card.MOUNTAIN_NATION -> Mountain(2, nation = true)
      Card.DESERT_NATION -> Desert(2, nation = true)
      Card.JUNGLE_NATION -> Jungle(2, nation = true)
      Card.FINISH -> Finish()
      Card.DESERT_JUNGLE -> DesertJungle()
      Card.DESERT_SWAMP -> DesertSwamp()
      Card.MOUNTAIN_DESERT -> MountainDesert()
      Card.MOUNTAIN_JUNGLE -> MountainJungle()
      Card.MOUNTAIN_SWAMP -> MountainSwamp()
      Card.SWAMP_JUNGLE -> SwampJungle()
      Card.HABSBURG -> Habsburg()
    }
  }
}

/**
 * A swamp card
 * @param gen generation of the swamp
 * @param nation True if this has the escalation effect on it
 */
@Preview
@Composable
fun Swamp(gen: Int = 1, nation: Boolean = false) {
  SingleDisplayCard(color = CardColor.SWAMP.color, text = stringResource(R.string.sumpf), generation = gen, nation = nation)
}

/**
 * A mountain card
 * @param gen generation of the mountain
 * @param nation True if this has the escalation effect
 */
@Preview
@Composable
fun Mountain(gen: Int = 2, nation: Boolean = false) {
  SingleDisplayCard(color = CardColor.MOUNTAIN.color, text = stringResource(R.string.berg), generation = gen, nation = nation)
}

/**
 * A desert card
 * @param gen generation of the desert card
 * @param nation True if this has the escalatino effect on it
 */
@Preview
@Composable
fun Desert(gen: Int = 1, nation: Boolean = false) {
  SingleDisplayCard(color = CardColor.DESERT.color, text = stringResource(R.string.desert), generation = gen, nation = nation)
}

/**
 * A jungle card
 * @param gen generation of the jungle card
 * @param nation True if this has the escalation effect on it
 */
@Preview
@Composable
fun Jungle(gen: Int = 1, nation: Boolean = false) {
  SingleDisplayCard(color = CardColor.JUNGLE.color, text = stringResource(R.string.dschungel), generation = gen, nation = nation)
}

/**
 * A coast card
 * @param gen generation of the jungle card
 */
@Preview
@Composable
fun Coast(gen: Int = 1) {
  SingleDisplayCard(color = CardColor.COAST.color, text = stringResource(R.string.coast), generation = gen)
}

/**
 * Bottom of the explore deck
 */
@Preview
@Composable
fun Finish() {
  SingleDisplayCard(color = CardColor.FINISH.color, text = stringResource(R.string.gewonnen))
}

/**
 * Card that must be added when playing with [Nation.Habsburg]
 */
@Preview
@Composable
fun Habsburg() {
  Card(
    border = BorderStroke(2.dp, Color.Black),
  ) {
    Box(
      contentAlignment = Alignment.TopCenter,
      modifier = Modifier
        .padding(horizontal = 10.dp, vertical = 20.dp)
        .fillMaxWidth()
    ) {
      Text(stringResource(R.string.habsburg))
    }
  }
}

/**
 * Empty or not revealed card
 * @param gen generation of the card
 */
@Preview
@Composable
fun Empty(gen: Int? = 2) {
  Card(
    border = BorderStroke(2.dp, Color.Black),
  ) {
    Box(contentAlignment = Alignment.TopCenter) {
      Image(
        painter = painterResource(id = R.drawable.invasoren), contentDescription = stringResource(R.string.empty), contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize()
      )
      Column {
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

/**
 * Desert and Jungle card
 */
@Preview
@Composable
fun DesertJungle() {
  DoubleDisplayCard(color1 = CardColor.DESERT.color, color2 = CardColor.JUNGLE.color, text1 = stringResource(R.string.desert), text2 = stringResource(R.string.dschungel))
}

/**
 * Desert and Swamp card
 */
@Preview
@Composable
fun DesertSwamp() {
  DoubleDisplayCard(color1 = CardColor.DESERT.color, color2 = CardColor.SWAMP.color, text1 = stringResource(R.string.desert), text2 = stringResource(R.string.sumpf))
}

/**
 * Mountain and Desert card
 */
@Preview
@Composable
fun MountainDesert() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.DESERT.color, text1 = stringResource(R.string.berg), text2 = stringResource(R.string.desert))
}

/**
 * Mountain and Jungle card
 */
@Preview
@Composable
fun MountainJungle() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.JUNGLE.color, text1 = stringResource(R.string.berg), text2 = stringResource(R.string.dschungel))
}

/**
 * Mountain and Swamp card
 */
@Preview
@Composable
fun MountainSwamp() {
  DoubleDisplayCard(color1 = CardColor.MOUNTAIN.color, color2 = CardColor.SWAMP.color, text1 = stringResource(R.string.berg), text2 = stringResource(R.string.sumpf))
}

/**
 * Swamp and Jungle card
 */
@Preview
@Composable
fun SwampJungle() {
  DoubleDisplayCard(color1 = CardColor.SWAMP.color, color2 = CardColor.JUNGLE.color, text1 = stringResource(R.string.sumpf), text2 = stringResource(R.string.dschungel))
}

/**
 * Composable that has two different biomes on it
 * @param color1 color of the first biome
 * @param color2 color of the second biome
 * @param text1 text of the first biome
 * @param text2 text of the second biome
 */
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

/**
 * A single card that is displayed
 * @param color color of the card
 * @param text text of the card
 * @param generation generation of the card
 * @param nation True if this contains the escalation effect
 */
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

/**
 * Represents the different biomes and their used color schemes
 */
enum class CardColor(val color: Color) {
  SWAMP(Color(0, 200, 200)), COAST(Color(55, 100, 178)), MOUNTAIN(Color(100, 100, 100)), DESERT(Color(196, 194, 98)), JUNGLE(Color(14, 81, 7)), FINISH(Color(250, 250, 250)),
}

/**
 * Represents a card of the invader deck
 */
enum class Card(val gen: Int) {
  SWAMP(1), JUNGLE(1), MOUNTAIN(1), DESERT(1), COAST(2), SWAMP_NATION(2), JUNGLE_NATION(2), MOUNTAIN_NATION(2), DESERT_NATION(2), MOUNTAIN_DESERT(3), SWAMP_JUNGLE(3), DESERT_JUNGLE(3), MOUNTAIN_JUNGLE(
    3
  ),
  DESERT_SWAMP(3), MOUNTAIN_SWAMP(3), FINISH(0), EMPTY(0), HABSBURG(0),
}

/**
 * Represents a adverserary of the game spirit island
 */
enum class Nation(val descId: Int, val flag: Int) {
  Brandenburg(R.string.brandenburg, R.drawable.brand_prussia_flag),
  England(R.string.england, R.drawable.england_wrinkledflag),
  Schweden(R.string.schweden, R.drawable.sweden_wrinkledflag),
  None(R.string.none, R.drawable.none_wrinkledflag),
  Russland(R.string.russland, R.drawable.russia_wrinkledflag),
  France(R.string.france, R.drawable.france_wrinkledflag),
  Habsburg(R.string.habsburg, R.drawable.habsburg_monarchy__wrinkledflag),
  Schottland(R.string.schottland, R.drawable.scotland_wrinkledflag)
}

/**
 * The configuration of a adverseray that has both nation and the level
 */
data class NationConfig(val nation: Nation, val level: Int)

/**
 * A invader deck
 * @param nationConfig config of the nation that may change the setup of the invader cards
 */
class Deck(nationConfig: NationConfig) {
  private val firstColors = mutableListOf(Card.SWAMP, Card.JUNGLE, Card.MOUNTAIN, Card.DESERT)
  private val secondColors = mutableListOf(Card.SWAMP_NATION, Card.JUNGLE_NATION, Card.DESERT_NATION, Card.COAST, Card.MOUNTAIN_NATION)
  private val thirdColors = mutableListOf(Card.MOUNTAIN_DESERT, Card.SWAMP_JUNGLE, Card.DESERT_JUNGLE, Card.MOUNTAIN_JUNGLE, Card.DESERT_SWAMP, Card.MOUNTAIN_SWAMP)

  private val deck: MutableList<Card> = emptyList<Card>().toMutableList()

  private val _removedCards = mutableListOf<Card>()

  var firstRemoved: Card
  var secondRemoved: Card
  var thirdRemoved: Card

  init {
    firstColors.shuffle()
    secondColors.shuffle()
    thirdColors.shuffle()

    firstRemoved = firstColors.removeAt(0)
    secondRemoved = secondColors.removeAt(0)
    thirdRemoved = thirdColors.removeAt(0)

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

      nationConfig.nation == Nation.Russland && nationConfig.level >= 4 -> {
        deck.addAll(firstColors)
        for ((index, card) in secondColors.withIndex()) {
          deck.add(card)
          deck.add(thirdColors[index])
        }
        deck.add(thirdColors.last())
      }

      nationConfig.nation == Nation.Habsburg -> {
        if (nationConfig.level >= 3) {
          firstColors.removeAt(0)
          deck.addAll(firstColors)
          deck.addAll(secondColors)
          deck.addAll(thirdColors)
          if (nationConfig.level >= 5) {
            deck.add(4, Card.HABSBURG)
          }
        }
      }

      else -> {
        deck.addAll(firstColors)
        deck.addAll(secondColors)
        deck.addAll(thirdColors)
      }
    }
  }

  /**
   * All the cards of the deck where the first card is the bottom of the deck
   */
  val cards get() = deck.toList().reversed()

  /**
   * All cards that where removed during the setup process
   */
  val removedCards get() = _removedCards.toMutableList()


}
