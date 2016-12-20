echo "Compressing Files"
zip -r out.zip -xi index.js node_modules/ > /dev/null
echo "Uploading to lambda"
aws lambda update-function-code --function-name queueBroker --zip-file fileb://out.zip
echo "Cleaning up"
rm out.zip
