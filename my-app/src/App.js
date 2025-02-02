import React from "react";
import { BrowserRouter as Router, Routes, Route, useLocation } from "react-router-dom";
import { AnimatePresence, motion } from "framer-motion";
import Home from "./pages/homepage/home";
import Portfolio from "./pages/portfoliopage/portfolio";
import Login from "./pages/loginpage/login";
import Register from "./pages/RegistrationPage/registration";
import InvestmentType from "./pages/investorTypePage/investorType";
import InfoStock from "./pages/stockinfopage/stockinfo";

function AnimatedRoutes() {
  const location = useLocation(); // Get current page location

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<PageTransition><Home /></PageTransition>} />
        <Route path="/portfolio" element={<PageTransition><Portfolio /></PageTransition>} />
        <Route path="/login" element={<PageTransition><Login /></PageTransition>} />
        <Route path="/registration" element={<PageTransition><Register /></PageTransition>} />
        <Route path="/InvestmentType" element={<PageTransition><InvestmentType /></PageTransition>} />
        <Route path="/InfoStock" element={<PageTransition><InfoStock /></PageTransition>} />
      </Routes>
    </AnimatePresence>
  );
}

// Page transition effect
const PageTransition = ({ children }) => (
  <motion.div
    initial={{ opacity: 0, scale: 0.9 }}
    animate={{ opacity: 1, scale: 1 }}
    exit={{ opacity: 0, scale: 1.1 }}
    transition={{ duration: 0.8, ease: "easeInOut" }} 
  >
    {children}
  </motion.div>
);

function App() {
  return (
    <Router>
      <AnimatedRoutes />
    </Router>
  );
}

export default App;
