echo "Deploying to prim server"
git pull

echo "Pulling primitive go project"
go get -u github.com/fogleman/primitive
cd server

echo "Installing NPM packages"
npm install

echo "Killing primitive server"
forever stop 0

echo "Starting server now..."
forever start index.js

