import React from 'react';

function Transaction(props) {
    return (
        <div className="transaction">
            <div className="transaction-column">Description: {props.transaction.type_description}</div>
              <div className="transaction-column-amount"> Amount: {props.transaction.amount}</div>
            <p>Counterparty name: {props.transaction.counterparty_name}</p>
            <p>Transaction date: {props.transaction.transaction_date}</p>
          
            <p>Booking date: {props.transaction.booking_date}</p>
            <p>Value date: {props.transaction.value_date}</p>
            <p>Narrative: {props.transaction.narrative}</p>
            <p>Message: {props.transaction.message}</p>
            <p>Status: {props.transaction.status}</p>
            <p>Own message: {props.transaction.own_message}</p>
            <p>Payment date: {props.transaction.payment_date}</p>
        </div>
    )
}

export default Transaction;