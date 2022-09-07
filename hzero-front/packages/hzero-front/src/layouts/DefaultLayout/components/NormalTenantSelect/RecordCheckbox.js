import React from 'react';
import { Checkbox } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

export default class RecordCheckbox extends React.Component {
  @Bind()
  handleClick() {
    const { record, onClick } = this.props;
    onClick(record);
  }

  render() {
    const { record, ...rest } = this.props;
    return <Checkbox {...rest} onClick={this.handleClick} />;
  }
}
