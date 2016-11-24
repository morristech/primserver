var express = require('express');
var bodyParser  = require('body-parser');

var app = express();
app.use(bodyParser.json());

app.post('/queue/add', function(req, res) {
    var fileKey = req.body.filekey;
    var etag = req.body.etag;
console.log(fileKey);
    res.json({message: fileKey});
});

app.get('/', function (req, res) {
  res.json({message : 'Primtive Internal API v1.0', value: 'You should not be seeing this'});
})

app.listen(55000, function () {
  console.log('Listening on 55000');
});
