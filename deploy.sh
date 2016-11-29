echo "Deploying to prim server"
git pull

echo "Pulling primitive go project"
go get -u github.com/fogleman/primitive
cd ec2

echo "Installing NPM packages"
npm install

echo "Killing primitive server"
forever stopall

echo "Starting server now..."
forever --minUptime 100 start worker.js
