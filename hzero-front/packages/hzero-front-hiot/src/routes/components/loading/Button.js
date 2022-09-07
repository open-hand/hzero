import React, { Component } from 'react';
import { Button as ChButton } from 'choerodon-ui/pro';
import { observer } from 'mobx-react';

@observer
export default class Button extends Component {
  render() {
    const { dataSet = {}, ...props } = this.props;
    const { status } = dataSet;
    return <ChButton loading={!!status && status !== 'ready'} {...props} />;
  }
}
