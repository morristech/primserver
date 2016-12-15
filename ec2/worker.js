var aws = require('aws-sdk');
var fs = require('fs');
var exec = require('child_process').exec;

if(!process.env.ACC_KEY_ID || !process.env.SEC_ACCESS_KEY || !process.env.SQS_QUEUE || !process.env.S3_IN_BUCKET) {
    console.log("The environment is not appropriatly set");
    return;
}

aws.config = {
    "accessKeyId": process.env.ACC_KEY_ID,
    "secretAccessKey": process.env.SEC_ACCESS_KEY,
    "region": "us-east-1"
};

var s3 = new aws.S3();
var sqs = new aws.SQS();
var queueUrl = process.env.SQS_QUEUE;


setInterval(function() {
    receiveMessage()
}, 3000);


var receiveMessage = function() {
    var params = { QueueUrl: queueUrl };
    sqs.receiveMessage(params, function(err, data) {
        if(err) {
            console.log(err);
        } else {
            if(data.Messages) {
                var message = data.Messages[0];
                if(message) {
                    console.log("Message received");
                    var body = message.Body;
                    var rcptHandle = message.ReceiptHandle;
                    processMessage(body, rcptHandle);          
                }   
            } else {
                //console.log(data);
            }   
        }   
    });
};


var processMessage = function(message, rcptHandle) {
    // param should be { Bucket: <bucket name>, Key: <key> }
    var params = {};
    try {
        var msg = JSON.parse(message);
        params.Key = msg.filekey;
        params.Bucket = process.env.S3_IN_BUCKET
    } catch(error) {
        console.log(error);
        console.log("Unable to process message.");
        console.log(message);
        deleteMessage(rcptHandle);
        return;
    }

    if(params && params.Key && params.Bucket) {
        deleteMessage(rcptHandle);
        var filePath = '/tmp/' + params.Key;
        var file = fs.createWriteStream(filePath);
        file.on('close', function() {
            processOnFile('/tmp/', params.Key);
        });
        console.log("Downloading file --> " + params.Key);
        s3.getObject(params).createReadStream().on('error', function(err) {
            console.log("Unable to get input file. Exiting");
            deleteMessage(rcptHandle);
            return;
        }).pipe(file);
    }
};


var processOnFile = function(tempFolder, fileKey) {
    console.log("Processing: " + fileKey);
    var primitive = 'primitive -i ' + tempFolder + fileKey + ' -o ' + tempFolder + fileKey + '-out.jpg -n 50';
    //var primitive = spawn('ls', ['-l', '~']);
    exec(primitive, (error, stdout, stderr) => {
        if(error) {
            console.log(error);
            return;
        }
        console.log("Success --> " + fileKey + '-out.jpg');
        // TODO Upload to S3
    });
};


var deleteMessage = function(receipt) {
    var params = {
        QueueUrl: queueUrl,
        ReceiptHandle: receipt
    };
    
    sqs.deleteMessage(params, function(err, data) {
        if(err) {
            console.log("Error deleting message");
        } else {
            console.log("Deletion completed");
        } 
    });
};
