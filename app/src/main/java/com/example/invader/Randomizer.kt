package com.example.invader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.min


/**
 * View Model of Randomize View
 */
class RandomizerViewModel : ViewModel() {

  /**
   * randomized list of maps and their orientation
   */
  var map by mutableStateOf(emptyList<Map>())

  /**
   * randomized setup
   */
  var setup by mutableStateOf(IslandLayout.StandardTwoPlayers)

  /**
   * List of randomized spirits
   */
  var spirits by mutableStateOf(emptyList<Spirit>())

  /**
   * Amount of spirits that are used
   */
  var num by mutableIntStateOf(2)

  /**
   * If only low complexity spirits will be used
   */
  var easyOnly by mutableStateOf(false)

  /**
   * List of active expansions for randomization
   */
  var expansions by mutableStateOf(listOf(Expansion.Base, Expansion.BranchAndClaw, Expansion.JaggedEarth, Expansion.FeatherAndFlame, Expansion.NatureIncarnate, Expansion.Horizons))

  /**
   * If no low Complexity spitits will be used
   */
  var lowComplexity by mutableStateOf(true)


  /**
   * Randomizes spirits map and setup
   */
  fun randomize() {
    spirits = randomSpirits(num, easyOnly, expansions, lowComplexity)
    map = randomMap(num)
    setup = IslandLayout.entries.stream().filter { l -> l.players == num }.collect(Collectors.toList()).random()
  }

  /**
   * Inceases the number of players by one up to a maximum of 6
   */
  fun increaseNum() {
    num = min(num + 1, 6)
  }

  /**
   * Decreases the number of players by one to a minimum of 1
   */
  fun decreaseNum() {
    num = max(1, num - 1)
  }

}

/**
 * A Composable where users can randomize spirits and the board setup.
 */
@Preview(
  device = "spec:width=411dp,height=700dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun Randomizer(viewModel: RandomizerViewModel = viewModel()) {

  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxSize()
      .padding(end = 20.dp)
  ) {

    Spirits(viewModel.spirits)

    MapView(viewModel.map, viewModel.setup)

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.width(210.dp)
    ) {

      ExpansionSelector(viewModel.expansions) { ex -> viewModel.expansions = ex }

      Column() {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = viewModel.easyOnly, onCheckedChange = { viewModel.easyOnly = !viewModel.easyOnly })
          Text(stringResource(R.string.easy))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = viewModel.lowComplexity, onCheckedChange = { viewModel.lowComplexity = !viewModel.lowComplexity })
          Text(stringResource(R.string.lowComplexity))
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { viewModel.decreaseNum() }) {
          Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "-1")
        }
        Text(text = "${viewModel.num}")
        IconButton(onClick = { viewModel.increaseNum() }) {
          Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "+1")
        }
        Button(onClick = { viewModel.randomize() }) {
          Text(stringResource(R.string.randomize))
        }
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
  MapView(listOf(Map.A, Map.B), IslandLayout.Flipped)
}

/**
 * A view of the maps and orientation
 * @param maps list of maps that will be displayed
 * @param setup setup of the boards
 */
@Composable
fun MapView(maps: List<Map>, setup: IslandLayout) {
  Column(
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    if (maps.isNotEmpty()) {
      Text(stringResource(setup.desc))
    }

    when (maps.size) {
      2 -> {
        when (setup) {
          IslandLayout.Flipped -> TwoPlayerMapFlipped(maps)
          IslandLayout.Opposing -> TwoPlayerMapOpposing(maps)
          IslandLayout.Fragment -> TwoPlayerMapFragment(maps)
          else -> TwoPlayerMapStandard(maps)
        }
      }

      3 -> {
        when (setup) {
          IslandLayout.Sunrise -> ThreePlayerSunrise(maps)
          IslandLayout.Coastline -> ThreePlayerCostline(maps)
          else -> ThreePlayerStandard(maps)
        }
      }

      4 -> {
        when (setup) {
          IslandLayout.Leaf -> FourPlayerMapLeaf(maps)
          IslandLayout.Snake -> FourPlayerMapSnake(maps)
          else -> FourPlayerMapStandard(maps)
        }
      }

      5 -> {
        when (setup) {
          IslandLayout.Claw -> FivePlayerMapClaw(maps)
          IslandLayout.Peninsula -> FivePlayerMapPeninsula(maps)
          IslandLayout.Snail -> FivePlayerMapSnail(maps)
          IslandLayout.V -> FivePlayerMapV(maps)
          else -> FivePlayerMapCrab(maps)
        }
      }

      6 -> {
        when (setup) {
          IslandLayout.Caldera -> SixPlayerMapCaldera(maps)
          IslandLayout.Flower -> SixPlayerMapFlower(maps)
          IslandLayout.Star -> SixPlayerMapStar(maps)
          else -> SixPlayerMapTwoCenters(maps)
        }
      }

      else -> {
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
                .height(50.dp),
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
  }
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
 * Represents a board setup
 * @param players number of players that this setup is used for
 */
enum class IslandLayout(val players: Int, val desc: Int) {
  StandardTwoPlayers(2, R.string.standard),
  Flipped(2, R.string.flipped),
  Opposing(2, R.string.opposing),
  Fragment(2, R.string.fragment),
  StandardThreePlayers(3, R.string.standard),
  Coastline(3, R.string.coastline),
  Sunrise(3, R.string.sunrise),
  StandardFourPlayers(4, R.string.standard),
  Leaf(4, R.string.leaf),
  Snake(4, R.string.snake),
  Crab(5, R.string.crab),
  Claw(5, R.string.claw),
  Peninsula(5, R.string.peninsula),
  Snail(5, R.string.snail),
  V(5, R.string.v),
  TwoCenters(6, R.string.two_centers),
  Caldera(6, R.string.caldera),
  Flower(6, R.string.flower),
  Star(6, R.string.star),
}

/**
 * Samples a list of random spirits based on criteria
 * @param num number of spirits returned
 * @param onlyLowComplexity True if there should only be low complexity spirits returned
 * @param expansions list of expansions that spirits need to be from
 * @param lowComplexity True if there should be no low complexity spirits
 * @return list of [Spirit] of empty list when there are not enough spirits with the criteria
 */
fun randomSpirits(num: Int, onlyLowComplexity: Boolean, expansions: List<Expansion>, lowComplexity: Boolean): List<Spirit> {
  val spirits = Spirit.entries.stream()
    .filter { s -> s.expansion in expansions && (!onlyLowComplexity || s.complexity == Complexity.Low) }
    .filter { s -> lowComplexity || s.complexity != Complexity.Low }
    .collect(Collectors.toList())
  spirits.shuffle()
  if (spirits.size < num) {
    return emptyList()
  }
  return spirits.subList(0, num).toList()
}

/**
 * Samples a random number of maps
 * This will not return map B and E or F and D if possible according to the rules.
 *
 * @param num number of maps that will be returned
 * @return list of [Map]
 */
fun randomMap(num: Int): MutableList<Map> {
  val allMaps = mutableListOf(Map.A, Map.B, Map.C, Map.D, Map.E, Map.F)
  val standardMaps = mutableListOf(Map.A, Map.B, Map.C, Map.D)

  if (num == 6) {
    return allMaps
  }
  if (num == 5) {
    allMaps.shuffle()
    val selection: MutableList<Map> = allMaps.subList(0, num)
    return selection
  }

  standardMaps.shuffle()
  val selection = standardMaps.subList(0, num)
  if (selection.contains(Map.B)) {
    if (Math.random() < .5) {
      selection[selection.indexOf(Map.B)] = Map.E
    }
  }
  if (selection.contains(Map.D)) {
    if (Math.random() < .5) {
      selection[selection.indexOf(Map.D)] = Map.F
    }
  }
  return selection
}