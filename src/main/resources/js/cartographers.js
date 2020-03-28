const points = new Set();
let currentTerrain = "FOREST";
let coinsCount = 0;
const emptyArray = [];
const scores = [emptyArray, emptyArray, emptyArray, emptyArray];
let nick = undefined;

// create initial board. it's gonna be mutated in the future so
// d3 does not have problems with pointing to old elements.
let board = emptyArray;
let maxX = 0, maxY = 0;

const drawableTerrains = ["FOREST", "WATER", "PLAINS", "CITY", "MONSTER"]

const host = window.location.hostname;
const port = window.location.port;

const ws = new WebSocket(
    host === "cartographers.herokuapp.com" ? "wss://cartographers.herokuapp.com/api" : "ws://localhost:8080/api");

let wsPingInterval;
let httpPingInterval;

ws.onopen = function () {
    wsPingInterval = setInterval(() => {
        // ping heroku to avoid H15 idle connection error. For some reason ping from server 
	    // to client does not work with heroku.
        ws.send(JSON.stringify({ "type": "ping"}));
    }, 3000);
    httpPingInterval = setInterval(() => {
        // ping heroku to avoid dyno shut down
        d3.request("/").get();
    }, 1000 * 60 * 5);
    d3.select("#create").on("click", () => {
        const roomId = d3.select("#roomId").property("value");
        const nick = d3.select("#nick").property("value");
        const swap = d3.select("#swap").property("checked");
        const advanced = d3.select("#adv").property("checked");
        const rect = d3.select("#rect").property("checked");
        ws.send(JSON.stringify({ "type": "create", "data": { "nick": nick, "gid": roomId,
            "options": {"swap": swap, "advanced": advanced, "rectangular": rect} } }));
    });
    d3.select("#join").on("click", () => {
        const roomId = d3.select("#roomId").property("value");
        const nick = d3.select("#nick").property("value");
        ws.send(JSON.stringify({ "type": "join", "data": { "nick": nick, "gid": roomId } }));
    });
};

ws.onclose = function () {
    clearInterval(wsPingInterval);
    clearInterval(httpPingInterval);
    d3.select("#errors")
        .append("div")
        .text("Disconnected - reload site and join with same nick and room id");
}

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
            const { quest1, quest2, coins, monsters, season } = event.score;
            let idx = undefined;
            if (season === "SPRING") {
                idx = 0;
            }
            if (season === "SUMMER") {
                idx = 1;
            }
            if (season === "AUTUMN") {
                idx = 2;
            }
            if (season === "WINTER") {
                idx = 3;
            }
            scores[idx] = [quest1, quest2, coins, -monsters, quest1 + quest2 + coins - monsters];
            drawPoints();
        }
        if (event["type"] === "GOALS") {
            const { spring, autumn, winter, summer } = event;
            const score_card_to_text = (prop, season) => {
                const sc = score_cards[prop];
                const title = sc.title;
                const desc = sc.description;
                return `<div><h4>[${season}] ${title}</h4><p>${desc}</p></div>`
            };
            d3.select("#goals-section")
                .html(`${score_card_to_text(spring, "WIOSNA")}${score_card_to_text(summer, "LATO")}
                ${score_card_to_text(autumn, "JESIEŃ")}${score_card_to_text(winter, "ZIMA")}`);
            d3.select("#start").style("display", "none");
        }
        if (event["type"] === "CREATE_SUCCESS" || event["type"] === "JOIN_SUCCESS") {
            const { nick : nickname, roomId } = event.data;
            nick = nickname;
            d3.select("#start").style("display", null);
            d3.select("#goals").style("display", null);
            d3.select("#submit").style("display", null);
            d3.select("#undo").style("display", null);
            d3.select("#game-creation").style("display", "none");
            d3.select("#game-info").html(`Nick: ${nick}<br>Room id: ${roomId}<br>`);
        }
        if (event["type"] === "ERROR") {
            const { error } = event;
            d3.select("#errors")
                .append("div")
                .text(error)
                .transition()
                .duration(3000)
                .remove();
        }
        if (event["type"] === "BOARD") {
            maxX = event.height;
            maxY = event.width;
            if (board === emptyArray) {
                board = [];
                for (let x = 0; x < maxX; x++) {
                    for (let y = 0; y < maxY; y++) {
                        board.push({ "x": -x, "y": y, "type": "EMPTY", "locked": false })
                    }
                }
                drawCoins();
                drawPoints();
            }
            resetBoard();
            event.board.forEach(cell => {
                const {x, y, terrain} = cell;
                const match = board.find(c => c.x === x && c.y === y);
                if (match) {
                    match.type = terrain;
                    match.locked = true;
                }
            });
            event.ruins.forEach(point => {
                const {x, y} = point;
                const match = board.find(c => c.x === x && c.y === y);
                if (match) {
                    match.hasRuins = true;
                }
            });
            drawBoard();
        }
        if (event["type"] === "COINS") {
            coinsCount = event.coins;
            drawCoins();
        }
    }
};

const rootSvg = d3.select("#game");

rootSvg.append("svg")
    .attr("id", "board");

d3.select("#start")
    .on("click", function () { ws.send(JSON.stringify({ "type": "start" })) });

d3.select("#goals")
    .on("click", function () {
        const gs = d3.select("#goals-section");
        const display = gs.style("display");
        gs.style("display", display === 'none' ? 'block' : 'none')
     });

function resetBoard() {
    for (let cell of board) {
        cell.type = 'EMPTY';
        cell.locked = false;
    }
}

function updateTerrains(terrains) {
    const terrainUpdate = d3.select("#current-card .terrain")
        .selectAll("rect")
        .data(drawableTerrains);

    const terrainEnter = terrainUpdate.enter()
        .append("rect")
        .attr("width", 30)
        .attr("height", 30)
        .style("stroke-width", "1px")
        .style("stroke", "black");

    terrainUpdate.merge(terrainEnter)
        .attr("transform", function (d, i) { return `translate(${i * 35} 0)`; })
        .style("fill", function (d) { return `rgb(${colorByType[d]})` })
        .style("fill-opacity", d => terrains.indexOf(d) == -1 ? 0.1 : 1)
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

d3.select("#undo")
    .on("click", function (event) {
        const msg = { "type": "undo"};
        ws.send(JSON.stringify(msg));
    });

function drawPoints() {

    const ps = d3.select("#points")
        .attr("width", "100%")
        .style("max-width", "550px")
        .selectAll(".pointsSection")
        .data([scores[0], scores[1], scores[2], scores[3]]);

    const enter = ps.enter();

    // in %
    const w = 19;
    const off = (100 - 5 * w) / 4;

    // in px
    const h = 150;

    enter.append("g")
        .attr("class", "pointsSection")
        .append("rect")
        .attr("x", function (d, i) { return `${i * (w + off)}%`; })
        .attr("width", `${w}%`)
        .attr("height", h)
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .style("fill", "white");

    enter.append("line")
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .attr("x1", (d, i) => `${i * (w + off) + w / 2}%`)
        .attr("y1", 0)
        .attr("x2", (d, i) => `${i * (w + off) + w / 2}%`)
        .attr("y2", h * 2 / 3);

    enter.append("line")
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .attr("x1", (d, i) => `${i * (w + off)}%`)
        .attr("y1", h * 2 / 3)
        .attr("x2", (d, i) => `${i * (w + off) + w}%`)
        .attr("y2", h * 2 / 3);

    enter.append("line")
        .style("stroke-width", "1px")
        .style("stroke", "black")
        .attr("x1", (d, i) => `${i * (w + off)}%`)
        .attr("y1", h / 3)
        .attr("x2", (d, i) => `${i * (w + off) + w}%`)
        .attr("y2", h / 3);

    const scoreUpdate = enter.merge(ps).selectAll(".score")
        .data((d, i) => d.map(num => ({"n": num, "pidx": i})));
    const scoreEnter = scoreUpdate.enter().append("text")
        .attr("class", "score")
        .attr("y", function (d, i) {
            let x = 0;
            if (i === 2 || i == 3) {
                x = -1;
            }
            if (i === 4) {
                x = -2;
            }
            return `${30 + -x * 50}px`;
        })
        .attr("x", function (d, i) {
            let y = 0;
            if (i === 1 || i == 3) {
                y = 1;
            }
            if (i === 4) {
                y = 0.5;
            }
            return `${w / 6 + y * w / 2 + d.pidx * (w + off)}%`;
        });

    scoreUpdate.merge(scoreEnter)
        .text(d => d.n);

    if (scores[3] !== emptyArray) {
        const total = scores[0][4] + scores[1][4] + scores[2][4] + scores[3][4];
        d3.select("#points")
            .selectAll("#total-score")
            .data([0])
            .enter()
            .append("text")
            .attr("id", "total-score")
            .attr("x", `${w / 4 + 4 * (w + off)}%`)
            .attr("y", "80")
            .attr("font-size", "36")
            .text(total);
    }
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

var boardInterval = undefined;

function drawBoard() {
    if (board === emptyArray) {
        return;
    }

    const cells = rootSvg.select("#board")
        .selectAll(".cell")
        .data(board, d => `${d.x} ${d.y}`);

    const cellWidth = 100 / maxY;
    const cellHeight = 100 / maxX;

    const cellEnter = cells.enter()
        .append("svg")
        .attr("x", d => `${d.y * cellWidth}%`)
        .attr("y", d => `${-d.x * cellHeight}%`);

    const enter = cellEnter
        .append("rect")
        .attr("class", "cell")
        .attr("width", `${cellWidth}%`)
        .attr("height", `${cellHeight}%`)
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
        .attr("width", `${cellWidth*0.6}%`)
        .attr("height", `${cellHeight*0.6}%`)
        .attr("x", `${cellWidth*0.2}%`)
        .attr("y", `${cellHeight*0.2}%`)
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
        .attr("width", `${cellWidth*0.6}%`)
        .attr("height", `${cellHeight*0.6}%`)
        .attr("x", `${cellWidth*0.2}%`)
        .attr("y", `${cellHeight*0.2}%`);

    const cellsToAnimate = cells.merge(enter)
        .style("fill", function (d) { return `rgb(${colorByType[d.type]})` })
        .style("fill-opacity", 1)
        .filter(d => !d.locked && d.type !== "EMPTY")

    cellsToAnimate.interrupt();
    if (boardInterval) {
        boardInterval.stop();
    }
    boardInterval = d3.interval(() => {
        cellsToAnimate.transition()
            .ease(d3.easeLinear)
            .duration(500)
            .style("fill-opacity", 0.3)
            .transition()
            .ease(d3.easeLinear)
            .duration(500)
            .style("fill-opacity", 1);
    }, 1000);

    setTimeout(() => {
        const boardWidth = d3.select("#board").node().getBBox().width;
        rootSvg.style("height", `${maxX * (boardWidth / maxY)}px`);
    }, 5);
}
