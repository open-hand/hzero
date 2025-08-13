import React from 'react';
import { connect } from 'dva';
import { Tabs } from 'hzero-ui';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import Point from './Point';
import Range from './Range';
import Rule from './Rule';

function Customize({ match }) {
  return (
    <React.Fragment>
      <Header title={intl.get('hpfm.customize.view.title.header').d('API 个性化')} />
      <Content>
        <Tabs animated={false}>
          <Tabs.TabPane
            key="point"
            tab={intl.get('hpfm.customize.view.title.point').d('个性化切入点')}
          >
            <Point match={match} />
          </Tabs.TabPane>
          <Tabs.TabPane key="rule" tab={intl.get('hpfm.customize.view.title.rule').d('个性化规则')}>
            <Rule match={match} />
          </Tabs.TabPane>
          <Tabs.TabPane
            key="range"
            tab={intl.get('hpfm.customize.view.title.range').d('个性化范围')}
          >
            <Range match={match} />
          </Tabs.TabPane>
        </Tabs>
      </Content>
    </React.Fragment>
  );
}

function mapStateToProps({ customize, loading }) {
  return { customize, loading };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(formatterCollections({ code: ['hpfm.customize'] })(Customize));
