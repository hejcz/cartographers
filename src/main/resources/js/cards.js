var cards = {
    "14": {
        "title": "NADRZEWNA OSADA",
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
        "title": "WIELKA RZEKA",
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
        "title": "ZAPOMNIANY LAS",
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
    "09": {
        "title": "MIASTO",
        "terrains": ["CITY"],
        "points": 1,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 1}, {"x": -1, "y": 0}],
                "coin": true
            },
            {
                "points": [{"x": 0, "y": 0}, {"x": 0, "y": 1}, {"x": -1, "y": 0}, {"x": -1, "y": 1}, {"x": 0, "y": 2}],
                "coin": false
            }
        ]
    },
    "11": {
        "title": "WIEJSKI STRUMIEŃ",
        "terrains": ["PLAINS", "WATER"],
        "points": 2,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": 0, "y": 1}, {"x": -1, "y": 0}, {"x": -2, "y": 0}, {"x": 0, "y": 2}],
                "coin": false
            }
        ]
    },
    "17": {
        "title": "SZCZELINY",
        "terrains": ["PLAINS", "WATER", "CITY", "FOREST", "MONSTER"],
        "points": 0,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}],
                "coin": false
            }
        ]
    },
    "12": {
        "title": "GOSPODARSTWO",
        "terrains": ["PLAINS", "CITY"],
        "points": 2,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 0}, {"x": -2, "y": 0}, {"x": -1, "y": 1}],
                "coin": false
            }
        ]
    },
    "15": {
        "title": "MOKRADŁA",
        "terrains": ["FOREST", "WATER"],
        "points": 2,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 0}, {"x": -2, "y": 0}, {"x": -1, "y": 1}, {"x": -1, "y": 2}],
                "coin": false
            }
        ]
    },
    "08": {
        "title": "POLE UPRAWNE",
        "terrains": ["PLAINS"],
        "points": 1,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 0}],
                "coin": true
            },
            {
                "points": [{"x": -1, "y": 0}, {"x": -1, "y": 1}, {"x": -1, "y": 2}, {"x": 0, "y": 1}, {"x": -2, "y": 1}],
                "coin": false
            }
        ]
    },
    "16": {
        "title": "WIOSKA RYBACKA",
        "terrains": ["WATER", "CITY"],
        "points": 2,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": 0, "y": 1}, {"x": 0, "y": 2}, {"x": 0, "y": 3}],
                "coin": false
            }
        ]
    },
    "01": {
        "title": "ATAK GOBLINÓW",
        "terrains": ["MONSTER"],
        "points": 0,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 1}, {"x": -2, "y": 2}],
                "coin": false
            }
        ]
    },
    "02": {
        "title": "NAPAD POSTRACHÓW",
        "terrains": ["MONSTER"],
        "points": 0,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 0}, {"x": 0, "y": 2},  {"x": -1, "y": 2}],
                "coin": false
            }
        ]
    },
    "03": {
        "title": "SZARŻA KOBOLDÓW",
        "terrains": ["MONSTER"],
        "points": 0,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 0}, {"x": -2, "y": 0}, {"x": -1, "y": 1}],
                "coin": false
            }
        ]
    },
    "04": {
        "title": "ATAK GOBLINÓW",
        "terrains": ["MONSTER"],
        "points": 0,
        "shapes": [
            {
                "points": [{"x": 0, "y": 0}, {"x": -1, "y": 0}, {"x": -2, "y": 0}, {"x": 0, "y": 1}, {"x": -2, "y": 1}],
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

const d3Card = d3.select("#current-card")
    .attr("width", 400)
    .attr("height", 200);

d3Card.append("g")
    .attr("transform", `translate(10 0)`)
    .attr("class", "title")
    .append("text")
    .attr("x", 0)
    .attr("y", 25);

d3Card.append("g")
    .attr("transform", `translate(10 35)`)
    .attr("class", "terrain");

function updateCard(card, ruins) {
    d3.select("#current-card")
        .style("display", "block")
        .select(".title text")
        .text(ruins ? card.title + " NA RUINACH" : card.title);

    // shapes
    const shapeUpdate = d3.select("#current-card").selectAll(".shape")
        .data(card.shapes);

    shapeUpdate.exit().remove();

    const shapeEnter = shapeUpdate.enter()
        .append("g")
        .attr("class", "shape");

    const cardSize = 30;

    shapeUpdate.merge(shapeEnter)
        .attr("transform", function (d, i) { return `translate(${10 + i * 150} ${80})`; });

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
