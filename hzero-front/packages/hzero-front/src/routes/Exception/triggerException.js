import React, { PureComponent } from 'react';
import { Button, Card, Spin } from 'hzero-ui';
import { connect } from 'dva';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import styles from './style.less';

@formatterCollections({ code: ['hzero.exceptionComponent'] })
@connect(state => ({
  isloading: state.error.isloading,
}))
export default class TriggerException extends PureComponent {
  state = {
    isloading: false,
  };

  triggerError = code => {
    this.setState({
      isloading: true,
    });
    this.props.dispatch({
      type: 'error/query',
      payload: {
        code,
      },
    });
  };

  render() {
    return (
      <Card>
        <Spin spinning={this.state.isloading} wrapperClassName={styles.trigger}>
          <Button type="danger" onClick={() => this.triggerError(401)}>
            {intl.get('hzero.exceptionComponent.view.button.trigger401').d('触发401')}
          </Button>
          <Button type="danger" onClick={() => this.triggerError(403)}>
            {intl.get('hzero.exceptionComponent.view.button.trigger403').d('触发403')}
          </Button>
          <Button type="danger" onClick={() => this.triggerError(500)}>
            {intl.get('hzero.exceptionComponent.view.button.trigger500').d('触发500')}
          </Button>
          <Button type="danger" onClick={() => this.triggerError(404)}>
            {intl.get('hzero.exceptionComponent.view.button.trigger404').d('触发404')}
          </Button>
        </Spin>
      </Card>
    );
  }
}
