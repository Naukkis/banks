import React from 'react';
import Grid from '@material-ui/core/Grid';
import op from './img/logos/op.jpg';
import nordea from './img/logos/nordea.gif';
import spankki from './img/logos/s-pankki.png';
import './bank-logo.css'
import { Link } from "react-router-dom";


function Main() {

    return (
        <div className="Main">
            <Grid item xs={12}>
                <Grid container justify="center" spacing={5} >
                    <Link to="/osuuspankki">
                        <Grid item xs={12} style={{ border: "1px solid grey"}}>
                            <img src={op} className="bank-logo" alt="logo-op" />
                        </Grid>
                    </Link>
                    <Link to="/nordea">
                        <Grid item xs={12} style={{ border: "1px solid grey"}}>
                            <img src={nordea} className="bank-logo" alt="logo-nordea" />
                        </Grid>
                    </Link>
                    <Link to="/spankki">
                        <Grid item xs={12} style={{ border: "1px solid grey"}}>
                            <img src={spankki} className="bank-logo" alt="logo-spankki" />
                        </Grid>
                    </Link>
                </Grid>
            </Grid>
        </div >
    );
}

export default Main;