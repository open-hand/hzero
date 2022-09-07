/**
 * @since 2019-10-16
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, TextField, DataSet, TextArea, Icon } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { detailFormDS } from '@/stores/syncLogGroupDS';

export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isExist: false,
    };
    this.detailFormDS = new DataSet(detailFormDS());
  }

  componentDidMount() {
    this.refresh();
  }

  // 刷新页面
  @Bind()
  refresh() {
    const { match } = this.props;
    const {
      params: { syncLogId = '' },
    } = match;
    this.detailFormDS.setQueryParameter('syncLogId', syncLogId);
    this.detailFormDS.query().then((res) => {
      if (res && res.syncDataUrl) {
        this.setState({
          isExist: true,
        });
      }
    });
  }

  @Bind()
  openSyncDataUrl() {
    const [{ syncDataUrl }] = this.detailFormDS.toData();
    if (syncDataUrl) {
      window.open(syncDataUrl);
    }
  }

  render() {
    const { isExist } = this.state;
    return (
      <React.Fragment>
        <Header
          title={intl.get('hsrh.syncLog.view.title.syncLogDetail').d('同步日志详情')}
          backPath="/hsrh/sync-log/list"
        />
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hsrh.syncLog.view.title.basicInformation').d('基本信息')}</h3>}
          >
            <Form labelLayout="horizontal" dataSet={this.detailFormDS} columns={3}>
              <TextField name="syncConfCode" disabled />
              <TextField name="syncStartTime" disabled />
              <TextField name="syncStatusMeaning" disabled />
              <TextField name="indexCode" disabled />
              <TextField name="syncEndTime" disabled />
              {isExist ? (
                <TextField
                  name="syncDataUrl"
                  disabled
                  addonAfter={
                    <Icon
                      style={{ cursor: 'pointer' }}
                      onClick={this.openSyncDataUrl}
                      type="arrow_downward"
                    />
                  }
                />
              ) : null}
              <TextArea newLine name="syncLog" colSpan={3} disabled style={{ height: 256 }} />
            </Form>
          </Card>
        </Content>
      </React.Fragment>
    );
  }
}
