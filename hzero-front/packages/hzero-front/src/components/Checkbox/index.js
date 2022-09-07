import React from 'react';
import { Checkbox as HzeroCheckbox } from 'hzero-ui';

export default class Checkbox extends React.Component {
  static displayName = 'HzeroCheckbox';

  render() {
    return <HzeroCheckbox {...this.props} checkedValue={1} unCheckedValue={0} />;
  }
}
