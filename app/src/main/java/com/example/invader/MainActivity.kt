package com.example.invader

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableIntStateOf
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
          val screen = remember { mutableStateOf(Screens.Invaders) }
          val deck = remember { mutableStateOf(Deck()) }
          val counter = remember { mutableIntStateOf(12) }
          val discardCard = remember { mutableStateOf(Card.EMPTY to 0) }
          val ravageCard = remember { mutableStateOf(Card.EMPTY to 0) }
          val exploreCard = remember { mutableStateOf(Card.EMPTY to 1) }
          val buildingCard = remember { mutableStateOf(Card.EMPTY to 0) }

          val activity = LocalContext.current as Activity
          activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

          val exploreClick: () -> Unit = {
            if (exploreCard.value.first == Card.EMPTY) {
              exploreCard.value = deck.value.next()
              counter.intValue -= 1
            } else if (exploreCard.value.first != Card.FINISH) {
              discardCard.value = ravageCard.value
              ravageCard.value = buildingCard.value
              buildingCard.value = exploreCard.value
              if (counter.intValue == 0)
                exploreCard.value = Card.FINISH to 0
              else
                exploreCard.value = Card.EMPTY to deck.value.nextGen()
            }
          }
          val resetDeck = {
            deck.value = Deck()
            exploreCard.value = Card.EMPTY to 1
            discardCard.value = Card.EMPTY to 0
            ravageCard.value = Card.EMPTY to 0
            buildingCard.value = Card.EMPTY to 0
            counter.intValue = 12
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
            Column(modifier = Modifier.padding(innerPadding)) {
              when (screen.value) {
                Screens.Randomizer -> Randomizer()
                Screens.Invaders -> Invader(
                  resetDeck = resetDeck,
                  buildingCard = buildingCard.value,
                  exploreCard = exploreCard.value,
                  discardCard = discardCard.value,
                  ravageCard = ravageCard.value,
                  exploreClick = exploreClick,
                  counter = counter.intValue
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