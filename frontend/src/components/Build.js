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
import { Redirect } from 'react-router-dom';

const useStyles = makeStyles(theme => ({
  root: {
    width: '100%',
    maxWidth: 360,
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

export default function Build(props) {
  const classes = useStyles();
  const [builds, setState] = useState({});
  const [redirect, setRedirect] = useState(false);
  const tempBuilds = props.builds;
  const build = tempBuilds[props.location.search.slice(4, props.location.search.length)];
  console.log(build)
  
  if (redirect) {
    return <Redirect push to={"/"} />;
  }

  return (
    <List className={classes.root} onClick={() => setRedirect(true)}>
        {!build ? null : 
            <ListItem alignItems="flex-start">
                 <ListItemText
                    primary={
                    <React.Fragment>
                    <Typography
                        component="span"
                        variant="h6"
                        className={classes.inline}
                        color={"textPrimary"}
                        style={{ color: build.status.slice(0, 1) === "s" ? "#FFF1D0" : "#DD524F" }}
                    >
                        {firstLetterUppercase(build.status)}
                    </Typography>
                    </React.Fragment>
                    }
                    secondary={
                    <React.Fragment>
                        <Typography
                        component="span"
                        variant="body2"
                        className={classes.inline}
                        color="textPrimary"
                        >
                        Commit SHA: {build.commitSha}
                        </Typography>
                        <div/>
                        <Typography
                        component="span"
                        variant="body2"
                        className={classes.inline}
                        color="textPrimary"
                        >
                        URL: {build.url}
                        </Typography>
                        <Divider variant="inset" component="li" style={{ marginLeft: 0, marginTop: 16, marginBottom: 16 }} />
                        <Typography
                        component="span"
                        variant="body2"
                        className={classes.inline}
                        >
                            <div key={0}>
                                <Typography variant="h6">Build info:</Typography>
                            </div>
                            {build.log.map((item, i) => {
                                return (
                                    item.map((row, j) => {
                                        if (j === item.length - 1 && i !== build.log.length - 1) {
                                            return [<div>{row}</div>, <Typography variant="h6">Test info:</Typography>]
                                        }
                                        return <div>{row}</div>
                                    }))
                            })}
                        </Typography>
                    </React.Fragment>
                    }
                    />
            </ListItem>
        }
    </List>
  );
}