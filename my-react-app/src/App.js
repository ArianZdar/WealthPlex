import React from 'react';
import logo from './logo.svg';
import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Greeting from './components/greeting';
import Home from './pages/homepage/home';

function App() {
  return (
      <div className="App">
        <Home></Home>
        <h1>app page</h1>
      </div>
  );
}

export default App;