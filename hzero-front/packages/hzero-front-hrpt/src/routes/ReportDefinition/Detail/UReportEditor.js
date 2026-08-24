import React, { Component } from 'react';
import { Spin } from 'hzero-ui';
import queryString from 'querystring';
import { Content, Header } from 'components/Page';

import {
  getAccessToken,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  getRequestId,
} from 'utils/utils';
import { HZERO_RPT, API_HOST } from 'utils/config';
import { closeTab } from 'utils/menuTab';
import intl from 'utils/intl';

import styles from './index.less';
// import { bind } from 'core-js/fn/function';

const currentTenantId = getCurrentOrganizationId();
const tenantRoleLevel = isTenantRoleLevel();
const token = getAccessToken();

export default class UReportEditor extends Component {
  constructor(props) {
    super(props);
    const {
      location: { search },
    } = props;
    const { id } = queryString.parse(search.substring(1));
    this.state = {
      loading: true,
      id,
      requestId: getRequestId(),
    };
  }

  componentDidMount() {
    const { id } = this.state;
    document.getElementById(id).onload = () => {
      this.setState({ loading: false });
    };
  }

  onBack() {
    closeTab('/hrpt/report-definition/u-report');
  }

  render() {
    const { loading, id, requestId } = this.state;
    const url = tenantRoleLevel
      ? `${API_HOST}${HZERO_RPT}/v1/${currentTenantId}/reports/designer/ureport?_u=${id}&access_token=${token}&H-Request-Id=${requestId}`
      : `${API_HOST}${HZERO_RPT}/v1/reports/designer/ureport?_u=${id}&access_token=${token}&H-Request-Id=${requestId}`;

    return (
      <>
        <Header
          title={intl.get('hrpt.reportDefinition.view.message.uReportEditor').d('设计器')}
          backPath="/hrpt/report-definition/list"
          onBack={this.onBack}
        />
        <Content
          style={{
            height: '100%',
          }}
        >
          <div className={styles['u-report-wrap']} width="100%" height="100%">
            <Spin
              style={{ width: '100%', height: '100%' }}
              spinning={loading}
              wrapperClassName={styles['u-report-spin']}
            >
              <iframe title="iframe" id={id} src={url} width="100%" height="100%" />
            </Spin>
          </div>
        </Content>
      </>
    );
  }
}
