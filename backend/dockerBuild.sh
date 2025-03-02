docker build -t wealthplex_backend .
cd ..
cd my-app
docker build -t wealthplex_frontend .
cd ..

docker compose up