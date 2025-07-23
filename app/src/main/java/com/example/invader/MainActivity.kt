package com.example.invader

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.invader.ui.theme.AppTheme
import kotlin.random.Random

/**
 * Main entry point of the app
 */
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      AppTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
        ) {
          val nationConfig = remember { mutableStateOf(NationConfig(Nation.None, 1)) }
          val screen = remember { mutableStateOf(Screens.Invaders) }
          val deck = remember { mutableStateOf(Deck(nationConfig.value)) }
          val discardCards = remember { mutableStateListOf<Card>() }
          val immigrationCards = remember { mutableStateListOf<Card>() }
          val russiaHiddenCards = remember { mutableStateListOf<Card>() }
          val ravageCards = remember { mutableStateListOf<Card>() }
          val exploreCards = remember { mutableStateListOf(*deck.value.cards.toTypedArray()) }
          val buildingCards = remember { mutableStateListOf<Card>() }
          val revealed = remember { mutableStateOf(false) }
          val russiaRevealed = remember { mutableStateOf(false) }

          val activity = LocalContext.current as Activity
          activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

          val exploreClick: () -> Unit = {
            if (!revealed.value) {
              revealed.value = true
            } else {
              if (!exploreCards.isEmpty()) {
                if (nationConfig.value.nation == Nation.England && nationConfig.value.level >= 4 || nationConfig.value.nation == Nation.England && nationConfig.value.level == 3 && (immigrationCards.isEmpty() || immigrationCards.first().gen == 1)) {
                  discardCards.addAll(immigrationCards)
                  immigrationCards.removeAll(immigrationCards)
                  immigrationCards.addAll(ravageCards)
                } else {
                  discardCards.addAll(ravageCards.filter { c -> c != Card.HABSBURG_MINING })
                }
                ravageCards.removeAll(ravageCards.filter { c -> c != Card.HABSBURG_MINING })
                ravageCards.addAll(buildingCards)
                buildingCards.removeAll(buildingCards)
                buildingCards.add(exploreCards.last())
                exploreCards.removeAt(exploreCards.lastIndex)
                revealed.value = false
              }
            }
          }

          val onDropHandler = { card: Card, list: MutableList<Card> ->
            ravageCards.remove(card)
            if (exploreCards.remove(card)) {
              revealed.value = false
            }
            immigrationCards.remove(card)
            discardCards.remove(card)
            buildingCards.remove(card)

            if (russiaHiddenCards.remove(card)) {
              russiaRevealed.value = false
            }

            list.add(card)

            if (immigrationCards.any { c -> c.gen == 2 } && nationConfig.value.nation == Nation.England && nationConfig.value.level == 3) {
              discardCards.addAll(immigrationCards)
              immigrationCards.removeAll(immigrationCards)
            }
          }

          val resetDeck = {
            exploreCards.removeAll(exploreCards)
            buildingCards.removeAll(buildingCards)
            ravageCards.removeAll(ravageCards)
            immigrationCards.removeAll(immigrationCards)
            discardCards.removeAll(discardCards)
            russiaHiddenCards.removeAll(russiaHiddenCards)

            deck.value = Deck(nationConfig.value)
            exploreCards.addAll(deck.value.cards)
            if (nationConfig.value.nation == Nation.Schweden && nationConfig.value.level >= 4) discardCards.add(exploreCards.removeAt(exploreCards.lastIndex))
            if (nationConfig.value.nation == Nation.Russland && nationConfig.value.level >= 5) {
              russiaHiddenCards.addAll(listOf(deck.value.thirdRemoved, deck.value.secondRemoved))
            }
            revealed.value = false
            russiaRevealed.value = false
          }

          val setNationConfig = { nc: NationConfig ->
            nationConfig.value = nc
          }

          val visionsOfAShiftingFutureHandler = {
            if (Random.nextFloat() < .5 && exploreCards.size >= 2) {
              val nextCard = exploreCards[exploreCards.size - 1]
              exploreCards[exploreCards.size - 1] = exploreCards[exploreCards.size - 2]
              exploreCards[exploreCards.size - 2] = nextCard
            }
          }

          val risingInterestInTheIslandHandler: () -> Unit = {
            when (nationConfig.value.nation) {
              Nation.Schottland -> {
                if (nationConfig.value.level >= 2) {
                  // Scotland order: 1-1-2-2-X-C-2-3-3-3-3-(3)
                  val next = exploreCards[exploreCards.size - 1]
                  if (next == Card.COAST) { // do not remove Coast
                    exploreCards.removeAt(exploreCards.size - 2)
                  } else if (next.gen == 1) { // do not remove X when X == 1
                    exploreCards.removeAt(exploreCards.size - 3)
                  } else if (next.gen == 3 && exploreCards.any { c -> c.gen == 2 }) { // do not remove X when X == 3
                    exploreCards.removeAt(exploreCards.size - 3)
                  } else {
                    exploreCards.removeAt(exploreCards.size - 1)
                  }
                } else {
                  exploreCards.removeAt(exploreCards.size - 1)
                }
              }

              Nation.HabsburgMining -> {
                if (exploreCards[exploreCards.size - 1] == Card.HABSBURG_MINING) {
                  exploreCards.removeAt(exploreCards.size - 2)
                } else {
                  exploreCards.removeAt(exploreCards.size - 1)
                }
              }

              Nation.Brandenburg -> {
                val next = exploreCards[exploreCards.size - 1]
                if (next.gen == 3 && exploreCards.any { c -> c.gen == 2 }) {
                  exploreCards.removeAt(exploreCards.size - 2)
                } else {
                  exploreCards.removeAt(exploreCards.size - 1)
                }
              }

              else -> exploreCards.removeAt(exploreCards.size - 1)
            }
          }

          val hardWorkingSettlersHandler = {
            for (card in exploreCards) {
              if (card.gen == 2) {
                exploreCards.remove(card)
                break
              }
            }
            for (card in exploreCards) {
              if (card.gen == 3) {
                exploreCards.remove(card)
                break
              }
            }
          }

          Scaffold(bottomBar = {
            BottomBar(screen.value) { s -> screen.value = s }
          }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
              when (screen.value) {
                Screens.Difficulty -> Difficulty()
                Screens.Randomizer -> Randomizer()
                Screens.Invaders -> Invader(
                  resetDeck = resetDeck,
                  discardCard = discardCards.toList(),
                  ravageCard = ravageCards.toList(),
                  exploreCard = exploreCards.toList(),
                  buildingCard = buildingCards.toList(),
                  immigrationCard = immigrationCards.toList(),
                  russiaHiddenCards = russiaHiddenCards.toList(),
                  exploreClick = exploreClick,
                  revealed = revealed.value,
                  nationConfig = nationConfig.value,
                  setNationConfig = setNationConfig,
                  addDiscardCard = { card -> onDropHandler(card, discardCards) },
                  addRavageCard = { card ->
                    onDropHandler(card, ravageCards)
                  },
                  addBuildingCard = { card ->
                    onDropHandler(card, buildingCards)
                  },
                  addImmigrationCard = { card ->
                    onDropHandler(card, immigrationCards)
                  },
                  addExploreCard = { card ->
                    onDropHandler(card, exploreCards)
                    revealed.value = true
                  },
                  russiaRevealed = russiaRevealed.value,
                  russiaOnClick = {
                    russiaRevealed.value = !russiaRevealed.value
                  },
                  fracturedHandler = { card ->
                    discardCards.remove(card)
                    val first = exploreCards.removeAt(exploreCards.size - 1)
                    discardCards.add(0, first)
                    exploreCards.add(card)
                  },
                  discardCards = discardCards,
                  hardWorkingSettlersHandler = hardWorkingSettlersHandler,
                  risingInterestInTheIslandHandler = risingInterestInTheIslandHandler,
                  visionsOfAShiftingFutureHandler = visionsOfAShiftingFutureHandler,
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Represents a screen of the app
 */
enum class Screens {
  Invaders, Randomizer, Difficulty,
}

/**
 * Preview of [BottomBar]
 */
@Composable
@Preview
fun BottomBarPreview() {
  BottomBar(Screens.Difficulty) {}
}

/**
 * Navigation bar of the App
 *
 * @param screen current screen
 * @param onChange change of the current screen
 */
@Composable
fun BottomBar(screen: Screens, onChange: (Screens) -> Unit) {
  BottomAppBar(
    containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary, modifier = Modifier.height(40.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth()
    ) {
      IconButton(
        onClick = { onChange(Screens.Randomizer) },
        modifier = Modifier
          .weight(1f, true)
          .background(if (screen == Screens.Randomizer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
      ) {
        Icon(
          painterResource(R.drawable.dice),
          contentDescription = "Randomize",
          tint = Color.Unspecified,
        )
      }
      VerticalDivider(thickness = 1.dp, color = Color.Black)

      IconButton(
        onClick = { onChange(Screens.Difficulty) },
        modifier = Modifier
          .weight(1f, true)
          .background(if (screen == Screens.Difficulty) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
      ) {
        Icon(
          painter = painterResource(R.drawable.difficulty),
          contentDescription = "Randomize",
          tint = if (screen == Screens.Difficulty) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.primary
        )
      }
      VerticalDivider(thickness = 1.dp, color = Color.Black)

      IconButton(
        onClick = { onChange(Screens.Invaders) },
        modifier = Modifier
          .weight(1f, true)
          .background(if (screen == Screens.Invaders) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
      ) {
        Icon(
          Icons.Filled.Home, contentDescription = "Invaders", tint = if (screen == Screens.Invaders) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}