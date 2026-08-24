import React, { Component } from 'react';

import { isTenantRoleLevel } from 'utils/utils';

import ConfigDetail from './ConfigDetail';

export default class Config extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isSite: !isTenantRoleLevel(),
    };
  }

  render() {
    const { isSite } = this.state;
    const { match } = this.props;
    return <ConfigDetail isSite={isSite} match={match} />;
  }
}
