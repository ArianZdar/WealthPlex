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

        const result = await response.json();
        console.log("Signup Response:", result);
        return result;
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

// Export both functions
export { signup, login ,getPortfolioValue};

