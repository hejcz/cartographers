const points = new Set();
const selectedRuins = new Set();
let currentTerrain = "FOREST";
let coinsCount = 0;
const emptyArray = [];
const scores = [emptyArray, emptyArray, emptyArray, emptyArray];
const nick = "julian" + Math.random();

const host = window.location.hostname;
const port = window.location.port;

const ws = new WebSocket(`wss://${host}${port === "" ? "" : ":" + port}/api`);
ws.onopen = function () {
    ws.send(JSON.stringify({ "type": "join", "data": { "nick": nick, "gid": "game1" } }));
};
ws.onmessage = function (event) {
    console.log(event);
    const events = JSON.parse(event.data);
    console.log(events);
    for (const event of events) {
        if (event["type"] === "ACCEPTED_SHAPE") {
            const { points: pts, terrain, totalCoins } = event;
            pts.filter(it => {
                const match = board.find(cell => cell.x === it.x && cell.y === it.y);
                if (match) {
                    match.terrain = terrain;
                    match.locked = true;
                }
            });
            points.clear();
            selectedRuins.clear();
            drawBoard();
            coinsCount = totalCoins;
            drawCoins();
        }
        if (event["type"] === "NEW_CARD") {
            const { card, ruins } = event;
            updateCard(cards[card], ruins);
            updateTerrains(cards[card].terrains);
            currentTerrain = cards[card].terrains[0];
        }
        if (event["type"] === "SCORE") {
            const { quest1, quest2, coins, monsters } = event.scores[nick];
            const idx = scores.indexOf(emptyArray);
            scores[idx] = [quest1, quest2, coins, -monsters];
            drawPoints();
        }
    }
};

const rootSvg = d3.select("#game")
rootSvg.append("svg")
    .attr("id", "terrain");
rootSvg.append("svg")
    .attr("id", "board");
rootSvg.append("svg")
    .attr("id", "buttons");

d3.select("#start")
    .on("click", function () { ws.send(JSON.stringify({ "type": "start" })) });

const board = []
for (let x = 0; x <= 10; x++) {
    for (let y = 0; y <= 10; y++) {
        board.push({ "x": -x, "y": y, "type": "EMPTY", "locked": false })
    }
}

for (const cell of board) {
    if (cell.x == -1 && cell.y == 5
        || cell.x == -2 && cell.y == 1
        || cell.x == -2 && cell.y == 9
        || cell.x == -8 && cell.y == 1
        || cell.x == -8 && cell.y == 9
        || cell.x == -9 && cell.y == 5) {
        cell.type = "RUINS";
    }

    if (
        cell.x == -1 && cell.y == 3
        || cell.x == -2 && cell.y == 8
        || cell.x == -5 && cell.y == 5
        || cell.x == -8 && cell.y == 2
        || cell.x == -9 && cell.y == 7
    ) {
        cell.type = "MOUNTAIN";
        cell.locked = true;
    }
}

function updateTerrains(terrains) {
    const terrainUpdate = d3.select("#current-card .terrain")
        .selectAll("rect")
        .data(terrains);

    const terrainEnter = terrainUpdate.enter()
        .append("rect")
        .attr("width", 30)
        .attr("height", 30)
        .style("stroke-width", "1px")
        .style("stroke", "black");

    terrainUpdate.merge(terrainEnter)
        .attr("transform", function (d, i) { return `translate(${i * 35} 0)`; })
        .style("fill", function (d) { return `rgb(${colorByType[d]})` })
        .on("click", function (d) {
            currentTerrain = d;
            [...points].filter(it => !it.locked).forEach(it => { it.type = "EMPTY"; });
            points.clear();
            [...selectedRuins].filter(it => !it.locked).forEach(it => { it.type = "RUINS"; });
            selectedRuins.clear();
            drawBoard();
        });

    terrainUpdate.exit().remove();
}

d3.select("#submit")
    .on("click", function (event) {
        const msg = { "type": "draw", "data": { "points": [...points].map(cell => ({ "x": cell.x, "y": cell.y })), "terrain": currentTerrain } };
        ws.send(JSON.stringify(msg));
    });

rootSvg.select("#board")
    .append("svg")
    .attr("id", "points")
    .attr("transform", function (d) { return `translate(0 620)`; })
    .attr("width", 600)
    .attr("height", 200);

drawBoard();
drawCoins();
drawPoints();

function drawPoints() {

    const ps = d3.select("#points").selectAll(".pointsSection")
        .data([scores[0], scores[1], scores[2], scores[3]]);

    const enter = ps.enter();

    const h = 150;
    const w = 100;
    const off = 10;

    enter.append("g")
        .attr("class", "pointsSection")
        .attr("transform", function (d, i) { return `translate(${i * (w + off)} 0)`; })
        .append("rect")
        .attr("width", w)
        .attr("height", h)
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .style("fill", "white");

    enter.append("line")
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .attr("x1", (d, i) => i * (w + off) + w / 2)
        .attr("y1", 0)
        .attr("x2", (d, i) => i * (w + off) + w / 2)
        .attr("y2", h * 2 / 3);

    enter.append("line")
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .attr("x1", (d, i) => i * (w + off))
        .attr("y1", h * 2 / 3)
        .attr("x2", (d, i) => i * (w + off) + w)
        .attr("y2", h * 2 / 3);

    enter.append("line")
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .attr("x1", (d, i) => i * (w + off))
        .attr("y1", h / 3)
        .attr("x2", (d, i) => i * (w + off) + w)
        .attr("y2", h / 3);

    const scoreUpdate = enter.merge(ps).selectAll(".score")
        .data(d => d);
    const scoreEnter = scoreUpdate.enter().append("text")
        .attr("class", "score")
        .attr("x", "20")
        .attr("y", "20");
    scoreUpdate.merge(scoreEnter)
        .attr("transform", function (d, i) {
            const x = i === 0 || i === 1 ? 0 : -1;
            const y = i === 0 || i === 2 ? 0 : 1;

            const l = (d + '').length;

            return `translate(${y * (w) / 2} ${w/6 + -x * (w) / 2})`;
        })
        .text(d => d);
}

function drawCoins() {
    const coins = rootSvg.select("#board")
        .selectAll("circle")
        .data(Array(coinsCount).fill({ "active": true }).concat(Array(15 - coinsCount).fill({ "active": false })));
    const enter = coins.enter()
        .append("circle")
        .attr("r", 15)
        .attr("cx", function (d, i) { return 25 + i * 35; })
        .attr("cy", 580);
    coins.merge(enter).style("fill",
        function (d) {
            if (d.active) {
                return "rgb(255, 230, 0)";
            } else {
                return "rgb(255, 247, 173)";
            }
        });
}

function drawBoard() {
    const cells = rootSvg.select("#board")
        .selectAll(".cell")
        .data(board);

    const enter = cells.enter()
        .append("rect")
        .attr("class", "cell")
        .attr("width", 50)
        .attr("height", 50)
        .attr("transform", function (d) { return `translate(${d.y * 50} ${-d.x * 50})`; })
        .style("stroke-width", "1px")
        .style("stroke", "grey")
        .on("click", function (d) {
            if (d.locked) {
                return;
            }
            if (d.type === "EMPTY" || d.type === "RUINS") {
                if (d.type === "RUINS") {
                    selectedRuins.add(d);
                }
                points.add(d);
                d.type = currentTerrain;
            } else {
                if (selectedRuins.has(d)) {
                    selectedRuins.delete(d);
                    d.type = "RUINS";
                } else {
                    d.type = "EMPTY";
                }
                points.delete(d);
            }
            drawBoard();
        });

    cells.merge(enter)
        .style("fill-opacity", function (d) { return d.type == "EMPTY" || d.locked ? 1.0 : 0.7; })
        .style("fill", function (d) { return `rgb(${colorByType[d.type]})` });
}