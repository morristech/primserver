var aws = require('aws-sdk');
var sqs = new aws.SQS();

exports.handler = function(event, context, callback) {
    if(!event.filekey || !event.fcmtoken) {
        callback("Filekey or FCM Token Missing");
        return;
    }

    if(event.filekey.length != 36 && event.filekey.split('-').length != 5) {
        callback("Filekey looks strange");
        return;
    }

    if(event.shape) {
        var shape = parseInt(event.shape);
        if(shape < 0 || shape > 8) {
            callback("Unknown shape");
            return;
        }
    }

    if(event.numshapes) {
        var numshapes = parseInt(event.numshapes);
        if(numshapes < 1 || numshapes > 200) {
            callback("Invalid number of shapes");
            return;
        }
    }

    var params = {
        QueueUrl: process.env.SQS_Q_URL,
        MessageBody: JSON.stringify(event),
    };

    sqs.sendMessage(params, function(err, data) {
        if(err)
            callback(err);
        else
            callback(null, data);
    });
};
