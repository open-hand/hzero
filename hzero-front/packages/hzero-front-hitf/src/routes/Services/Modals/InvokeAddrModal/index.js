/**
 * AddDataModal - 内部接口弹窗
 * @author: lingfangzi.hu01@hand-china.com
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { Button, Modal } from 'hzero-ui';
import React, { PureComponent } from 'react';
import { isUndefined } from 'lodash';
import intl from 'utils/intl';
import { createPagination } from 'utils/utils';
import { Bind } from 'lodash-decorators';
import List from './List';

const listRowKey = 'interfaceId';

export default class InvokeAddrModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      listDataSource: [],
      pagination: createPagination({ number: 0, size: 10, totalElements: 0 }),
    };
  }

  getSnapshotBeforeUpdate(prevProps = {}) {
    const { visible, defaultDataSource = {} } = this.props;
    return (
      visible &&
      !isUndefined(defaultDataSource.interfaceServerId) &&
      defaultDataSource.interfaceServerId !== (prevProps.defaultDataSource || {}).interfaceServerId
    );
  }

  // applicationId !== prevProps.applicationId
  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.fetchInvokeAddresses();
    }
  }

  // 查询透传地址
  @Bind()
  fetchInvokeAddresses(param = {}) {
    const { fetchInvokeAddresses = (e) => e } = this.props;
    fetchInvokeAddresses(param).then((res) => {
      if (res) {
        this.setState({
          listDataSource: res.content || [],
          pagination: createPagination(res),
        });
      }
    });
  }

  @Bind()
  onListChange(params = {}) {
    const { defaultDataSource } = this.props;
    const { interfaceServerId } = defaultDataSource;
    const { current = 1, pageSize = 10 } = params;
    this.fetchInvokeAddresses({ interfaceServerId, page: { current, pageSize } });
  }

  @Bind()
  cancel() {
    const { onCancel = (e) => e } = this.props;
    onCancel();
    this.setState({
      listDataSource: [],
    });
  }

  render() {
    const { visible, processing = {}, services = {} } = this.props;
    const { enumMap = {} } = services;
    const {
      serviceTypes = [], // 服务类型值集、发布类型
      interfaceStatus = [], // 接口状态
    } = enumMap;
    const { listDataSource = [], pagination = {} } = this.state;

    const listProps = {
      rowKey: listRowKey,
      dataSource: listDataSource,
      pagination,
      onChange: this.onListChange,
      loading: processing.fetchingInvokeAddrLoading || false,
      fetchInformation: this.fetchInvokeAddresses,
      serviceTypes,
      interfaceStatus,
    };

    return (
      <Modal
        width={1200}
        destroyOnClose
        title={intl.get('hitf.services.view.title.invokeAddr').d('透传地址')}
        visible={visible}
        confirmLoading={processing.fetchingInvokeAddrLoading}
        onCancel={this.cancel}
        footer={[
          <Button type="primary" key="close" onClick={this.cancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>,
        ]}
      >
        <List {...listProps} />
      </Modal>
    );
  }
}
