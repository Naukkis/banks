import React from 'react';
import { Switch, Route, Link } from "react-router-dom";
import OpAccountListing from "./OpAccountListing";
import OpTransactions from './OpTransactions'
import Payments from './Payments'

class OpApiSelection extends React.Component {
    constructor() {
        super();
        this.state = {
            error: null,
            isLoaded: false,
            accounts: []
        };
    }

    componentDidMount() {
        fetch("https://localhost:8443/op/accounts/all")
            .then(res => res.json())
            .then(
                (result) => {
                    if (result.accounts) {
                        this.setState({
                            isLoaded: true,
                            accounts: result.accounts
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
                    <h3>Osuuspankki</h3>
                </div>
                <div className="navbar">
                    <Link to="/op/accounts">
                        <p>Accounts</p>
                    </Link>
                    <Link to="/op/cards">
                        <p>Cards</p>
                    </Link>
                    <Link to="/op/payments">
                        <p>Payments</p>
                    </Link>
                </div>


                <Switch>
                    <Route exact path={"/op/accounts"}>
                        <OpAccountListing accounts={this.state.accounts} />
                    </Route>
                    <Route
                        path="/op/accounts/:accountId"
                        render={({ match }) => <OpTransactions match={match} />} />
                    <Route exact path={"/op/payments"}>
                        <Payments />
                    </Route>
                </Switch>

            </div>
        );
    }

}

export default OpApiSelection;