import React, { Component, Fragment } from 'react';
import { Spin, Button, Icon, Tooltip } from 'hzero-ui';

import intl from 'utils/intl';

import styles from './style/index.less';

export default class GroupTree extends Component {
  render() {
    const {
      unitGroups = [],
      selectedGroupCode,
      fetchUnitGroupLoading,
      handleSelectGroup = () => {},
      handleOpenGroupModal = () => {},
      handleEdit = () => {},
    } = this.props;

    return (
      <Fragment>
        <div className={styles['group-tree-title']}>
          {intl.get('hpfm.individuationUnit.view.message.title.unitGroup').d('单元组')}
          <Tooltip placement="bottom" title={intl.get('hzero.common.button.create').d('新建')}>
            <Button icon="plus" size="small" ghost shape="circle" onClick={handleOpenGroupModal} />
          </Tooltip>
        </div>
        <Spin
          spinning={fetchUnitGroupLoading || false}
          wrapperClassName={styles['group-tree-spin']}
        >
          {unitGroups.map(item => (
            <Button
              type="dashed"
              className={item.groupCode === selectedGroupCode && styles['group-selected']}
              onClick={() => handleSelectGroup(item)}
            >
              <div className={styles['group-table-extra']}>
                <div className={styles['unit-operator']}>
                  <Tooltip
                    placement="bottom"
                    title={intl.get('hzero.common.button.edit').d('编辑')}
                  >
                    <Icon type="edit" onClick={() => handleEdit(item)} />
                  </Tooltip>
                </div>
                <div className={styles['group-name']}>{item.groupName}</div>
                <div className={styles['group-code']}>{item.groupCode}</div>
              </div>
            </Button>
          ))}
        </Spin>
      </Fragment>
    );
  }
}
