import React from 'react';

function OpTransaction(props) {
    return (
        <div className="transaction">
            <div className="transaction-column">Description: {props.transaction.type_description}</div>
              <div className="transaction-column-amount"> Amount: {props.transaction.amount}</div>
            <p>Creditor name: {props.transaction.creditor.accountName}</p>
            <p>Creditor account: {props.transaction.creditor.accountIdentifier}</p>
            <p>Booking date: {props.transaction.bookingDateTime}</p>
            <p>Value date: {props.transaction.valueDateTime}</p>
            <p>Narrative: {props.transaction.narrative}</p>
            <p>Message: {props.transaction.message}</p>
            <p>Status: {props.transaction.status}</p>
            <p>Own message: {props.transaction.message}</p>
        </div>
    )
}

export default OpTransaction;