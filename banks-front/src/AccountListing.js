import React from 'react';
import Grid from '@material-ui/core/Grid';
import { Switch, Route, Link } from "react-router-dom";
import AccountInfo from './AccountInfo'

function AccountListing(props) {

    return (
        <div className="bank-main-accounts">
            <h3>All accounts</h3>
            <Grid item xs={12}>
                <Grid container justify="center" spacing={5} >
                    {props.accounts.map(item =>
                        <Grid item xs={6}>
                            <Link to={`/nordea/accounts/${item._id}`}>
                                <AccountInfo account={item} />
                            </Link>
                        </Grid>
                    )}
                </Grid>
            </Grid>

            <Switch>
                <Route path={"/nordea/accounts/:accoundId"}>
                    <p>asd </p>
                </Route>
            </Switch>
        </div>
    );

}

export default AccountListing;