import React from 'react';
import { Modal, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import Drawer from './Drawer';

// @Form.create({ fieldNameProp: null })
export default class RequestDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  @Bind()
  hiddenModal() {
    this.setState({
      drawerVisible: false,
    });
  }

  @Bind()
  showModal(record) {
    const { onSearchDetail = (e) => e } = this.props;
    this.setState({
      drawerVisible: true,
    });
    onSearchDetail(record);
  }

  render() {
    const {
      data = {},
      visible,
      onCancel,
      onOk,
      requestStatusList,
      tableLoading = false,
      fetchRequestListLoading = false,
      fetchRequestDetailLoading = false,
      onSearch = (e) => e,
      onExport = (e) => e,
      onBindRef,
    } = this.props;
    const { drawerVisible } = this.state;
    const { list = [], pagination, requestDetail = {} } = data;
    const filterProps = {
      requestStatusList,
      onSearch,
      onRef: onBindRef,
    };
    const listProps = {
      pagination,
      loading: fetchRequestListLoading || tableLoading,
      dataSource: list,
      onDetail: this.showModal,
      onExport,
      onChange: onSearch,
    };
    const drawerProps = {
      fetchRequestDetailLoading,
      initData: requestDetail,
      visible: drawerVisible,
      onOk: this.hiddenModal,
      onCancel: this.hiddenModal,
      onExport,
    };
    return (
      <Modal
        destroyOnClose
        title={intl
          .get('hrpt.reportQuery.model.reportQuery.viewIndividualReport')
          .d('查看个人报表请求')}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        footer={[
          <Button key="detail" type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
        width={1100}
      >
        <div className="table-list-search">
          <FilterForm {...filterProps} />
        </div>
        <ListTable {...listProps} />
        {drawerVisible && <Drawer {...drawerProps} />}
      </Modal>
    );
  }
}
