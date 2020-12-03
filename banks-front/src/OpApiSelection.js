import React from 'react';
import { Link } from "react-router-dom";
import AccountListing from "./AccountListing";

class OpApiSelection extends React.Component {
    constructor() {
        super();
        this.state = {
            error: null,
            isLoaded: false,
            items: []
        };
    }

    componentDidMount() {
        fetch("https://localhost:8443/accountsall")
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        items: result.items
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
                    <h3>Osuuspankki</h3>
                </div>
                <div>
                    <Link to="/op/accounts">
                        <AccountListing accounts={this.state.items}/>
                    </Link>
                    <Link to="/op/cards">
                        <p>Cards</p>
                    </Link>
                </div>
            </div>
        );
    }

}

export default OpApiSelection;