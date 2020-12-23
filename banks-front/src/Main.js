import React from 'react';
import Grid from '@material-ui/core/Grid';
import op from './img/logos/osuuspankki.png';
import nordea from './img/logos/nordea.png';
import handelsbanken from './img/logos/handelsbanken.png';
import './bank-logo.css'
import { Link } from "react-router-dom";


function Main() {

    return (
        <div className="Main">
            <Grid item xs={12}>
                <Grid container justify="center" spacing={5} >
                    <Link to="/op" className="bank-selection">
                        <Grid item xs={12} >
                            <img src={op} className="bank-logo" alt="logo-op" />
                        </Grid>
                    </Link>
                    <Link to="/nordea" className="bank-selection">
                        <Grid item xs={12} >
                            <img src={nordea} className="bank-logo" alt="logo-nordea" />
                        </Grid>
                    </Link>
                    <Link to="/handelsbanken" className="bank-selection">
                        <Grid item xs={12} >
                            <img src={handelsbanken} className="bank-logo" alt="logo-handelsbanken" />
                        </Grid>
                    </Link>
                </Grid>
            </Grid>
        </div >
    );
}

export default Main;