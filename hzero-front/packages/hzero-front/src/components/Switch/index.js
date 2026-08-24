import React from 'react';
import { Switch as HzeroSwitch } from 'hzero-ui';

export default class Switch extends React.Component {
  static displayName = 'HzeroSwitch';

  render() {
    return <HzeroSwitch {...this.props} unCheckedValue={0} checkedValue={1} />;
  }
}
