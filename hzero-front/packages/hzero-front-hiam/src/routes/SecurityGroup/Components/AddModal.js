/**
 * AddModal - 多选添加弹窗
 * @date: 2019-12-11
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { DataSet, Table, Modal, CheckBox } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import notification from 'utils/notification';

import { addRoleDS, addModalDS, addCardDS } from '@/stores/SecurityGroupDS';

const modalKey = Modal.key();

class AddModal extends Component {
  constructor(props) {
    super(props);
    const { addModalOptions = {} } = props;
    const { isAddRole = false, isAddCard = false } = addModalOptions;
    this.addModalDS = new DataSet(
      // eslint-disable-next-line no-nested-ternary
      isAddRole
        ? addRoleDS(addModalOptions)
        : isAddCard
        ? addCardDS(addModalOptions)
        : addModalDS(addModalOptions)
    );
    this.props.callback(this.addModalDS);
  }

  @Bind()
  handleSelectAll(value) {
    const { records = [] } = this.addModalDS;
    if (!isEmpty(records)) {
      records.forEach(record => {
        record.set('isSelected', value);
      });
    }
  }

  get columns() {
    const { addModalOptions = {} } = this.props;
    const { isAddRole = false, isAddCard = false } = addModalOptions;
    const commonColumn = [
      {
        name: 'isSelected',
        editor: true,
        width: 60,
        header: () => <CheckBox onChange={this.handleSelectAll} />,
      },
    ];
    // eslint-disable-next-line no-nested-ternary
    return isAddRole
      ? [...commonColumn, { name: 'name' }, { name: 'code' }, { name: 'tenantName' }]
      : isAddCard
      ? [...commonColumn, { name: 'name' }, { name: 'code' }, { name: 'catalogMeaning' }]
      : [...commonColumn, { name: 'tenantName' }, { name: 'dataName' }, { name: 'dataCode' }];
  }

  render() {
    return <Table dataSet={this.addModalDS} columns={this.columns} queryFieldsLimit={3} />;
  }
}

let modalDS;
function callback(addDataModalDS) {
  modalDS = addDataModalDS;
}

// 提交
async function handleSubmit(pageDataSet) {
  const res = await modalDS.submit();
  if (!isEmpty(res) && res.failed && res.message) {
    notification.error({
      message: res.message,
    });
  } else if (!isEmpty(res) && res.success) {
    pageDataSet.query();
    return true;
  } else {
    notification.warning({
      message: intl.get('hzero.common.message.validation.atLeast').d('请至少选择一条数据'),
    });
  }
  return false;
}

export default function openAddModal(addModalOptions) {
  const { title, pageDataSet } = addModalOptions;
  Modal.open({
    title,
    closable: true,
    key: modalKey,
    style: {
      width: 1000,
    },
    children: <AddModal addModalOptions={addModalOptions} callback={callback} />,
    destroyOnClose: true,
    onOk: () => handleSubmit(pageDataSet),
  });
}
