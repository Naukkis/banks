import React from 'react';
import './App.css';
import Main from './Main';
import Op from './Op';
import Nordea from './Nordea';
import Spankki from './Spankki';

import {
  BrowserRouter as Router,
  Switch, Route
} from "react-router-dom";


function App() {
  return (
    <Router>
        <div className="App">
      <header className="App-header">
        <p>
         Banks-api
        </p>
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
