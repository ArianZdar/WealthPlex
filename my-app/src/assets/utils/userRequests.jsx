import { useEffect, useState } from "react";

const root = "http://localhost:8080";

async function signup(username, password) {
    try {
        const response = await fetch(root + "/users", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
        });

        if (!response.ok) throw new Error("Failed to signup");
        return await response;
        
    } catch (error) {
        console.error("Signup Error:", error.message);
        throw error; // Rethrow error to handle it in UI
    }
}

async function login(username, password) {
    try {
        const response = await fetch(root + `/users/login`, { // Assuming login endpoint
            method: "POST", // Change GET to POST for authentication
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
        });

        if (!response.ok) throw new Error("Failed to login");

        const result = await response;
        console.log("Login Response:", result);
        return result;
    } catch (error) {
        console.error("Login Error:", error.message);
        throw error;
    }
}

async function getPortfolioValue(username) {
    try {
        const response = await fetch(root + `/users/`+username+`/portfolio/value`,);

        if (!response.ok) throw new Error("Failed to get protfolio value");

        const result = await response.text();
        console.log("Got profolio value :", result);
        return result;
    } catch (error) {
        console.error("Fetch portfolio value error :", error.message);
    }

}

async function getUserProfit(username) {
    try {
        const response = await fetch(root + "/users/"+username+"/profit",);

        if (!response.ok) throw new Error("Failed to get protfolio profit");

        const result = await response.text();
        console.log("Got profolio value :", result);
        return result;
    } catch (error) {
        console.error("Fetch portfolio value error :", error.message);
    }

}



async function addStockToWatchlist(username, symbol) {
    try {
        const response = await fetch(root + "/users/"+ username + "/watchlist/"+symbol, {
            method: "POST"
        });

        if (!response.ok) throw new Error("Failed to add stock to list");

        const result = await response.json();
        console.log("Add to watchlist :", result);
        return result;
    } catch (error) {
        console.error("Error adding to watchlist:", error.message);
    }
}

async function setLongTermInvestor(userId, isLongTermInvestor) {
    try {
        const response = await fetch(root + "/users/"+ userId + "/investmentType/"+isLongTermInvestor, {
            method: "POST"
        });

        if (!response.ok) throw new Error("Failed to set investment type");

        const result = await response.json();
        console.log("Set investment type :", result);
        return result;
    } catch (error) {
        console.error("Error setting investment type :", error.message);
    }

}

async function removeStockFromWatchlist(username, symbol) {
    try {
        const response = await fetch(root + "/users/"+ username + "/watchlist/"+symbol, {
            method: "DELETE"
        });

        if (!response.ok) throw new Error("Failed to remove stock from watchlist");

        const result = await response.json();
        console.log("Remove from watchlist response :", result);
        return result;
    } catch (error) {
        console.error("Error removing stock from watchlust:", error.message);
    }
}

async function getUserWatchlist(username) {
    try {
        const response = await fetch(root + "/users/"+ username + "/watchlist", {
            method: "GET"
        });

        if (!response.ok) throw new Error("Failed to get user watchlist");

        const result = await response.json();
        console.log("User watchlist Response:", result);
        return result;
    } catch (error) {
        error.message = "Failed to get user watchlist";
        console.error("User watchlist Error:", error.message);
    }
}

async function buyStock(username, amount, symbol, price) {
    try {
        const response = await fetch(root + "/users/"+ username + "/stocks/"+symbol, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ amount, price }),
        });

        if (!response.ok) throw new Error("Failed to buy stocks");

        const result = await response.json();
        console.log("Buy stock response:", result);
        return result;
    } catch (error) {
        console.error("Buy stock Error:", error.message);
    }
}

async function sellStock(username, amount, symbol) {
    try {
        const response = await fetch(root + "/users/"+ username + "/stocks/"+symbol+"/sell/"+amount, {
            method: "POST",
           
        });

        if (!response.ok) throw new Error("Failed to sell stock ");

        const result = await response.json();
        console.log("Sell stock Response:", result);
        return result;
    } catch (error) {
        console.error("Sell stock Error:", error.message);
    }
}

async function getStocks(username) {
    try {
        const response = await fetch(root + "/users/"+ username + "/stocks", {
            method: "GET"
        });

        if (!response.ok) throw new Error("Failed get stocks");

        const result = await response.json();
        console.log("Get stock Response:", result);
        return result;
    } catch (error) {
        console.error("Get stock Error:", error.message);
    }
}

async function getProfitOnStock(username,Symbol) {
    try {
        const response = await fetch(root + "/users/"+ username + "/stocks/"+Symbol+"/profit", {
            method: "GET"
        });

        if (!response.ok) throw new Error("Failed to get profit on position");

        let result = await response.text()
        result = parseFloat(result).toFixed(2);
        console.log("Get profit response:", result);
        return result;
    } catch (error) {
        console.error("Get profit error:", error.message);
        error("Failed to get profit on position");
    }

}








export { signup, login ,getPortfolioValue,setLongTermInvestor,getUserProfit,addStockToWatchlist,removeStockFromWatchlist,getUserWatchlist,buyStock,sellStock,getStocks,getProfitOnStock};

