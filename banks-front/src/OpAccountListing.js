import React from 'react';
import Grid from '@material-ui/core/Grid';
import {  Link } from "react-router-dom";
import OpAccountInfo from './OpAccountInfo'


function OpAccountListing(props) {

    return (
        <div className="bank-main-accounts">
            <h3>All accounts</h3>
            <Grid item xs={12}>
                <Grid container justify="flex-start" spacing={5} >
                    {props.accounts.map(item =>
                        <Grid key={item.accountId} item xs={12}>
                            <OpAccountInfo account={item} />
                            <Link to={`/op/accounts/${item.accountId}`}>
                                <p>Transactions</p>
                            </Link>
                        </Grid>
                    )}
                </Grid>
            </Grid>
        </div>
    );
}


export default OpAccountListing;