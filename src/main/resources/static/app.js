var stompClient = null;

init();

function init(){
    var socket = new SockJS('/grid-eye-app');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/client/temperature', function (temperature) {
            console.log(temperature);
        });
    });
}