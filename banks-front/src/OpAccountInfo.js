import React from 'react';

 function OpAccountInfo(props){

        return (
        <div className="account">
            <p>Name: {props.account.name}</p>
            <p>Available balance: {props.account.balance}</p>
            <p>Account number: {props.account.identifier}</p>
            <p>Currency: {props.account.currency}</p>
        </div>
        )
    } 

export default OpAccountInfo;