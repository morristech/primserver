var aws      = require('aws-sdk');
aws.config   = {
    "accessKeyId": "AKIAI5VAD6UNWTKDJUWA",
    "secretAccessKey": "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",
    "region": "us-east-1"
};

var sqs      = new aws.SQS();
var queueUrl = "https://sqs.us-east-1.amazonaws.com/873541805960/inputq";

setInterval(function() {
    receiveMessage()
}, 1000);

var receiveMessage = function() {
    var params = { QueueUrl: queueUrl };
    sqs.receiveMessage(params, function(err, data) {
        if(err) {
            console.log(err);
        } else {
            if(data.Messages) {
                var message = data.Messages[0];
                if(message) {
                    var body = message.Body;
                    var rcptHandle = message.ReceiptHandle;
                    processMessage(body, rcptHandle);          
                }   
            } else {
                console.log(data);
            }   
        }   
    });
};

var processMessage = function(message, rcptHandle) {
    // DO the processing here
};

var deleteMessage = function(receipt) {
    var params = {
        QueueUrl: queueUrl,
        ReceiptHandle: receipt
    };
    
    sqs.deleteMessage(params, function(err, data) {
        if(err) {
            res.send(err);
        } 
        else {
            res.send(data);
        } 
    });
};
