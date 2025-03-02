# Project setup

## Required files and environment related setup
- Make sure you have a .env file in the backend directory, it must have the following variables (with the correct associated values) :WEALTHPLEX_APPLICATION_CREDENTIALS (which is the path to service account),WEALTHPLEX_PROJECTID
  OPENAI_API_KEY,FINNHUB_API_KEY
- In docker compose.yaml you will notice LOCAL_IP and REACT_BACKEND_URL. Both of these have a blank ip address you can fill out with you actual ipv4 (local) address. This should be the address on which you will be running your docker containers
- Make sure you have docker desktop installed and running

## Running the project
- You will notice there is a compose build and run.bat file, run that script and it will rebuild and start the containers
- If you want to stop it, you can do it through the docker desktop, or when in the root of the project doing docker compose down
- If you made some changes, then run the script again, otherwise you just need to do docker compose up
- Once it is running, you can access the frontend on any device on your network through the url provided in the backend container's console
- (note that you might have to change the ip often, if your host machine disconnected and reconnected to the network)