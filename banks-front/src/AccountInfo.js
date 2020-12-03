import React from 'react';

 function AccountInfo(props){

        return (
        <div className="account">
            <p>Name: {props.account.account_name}</p>
            <p>Available balance: {props.account.available_balance}</p>
            <p>Booked balance: {props.account.booked_balance}</p>
            <p>Type: {props.account.product}</p>
            <p>Account number: {props.account.account_numbers[0].value}</p>
            <p>Country: {props.account.country}</p>
            <p>Currency: {props.account.currency}</p>
        </div>
        )
    } 

export default AccountInfo;