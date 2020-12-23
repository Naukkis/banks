import React from 'react';
import {

} from 'react-router-dom'

import OpTransaction from './OpTransaction'

class OpTransactions extends React.Component {
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
    fetch(`https://localhost:8443/op/accounts/${accountId}/transactions`)
      .then(res => res.json())
      .then(
        (result) => {
          if (result.transactions) {
            this.setState({
              loaded: true,
              transactions: result.transactions
            })
          }

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
            <OpTransaction key={item.transactionId} transaction={item} />)
        }
      </div >
    )
  }

}


export default OpTransactions;


