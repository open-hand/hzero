import React, { Component } from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import { Button as ButtonPermission } from 'components/Permission';

import { retryDS } from '@/stores/otaUpgradeTaskDS';

const prefix = 'hiot.ota';

export default class DeviceInfo extends Component {
  constructor(props) {
    super(props);
    this.retryDs = new DataSet(retryDS());
    this.state = {};
  }

  get columns() {
    return [
      {
        name: 'thingName',
        width: 150,
      },
      {
        name: 'thingCode',
        width: 150,
      },
      {
        name: 'versionBefore',
        width: 140,
      },
      {
        name: 'startTime',
        width: 180,
      },
      {
        name: 'endTime',
        width: 180,
      },
      {
        name: 'otaStatus',
        renderer: ({ record, value }) => {
          const statusColor = {
            RUNNING: '#ffcc00',
            SUCCESS: '#0bce0b',
            FAILED: '#ff0000',
            PENDING: '#0000ff',
          };
          return (
            <span style={{ color: statusColor[value] }}>{record.get('otaStatusMeaning')}</span>
          );
        },
      },
      {
        name: 'percentage',
      },
      {
        name: 'errorMessage',
        width: 200,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 80,
        lock: 'right',
        renderer: ({ record }) => {
          const { path } = this.props;
          const operators = [];
          if (record.get('otaStatus') === 'FAILED') {
            operators.push({
              key: 'retry',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.retry`,
                      type: 'button',
                      meaning: 'OTA升级任务-重试',
                    },
                  ]}
                  onClick={() => {
                    this.handleRetry(record);
                  }}
                >
                  {intl.get(`${prefix}.view.button.retry`).d('重试')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get(`${prefix}.view.button.retry`).d('重试'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];
  }

  // 重试
  @Bind()
  async handleRetry(record) {
    const { dataSet } = this.props;
    const data = record.toData();
    this.retryDs.create(data, 0);
    try {
      const validate = await this.retryDs.submit();
      if (validate) {
        dataSet.query();
      }
    } catch (e) {
      // return e
    }
  }

  render() {
    const { dataSet } = this.props;
    return (
      <Card
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={intl.get(`${prefix}.view.device.info`).d('设备信息')}
      >
        <Table
          selectionMode="click"
          columns={this.columns}
          dataSet={dataSet}
          queryFieldsLimit={3}
        />
      </Card>
    );
  }
}
