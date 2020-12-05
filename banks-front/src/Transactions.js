import React from 'react';
import {
   
} from 'react-router-dom'

import Transaction from './Transaction'

class Transactions extends React.Component {
    constructor() {
        super();
        this.state = {
            loaded: false,
            transactions: [],
            error: {}
        }
    }

    componentDidMount() {
        let { accountId } = this.props.match.params
        fetch(`https://localhost:8443/nordea/accounts/${accountId}/transactions`)
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        loaded: true,
                        transactions: result.response.transactions
                    })
                },
                (error) => {
                    this.setState({
                        loaded: true,
                        transactions: {},
                        error: error
                    })
                });
    }

    render() {
        return (
            <div>
                {
                    this.state.transactions && this.state.transactions.map(item =>
                        <Transaction key={item.transaction_id} transaction={item} />)
                }
            </div >
        )
    }

}


export default Transactions;


