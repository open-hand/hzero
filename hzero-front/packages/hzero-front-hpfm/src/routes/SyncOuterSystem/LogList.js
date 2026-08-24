import React from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';

import { logDs } from '../../stores/syncOuterSystemDS';
import LogDrawer from './LogDrawer';

@formatterCollections({ code: ['hpfm.syncOuterSystem'] })
export default class LogList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      currentEditData: {},
    };
  }

  logDs = new DataSet(logDs());

  get columns() {
    return [
      {
        name: 'lastUpdateDate',
        header: intl.get('hpfm.syncOuterSystem.model.sync.lastUpdateDate').d('同步日期'),
        width: 200,
      },
      {
        name: 'syncDirectionMeaning',
        header: intl.get('hpfm.syncOuterSystem.model.sync.syncDirection').d('同步方向'),
        width: 100,
      },
      {
        name: 'syncTypeMeaning',
        header: intl.get('hpfm.syncOuterSystem.model.sync.syncTypeMeaning').d('同步类型'),
        width: 200,
      },
      {
        name: 'deptStatusMeaning',
        header: intl.get('hpfm.syncOuterSystem.model.sync.deptStatusMeaning').d('部门同步状态'),
      },
      {
        name: 'empStatusMeaning',
        header: intl.get('hpfm.syncOuterSystem.model.sync.empStatusMeaning').d('员工同步状态'),
        width: 200,
      },
      {
        header: intl.get('hpfm.syncOuterSystem.view.button.detailLog').d('详情日志'),
        width: 120,
        renderer: ({ record }) => {
          const {
            match: { path },
          } = this.props;
          const actions = [];
          actions.push({
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}/seeLog`,
                    type: 'button',
                    meaning: '组织信息同步-查看日志',
                  },
                ]}
                onClick={() => {
                  this.handleOpenDrawer(record);
                }}
                disabled={record.get('deptStatusCode') === 0 || record.get('empStatusCode') === 0}
              >
                {intl.get('hpfm.syncOuterSystem.view.button.seeLog').d('查看日志')}
              </ButtonPermission>
            ),
            key: 'seeLog',
            len: 4,
            title: intl.get('hpfm.syncOuterSystem.view.button.seeLog').d('查看日志'),
          });
          return [operatorRender(actions)];
        },
        lock: 'right',
      },
    ];
  }

  componentDidMount() {
    const {
      match: {
        params: { syncId },
      },
    } = this.props;
    this.logDs.syncId = syncId;
    this.logDs.query();
  }

  @Bind()
  handleOpenDrawer(record) {
    this.setState({
      visible: true,
      currentEditData: record.toData(),
    });
  }

  @Bind()
  handleSearch() {
    this.logDs.query();
  }

  @Bind()
  handleClose() {
    this.setState({
      visible: false,
    });
  }

  render() {
    const { visible, currentEditData } = this.state;
    return (
      <>
        <Header
          title={intl.get('hpfm.syncOuterSystem.view.message.title.logList').d('日志列表')}
          backPath="/hpfm/sync-to-outer-system/list"
        />
        <Content>
          <Table dataSet={this.logDs} columns={this.columns} queryFieldsLimit={2} />
          {visible && <LogDrawer currentEditData={currentEditData} onClose={this.handleClose} />}
        </Content>
      </>
    );
  }
}
