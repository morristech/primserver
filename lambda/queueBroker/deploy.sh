zip -r out.zip -xi index.js node_modules/ > /dev/null
aws lambda update-function-code --function-name queueBroker --zip-file fileb://out.zip
rm out.zip
