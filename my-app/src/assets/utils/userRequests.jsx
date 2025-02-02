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
        await response;
        
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

        const result = await response;
        console.log("Login Response:", result);
        return result;
    } catch (error) {
        console.error("Login Error:", error.message);
        throw error;
    }

}

async function addStockToWatchlist(username, symbol) {
    try {
        const response = await fetch(root + "/users/"+ username + "/watchlist/"+symbol, {
            method: "POST"
        });

        if (!response.ok) throw new Error("Failed to add stock to list");

        const result = await response.json();
        console.log("Signup Response:", result);
        return result;
    } catch (error) {
        console.error("Signup Error:", error.message);
        throw error; // Rethrow error to handle it in UI
    }
}


async function removeStockFromWatchlist(username, symbol) {
    try {
        const response = await fetch(root + "/users/"+ username + "/watchlist/"+symbol, {
            method: "DELETE"
        });

        if (!response.ok) throw new Error("Failed to add stock to list");

        const result = await response.json();
        console.log("Signup Response:", result);
        return result;
    } catch (error) {
        console.error("Signup Error:", error.message);
        throw error; // Rethrow error to handle it in UI
    }
}

async function getUserWatchlist(username) {
    try {
        const response = await fetch(root + "/users/"+ username + "/watchlist", {
            method: "GET"
        });

        if (!response.ok) throw new Error("Failed to add stock to list");

        const result = await response.json();
        console.log("Signup Response:", result);
        return result;
    } catch (error) {
        console.error("Signup Error:", error.message);
        throw error; // Rethrow error to handle it in UI
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
        console.log("Signup Response:", result);
        return result;
    } catch (error) {
        console.error("Signup Error:", error.message);
        throw error; // Rethrow error to handle it in UI
    }
}

async function sellStock(username, amount, symbol) {
    try {
        const response = await fetch(root + "/users/"+ username + "/stocks/"+symbol+"/sell/"+amount, {
            method: "POST",
           
        });

        if (!response.ok) throw new Error("Failed to sell stock ");

        const result = await response.json();
        console.log("Signup Response:", result);
        return result;
    } catch (error) {
        console.error("Signup Error:", error.message);
        throw error; // Rethrow error to handle it in UI
    }
}

async function getStocks(username) {
    try {
        const response = await fetch(root + "/users/"+ username + "/stocks", {
            method: "GET"
        });

        if (!response.ok) throw new Error("Failed to add stock to list");

        const result = await response.json();
        console.log("Signup Response:", result);
        return result;
    } catch (error) {
        console.error("Signup Error:", error.message);
        throw error; // Rethrow error to handle it in UI
    }
}

async function getProfitOnStock(username,Symbol) {
    try {
        const response = await fetch(root + "/users/"+ username + "/stocks/"+Symbol+"/profit", {
            method: "GET"
        });

        if (!response.ok) throw new Error("Failed to add stock to list");

        const result = await response;
        console.log("Signup Response:", result);
        return result;
    } catch (error) {
        console.error("Signup Error:", error.message);
        throw error; // Rethrow error to handle it in UI
    }

}







// Export both functions
export { signup, login ,getPortfolioValue,addStockToWatchlist,removeStockFromWatchlist,getUserWatchlist,buyStock,sellStock,getStocks,getProfitOnStock};

