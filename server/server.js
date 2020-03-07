var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

server.listen(8081, function(){
    console.log("server is running")
})

