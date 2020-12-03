import React from 'react';
import { Switch, Route, Link } from "react-router-dom";
import AccountListing from "./AccountListing";

class NordeaApiSelection extends React.Component {
  constructor() {
    super();
    this.state = {
      error: null,
      isLoaded: false,
      accounts: []
    };
  }


  /*  componentDidMount() {
   fetch("https://localhost:8443/nordea/accounts")
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
  } */


  render() {
    const dummyData = {
      group_header: {
        message_identification: "GdGQd2RvjgBjzNwZ",
        creation_date_time: "2020-12-03T15:00:34.773286Z",
        http_code: 200
      },
      response:
      {
        accounts:
          [{
            country: "FI",
            account_numbers: [{ value: "FI7473834510057469", _type: "IBAN" }], 
            currency: "EUR", 
            account_name: "Aino Salo", 
            product: "KÄYTTÖTILI", 
            account_type: "Current", 
            available_balance: "3395.40", 
            booked_balance: "3383.30", 
            value_dated_balance: "3383.30", 
            bank: { name: "Nordea", bic: "NDEAFIHH", country: "FI" }, 
            status: "OPEN", 
            credit_limit: "100.00",
            latest_transaction_booking_date: "2020-12-03",
            _links: [{ rel: "details", href: "/v4/accounts/FI7473834510057469-EUR" },
            { rel: "transactions", href: "/v4/accounts/FI7473834510057469-EUR/transactions" }],
            _id: "FI7473834510057469-EUR"
          },
          {
            country: "FI",
            account_numbers: [{ value: "FI7473834510057469", _type: "IBAN" }], 
            currency: "EUR", 
            account_name: "Aino Salo", 
            product: "KÄYTTÖTILI", 
            account_type: "Current", 
            available_balance: "3395.40", 
            booked_balance: "3383.30", 
            value_dated_balance: "3383.30", 
            bank: { name: "Nordea", bic: "NDEAFIHH", country: "FI" }, 
            status: "OPEN", 
            credit_limit: "100.00",
            latest_transaction_booking_date: "2020-12-03",
            _links: [{ rel: "details", href: "/v4/accounts/FI7473834510057469-EUR" },
            { rel: "transactions", href: "/v4/accounts/FI7473834510057469-EUR/transactions" }],
            _id: "FI7473834510057469-EUR"
          }
          ]
      }
    };

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
          <Route path={"/nordea/accounts"}>
            <AccountListing accounts={dummyData.response.accounts} />
          </Route>
          <Route path="/nordea">
            <h3>Please select a topic.</h3>
          </Route>
        </Switch>

      </div>
    );
  }


}

export default NordeaApiSelection;