import { useEffect, useState } from "react";

const root = "http://localhost:8080";


async function getStockRequests(symbol) {
    try {
        const response = await fetch(root + "/stocks/"+ symbol, {
            method: "GET"
        });

        if (!response.ok) throw new Error("Failed to get stock recomendations");

        const result = await response.json();
        console.log("stock recomendations Response:", result);
        return result;
    } catch (error) {
        console.error("stock recomendations error:", error.message);
    }
}



    async function getStockRating(symbol) {
        try {
            const username = localStorage.getItem("loggedInUsername");
            const response = await fetch(`${root}/api/stocks/rating/${symbol}?username=${username}`, {
                method: "GET"
            });
    
            if (!response.ok) throw new Error("Failed to get stock rating");
            const result = await response.text();
            console.log("stock rating Response:", result);
            const extractedInt = parseInt(result.split("/")[0], 10);
            return extractedInt;
        } catch (error) {
            console.error("stock rating error:", error.message);
        }
    }

    async function getStockHistory(symbol) {
        try {
            const username = localStorage.getItem("loggedInUsername");
            const response = await fetch(`${root}/stocks/history/${symbol}`, {
                method: "GET"
            });
    
            if (!response.ok) throw new Error("Failed to get stock history");
            const result = await response.json();
            console.log(result);
            return (result);
        } catch (error) {
            console.error("stock history error:", error.message);
        }
    }

    async function getStockExplanation(symbol) {
        try {
            const username = localStorage.getItem("loggedInUsername");
            const response = await fetch(`${root}/api/stocks/explanation/${symbol}?username=${username}`, {
                method: "GET"
            });
    
            if (!response.ok) throw new Error("Failed to get stock rating");
            const result = await response.text();
            return result;
        } catch (error) {
            console.error("stock rating error:", error.message);
        }
    }




export {getStockRequests, getStockRating, getStockExplanation,getStockHistory};