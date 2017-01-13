var aws = require('aws-sdk');
var fs = require('fs');
var exec = require('child_process').exec;
var request = require('request');

if(!process.env.ACC_KEY_ID ||
       !process.env.SEC_ACCESS_KEY ||
       !process.env.SQS_QUEUE ||
       !process.env.S3_IN_BUCKET ||
       !process.env.S3_OUT_BUCKET ||
       !process.env.FCM_API_KEY) {
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
    var msg = {};
    try {
        msg = JSON.parse(message);
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
            processOnFile('/tmp/', params.Key, msg.fcmtoken);
        });
        console.log("Downloading file --> " + params.Key);
        s3.getObject(params).createReadStream().on('error', function(err) {
            console.log("Unable to get input file. Exiting");
            deleteMessage(rcptHandle);
            return;
        }).pipe(file);
    }
};


var processOnFile = function(tempFolder, fileKey, fcmToken) {
    console.log("Processing: " + fileKey);
    var primitive = 'primitive -i ' + tempFolder + fileKey + ' -o ' + tempFolder + fileKey + '-out.jpg -n 50';
    //var primitive = spawn('ls', ['-l', '~']);
    exec(primitive, (error, stdout, stderr) => {
        if(error) {
            console.log(error);
            return;
        }
        console.log("Success --> " + fileKey + '-out.jpg');
        uploadToS3(fileKey, fcmToken);
    });
};

var uploadToS3 = function(fileKey, fcmToken) {
    var fileName = '/tmp/' + fileKey + '-out.jpg';
    fs.readFile(fileName, (err, fileData) => {
        if(err) {
            console.log(err);
            console.log("Unable to read the result file, exiting");
            return;
        }        
        var params = {};
        params.Bucket = process.env.S3_OUT_BUCKET;
        params.Key = fileKey;
        params.Body = fileData;
        params.ACL = 'public-read';
        params.ContentType = 'image/jpeg';
        s3.putObject(params, (err, data) => {
            if(err) {
                console.log(err);
                console.log("Unable to upload output file to S3, exiting");
                return;
            }
            console.log("Successfully uploaded output file");
            sendNotification(fileKey, fcmToken);
        });
    }); 
};

var sendNotification = function(fileKey, fcmToken) {
    console.log("Sending Notification : Filekey = " + fileKey + " FcmToken = " + fcmToken);
    var options = {
        method: 'POST',
        url: 'https://fcm.googleapis.com/fcm/send',
        headers: {
            'Authorization': 'key=' + process.env.FCM_AUTH,
            'Content-Type': 'application/json'
        },
        body: {
            to: fcmToken,
            data: {
                key: fileKey
            }
        },
        json: true
    };

    request(options, function (error, response, body) {
        if (error) {
            console.log(error);
            console.log("Error sending notification");
            return;
        }
        console.log(body);
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
