var stompClient = null;
var minTempInput;
var maxTempInput;
var maxTemperature;
var minTemperature;
var autoRangeBox;

$(document).ready(function () {
    minTempInput = document.getElementsByName("minTempInput")[0];
    maxTempInput = document.getElementsByName("maxTempInput")[0];
    maxTemperature;
    minTemperature;
    autoRangeBox = document.getElementsByName("autoRange")[0];
});

init();

function init() {
    var socket = new SockJS('/grid-eye-app');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/temperature', function (message) {
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
            setTemperature(temperature, minTemperature, maxTemperature);
        });
    });
}

function setTemperature(temperature, minTemperature, maxTemperature) {
    var table = document.getElementById("temperature-table").getElementsByTagName("td");
    for (i = 0, j = 63; i < table.length; i++, j--) {
        table[i].innerHTML = temperature[j];
        table[i].style.backgroundColor =
            getColorForPercentage((temperature[j] - minTemperature) / (maxTemperature - minTemperature) * 100);
    }
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
