var aws = require('aws-sdk');
var sqs = new aws.SQS();

exports.handler = function(event, context, callback) {
    if(!event.filekey || !event.fcm_token) {
        callback("Filekey or FCM Token Missing");
        return;
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
