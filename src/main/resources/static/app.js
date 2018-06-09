var stompClient = null;
var minTempInput;
var maxTempInput;
var maxTemperature;
var minTemperature;
var autoRangeBox;
var smoothingBox;
var c;
var ctx;
var image1;
var temperatureTable;
var colorSelectBox;
var colorScheme;

$(document).ready(function () {
    minTempInput = document.getElementsByName("minTempInput")[0];
    maxTempInput = document.getElementsByName("maxTempInput")[0];
    autoRangeBox = document.getElementsByName("autoRange")[0];
    smoothingBox = document.getElementsByName("smoothing")[0];
    temperatureTable = document.getElementById("temperature-table");
    colorSelectBox = document.getElementById("colorBar");
    image1 = document.getElementById("image1");
    c = document.getElementById("myCanvas");
    ctx = c.getContext("2d");
    colorScheme = 1;
});

init();

function init() {
    var socket = new SockJS('/grid-eye-app');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    stompClient.connect({}, function (frame) {
        // console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/temperature', function (message) {
            colorScheme = colorSelectBox.value;
            var temperature = JSON.parse(message.body).temperature.map(Number);
            if (autoRangeBox.checked) {
                minTempInput.disabled = true;
                maxTempInput.disabled = true;
                minTemperature = Math.min.apply(Math, temperature);
                maxTemperature = Math.max.apply(Math, temperature);
                minTempInput.value = minTemperature;
                maxTempInput.value = maxTemperature;
            } else {
                minTempInput.disabled = false;
                maxTempInput.disabled = false;
                minTemperature = minTempInput.value;
                maxTemperature = maxTempInput.value;
            }
            if (smoothingBox.checked) {
                temperatureTable.hidden = true;
                image1.hidden = false;
            } else {
                temperatureTable.hidden = false;
                image1.hidden = true;
            }
            setTemperature(temperature, minTemperature, maxTemperature);
        });
    });
}

function setTemperature(temperature, minTemperature, maxTemperature) {
    var table = temperatureTable.getElementsByTagName("td");
    for (let i = 0; i < table.length; i++) {
        table[i].innerHTML = temperature[i];
        switch (colorScheme) {
            case "1":
                table[i].style.backgroundColor =
                    getColorForPercentage((temperature[i] - minTemperature) / (maxTemperature - minTemperature) * 100);
                break;
            case "2":
                table[i].style.backgroundColor = GetHotColdColor(temperature[i], minTemperature, maxTemperature);
                break;
            case "3":
                table[i].style.backgroundColor = temp_rgb(temperature[i], minTemperature, maxTemperature);
                break;
        }
    }

    // var colorArrayR = [];
    // var colorArrayG = [];
    // var colorArrayB = [];
    // for (var a = 0; a < 8; a++) {
    //     colorArrayR[a] = new Array(8);
    //     colorArrayG[a] = new Array(8);
    //     colorArrayB[a] = new Array(8);
    // }
    var xMatrix = 0;
    var yMatrix = 0;
    for (let i = 0; i < table.length; i++) {
        var element = table[i].style.backgroundColor;
        ctx.fillStyle = element;
        ctx.fillRect(xMatrix, yMatrix, 1, 1);
        // var color = element.replace(/[A-Za-z$-()]/g, "").split(", ");
        // for (var k in color) {
        //     color[k] = parseInt(color[k]);
        //}
        //
        // colorArrayR[xMatrix][yMatrix] = color[0];
        // colorArrayG[xMatrix][yMatrix] = color[1];
        // colorArrayB[xMatrix][yMatrix] = color[2];
        //
        xMatrix++;
        if (xMatrix != 0 && xMatrix % 8 == 0) {
            xMatrix = 0;
            yMatrix++;
        }
    }
    // var colorArrayRGB = [];
    // colorArrayRGB[0] = colorArrayR;
    // colorArrayRGB[1] = colorArrayG;
    // colorArrayRGB[2] = colorArrayB;

    // var strR = colorArrayR.toString().replace(/,/g, ' ');
    // var strG = colorArrayG.toString().replace(/,/g, ' ');
    // var strB = colorArrayB.toString().replace(/,/g, ' ');

    image1.src = c.toDataURL();
}

function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(r, g, b) {
    //return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
    return componentToHex(r) + componentToHex(g) + componentToHex(b);
}

function Interpolate(start, end, steps, count) {
    var s = start,
        e = end,
        final = s + (((e - s) / steps) * count);
    return Math.floor(final);
}

function Color(_r, _g, _b) {
    var r, g, b;
    var setColors = function (_r, _g, _b) {
        r = _r;
        g = _g;
        b = _b;
    };

    setColors(_r, _g, _b);
    this.getColors = function () {
        var colors = {
            r: r,
            g: g,
            b: b
        };
        return colors;
    };
}

var getColorForPercentage = function (val) {
    var red = new Color(255, 0, 0),
        blue = new Color(0, 0, 255),
        start = blue,
        end = red;

    var startColors = start.getColors(),
        endColors = end.getColors();

    var r = Interpolate(startColors.r, endColors.r, 100, val);
    var g = Interpolate(startColors.g, endColors.g, 100, val);
    var b = Interpolate(startColors.b, endColors.b, 100, val);

    return 'rgb(' + [r, g, b].join(',') + ')';
};

function GetHotColdColor(v, vmin, vmax) {


    let r, g, b, dv;
    r = 1.0;
    g = 1.0;
    b = 1.0;

    if (v < vmin) {
        v = vmin;
    }
    if (v > vmax) {
        v = vmax;
    }
    dv = vmax - vmin;

    if (v < (vmin + 0.25 * dv)) {
        r = 0;
        g = 4 * (v - vmin) / dv;
    }
    else if (v < (vmin + 0.5 * dv)) {
        r = 0;
        b = 1 + 4 * (vmin + 0.25 * dv - v) / dv;
    }
    else if (v < (vmin + 0.75 * dv)) {
        r = 4 * (v - vmin - 0.5 * dv) / dv;
        b = 0
    }
    else {
        g = 1 + 4 * (vmin + 0.75 * dv - v) / dv;
        b = 0;
    }

    return 'rgb(' + [r * 255, g * 255, b * 255].join(',') + ')';
}

const colR = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23, 62, 101, 138, 176, 215, 253, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255];
const colG = [100, 116, 132, 148, 164, 180, 196, 212, 228, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 250, 240, 230, 220, 210, 200, 190, 180, 170, 160, 150, 140, 130, 120, 110, 100, 90, 80, 70, 60, 50, 40, 30, 20, 10, 0, 0, 0, 0, 0];
const colB = [255, 255, 255, 255, 255, 255, 255, 255, 255, 244, 208, 168, 131, 92, 54, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 32, 48, 64];

function temp_rgb(temp, min, max) {

    if (temp < min) {
        temp = min;
    }
    else if (temp > max) {
        temp = max;
    }
    let index = (((temp - min) * (53 - 0)) / (max - min)) + 0;

    return 'rgb(' + [colR[Math.round(index)], colG[Math.round(index)], colB[Math.round(index)]].join(',') + ')';
}