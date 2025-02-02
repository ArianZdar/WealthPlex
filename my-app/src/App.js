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
  const location = useLocation(); 

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<FadeInScale><Home /></FadeInScale>} />
        <Route path="/portfolio" element={<SlideFromRight><Portfolio /></SlideFromRight>} />
        <Route path="/login" element={<FadeOnly><Login /></FadeOnly>} />
        <Route path="/registration" element={<SlideFromRight><Register /></SlideFromRight>} />
        <Route path="/InvestmentType" element={<FadeInScale><InvestmentType /></FadeInScale>} />
        <Route path="/InfoStock" element={<FadeOnly><InfoStock /></FadeOnly>} />
      </Routes>
    </AnimatePresence>
  );
}

// Page transitions
const FadeInScale = ({ children }) => (
  <motion.div
    initial={{ opacity: 0, scale: 0.9 }}
    animate={{ opacity: 1, scale: 1 }}
    exit={{ opacity: 0, scale: 1.05 }}
    transition={{ duration: 0.3, ease: "easeInOut" }} 
  >
    {children}
  </motion.div>
);

const SlideFromRight = ({ children }) => (
  <motion.div
    initial={{ x: "100%", opacity: 0 }}
    animate={{ x: 0, opacity: 1 }}
    exit={{ x: "-100%", opacity: 0 }}
    transition={{ duration: 0.3, ease: "easeInOut" }}
  >
    {children}
  </motion.div>
);

const FadeOnly = ({ children }) => (
  <motion.div
    initial={{ opacity: 0 }}
    animate={{ opacity: 1 }}
    exit={{ opacity: 0 }}
    transition={{ duration: 0.2, ease: "easeOut" }}
  >
    {children}
  </motion.div>
);

// ✅ Wrap `AnimatedRoutes` inside `App` and export it
function App() {
  return (
    <Router>
      <AnimatedRoutes />
    </Router>
  );
}

export default App;
