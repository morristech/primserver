var express = require('express');
var bodyParser  = require('body-parser');
var fs = require('fs');
var file = 'test.db';
var exists = fs.existsSync(file);
var sqlite3 = require('sqlite3').verbose();
var db = new sqlite3.Database(file);
var app = express();
app.use(bodyParser.json());




app.post('/queue/add', function(req, res) {
    var fileKey = req.body.filekey;
    var etag = req.body.etag;
    res.json({message: "Ok, job queued"});
});



app.listen(55000, function () {
  console.log('Listening on 55000');
});
