package com.example.invader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material3.IconToggleButton
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.min

/**
 * A Composable where users can randomize spirits and the board setup.
 */
@Preview(
  device = "spec:width=411dp,height=700dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun Randomizer() {
  val map = remember { mutableStateOf(emptyList<Map>() to Orientation.Up) }
  val spirits = remember { mutableStateOf(emptyList<Spirit>()) }
  val num = remember { mutableIntStateOf(2) }
  val easyOnly = remember { mutableStateOf(false) }
  val expansions = remember { mutableStateOf(listOf(Expansion.Base, Expansion.BranchAndClaw, Expansion.JaggedEarth, Expansion.FeatherAndFlame)) }

  val handleRandomizer: () -> Unit = {
    spirits.value = randomSpirits(num.intValue, easyOnly.value, expansions.value)
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
    MapView(map.value.first, map.value.second)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.width(200.dp)
    ) {
      ExpansionSelector(expansions.value) { ex -> expansions.value = ex }
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

/**
 * Preview of [ExpansionSelector]
 */
@Preview
@Composable
fun ExpansionSelectorPreview() {
  ExpansionSelector(listOf(Expansion.Base, Expansion.JaggedEarth, Expansion.BranchAndClaw, Expansion.FeatherAndFlame)) { }
}

/**
 * Component that lets you select expansions.
 * @param expansions list of expansions that are selected
 * @param onChange callback that has the new selection of expansions
 */
@Composable
fun ExpansionSelector(expansions: List<Expansion>, onChange: (List<Expansion>) -> Unit) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    itemsIndexed(Expansion.entries) { _, expansion ->
      IconToggleButton(
        checked = expansion in expansions,
        onCheckedChange = { c -> if (c) onChange(expansions + expansion) else onChange(expansions - expansion) },
        modifier = Modifier.size(50.dp)
      ) {
        Image(
          painter = painterResource(expansion.icon), stringResource(expansion.desc),
          colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { if (expansion !in expansions) setToSaturation(0F) }),
          modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .size(50.dp),
          contentScale = ContentScale.Crop
        )
      }
    }
  }
}

/**
 * Preview of [MapView]
 */
@Preview
@Composable
fun MapPreview() {
  MapView(listOf(Map.A, Map.B), Orientation.Up)
}

/**
 * A view of the maps and orientation
 * @param maps list of maps that will be displayed
 * @param orientation orientation that the maps will be displayed
 */
@Composable
fun MapView(maps: List<Map>, orientation: Orientation) {
  if (maps.size == 2) {
    when (BoardSetupTwoPlayer.entries.random()) {
      BoardSetupTwoPlayer.Flipped -> TwoPlayerMapFlipped(maps)
      BoardSetupTwoPlayer.Opposing -> TwoPlayerMapOpposing(maps)
      BoardSetupTwoPlayer.Fragment -> TwoPlayerMapFragment(maps)
      BoardSetupTwoPlayer.Standard -> TwoPlayerMapStandard(maps)
    }
  } else {
    LazyHorizontalGrid(
      rows = GridCells.Fixed(2),
      contentPadding = PaddingValues(10.dp),
      modifier = Modifier.size(242.dp, 300.dp)
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

/**
 * Container for two maps. It will have a fixed size.
 */
@Composable
fun TwoPlayerMapContainer(content: @Composable (BoxScope.() -> Unit)) {
  Box(
    modifier = Modifier
      .width(352.dp)
      .height(300.dp),
    contentAlignment = Alignment.Center,
    content = content
  )
}

/**
 * General view of two maps that can be arranged by parameters.
 * @param first first map
 * @param second second map
 * @param firstModifier modifier for the first map. changes how it will be placed
 * @param secondModifier modifier for the second map. changes how it will be placed
 */
@Composable
fun TwoPlayerMap(first: Map, second: Map, firstModifier: Modifier, secondModifier: Modifier) {
  TwoPlayerMapContainer {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = firstModifier
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
        modifier = secondModifier
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

/**
 * Preview of [TwoPlayerMapStandard]
 */
@Preview
@Composable
fun TwoPlayerMapStandardPreview() {
  TwoPlayerMapStandard(listOf(Map.A, Map.B))
}

/**
 * A view of the standard two player setup for the island
 * @param maps list of two maps
 */
@Composable
fun TwoPlayerMapStandard(maps: List<Map>) {
  TwoPlayerMap(
    maps[0], maps[1],
    Modifier
      .width(250.dp)
      .offset(x = (37).dp, y = (-64).dp),
    Modifier
      .width(250.dp)
      .offset(x = (-38).dp, y = (65).dp)
      .rotate(180F)
  )
}

/**
 * Preview of [TwoPlayerMapFlipped]
 */
@Preview
@Composable
fun TwoPlayerMapFlippedPreview() {
  TwoPlayerMapFlipped(listOf(Map.A, Map.B))
}

/**
 * A view of the flipped standard player setup for the island
 * @param maps list of two maps
 */
@Composable
fun TwoPlayerMapFlipped(maps: List<Map>) {
  TwoPlayerMap(
    maps[0], maps[1],
    Modifier
      .width(250.dp)
      .offset(x = (39).dp, y = (-67).dp)
      .rotate(180F),
    Modifier
      .width(250.dp)
      .offset(x = (-39).dp, y = (67).dp)
  )
}

/**
 * Preview of [TwoPlayerMapOpposing]
 */
@Preview
@Composable
fun TwoPlayerMapOpposingPreview() {
  TwoPlayerMapOpposing(listOf(Map.A, Map.B))
}

/**
 * A view of two maps with opposing oceans
 * @param maps list of two maps
 */
@Composable
fun TwoPlayerMapOpposing(maps: List<Map>) {
  TwoPlayerMap(
    maps[0], maps[1],
    Modifier
      .width(220.dp)
      .offset(x = (-68).dp),
    Modifier
      .width(220.dp)
      .offset(x = 67.dp, y = (-1).dp)
      .rotate(180F)
  )
}

/**
 * preview of [TwoPlayerMapFragment]
 */
@Preview
@Composable
fun TwoPlayerMapFragmentPreview() {
  TwoPlayerMapFragment(listOf(Map.A, Map.B))
}

/**
 * A view of the fragment setup as per the jagged earth rules
 * @param maps list of two maps
 */
@Composable
fun TwoPlayerMapFragment(maps: List<Map>) {
  TwoPlayerMap(
    maps[0], maps[1],
    Modifier
      .width(250.dp)
      .height(250.dp)
      .offset(x = (-65).dp, y = -(30.dp))
      .rotate(-90F),
    Modifier
      .width(250.dp)
      .offset(y = (-31).dp, x = (66).dp)
      .rotate(150F)
  )
}

/**
 * Preview of [Spirits]
 */
@Preview
@Composable
fun SpiritsPreview() {
  Spirits(listOf(Spirit.Teeth, Spirit.Erde))
}

/**
 * View of a selection of Spirits as a Grid. Will shrink and grow between 1-4 spirits.
 * @param spirits list of spirits limited by 4
 */
@Composable
fun Spirits(spirits: List<Spirit>) {
  LazyHorizontalGrid(
    rows = GridCells.Adaptive(minSize = 125.dp),
    contentPadding = PaddingValues(10.dp),
    modifier = Modifier.size(if (spirits.size <= 2) 150.dp else 300.dp, 300.dp)
  ) {
    items(spirits) { spirit ->
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .padding(10.dp)
          .size(125.dp)
      ) {
        Image(
          painter = painterResource(id = spirit.drawable),
          contentDescription = stringResource(spirit.descResource),
          modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .size(125.dp),
          contentScale = ContentScale.FillBounds,
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

/**
 * Represents a game or expansion of spirit island
 */
enum class Expansion(val desc: Int, val icon: Int) {
  Base(R.string.base, R.drawable.spirit_island),
  BranchAndClaw(R.string.branchandclaw, R.drawable.branch_and_claw),
  JaggedEarth(R.string.jaggedearth, R.drawable.jagged_earth),
  FeatherAndFlame(R.string.featherandflame, R.drawable.feather_and_flame),
  Horizons(R.string.horizons, R.drawable.horizons),
  NatureIncarnate(R.string.natureincarnate, R.drawable.nature_incarnate),
}

/**
 * Represents the complexity rating of a spirit.
 * see also [Spirit]
 */
enum class Complexity {
  Moderate,
  High,
  VeryHigh,
  Low
}

/**
 * Represents a playable spirit in spirit island
 */
enum class Spirit(val drawable: Int, val descResource: Int, val expansion: Expansion, val complexity: Complexity) {
  Erde(R.drawable.resilience, R.string.erde, Expansion.Base, Complexity.Low),
  Flamme(R.drawable.madness, R.string.schatten, Expansion.Base, Complexity.Low),
  Blitz(R.drawable.wind, R.string.blitz, Expansion.Base, Complexity.Low),
  Fluss(R.drawable.sunshine, R.string.fluss, Expansion.Base, Complexity.Low),
  Ozean(R.drawable.deeps, R.string.ozean, Expansion.Base, Complexity.High),
  Angst(R.drawable.violence, R.string.bote, Expansion.Base, Complexity.High),
  Dahan(R.drawable.tactician, R.string.donner, Expansion.Base, Complexity.Moderate),
  Wald(R.drawable.tangles, R.string.wald, Expansion.Base, Complexity.Moderate),
  Keeper(R.drawable.keeper, R.string.keeper, Expansion.BranchAndClaw, Complexity.Moderate),
  Behemoth(R.drawable.behemoth, R.string.behemoth, Expansion.NatureIncarnate, Complexity.Moderate),
  Darkness(R.drawable.darkness, R.string.darkness, Expansion.NatureIncarnate, Complexity.High),
  Downpour(R.drawable.downpour, R.string.downpour, Expansion.FeatherAndFlame, Complexity.High),
  Earthquakes(R.drawable.earthquakes, R.string.earthquakes, Expansion.NatureIncarnate, Complexity.VeryHigh),
  Eyes(R.drawable.eyes, R.string.eyes, Expansion.Horizons, Complexity.Low),
  Fangs(R.drawable.fangs, R.string.fangs, Expansion.BranchAndClaw, Complexity.Moderate),
  Finder(R.drawable.finder, R.string.finder, Expansion.FeatherAndFlame, Complexity.VeryHigh),
  Fractured(R.drawable.fractured, R.string.fractured, Expansion.JaggedEarth, Complexity.VeryHigh),
  Gaze(R.drawable.gaze, R.string.gaze, Expansion.NatureIncarnate, Complexity.High),
  Hearth(R.drawable.hearth, R.string.hearth, Expansion.NatureIncarnate, Complexity.Moderate),
  Heat(R.drawable.heat, R.string.heat, Expansion.Horizons, Complexity.Low),
  Lure(R.drawable.lure, R.string.lure, Expansion.JaggedEarth, Complexity.Moderate),
  Minds(R.drawable.minds, R.string.minds, Expansion.JaggedEarth, Complexity.Moderate),
  Memory(R.drawable.memory, R.string.memory, Expansion.JaggedEarth, Complexity.Moderate),
  Mist(R.drawable.mist, R.string.mist, Expansion.JaggedEarth, Complexity.Moderate),
  Mud(R.drawable.mud, R.string.swamp, Expansion.Horizons, Complexity.Low),
  Roots(R.drawable.roots, R.string.roots, Expansion.NatureIncarnate, Complexity.Moderate),
  Serpent(R.drawable.serpent, R.string.serpent, Expansion.FeatherAndFlame, Complexity.High),
  Starlight(R.drawable.starlight, R.string.starlight, Expansion.JaggedEarth, Complexity.VeryHigh),
  Stone(R.drawable.stone, R.string.stone, Expansion.JaggedEarth, Complexity.Moderate),
  Teeth(R.drawable.teeth, R.string.teeth, Expansion.Horizons, Complexity.Low),
  Trickster(R.drawable.trickster, R.string.trickster, Expansion.JaggedEarth, Complexity.Moderate),
  Vengeance(R.drawable.vengeance, R.string.vengeance, Expansion.JaggedEarth, Complexity.High),
  Voice(R.drawable.voice, R.string.voice, Expansion.NatureIncarnate, Complexity.High),
  Volcano(R.drawable.volcano, R.string.volcano, Expansion.JaggedEarth, Complexity.Moderate),
  Whirlwind(R.drawable.whirlwind, R.string.whirlwind, Expansion.Horizons, Complexity.Low),
  Wildfire(R.drawable.wildfire, R.string.wildfire, Expansion.FeatherAndFlame, Complexity.High),
  Wounded(R.drawable.wounded, R.string.wounded, Expansion.NatureIncarnate, Complexity.High),
}

/**
 * Represents a map in spirit island
 */
enum class Map(val drawable: Int) {
  A(R.drawable.board_a),
  B(R.drawable.board_b),
  C(R.drawable.board_c),
  D(R.drawable.board_d),
  E(R.drawable.board_e),
  F(R.drawable.board_f),
}

/**
 * TODO: create more 3-6 Player Map setups and remove orientation
 */
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

/**
 * Represents a board setup for a two player game
 */
enum class BoardSetupTwoPlayer {
  Standard,
  Flipped,
  Opposing,
  Fragment,
}

/**
 * Samples a list of random spirits based on criteria
 * @param num number of spirits returned
 * @param onlyLowComplexity True if there should only be low complexity spirits returned
 * @param expansions list of expansions that spirits need to be from
 * @return list of [Spirit] of empty list when there are not enough spirits with the criteria
 */
fun randomSpirits(num: Int, onlyLowComplexity: Boolean, expansions: List<Expansion>): List<Spirit> {
  val spirits = Spirit.entries.stream().filter { s -> s.expansion in expansions && (!onlyLowComplexity || s.complexity == Complexity.Low) }.collect(Collectors.toList())
  spirits.shuffle()
  if (spirits.size < num) {
    return emptyList()
  }
  return spirits.subList(0, num).toList()
}

/**
 * Samples a random number of maps
 * @param num number of maps that will be returned
 * @return list of [MapView] and [Orientation]
 */
fun randomMap(num: Int): Pair<MutableList<Map>, Orientation> {
  val allMaps = mutableListOf(Map.A, Map.B, Map.C, Map.D, Map.E, Map.F)
  val allOrientations = mutableListOf(Orientation.Down, Orientation.Up)

  var selection: MutableList<Map>
  do {
    allMaps.shuffle()
    selection = allMaps.subList(0, num)
  } while (num == 2 && selection.containsAll(listOf(Map.E, Map.B)) || selection.containsAll(listOf(Map.F, Map.D)))

  allOrientations.shuffle()
  return selection to allOrientations[0]
}