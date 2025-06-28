package com.example.invader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.min

/**
 * View model for difficulty randomization
 */
class DifficultyViewModel : ViewModel() {
  val initialLow = 1
  val initialHigh = 12
  var randomized by mutableStateOf(randomizeSetup(initialLow, initialHigh, usesNation = true, usesScenarios = true))
}

/**
 * Composable that lets the user randomize the setup of the game by selecting a range of difficulty and of nation and/or scenarios should be used.
 */
@Composable
@Preview
fun Difficulty(viewModel: DifficultyViewModel = viewModel()) {

  Row(
    modifier = Modifier
      .padding(30.dp)
      .fillMaxWidth(), horizontalArrangement = Arrangement.End
  ) {
    Setup(viewModel.randomized)
    Spacer(modifier = Modifier.width(30.dp))
    DifficultySelector(viewModel.initialLow, viewModel.initialHigh, initNation = true, initScenario = true) { low, high, usesNation, usesScenario ->
      viewModel.randomized = randomizeSetup(low, high, usesNation, usesScenario)
    }
  }

}

/**
 * View of a setup
 * @param setup triple of the current nation config, the scenario and the difficulty
 */
@Composable
fun Setup(setup: Triple<NationConfig?, Scenario?, Int?>) {
  Column {
    if (setup.first != null) {
      Text(stringResource(R.string.nation) + ": " + stringResource(setup.first!!.nation.descId) + " " + setup.first!!.level)
      Image(painterResource(setup.first!!.nation.flag), stringResource(setup.first!!.nation.descId))
    }
    if (setup.second != null) {
      Text(stringResource(R.string.scenario) + ": " + stringResource(setup.second!!.desc))
    }
    if (setup.third != null) {
      Text(stringResource(R.string.difficulty) + ": ${setup.third!!}")
    }
  }
}

/**
 * Preview of [DifficultySelector]
 */
@Preview
@Composable
fun DifficultySelectorPreview() {
  DifficultySelector(1, 2, initNation = false, initScenario = true, onSelection = { _, _, _, _ -> })
}

/**
 * Selector of difficulty range
 *
 * @param initialLow initial value for the min value of the range
 * @param initialHigh initial value of the max value of the range
 * @param initNation True if nation will be used
 * @param initScenario True if scenario will be used
 * @param onSelection on change of the range or nation/scenario values
 */
@Composable
fun DifficultySelector(initialLow: Int, initialHigh: Int, initNation: Boolean, initScenario: Boolean, onSelection: (Int, Int, Boolean, Boolean) -> Unit) {
  val low = remember { mutableIntStateOf(initialLow) }
  val high = remember { mutableIntStateOf(initialHigh) }
  val usesNation = remember { mutableStateOf(initNation) }
  val usesScenario = remember { mutableStateOf(initScenario) }

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(stringResource(R.string.difficulty) + ":")
    Row {
      Column {
        Text(stringResource(R.string.von))
        IntSelector(0, high.intValue, low.intValue) { v -> low.intValue = v }
      }
      Spacer(modifier = Modifier.width(20.dp))

      Column {
        Text(stringResource(R.string.bis))
        IntSelector(low.intValue, 15, high.intValue) { v -> high.intValue = v }
      }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.nation))
        Checkbox(usesNation.value, onCheckedChange = { v -> usesNation.value = v })
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.scenario))
        Checkbox(usesScenario.value, onCheckedChange = { v -> usesScenario.value = v })
      }
    }
    Button(onClick = { onSelection(low.intValue, high.intValue, usesNation.value, usesScenario.value) }) {
      Text(stringResource(R.string.randomize))
    }
  }
}

/**
 * Integer selector
 *
 * @param min min number that can be selected
 * @param max max number that can be selected
 * @param value current value that is selected
 * @param onChange callback when number is changed
 */
@Composable
fun IntSelector(min: Int, max: Int, value: Int, onChange: (Int) -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    IconButton(onClick = { onChange(max(min, value - 1)) }) {
      Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "-1")
    }
    Text(text = "$value")
    IconButton(onClick = { onChange(min(max, value + 1)) }) {
      Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "+1")
    }
  }
}

/**
 * Creates a random Configuration of a game consisting of nation and scenario or either one of
 * them within the requested difficulty bounds.
 * It may not return anything if there is no combination of nation and scenario that is within the difficulty.
 *
 * @param lowerDifficulty result must have at least this difficulty
 * @param upperDifficulty result will have at most this difficulty
 * @param usesNation if it should return a nation
 * @param usesScenarios if it should return a scenario
 * @return NationConfig and Scenario with Difficulty if there is one within the bounds. Parts will be null if not requested/not existing
 */
private fun randomizeSetup(lowerDifficulty: Int, upperDifficulty: Int, usesNation: Boolean, usesScenarios: Boolean): Triple<NationConfig?, Scenario?, Int?> {
  if (usesNation && usesScenarios) {
    val scenario = scenariosByDifficulty(max(0, lowerDifficulty - 11), upperDifficulty).random()
    val nation = nationRandomize(lowerDifficulty - scenario.difficulty, upperDifficulty - scenario.difficulty)
    return Triple(nation?.first, scenario, scenario.difficulty + (nation?.second ?: 0))
  } else {
    if (usesNation) {
      val nation = nationRandomize(lowerDifficulty, upperDifficulty)
      return Triple(nation?.first, null, nation?.second)
    }
    if (usesScenarios) {
      val scenario = randomizeScenario(lowerDifficulty, upperDifficulty)
      return Triple(null, scenario, scenario?.difficulty)
    }
  }
  return Triple(null, null, null)
}

/**
 * @return random Scenario if there is one within the lower and upper bounds of difficulty, else null
 */
private fun randomizeScenario(lowerDifficulty: Int, upperDifficulty: Int): Scenario? {
  return Scenario.entries.stream().filter { s -> s.difficulty in lowerDifficulty..upperDifficulty }.collect(Collectors.toList()).randomOrNull()
}

/**
 * @return List of Scenarios that are within the lower and upper bound of difficulty, else an empty list
 */
private fun scenariosByDifficulty(lowerDifficulty: Int, upperDifficulty: Int): List<Scenario> {
  return Scenario.entries.stream().filter { s -> s.difficulty in lowerDifficulty..upperDifficulty }.collect(Collectors.toList())
}

/**
 * Represents a Scenario of Spirit Island. Comes with description as string resource and difficulty as from:
 * https://docs.google.com/spreadsheets/d/13YKGn8nBPD3b84pazu4iedj99wowHuXkC_afTG1wf4U/edit?gid=906893594#gid=906893594
 */
enum class Scenario(val desc: Int, val difficulty: Int) {
  DiversityOfSpirits(R.string.diversity_of_spirits_je, 0),
  Blitz(R.string.blitzkrieg, 0),
  GuardTheIslesHeart(R.string.guard_the_isle_s_heart, 0),
  PowersLongForgotten(R.string.powers_long_forgotten_bc, 1),
  ElementalInvocation(R.string.elemental_invocation_bc, 1),
  SecondWave(R.string.second_wave_bc, 1),
  DespicableTheft(R.string.despicable_theft_je, 2),
  VariedTerrains(R.string.varied_terrains_je, 2),
  WardTheShores(R.string.ward_the_shores_bc, 2),
  TheGreatRiver(R.string.the_great_river_je, 3),
  RitualsOfDestroyingFlame(R.string.rituals_of_destroying_flame_bc, 3),
  RitualsOfTerror(R.string.rituals_of_terror, 3),
  DahanInsurrection(R.string.dahan_insurrection, 4),
}

/**
 * @return NationConfig and Difficulty of that or null if there is none in bounds
 */
private fun nationRandomize(lowerDifficulty: Int, upperDifficulty: Int): Pair<NationConfig, Int>? {
  val configs = mutableListOf<Pair<NationConfig, Int>>()
  for (diff in lowerDifficulty..upperDifficulty) {
    configs += nationByDifficulty(diff)
  }
  return configs.randomOrNull()
}

/**
 * Difficulty is from https://docs.google.com/spreadsheets/d/13YKGn8nBPD3b84pazu4iedj99wowHuXkC_afTG1wf4U/edit?gid=906893594#gid=906893594
 *
 * @return List of NationConfigs that are of difficulty
 */
private fun nationByDifficulty(difficulty: Int): List<Pair<NationConfig, Int>> {
  when (difficulty) {
    1 -> return listOf(
      NationConfig(Nation.Brandenburg, 0),
      NationConfig(Nation.Schweden, 0),
      NationConfig(Nation.England, 0),
      NationConfig(Nation.Schottland, 0),
      NationConfig(Nation.Russland, 0),
      NationConfig(Nation.HabsburgMining, 0),
    ).map { c -> Pair(c, 1) }.toList()

    2 -> return listOf(
      NationConfig(Nation.Brandenburg, 1),
      NationConfig(Nation.Schweden, 1),
      NationConfig(Nation.France, 0),
      NationConfig(Nation.Habsburg, 0)
    ).map { c -> Pair(c, 2) }.toList()

    3 -> return listOf(
      NationConfig(Nation.Schweden, 2),
      NationConfig(Nation.England, 1),
      NationConfig(Nation.France, 1),
      NationConfig(Nation.Schottland, 1),
      NationConfig(Nation.Russland, 1),
      NationConfig(Nation.Habsburg, 1),
      NationConfig(Nation.HabsburgMining, 1),
    ).map { c -> Pair(c, 3) }.toList()

    4 -> return listOf(
      NationConfig(Nation.Brandenburg, 2),
      NationConfig(Nation.England, 2),
      NationConfig(Nation.Schottland, 2),
      NationConfig(Nation.Russland, 2),
      NationConfig(Nation.HabsburgMining, 2),
    ).map { c -> Pair(c, 4) }.toList()

    5 -> return listOf(
      NationConfig(Nation.Schweden, 3),
      NationConfig(Nation.France, 2),
      NationConfig(Nation.Habsburg, 2),
      NationConfig(Nation.HabsburgMining, 3),
    ).map { c -> Pair(c, 5) }.toList()

    6 -> return listOf(
      NationConfig(Nation.Brandenburg, 3),
      NationConfig(Nation.Schweden, 4),
      NationConfig(Nation.England, 3),
      NationConfig(Nation.Schottland, 3),
      NationConfig(Nation.Russland, 3),
      NationConfig(Nation.Habsburg, 3),
    ).map { c -> Pair(c, 6) }.toList()

    7 -> return listOf(
      NationConfig(Nation.Brandenburg, 4),
      NationConfig(Nation.Schweden, 5),
      NationConfig(Nation.England, 4),
      NationConfig(Nation.France, 3),
      NationConfig(Nation.Schottland, 4),
      NationConfig(Nation.Russland, 4),
      NationConfig(Nation.HabsburgMining, 4),
    ).map { c -> Pair(c, 7) }.toList()

    8 -> return listOf(
      NationConfig(Nation.Schweden, 6),
      NationConfig(Nation.France, 4),
      NationConfig(Nation.Schottland, 5),
      NationConfig(Nation.Habsburg, 4),
    ).map { c -> Pair(c, 8) }.toList()

    9 -> return listOf(
      NationConfig(Nation.Brandenburg, 5),
      NationConfig(Nation.England, 5),
      NationConfig(Nation.France, 5),
      NationConfig(Nation.Russland, 5),
      NationConfig(Nation.Habsburg, 5),
      NationConfig(Nation.HabsburgMining, 5),
    ).map { c -> Pair(c, 9) }.toList()

    10 -> return listOf(
      NationConfig(Nation.Brandenburg, 6),
      NationConfig(Nation.France, 6),
      NationConfig(Nation.Schottland, 6),
      NationConfig(Nation.Habsburg, 6),
      NationConfig(Nation.HabsburgMining, 6),
    ).map { c -> Pair(c, 10) }.toList()

    11 -> return listOf(
      NationConfig(Nation.England, 6),
      NationConfig(Nation.Russland, 6)
    ).map { c -> Pair(c, 11) }.toList()

    else -> return emptyList()
  }
}