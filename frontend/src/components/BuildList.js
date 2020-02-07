import React, {useState, useEffect} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Divider from '@material-ui/core/Divider';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import Avatar from '@material-ui/core/Avatar';
import Typography from '@material-ui/core/Typography';
import 'typeface-roboto';
import axios from 'axios';
import checkbox from './../img/checkbox.png';
import { Redirect, Link } from 'react-router-dom';

const useStyles = makeStyles(theme => ({
  root: {
    width: '100%',
    maxWidth: "min-content",
    backgroundColor: '#F0C808',
    borderRadius: 16,
    border: 2,
    boxShadow: '2px 5px 1px rgba(0, 0, 0, 0.15)'
  },
  inline: {
    display: 'inline',
    color: '#FFF1D0',
    fontFamily: 'Roboto'
  },
  success: {
    color: "red"
  },
  failure: {
    color: "green"
  },
  checkbox: {
    height: 12,
    width: 12,
    paddingRight: 8,
  }
}));

let firstLetterUppercase = (s) => {
  let temp = s.slice(0, 1);
  temp = temp.toUpperCase();
  s = temp + s.slice(1, s.length);

  return s;
}

/**
 * Lists all builds.
 */
export default function BuildList(props) {
  const [redirect, setRedirect] = useState("");
  const classes = useStyles();
  const builds = props.builds;

  if (redirect !== "") {
    return <Redirect push to={"/build?id=" + redirect} />;
  }

  return (
    <List className={classes.root}>
      {Object.entries(builds).length === 0 ? null : Object.keys(builds).map((key, index) => {
        let divider = index !== Object.keys(builds).length - 1 ? <Divider variant="inset" component="li" style={{ marginLeft: 16 }}/> : null;
        return ([<ListItem key={key} alignItems="flex-start" onClick={() => setRedirect(key)}>
          <ListItemText
            primary={
            <React.Fragment>
              <Typography
                  component="span"
                  variant="h6"
                  className={classes.inline}
                  color={"textPrimary"}
                  style={{ color: builds[key].status.slice(0, 1) === "s" ? "#FFF1D0" : "#DD524F" }}
              >
                  {firstLetterUppercase(builds[key].status)}
              </Typography>
            </React.Fragment>
          }
            secondary={
              <React.Fragment>
                <Link to={`/build/${builds[key].jobID}`}>
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  {builds[key].jobID}
                </Typography>
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                >
                  {builds[key].commitSha}
                </Typography>
                </Link>
              </React.Fragment>
            }
          />
        </ListItem>, divider])
      })}
    </List>
  );
}