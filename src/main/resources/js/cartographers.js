const points = new Set();
let currentTerrain = "FOREST";
let coinsCount = 0;
const emptyArray = [];
const scores = [emptyArray, emptyArray, emptyArray, emptyArray];
const nick = "julian" + Math.random();

const host = window.location.hostname;
const port = window.location.port;

const ws = new WebSocket(
    host === "cartographers.herokuapp.com" ? "wss://cartographers.herokuapp.com/api" : "ws://localhost:8080/api");
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
            drawBoard();
            coinsCount = totalCoins;
            drawCoins();
        }
        if (event["type"] === "NEW_CARD") {
            const { card, ruins, currentTurnPoints, maxTurnPoints } = event;
            updateCard(cards[card], ruins);
            updateTerrains(cards[card].terrains);
            currentTerrain = cards[card].terrains[0];
            d3.select("#points-in-turn")
                .text(currentTurnPoints >= maxTurnPoints
                    ? "OSTATNIA RUNDA W TEJ PORZE ROKU"
                    : `PUNKTY PO TEJ TURZE: ${currentTurnPoints} / ${maxTurnPoints}`)
        }
        if (event["type"] === "SCORE") {
            const { quest1, quest2, coins, monsters } = event.scores[nick];
            const idx = scores.indexOf(emptyArray);
            scores[idx] = [quest1, quest2, coins, -monsters, quest1 + quest2 + coins - monsters];
            drawPoints();
        }
        if (event["type"] === "GOALS") {
            const { spring, autumn, winter, summer } = event;
            const score_card_to_text = (prop, season) => {
                const sc = score_cards[prop];
                const title = sc.title;
                const desc = sc.description;
                return `<b>[${season}] ${title}</b><br />${desc}`
            };
            d3.select("#goals-section")
                .html(`${score_card_to_text(spring, "WIOSNA")}<br />${score_card_to_text(summer, "LATO")}
                <br />${score_card_to_text(autumn, "JESIEŃ")}<br />${score_card_to_text(winter, "ZIMA")}`);
        }
    }
};

const rootSvg = d3.select("#game");
rootSvg.style("height", rootSvg.style("width"));

rootSvg.append("svg")
    .attr("id", "board");

d3.select("#start")
    .on("click", function () { ws.send(JSON.stringify({ "type": "start" })) });

d3.select("#leave")
    .on("click", function () { ws.send(JSON.stringify({ "type": "leave" })) });

d3.select("#goals")
    .on("click", function () {
        const gs = d3.select("#goals-section");
        const display = gs.style("display");
        gs.style("display", display === 'none' ? 'block' : 'none')
     });

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
        cell.hasRuins = true;
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
            drawBoard();
        });

    terrainUpdate.exit().remove();
}

d3.select("#submit")
    .on("click", function (event) {
        const msg = { "type": "draw", "data": { "points": [...points].map(cell => ({ "x": cell.x, "y": cell.y })), "terrain": currentTerrain } };
        ws.send(JSON.stringify(msg));
    });

d3.select("#points")
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
            let x = 0;
            if (i === 2 || i == 3) {
                x = -1;
            }
            if (i === 4) {
                x = -2;
            }
            let y = 0;
            if (i === 1 || i == 3) {
                y = 1;
            }
            if (i === 4) {
                y = 0.5;
            }

            return `translate(${y * (w) / 2} ${w/6 + -x * (w) / 2})`;
        })
        .text(d => d);
}

function drawCoins() {
    const coinsInRow = 15;
    const offsetToR = 0.1;
    const r = 100 / (2 * coinsInRow + (coinsInRow - 1) * offsetToR);
    const offset = offsetToR * r;

    const coins = d3.select("#coins")
        .selectAll("circle")
        .data(Array(coinsCount).fill({ "active": true }).concat(Array(15 - coinsCount).fill({ "active": false })));
    const enter = coins.enter()
        .append("circle")
        .attr("r",`${r}%`)
        .attr("cx", function (d, i) { return `${offset + r + i * (2 * r + offset)}%`; })
        .attr("cy", 30);
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

    const cellSize = 100 / 11;

    const cellEnter = cells.enter()
        .append("svg")
        .attr("x", d => `${d.y * cellSize}%`)
        .attr("y", d => `${-d.x * cellSize}%`);

    const enter = cellEnter
        .append("rect")
        .attr("class", "cell")
        .attr("width", `${cellSize}%`)
        .attr("height", `${cellSize}%`)
        .style("stroke-width", "1px")
        .style("stroke", "grey")
        .on("click", function (d) {
            if (d.locked) {
                return;
            }
            if (d.type === "EMPTY") {
                points.add(d);
                d.type = currentTerrain;
            } else {
                d.type = "EMPTY";
                points.delete(d);
            }
            drawBoard();
        });

    const ruinsImage = cellEnter.filter(d => d.hasRuins)
        .append("image")
        .attr("href", "/game/ruins.svg")
        .attr("width", `${cellSize*0.6}%`)
        .attr("height", `${cellSize*0.6}%`)
        .attr("x", `${cellSize*0.2}%`)
        .attr("y", `${cellSize*0.2}%`)
        .on("click", function (d) {
            if (d.locked) {
                return;
            }
            if (d.type === "EMPTY") {
                points.add(d);
                d.type = currentTerrain;
            } else {
                d.type = "EMPTY";
                points.delete(d);
            }
            drawBoard();
        });

        const mountainImage = cellEnter.filter(d => d.type === "MOUNTAIN")
            .append("image")
            .attr("href", "/game/mountain.svg")
            .attr("width", `${cellSize*0.6}%`)
            .attr("height", `${cellSize*0.6}%`)
            .attr("x", `${cellSize*0.2}%`)
            .attr("y", `${cellSize*0.2}%`);

    cells.merge(enter)
        .style("fill-opacity", function (d) { return d.type == "EMPTY" || d.locked ? 1.0 : 0.7; })
        .style("fill", function (d) { return `rgb(${colorByType[d.type]})` });
}