import React from 'react';
import './../App.css';
import Build from '../components/Build'
import Typography from '@material-ui/core/Typography';
import 'typeface-roboto';
import { makeStyles } from '@material-ui/core/styles';

// Color scheme: https://coolors.co/06aed5-086788-f0c808-fff1d0-dd1c1a

const useStyles = makeStyles(theme => ({
  title: {
    display: 'inline',
    color: '#F0C808',
    fontFamily: 'Roboto',
  }
}));

/**
 * Displays the information of one build. 
 */
export default function BuildInfo(props) {
  const classes = useStyles();
  const tempBuilds = props.builds;
  const build = tempBuilds.find(build => build.jobID === props.location.pathname.slice(7, props.location.pathname.length));

  return (
      <header className="App-header">
        <div style={{display: "block", textAlign: "left", margin: "8px"}}>
          {!build ? 
          <p style={{fontSize: "32px", textAlign: "center"}}>Loading id...</p>
          :
          <>
              <p style={{fontSize: "32px"}}>{build.jobID}</p>
              <p style={{fontSize: "24px", color: "black"}}>Status: {
                build.status === "success" ? 
                  <span style={{color: "green"}}>{build.status}</span> : 
                  <span style={{color: "red"}}>{build.status}</span>
              }</p>
              <p style={{fontSize: "24px", color: "black"}}>SHA: {build.commitSha}</p>
            <Typography
                    component="p"
                    variant="h4"
                    className={classes.title}
            >
              Log:
            </Typography>
            <ul style={{textAlign: "left"}}>
              {build.log.map(logEntry => <li>{logEntry.map(ln => <p style={{fontSize: "16px", color: "black"}}>{ln}</p>)} </li>)}
            </ul>
          </>
          }
        </div>
        <Build {... props} />
      </header>
  );
}
