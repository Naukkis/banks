import React from 'react';

class Payment extends React.Component {
    constructor(props) {
        super();
        this.state = {
            status: props.status
        };

        this.confirmPayment = this.confirmPayment.bind(this);
        this.deletePayment = this.deletePayment.bind(this);
    }

    confirmPayment() {
        fetch(`https://localhost:8443/nordea/payments/confirm`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ payments_ids: [this.props.paymentId] })
        })
            .then(res => res.json())
            .then((response) => {
                if (response.response) {
                    this.setState({
                        status: 'Confirmed'
                    })
                }
            });
    }

    deletePayment(){
        fetch(`https://localhost:8443/nordea/payments/delete/?paymentId=${this.props.paymentId}`, {
            method: 'DELETE'
        })
            .then(res => res.json())
            .then((response) => {
                if (response.response) {
                    this.setState({
                        status: 'Deleted'
                    })
                }
            });
    }

    render() {


        return (
            <div>
                <p>Receiver name: {this.props.receiver}</p>
                <p>Receiver account: {this.props.receiverAccount}</p>
                <p>Amount: {this.props.amount}</p>
                <p>Message: {this.props.message}</p>
                <p>Status: {this.state.status}</p>
                <p>Excecution date: {this.props.date}</p>
                <input type="button"
                    value="Delete payment"
                    onClick={this.deletePayment}
                />
                <input type="button"
                    value="Confirm payment"
                    onClick={this.confirmPayment}
                />
            </div>
        )
    }


}

export default Payment;