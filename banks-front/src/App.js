import React from 'react';
import './App.css';
import Main from './Main';
import Op from './Op';
import Nordea from './Nordea';
import Spankki from './Spankki';

import {
  BrowserRouter as Router,
  Switch, Route, Link
} from "react-router-dom";


function App() {
  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <Link to="/" className="App-header-link">
            <p>Banks-api</p>
          </Link>
        </header>
      </div>

      <Switch>
        <Route path="/osuuspankki">
          <Op />
        </Route>
        <Route path="/nordea">
          <Nordea />
        </Route>
        <Route path="/spankki">
          <Spankki />
        </Route>
        <Route path="/">
          <Main />
        </Route>
      </Switch>

    </Router>
  );
}

export default App;
