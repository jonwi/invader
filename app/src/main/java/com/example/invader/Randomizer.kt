package com.example.invader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun Randomizer() {
  val map = remember { mutableStateOf(emptyList<Map>() to Orientation.Up) }
  val spirits = remember { mutableStateOf(emptyList<Spirit>()) }
  val num = remember { mutableIntStateOf(2) }
  val easyOnly = remember { mutableStateOf(true) }

  val handleRandomizer: () -> Unit = {
    spirits.value = randomSpirits(num.intValue, easyOnly.value)
    map.value = randomMap(num.intValue)
  }

  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxSize()
      .padding(end = 20.dp)
  ) {
    Spirits(spirits.value)
    Map(map.value.first, map.value.second)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = easyOnly.value, onCheckedChange = { easyOnly.value = !easyOnly.value })
        Text("Nur einfache Geister")
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { num.intValue -= 1 }) {
          Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "-1")
        }
        Text(text = "${num.intValue}")
        IconButton(onClick = { num.intValue += 1 }) {
          Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "+1")
        }
      }
      Button(onClick = handleRandomizer) {
        Text("Zufall")
      }
    }
  }
}

@Preview
@Composable
fun MapPreview() {
  Map(listOf(Map.A, Map.B, Map.C), Orientation.Down)
}

@Composable
fun Map(maps: List<Map>, orientation: Orientation) {
  LazyHorizontalGrid(
    rows = GridCells.Adaptive(minSize = 100.dp),
    contentPadding = PaddingValues(10.dp),
    modifier = Modifier.size(300.dp, 300.dp)
  )
  {
    itemsIndexed(maps) { i, map ->
      Box(
        modifier = Modifier
          .size(90.dp, 90.dp)
          .rotate(if (orientation == Orientation.Down && i % 2 == 0) -180f else 0f)
          .padding(5.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          map.name,
          style = TextStyle(shadow = Shadow(MaterialTheme.colorScheme.inverseOnSurface, blurRadius = 10f)),
          fontSize = 50.sp,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Preview
@Composable
fun SpiritsPreview() {
  Spirits(listOf(Spirit.Fluss, Spirit.Erde))
}

@Composable
fun Spirits(spirits: List<Spirit>) {
  LazyHorizontalGrid(
    rows = GridCells.Adaptive(minSize = 150.dp),
    contentPadding = PaddingValues(10.dp),
    modifier = Modifier.size(350.dp, 350.dp)
  ) {
    items(spirits) { spirit ->
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .padding(10.dp)
          .width(150.dp)
      ) {
        Image(
          painter = painterResource(id = spirit.drawable),
          contentDescription = spirit.desc,
          modifier = Modifier.clip(RoundedCornerShape(30.dp)),
          contentScale = ContentScale.Fit
        )
        Text(
          spirit.desc,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          textAlign = TextAlign.Center,
          style = TextStyle(
            fontSize = 20.sp,
            shadow = Shadow(MaterialTheme.colorScheme.inversePrimary, blurRadius = 5f)
          ),
          modifier = Modifier.padding(5.dp)
        )
      }
    }
  }
}


enum class Spirit(val drawable: Int, val desc: String) {
  Erde(R.drawable.resilience, "Erde"),
  Flamme(R.drawable.madness, "Flackernde Flamme"),
  Blitz(R.drawable.wind, "Blitzschneller Blitz"),
  Fluss(R.drawable.sunshine, "Sonnengenährter Fluss"),
  Ozean(R.drawable.deeps, "Tiefer Ozean"),
  Angst(R.drawable.violence, "Angst und Schatten"),
  Dahan(R.drawable.tactician, "Taktischer Rückzug"),
  Wald(R.drawable.tangles, "Wald und noch mehr Wald"),
}

enum class Map {
  A,
  B,
  C,
  D,
}

enum class Orientation {
  Up,
  Down,
}

fun randomSpirits(num: Int, easyOnly: Boolean): List<Spirit> {
  val allSpirits = mutableListOf(Spirit.Erde, Spirit.Flamme, Spirit.Blitz, Spirit.Fluss, Spirit.Ozean, Spirit.Angst, Spirit.Dahan, Spirit.Wald)
  val easySpirits = mutableListOf(Spirit.Erde, Spirit.Flamme, Spirit.Blitz, Spirit.Fluss)

  return if (easyOnly) {
    easySpirits.shuffle()
    easySpirits.subList(0, num).toList()
  } else {
    allSpirits.shuffle()
    allSpirits.subList(0, num).toList()
  }
}

fun randomMap(num: Int): Pair<MutableList<Map>, Orientation> {
  val allMaps = mutableListOf(Map.A, Map.B, Map.C, Map.D)
  val allOrientations = mutableListOf(Orientation.Down, Orientation.Up)
  allMaps.shuffle()
  allOrientations.shuffle()
  return allMaps.subList(0, num) to allOrientations[0]
}