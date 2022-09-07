import React, { Component } from 'react';
import { Spin as ChSpin } from 'choerodon-ui/pro';
import { observer } from 'mobx-react';

@observer
export default class Spin extends Component {
  render() {
    const { dataSet = {}, ...props } = this.props;
    const { status } = dataSet;
    return <ChSpin spinning={status !== 'ready'} {...props} />;
  }
}
