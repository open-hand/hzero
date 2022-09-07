import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Content as ChContent } from 'components/Page';
import Spin from '@/routes/components/loading/Spin';

@observer
export default class Content extends Component {
  render() {
    const { dataSet = {}, children, ...props } = this.props;
    return (
      <ChContent {...props}>
        <Spin dataSet={dataSet}>{children}</Spin>
      </ChContent>
    );
  }
}
