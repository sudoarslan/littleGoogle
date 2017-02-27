var express = require('express');
var path = require("path");
var app = express();

app.get('/', function(req, res) {
  res.sendFile(path.join(__dirname + '/home.html'));
})

app.listen(3000, function() {
  console.log('Little-Google listening at http://localhost:3000/');
})
