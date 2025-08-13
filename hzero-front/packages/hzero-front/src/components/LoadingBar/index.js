import React from 'react';
import NProgress from '../NProgress';

export default class LoadingBar extends React.Component {
  componentDidMount() {
    NProgress.start();
  }

  componentWillUnmount() {
    NProgress.done();
  }

  render() {
    return null;
  }
}
