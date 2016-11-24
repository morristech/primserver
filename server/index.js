var express = require('express');
var app = express();

app.get('/', function (req, res) {
  res.json({message : 'PRIMITIVE API v1.0'})'
})

app.listen(55000, function () {
  console.log('Running on 55000')
});
