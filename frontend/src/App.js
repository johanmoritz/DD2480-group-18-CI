import React, { useEffect, useState } from 'react';
import logo from './logo.svg';
import './App.css';
import CommitList from './components/BuildList'
import Typography from '@material-ui/core/Typography';
import 'typeface-roboto';
import { makeStyles } from '@material-ui/core/styles';
import AllBuilds from './pages/AllBuilds';
import BuildInfo from './pages/BuildInfo';
import { Router, Route, BrowserRouter, useLocation, Switch } from 'react-router-dom';
import axios from 'axios';

import env from './env';

// Color scheme: https://coolors.co/06aed5-086788-f0c808-fff1d0-dd1c1a


const useStyles = makeStyles(theme => ({
  title: {
    display: 'inline',
    color: '#F0C808',
    fontFamily: 'Roboto',
  }
}));


/**
 * Main router.
 */
function App() {
  const classes = useStyles();
  const [builds, setState] = useState([]);

  useEffect(() => {
    axios.get(env.REACT_APP_PROXY + env.REACT_APP_URL + "/ci/get")
    .then((res) => {
      setState(res['data'])
    })
    .catch((e) => {
      setState([]);
    })

  }, [])

  return (
    <div className="App">
      <BrowserRouter>
          <Route exact path={"/"} render={(props) => <AllBuilds {... props} builds={builds} />} />
          <Route path={"/build"} render={(props) => <BuildInfo {... props} builds={builds} />} />
      </BrowserRouter>
    </div>
  );
}

export default App;
