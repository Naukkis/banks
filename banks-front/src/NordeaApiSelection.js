import React from 'react';
import { Switch, Route, Link } from "react-router-dom";
import AccountListing from "./AccountListing";
import Transactions from './Transactions'

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
          this.setState({
            isLoaded: true,
            accounts: result.response.accounts
          });
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
        <div>
          <Link to="/nordea/accounts">
            <p>Accounts</p>
          </Link>
          <Link to="/nordea/cards">
            <p>Cards</p>
          </Link>
        </div>

        <Switch>
          <Route exact path={"/nordea/accounts"}>
            <AccountListing accounts={this.state.accounts} />
          </Route>
          <Route
            path="/nordea/accounts/:accountId"
            render={({ match }) => <Transactions match={match} />} />
        </Switch>

      </div>
    );
  }


}

export default NordeaApiSelection;