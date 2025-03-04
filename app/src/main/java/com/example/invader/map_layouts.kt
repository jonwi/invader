package com.example.invader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Preview
@Composable
fun SingleMapPreview() {
  SingleMap(Map.A, Modifier.width(100.dp))
}

@Composable
fun SingleMap(map: Map, modifier: Modifier) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
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

@Composable
fun ThreePlayerMap(
  map1: Map,
  map2: Map,
  map3: Map,
  modifier1: Modifier,
  modifier2: Modifier,
  modifier3: Modifier
) {
  Box(
    modifier = Modifier.size(242.dp, 300.dp)
  ) {
    SingleMap(map1, modifier1)
    SingleMap(map2, modifier2)
    SingleMap(map3, modifier3)
  }
}

@Preview
@Composable
fun ThreePlayerStandardPreview() {
  ThreePlayerStandard(listOf(Map.A, Map.B, Map.C))
}

/**
 * Map of three player standard
 * @param maps List of three maps
 */
@Composable
fun ThreePlayerStandard(maps: List<Map>) {
  ThreePlayerMap(
    maps[0], maps[1], maps[2],
    Modifier.width(175.dp),
    Modifier
      .width(175.dp)
      .offset(y = 91.dp, x = 1.dp)
      .rotate(240f),
    Modifier
      .width(175.dp)
      .offset(y = (45).dp, x = 79.dp)
      .rotate(120f)
  )
}

/**
 * Preview of [ThreePlayerCostline]
 */
@Preview
@Composable
fun ThreePlayerCoastlinePreview() {
  ThreePlayerCostline(listOf(Map.A, Map.B, Map.C))
}

/**
 * Map of three player costline
 * @param maps list of three maps
 */
@Composable
fun ThreePlayerCostline(maps: List<Map>) {
  ThreePlayerMap(
    maps[0], maps[1], maps[2],
    Modifier
      .width(150.dp)
      .offset(y = 1.dp, x = 95.dp),
    Modifier
      .width(150.dp)
      .offset(y = 78.dp, x = 49.dp),
    Modifier
      .width(150.dp)
      .offset(y = 156.dp, x = 3.dp),
  )
}


/**
 * Preview of [ThreePlayerSunrise]
 */
@Preview
@Composable
fun ThreePlayerSunrisePreview() {
  ThreePlayerSunrise(listOf(Map.A, Map.B, Map.C))
}

/**
 * Map of three player sunrise
 * @param maps list of three maps
 */
@Composable
fun ThreePlayerSunrise(maps: List<Map>) {
  ThreePlayerMap(
    maps[0], maps[1], maps[2],
    Modifier
      .width(150.dp)
      .offset(y = 15.dp)
      .rotate(152f),
    Modifier
      .width(150.dp)
      .offset(y = 81.dp, x = 38.dp)
      .rotate(210f),
    Modifier
      .width(150.dp)
      .offset(y = 150.dp, x = (-1).dp)
      .rotate(270f),
  )
}

/**
 * View with four maps modifiers are applied to the maps for positioning
 */
@Composable
fun FourPlayerMap(
  map1: Map,
  map2: Map,
  map3: Map,
  map4: Map,
  modifier1: Modifier,
  modifier2: Modifier,
  modifier3: Modifier,
  modifier4: Modifier,
) {
  Box(
    modifier = Modifier.size(242.dp, 300.dp)
  ) {
    SingleMap(map1, modifier1)
    SingleMap(map2, modifier2)
    SingleMap(map3, modifier3)
    SingleMap(map4, modifier4)
  }
}

/**
 * Preview for [FourPlayerMapStandard]
 */
@Preview
@Composable
fun FourPlayerMapStandardPreview() {
  FourPlayerMapStandard(
    listOf(Map.A, Map.B, Map.C, Map.D)
  )
}

/**
 * Four Player map with standard layout
 */
@Composable
fun FourPlayerMapStandard(maps: List<Map>) {
  FourPlayerMap(
    maps[0], maps[1], maps[2], maps[3],
    Modifier
      .width(120.dp)
      .offset(x = 46.dp),
    Modifier
      .width(120.dp)
      .offset(y = (-1).dp, x = 120.dp)
      .rotate(180f),
    Modifier
      .width(120.dp)
      .offset(x = 9.dp, y = 62.dp),
    Modifier
      .width(120.dp)
      .offset(y = 61.dp, x = 83.dp)
      .rotate(180f),
  )
}

/**
 * Preview for [FourPlayerMapLeaf]
 */
@Preview
@Composable
fun FourPlayerMapLeafPreview() {
  FourPlayerMapLeaf(
    listOf(Map.A, Map.B, Map.C, Map.D)
  )
}

/**
 * Four Player map with leaf layout
 */
@Composable
fun FourPlayerMapLeaf(maps: List<Map>) {
  FourPlayerMap(
    maps[0], maps[1], maps[2], maps[3],
    Modifier
      .width(150.dp)
      .offset(x = 21.dp, y = 25.dp)
      .rotate(120f),
    Modifier
      .width(150.dp)
      .offset(y = 65.dp, x = 90.dp)
      .rotate(180f),
    Modifier
      .width(150.dp)
      .offset(x = (-23).dp, y = 105.dp)
      .rotate(300f),
    Modifier
      .width(150.dp)
      .offset(y = 144.dp, x = 44.dp)
      .rotate(180f),
  )
}

/**
 * Preview for [FourPlayerMapSnake]
 */
@Preview
@Composable
fun FourPlayerMapSnakePreview() {
  FourPlayerMapSnake(
    listOf(Map.A, Map.B, Map.C, Map.D)
  )
}

/**
 * Four Player map with snake layout
 */
@Composable
fun FourPlayerMapSnake(maps: List<Map>) {
  FourPlayerMap(
    maps[0], maps[1], maps[2], maps[3],
    Modifier
      .width(100.dp)
      .offset(x = 2.dp, y = 125.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 32.dp, y = 75.dp)
      .rotate(0f),
    Modifier
      .width(100.dp)
      .offset(x = 93.dp, y = 74.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 124.dp, y = 22.dp)
      .rotate(180f),
  )
}

/**
 * five player map container
 */
@Composable
fun FivePlayerMap(
  map1: Map,
  map2: Map,
  map3: Map,
  map4: Map,
  map5: Map,
  modifier1: Modifier,
  modifier2: Modifier,
  modifier3: Modifier,
  modifier4: Modifier,
  modifier5: Modifier,
) {
  Box(
    modifier = Modifier.size(242.dp, 300.dp)
  ) {
    SingleMap(map1, modifier1)
    SingleMap(map2, modifier2)
    SingleMap(map3, modifier3)
    SingleMap(map4, modifier4)
    SingleMap(map5, modifier5)
  }
}

/**
 * Preview of [FivePlayerMapCrab]
 */
@Preview
@Composable
fun FivePlayerMapCrabPreview() {
  FivePlayerMapCrab(listOf(Map.A, Map.B, Map.C, Map.D, Map.E))
}

/**
 * Five player crab layout
 */
@Composable
fun FivePlayerMapCrab(maps: List<Map>) {
  FivePlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4],
    Modifier
      .width(100.dp)
      .offset(x = 0.dp, y = 20.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 30.dp, y = 73.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 75.dp, y = 99.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 75.dp, y = 47.dp)
      .rotate(60f),
    Modifier
      .width(100.dp)
      .offset(x = 120.dp, y = 21.dp)
      .rotate(120f),
  )
}

/**
 * Preview of [FivePlayerMapClaw]
 */
@Preview
@Composable
fun FivePlayerMapClawPreview() {
  FivePlayerMapClaw(listOf(Map.A, Map.B, Map.C, Map.D, Map.E))
}

/**
 * Five player claw layout
 */
@Composable
fun FivePlayerMapClaw(maps: List<Map>) {
  FivePlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4],
    Modifier
      .width(100.dp)
      .offset(x = 75.dp, y = 20.dp)
      .rotate(30f),
    Modifier
      .width(100.dp)
      .offset(x = 22.dp, y = 50.dp)
      .rotate(30f),
    Modifier
      .width(100.dp)
      .offset(x = 48.dp, y = 96.dp)
      .rotate(330f),
    Modifier
      .width(100.dp)
      .offset(x = 101.dp, y = 96.dp)
      .rotate(270f),
    Modifier
      .width(100.dp)
      .offset(x = 128.dp, y = 50.dp)
      .rotate(210f),
  )
}

/**
 * Preview of [FivePlayerMapPeninsula]
 */
@Preview
@Composable
fun FivePlayerMapPeninsulaPreview() {
  FivePlayerMapPeninsula(listOf(Map.A, Map.B, Map.C, Map.D, Map.E))
}

/**
 * Five player peninsula layout
 */
@Composable
fun FivePlayerMapPeninsula(maps: List<Map>) {
  FivePlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4],
    Modifier
      .width(100.dp)
      .offset(x = 24.dp, y = 0.dp)
      .rotate(0f),
    Modifier
      .width(100.dp)
      .offset(x = 25.dp, y = 52.dp)
      .rotate(240f),
    Modifier
      .width(100.dp)
      .offset(x = 69.dp, y = 25.dp)
      .rotate(120f),
    Modifier
      .width(100.dp)
      .offset(x = 99.dp, y = 77.dp)
      .rotate(120f),
    Modifier
      .width(100.dp)
      .offset(x = 129.dp, y = 130.dp)
      .rotate(120f),
  )
}

/**
 * Preview of [FivePlayerMapSnail]
 */
@Preview
@Composable
fun FivePlayerMapSnailPreview() {
  FivePlayerMapSnail(listOf(Map.A, Map.B, Map.C, Map.D, Map.E))
}

/**
 * Five player snail layout
 */
@Composable
fun FivePlayerMapSnail(maps: List<Map>) {
  FivePlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4],
    Modifier
      .width(100.dp)
      .offset(x = 34.dp, y = 29.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 78.dp, y = 3.dp)
      .rotate(60f),
    Modifier
      .width(100.dp)
      .offset(x = 79.dp, y = 55.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 79.dp, y = 107.dp)
      .rotate(240f),
    Modifier
      .width(100.dp)
      .offset(x = 18.dp, y = 107.dp)
      .rotate(240f),
  )
}

/**
 * Preview of [FivePlayerMapV]
 */
@Preview
@Composable
fun FivePlayerMapVPreview() {
  FivePlayerMapV(listOf(Map.A, Map.B, Map.C, Map.D, Map.E))
}

/**
 * Five player V layout
 */
@Composable
fun FivePlayerMapV(maps: List<Map>) {
  FivePlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4],
    Modifier
      .width(100.dp)
      .offset(x = 0.dp, y = 30.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 29.dp, y = 83.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 74.dp, y = 108.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 105.dp, y = 56.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 136.dp, y = 4.dp)
      .rotate(180f),
  )
}

/**
 * six map container
 */
@Composable
fun SixPlayerMap(
  map1: Map,
  map2: Map,
  map3: Map,
  map4: Map,
  map5: Map,
  map6: Map,
  modifier1: Modifier,
  modifier2: Modifier,
  modifier3: Modifier,
  modifier4: Modifier,
  modifier5: Modifier,
  modifier6: Modifier
) {
  Box(
    modifier = Modifier.size(242.dp, 300.dp)
  ) {
    SingleMap(map1, modifier1)
    SingleMap(map2, modifier2)
    SingleMap(map3, modifier3)
    SingleMap(map4, modifier4)
    SingleMap(map5, modifier5)
    SingleMap(map6, modifier6)
  }
}

/**
 * Preview of [SixPlayerMapTwoCenters]
 */
@Preview
@Composable
fun SixPlayerMapTwoCentersPreview() {
  SixPlayerMapTwoCenters(listOf(Map.A, Map.B, Map.C, Map.D, Map.E, Map.F))
}

/**
 * six player two center layout
 */
@Composable
fun SixPlayerMapTwoCenters(maps: List<Map>) {
  SixPlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4], maps[5],
    Modifier
      .width(100.dp)
      .offset(x = 0.dp, y = 150.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 45.dp, y = 124.dp)
      .rotate(60f),
    Modifier
      .width(100.dp)
      .offset(x = 45.dp, y = 176.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 106.dp, y = 125.dp)
      .rotate(240f),
    Modifier
      .width(100.dp)
      .offset(x = 105.dp, y = 73.dp),
    Modifier
      .width(100.dp)
      .offset(x = 151.dp, y = 99.dp)
      .rotate(120f),
  )
}

/**
 * Preview of [SixPlayerMapCaldera]
 */
@Preview
@Composable
fun SixPlayerMapCalderaPreview() {
  SixPlayerMapCaldera(listOf(Map.A, Map.B, Map.C, Map.D, Map.E, Map.F))
}

/**
 * six player caldera layout
 */
@Composable
fun SixPlayerMapCaldera(maps: List<Map>) {
  SixPlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4], maps[5],
    Modifier
      .width(100.dp)
      .offset(x = 44.dp, y = 4.dp)
      .rotate(60f),
    Modifier
      .width(100.dp)
      .offset(x = 104.dp, y = 5.dp)
      .rotate(60f),
    Modifier
      .width(100.dp)
      .offset(x = 105.dp, y = 56.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 74.dp, y = 107.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 29.dp, y = 82.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 0.dp, y = 30.dp)
      .rotate(300f),
  )
}


/**
 * Preview of [SixPlayerMapFlower]
 */
@Preview
@Composable
fun SixPlayerMapFlowerPreview() {
  SixPlayerMapFlower(listOf(Map.A, Map.B, Map.C, Map.D, Map.E, Map.F))
}

/**
 * six player flower layout
 */
@Composable
fun SixPlayerMapFlower(maps: List<Map>) {
  SixPlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4], maps[5],
    Modifier
      .width(100.dp)
      .offset(x = 74.dp, y = 56.dp)
      .rotate(60f),
    Modifier
      .width(100.dp)
      .offset(x = 134.dp, y = 56.dp)
      .rotate(60f),
    Modifier
      .width(100.dp)
      .offset(x = 74.dp, y = 108.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 43.dp, y = 159.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 29.dp, y = 82.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 0.dp, y = 30.dp)
      .rotate(300f),
  )
}

/**
 * Preview of [SixPlayerMapStar]
 */
@Preview
@Composable
fun SixPlayerMapStarPreview() {
  SixPlayerMapStar(listOf(Map.A, Map.B, Map.C, Map.D, Map.E, Map.F))
}

/**
 * Six player star layout
 */
@Composable
fun SixPlayerMapStar(maps: List<Map>) {
  SixPlayerMap(
    maps[0], maps[1], maps[2], maps[3], maps[4], maps[5],
    Modifier
      .width(100.dp)
      .offset(x = 14.dp, y = 46.dp)
      .rotate(60f),
    Modifier
      .width(100.dp)
      .offset(x = 60.dp, y = 20.dp)
      .rotate(120f),
    Modifier
      .width(100.dp)
      .offset(x = 105.dp, y = 46.dp)
      .rotate(180f),
    Modifier
      .width(100.dp)
      .offset(x = 105.dp, y = 99.dp)
      .rotate(240f),
    Modifier
      .width(100.dp)
      .offset(x = 60.dp, y = 125.dp)
      .rotate(300f),
    Modifier
      .width(100.dp)
      .offset(x = 14.dp, y = 98.dp)
      .rotate(0f),
  )
}