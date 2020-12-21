import React from 'react';
import Payment from './Payment';

class Payments extends React.Component {
    constructor() {
        super();
        this.state = {
            amount: '',
            recipientName: '',
            recipientAccount: '',
            message: '',
            payments: []
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.fetchPayments = this.fetchPayments.bind(this);
    }

    fetchPayments() {
        fetch("https://localhost:8443/nordea/payments/")
            .then(res => res.json())
            .then((allPaymentsResponse) => {
                if (allPaymentsResponse.response) {
                    this.setState({
                        payments: allPaymentsResponse.response.payments
                    })
                }
            });
    }

    handleChange(event) {
        const target = event.target;
        this.setState({ [target.name]: target.value });
    }

    handleSubmit(event) {
        event.preventDefault();
        const payment = {
            amount: this.state.amount,
            recipientName: this.state.recipientName,
            recipientAccount: this.state.recipientAccount,
            message: this.state.message,
            debtorAccountNumber: "FI7473834510057469"
        }

        fetch(`https://localhost:8443/nordea/payments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payment)

        })
            .then(res => res.json())
            .then((result) => {
                this.setState({
                    amount: '',
                    recipientName: '',
                    recipientAccount: '',
                    message: '',
                });
                this.fetchPayments();
            },
                (error) => {
                    this.setState({
                        error
                    });
                }
            )
    }

    componentDidMount() {
        this.fetchPayments();
    }

    render() {

        return (
            <div>
                <h3>Create payment</h3>
                <form onSubmit={this.handleSubmit}>
                    <label>Amount:
                        <input
                            name="amount"
                            type="text"
                            value={this.state.amount}
                            onChange={this.handleChange} />
                    </label>
                    <label>Receiver name:
                        <input
                            name="recipientName"
                            type="text"
                            value={this.state.recipientName}
                            onChange={this.handleChange} />
                    </label>
                    <label>Reveicer account:
                        <input
                            name="recipientAccount"
                            type="text"
                            value={this.state.recipientAccount}
                            onChange={this.handleChange} />
                    </label>
                    <label>Message:
                        <input
                            name="message"
                            type="text"
                            value={this.state.message}
                            onChange={this.handleChange} />
                    </label>
                    <input type="submit" value="Create payment" />
                </form>
                <p>{this.state.amount}</p>
                <p>{this.state.recipientName}</p>
                <p>{this.state.recipientAccount}</p>
                <p>{this.state.message}</p>

                <h3>Payments</h3>
                {this.state.payments &&
                    this.state.payments.map(payment =>
                        <Payment
                            key={payment._id}
                            paymentId={payment._id}
                            receiver={payment.creditor.name}
                            receiverAccount={payment.creditor.account.value}
                            amount={payment.amount}
                            message={payment.creditor.message}
                            status={payment.payment_status}
                            date={payment.requested_execution_date}
                        />

                    )

                }
            </div>
        )
    }

}


export default Payments;