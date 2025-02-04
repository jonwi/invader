package com.example.invader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

@Preview
@Composable
fun Randomizer() {
  val map = remember { mutableStateOf(emptyList<Map>() to Orientation.Up) }
  val spirits = remember { mutableStateOf(emptyList<Spirit>()) }
  val num = remember { mutableIntStateOf(2) }
  val easyOnly = remember { mutableStateOf(false) }

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
        Text(stringResource(R.string.easy))
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { num.intValue = max(1, num.intValue - 1) }) {
          Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "-1")
        }
        Text(text = "${num.intValue}")
        IconButton(onClick = { num.intValue = min(num.intValue + 1, 4) }) {
          Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "+1")
        }
      }
      Button(onClick = handleRandomizer) {
        Text(stringResource(R.string.randomize))
      }
    }
  }
}

@Preview
@Composable
fun MapPreview() {
  Map(listOf(Map.A, Map.B), Orientation.Up)
}

@Composable
fun Map(maps: List<Map>, orientation: Orientation) {
  if (maps.size == 2) {
    when (BoardSetup.entries.random()) {
      BoardSetup.Flipped -> TwoPlayerMapFlipped(maps)
      BoardSetup.Opposing -> TwoPlayerMapOpposing(maps)
      BoardSetup.Fragment -> TwoPlayerMapFragment(maps)
      BoardSetup.Standard -> TwoPlayerMapStandard(maps)
    }
  } else {
    LazyHorizontalGrid(
      rows = GridCells.Fixed(2),
      contentPadding = PaddingValues(10.dp),
      modifier = Modifier.size(300.dp, 300.dp)
    )
    {
      itemsIndexed(maps) { i, map ->
        Box(
          modifier = Modifier
            .width(100.dp)
            .height(50.dp)
            .rotate(orientation.degree(i)),
          contentAlignment = Alignment.Center
        ) {
          Image(painterResource(map.drawable), "")
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
}

@Preview
@Composable
fun TwoPlayerMapStandardPreview() {
  TwoPlayerMapStandard(listOf(Map.A, Map.B))
}

@Composable
fun TwoPlayerMapStandard(maps: List<Map>) {
  val first = maps[0]
  val second = maps[1]
  Box(modifier = Modifier
    .width(300.dp)
    .height(200.dp), contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .width(196.dp)
        .height(182.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .width(150.dp)
      ) {
        Image(painterResource(first.drawable), "")
        Text(
          first.name,
          style = TextStyle(shadow = Shadow(MaterialTheme.colorScheme.inverseOnSurface, blurRadius = 10f)),
          fontSize = 50.sp,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.BottomStart)
          .rotate(180F)
          .width(150.dp)
      ) {
        Image(painterResource(second.drawable), "")
        Text(
          second.name,
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
fun TwoPlayerMapFlippedPreview() {
  TwoPlayerMapFlipped(listOf(Map.A, Map.B))
}

@Composable
fun TwoPlayerMapFlipped(maps: List<Map>) {
  val first = maps[0]
  val second = maps[1]
  Box(modifier = Modifier
    .width(300.dp)
    .height(200.dp), contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .width(197.dp)
        .height(184.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .width(150.dp)
          .rotate(180F)
      ) {
        Image(painterResource(first.drawable), "")
        Text(
          first.name,
          style = TextStyle(shadow = Shadow(MaterialTheme.colorScheme.inverseOnSurface, blurRadius = 10f)),
          fontSize = 50.sp,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.BottomStart)
          .width(150.dp)
      ) {
        Image(painterResource(second.drawable), "")
        Text(
          second.name,
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
fun TwoPlayerMapOpposingPreview() {
  TwoPlayerMapOpposing(listOf(Map.A, Map.B))
}

@Composable
fun TwoPlayerMapOpposing(maps: List<Map>) {
  val first = maps[0]
  val second = maps[1]
  Box(modifier = Modifier
    .width(300.dp)
    .height(200.dp), contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .width(242.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.TopStart)
          .width(150.dp)
      ) {
        Image(painterResource(first.drawable), "")
        Text(
          first.name,
          style = TextStyle(shadow = Shadow(MaterialTheme.colorScheme.inverseOnSurface, blurRadius = 10f)),
          fontSize = 50.sp,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .width(150.dp)
          .rotate(180F)
          .offset(y = 1.dp)
      ) {
        Image(painterResource(second.drawable), "")
        Text(
          second.name,
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
fun TwoPlayerMapFragmentPreview() {
  TwoPlayerMapFragment(listOf(Map.A, Map.B))
}

@Composable
fun TwoPlayerMapFragment(maps: List<Map>) {
  val first = maps[0]
  val second = maps[1]
  Box(modifier = Modifier
    .height(200.dp)
    .width(300.dp), contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .width(196.dp)
        .height(185.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.TopStart)
          .width(150.dp)
          .height(130.dp)
          .rotate(-90F)
          .offset(y = -(30.dp))
      ) {
        Image(painterResource(first.drawable), "")
        Text(
          first.name,
          style = TextStyle(shadow = Shadow(MaterialTheme.colorScheme.inverseOnSurface, blurRadius = 10f)),
          fontSize = 50.sp,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .width(150.dp)
          .offset(y = 12.dp, x = 3.dp)
          .rotate(150F)
      ) {
        Image(painterResource(second.drawable), "")
        Text(
          second.name,
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
          contentDescription = stringResource(spirit.descResource),
          modifier = Modifier.clip(RoundedCornerShape(30.dp)),
          contentScale = ContentScale.Fit
        )
        Text(
          stringResource(spirit.descResource),
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


enum class Spirit(val drawable: Int, val descResource: Int) {
  Erde(R.drawable.resilience, R.string.erde),
  Flamme(R.drawable.madness, R.string.schatten),
  Blitz(R.drawable.wind, R.string.blitz),
  Fluss(R.drawable.sunshine, R.string.fluss),
  Ozean(R.drawable.deeps, R.string.ozean),
  Angst(R.drawable.violence, R.string.bote),
  Dahan(R.drawable.tactician, R.string.donner),
  Wald(R.drawable.tangles, R.string.wald),
}

enum class Map(val drawable: Int) {
  A(R.drawable.board_a),
  B(R.drawable.board_b),
  C(R.drawable.board_c),
  D(R.drawable.board_d),
  E(R.drawable.board_e),
  F(R.drawable.board_f),
}

enum class Orientation {
  Up,
  Down;

  fun degree(index: Int): Float {
    return if (this == Up) {
      if (index % 2 == 0)
        0f
      else
        -180f
    } else {
      if (index % 2 == 0)
        -180f
      else
        0f
    }
  }
}

enum class BoardSetup {
  Standard,
  Flipped,
  Opposing,
  Fragment,
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
  val allMaps = mutableListOf(Map.A, Map.B, Map.C, Map.D, Map.E, Map.F)
  val allOrientations = mutableListOf(Orientation.Down, Orientation.Up)

  var selection = allMaps.subList(0, num)
  do {
    allMaps.shuffle()
    selection = allMaps.subList(0, num)
  } while (num == 2 && selection.containsAll(listOf(Map.E, Map.B)) || selection.containsAll(listOf(Map.F, Map.D)))

  allOrientations.shuffle()
  return selection to allOrientations[0]
}