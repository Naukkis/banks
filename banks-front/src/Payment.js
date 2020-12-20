import React from 'react';

function Payment(props) {
    return (
        <div>
            <p>Receiver name: {props.receiver}</p>
            <p>Receiver account: {props.receiverAccount}</p>
            <p>Amount: {props.amount}</p>
            <p>Message: {props.message}</p>
            <p>Status: {props.status}</p>
            <p>Excecution date: {props.date}</p>
        </div>
    )
}

export default Payment;