import React from 'react';
import { Switch, Route, Link } from "react-router-dom";
import NordeaAccountListing from "./NordeaAccountListing";
import Transactions from './Transactions'
import Payments from './Payments'

class NordeaApiSelection extends React.Component {
  constructor() {
    super();
    this.state = {
      error: null,
      isLoaded: false,
      accounts: []
    };
  }

  componentDidMount() {
    fetch("https://localhost:8443/nordea/accounts/all")
      .then(res => res.json())
      .then(
        (result) => {
          if (result.response) {
            this.setState({
              isLoaded: true,
              accounts: result.response.accounts
            });
          }
        },
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
        }
      )
  }


  render() {

    return (
      <div>
        <div className="bank-main">
          <h3>Nordea</h3>
        </div>
        <div className="navbar">
          <Link to="/nordea/accounts">
            <p>Accounts</p>
          </Link>
          <Link to="/nordea/cards">
            <p>Cards</p>
          </Link>
          <Link to="/nordea/payments">
            <p>Payments</p>
          </Link>
        </div>

        <Switch>
          <Route exact path={"/nordea/accounts"}>
            <NordeaAccountListing accounts={this.state.accounts} />
          </Route>
          <Route
            path="/nordea/accounts/:accountId"
            render={({ match }) => <Transactions match={match} />} />
          <Route exact path={"/nordea/payments"}>
            <Payments />
          </Route>
        </Switch>

      </div>
    );
  }


}

export default NordeaApiSelection;