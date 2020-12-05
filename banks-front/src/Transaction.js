import React from 'react';
import Grid from '@material-ui/core/Grid';

function Transaction(props){
    return (
        <div className="transaction">
             <p>Booking date: {props.transaction.booking_date}</p>
             <p>Value date: {props.transaction.value_date}</p>
             <p>Description: {props.transaction.type_description}</p>
             <p>Amount: {props.transaction.amount}</p>
             <p>Narrative: {props.transaction.narrative}</p>
             <p>Message: {props.transaction.message}</p>

        </div>
        )
}

export default Transaction;