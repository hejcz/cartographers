var cards = {
    "14": {
        "title": "Nadrzewna Osada",
        "terrains": ["CITY", "FOREST"],
        "points": 2,
        "shapes": [
            {
                "points": [{"x": -1, "y": 0}, {"x": -1, "y": 1}, {"x": -1, "y": 2}, {"x": 0, "y": 2}, {"x": 0, "y": 3}],
                "coin": false
            }
        ]
    },
    "07": {
        "title": "Wielka rzeka",
        "terrains": ["WATER"],
        "points": 2,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 0}, {"x": -2, "y": 0}],
                "coin": true
            },
            {
                "points": [{"x": -2, "y": 0}, {"x": -2, "y": 1}, {"x": -1, "y": 1}, {"x": -1, "y": 2}, {"x": 0, "y": 2}],
                "coin": false
            }
        ]
    },
    "10": {
        "title": "Zapomniany Las",
        "terrains": ["FOREST"],
        "points": 1,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 1}],
                "coin": true
            },
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 0}, {"x": -1, "y": 1}, {"x": -2, "y": 1}],
                "coin": false
            }
        ]
    },
    "13": {
        "title": "SAD",
        "terrains": ["FOREST", "PLAINS"],
        "points": 2,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": 0, "y": 1}, {"x": -1, "y": 2}, {"x": 0, "y": 2}],
                "coin": false
            }
        ]
    },


};

function bounds(points) {
    let minX = maxX = points[0].x;
    let minY = maxY = points[0].y;
    for (const p of points) {
        if (p.x < minX) {
            minX = p.x;
        } else if (p.x > maxX) {
            maxX = p.x;
        }
        if (p.y < minY) {
            minY = p.y;
        } else if (p.y > maxY) {
            maxY = p.y;
        }
    }
    return {height: maxX - minX + 1, width: maxY - minY + 1};
}

let currentCard = ["14", "10", "07", "13"];
let index = 0;

setInterval(() => {
    if (index == 3) {
        index = 0;
    } else {
        index++;
    }
    updateCard(cards[currentCard[index]]);
}, 1000);

const d3Card = d3.select("#current-card")
    .attr("width", 400)
    .attr("height", 200);

d3Card.append("g")
    .attr("transform", `translate(10 0)`)
    .attr("class", "title")
    .append("text")
    .attr("x", 0)
    .attr("y", 15);

d3Card.append("g")
    .attr("transform", `translate(10 25)`)
    .attr("class", "terrain");

updateCard(cards["10"]);

function updateCard(card) {
    d3.select("#current-card .title text")
        .text(card.title);

    // terrains
    const terrainUpdate = d3.select("#current-card .terrain")
        .selectAll("rect")
        .data(card.terrains);

    const terrainEnter = terrainUpdate.enter()
        .append("rect")
        .attr("width", 30)
        .attr("height", 30)
        .style("stroke-width", "1px")
        .style("stroke", "black");

    terrainUpdate.merge(terrainEnter)
        .attr("transform", function (d, i) { return `translate(${i * 35} 0)`; })
        .style("fill", function (d) { return `rgb(${colorByType[d]})` });

    terrainUpdate.exit().remove();

    // shapes
    const shapeUpdate = d3.select("#current-card").selectAll(".shape")
        .data(card.shapes);

    shapeUpdate.exit().remove();

    const shapeEnter = shapeUpdate.enter()
        .append("g")
        .attr("class", "shape");

    const cardSize = 30;

    shapeUpdate.merge(shapeEnter)
        .attr("transform", function (d, i) { return `translate(${10 + i * 150} ${70})`; });

    const shapeElementUpdate = shapeUpdate.merge(shapeEnter).selectAll("rect")
        .data(data => data.points);

    const shapeElementEnter = shapeElementUpdate.enter()
        .append("rect")
        .attr("width", cardSize)
        .attr("height", cardSize)
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .style("fill", "white")

    shapeElementUpdate.exit().remove();

    shapeElementEnter.merge(shapeElementUpdate)
        .attr("transform", function (d, i) { return `translate(${d.y * cardSize} ${-d.x * cardSize})`; });

    // shapes with coins have max width = 2
    const coinUpdate = shapeEnter.merge(shapeUpdate).selectAll("circle")
        .data(data => data.coin ? [bounds(data.points)] : []);
    const coinEnter = coinUpdate.enter()
        .append("circle")
        .attr("r", 15);
    coinUpdate.merge(coinEnter)
        .attr("cx", data => 15 + data.width * cardSize + 10)
        .attr("cy", data => cardSize * data.height / 2.0)
        .style("fill", "rgb(255, 230, 0)");
    coinUpdate.exit().remove();
}


//object TreeFortress14 : Card {

//
//
//}
//
//object City09 : Card {
//    override fun number(): String = "09"
//
//    private val availableShapes =
//        Shape.create(
//            """
//            [ ]
//            [ ][ ]
//            """
//        ).createAllVariations() +
//                Shape.create(
//                    """
//            [ ][ ][ ]
//            [ ][ ]
//            """
//                ).createAllVariations()
//
//    override fun availableShapes(): Set<Shape> =
//        availableShapes
//
//    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.CITY
//
//    override fun points(): Int = 1
//
//    override fun givesCoin(shape: Shape): Boolean = shape.size() == 3
//}
//
//object Ruins : Card {
//    override fun number(): String = "ruins"
//
//    override fun availableShapes(): Set<Shape> = emptySet()
//
//    override fun isValid(terrain: Terrain): Boolean = false
//
//    override fun points(): Int = 0
//
//    override fun givesCoin(shape: Shape): Boolean = false
//}
//
//object RuralStream11 : Card {
//    override fun number(): String = "11"
//
//    private val availableShapes =
//        Shape.create(
//            """
//            [ ][ ][ ]
//            [ ]
//            [ ]
//            """
//        ).createAllVariations()
//
//    override fun availableShapes(): Set<Shape> =
//        availableShapes
//
//    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.WATER || terrain == Terrain.PLAINS
//
//    override fun points(): Int = 2
//
//    override fun givesCoin(shape: Shape): Boolean = false
//}
//
//object Cracks17 : Card {
//    override fun number(): String = "17"
//
//    private val availableShapes =
//        Shape.create(
//            """
//            [ ]
//            """
//        ).createAllVariations()
//
//    private val matchingTerrains = setOf(
//        Terrain.PLAINS,
//        Terrain.WATER,
//        Terrain.CITY,
//        Terrain.FOREST,
//        Terrain.MONSTER
//    )
//
//    override fun availableShapes(): Set<Shape> =
//        availableShapes
//
//    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains
//
//    override fun points(): Int = 0
//
//    override fun givesCoin(shape: Shape): Boolean = false
//}
//
//object Farm12 : Card {
//    override fun number(): String = "12"
//
//    private val availableShapes =
//        Shape.create(
//            """
//            [ ]
//            [ ][ ]
//            [ ]
//            """
//        ).createAllVariations()
//
//    private val matchingTerrains = setOf(
//        Terrain.PLAINS,
//        Terrain.CITY
//    )
//
//    override fun availableShapes(): Set<Shape> =
//        availableShapes
//
//    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains
//
//    override fun points(): Int = 2
//
//    override fun givesCoin(shape: Shape): Boolean = false
//}
//
//object Fends15 : Card {
//    override fun number(): String = "15"
//
//    private val availableShapes =
//        Shape.create(
//            """
//            [ ]
//            [ ][ ][ ]
//            [ ]
//            """
//        ).createAllVariations()
//
//    private val matchingTerrains = setOf(
//        Terrain.FOREST,
//        Terrain.WATER
//    )
//
//    override fun availableShapes(): Set<Shape> =
//        availableShapes
//
//    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains
//
//    override fun points(): Int = 2
//
//    override fun givesCoin(shape: Shape): Boolean = false
//}
//
//object Fields08 : Card {
//    override fun number(): String = "08"
//
//    private val availableShapes =
//        Shape.create(
//            """
//            [ ]
//            [ ]
//            """
//        ).createAllVariations() +
//                Shape.create(
//                    """
//               [ ]
//            [ ][ ][ ]
//               [ ]
//            """
//                ).createAllVariations()
//
//    override fun availableShapes(): Set<Shape> =
//        availableShapes
//
//    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.PLAINS
//
//    override fun points(): Int = 1
//
//    override fun givesCoin(shape: Shape): Boolean = shape.size() == 2
//}
//
//object FishermanVillage16 : Card {
//    override fun number(): String = "16"
//
//    private val availableShapes =
//        Shape.create(
//            """
//            [ ][ ][ ][ ]
//            """
//        ).createAllVariations()
//
//    private val matchingTerrains = setOf(
//        Terrain.CITY,
//        Terrain.WATER
//    )
//
//    override fun availableShapes(): Set<Shape> =
//        availableShapes
//
//    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains
//
//    override fun points(): Int = 2
//
//    override fun givesCoin(shape: Shape): Boolean = false
//}