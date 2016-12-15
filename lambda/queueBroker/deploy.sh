zip -r out.zip -xi index.js node_modules/
aws lambda update-function-code --function-name queueBroker --zip-file fileb://out.zip
rm out.zip
