import React from 'react';
import Grid from '@material-ui/core/Grid';
import { Switch, Route, Link, useLocation } from "react-router-dom";
import AccountInfo from './AccountInfo'


function AccountListing(props) {
    let query = useQuery();

    return (
        <div className="bank-main-accounts">
            <h3>All accounts</h3>
            <Grid item xs={12}>
                <Grid container justify="flex-start" spacing={5} >
                    {props.accounts.map(item =>
                        <Grid key={item._id} item xs={6}>
                            <AccountInfo account={item} />
                            <Link to={`/nordea/accounts/${item._id}`}>
                                <p>Transactions</p>
                            </Link>
                        </Grid>
                    )}
                </Grid>
            </Grid>
        </div>
    );
}

function useQuery() {
    return new URLSearchParams(useLocation().search);
}



export default AccountListing;