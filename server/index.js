var express = require('express');
var bodyParser  = require('body-parser');
var fs = require('fs');
var file = 'test.db';
var exists = fs.existsSync(file);
var sqlite3 = require('sqlite3').verbose();
//var db = new sqlite3.Database(file);
var app = express();
app.use(bodyParser.json());

app.post('/queue/add', function(req, res) {
    var fileKey = req.body.fk;
    var etag = req.body.et;
    var gcmToken = req.body.gcm;

    res.json({ message: "job queued" });

    // Open DB and add job to queue
    var db = new sqlite3.Database(file);
    db.serialize(function() {
        if(!exists) {
            console.log("Creating tables now");
            db.run('create table jobqueue (filkey TEXT, etag TEXT, gcmToken)');
        }
        var insert = 'insert into jobqueue values ({values})';
        var values = '"' + fileKey + '","' + etag + '","' + gcmToken + '"';
        insert = insert.replace('{values}', values);
        db.run(insert);
    });
    db.close();
});


app.listen(55000, function () {
  console.log('Listening on 55000');
});
