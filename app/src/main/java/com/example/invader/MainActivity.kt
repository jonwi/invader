package com.example.invader

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      AppTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
        ) {
          val nationConfig = remember { mutableStateOf(NationConfig(Nation.Brandenburg, 6)) }
          val screen = remember { mutableStateOf(Screens.Invaders) }
          val deck = remember { mutableStateOf(Deck(nationConfig.value)) }
          val discardCard = remember { mutableStateOf(Card.EMPTY) }
          val ravageCard = remember { mutableStateOf(Card.EMPTY) }
          val exploreCard = remember { mutableStateOf(deck.value.next()) }
          val buildingCard = remember { mutableStateOf(Card.EMPTY) }
          val revealded = remember { mutableStateOf(false) }

          val activity = LocalContext.current as Activity
          activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

          val exploreClick: () -> Unit = {
            if (!revealded.value) {
              revealded.value = true
            } else {
              if (deck.value.size > 0) {
                discardCard.value = ravageCard.value
                ravageCard.value = buildingCard.value
                buildingCard.value = exploreCard.value
                exploreCard.value = deck.value.next()
                revealded.value = false
              }
            }
          }

          val resetDeck = {
            deck.value = Deck(nationConfig.value)
            exploreCard.value = deck.value.next()
            discardCard.value = Card.EMPTY
            ravageCard.value = Card.EMPTY
            buildingCard.value = Card.EMPTY
            revealded.value = false
          }

          val setNationConfig = { nc: NationConfig ->
            nationConfig.value = nc
          }

          Scaffold(
            bottomBar = {
              BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(40.dp)
              ) {
                Row(
                ) {
                  IconButton(
                    onClick = { screen.value = Screens.Randomizer },
                    modifier = Modifier
                      .weight(1f, true)
                      .background(if (screen.value == Screens.Randomizer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
                  ) {
                    Icon(
                      Icons.Filled.Refresh,
                      contentDescription = "Randomize",
                      tint = if (screen.value == Screens.Randomizer) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.primary
                    )
                  }
                  IconButton(
                    onClick = { screen.value = Screens.Invaders },
                    modifier = Modifier
                      .weight(1f, true)
                      .background(if (screen.value == Screens.Invaders) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
                  ) {
                    Icon(
                      Icons.Filled.Home,
                      contentDescription = "Invaders",
                      tint = if (screen.value == Screens.Invaders) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.primary
                    )
                  }
                }
              }
            }
          ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
              when (screen.value) {
                Screens.Randomizer -> Randomizer()
                Screens.Invaders -> Invader(
                  resetDeck = resetDeck,
                  buildingCard = buildingCard.value,
                  exploreCard = exploreCard.value,
                  discardCard = discardCard.value,
                  ravageCard = ravageCard.value,
                  exploreClick = exploreClick,
                  counter = deck.value.size,
                  revealed = revealded.value,
                  nationConfig = nationConfig.value,
                  setNationConfig = setNationConfig
                )
              }
            }
          }
        }
      }
    }
  }
}

enum class Screens {
  Invaders,
  Randomizer,
}