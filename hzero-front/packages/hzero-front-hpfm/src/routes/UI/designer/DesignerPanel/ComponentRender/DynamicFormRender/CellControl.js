/**
 * CellControl
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */

import React from 'react';
import { isFunction, cloneDeep, concat, slice, every, isNumber, forEach } from 'lodash';
import { Menu, Dropdown, Icon, Button, Modal, Popconfirm } from 'hzero-ui';

import notification from 'utils/notification';
import intl from 'utils/intl';

import styles from '../../index.less';
import { emptyField, emptyFieldType } from '../../../config';

const menuBtnStyle = { padding: 2 };

const menuItemKey = {
  appendRow: 'appendRow',
  appendCol: 'appendCol',
  removeRow: 'removeRow',
  removeCol: 'removeCol',
  mergeRight: 'mergeRight',
  cancelMergeRight: 'cancelMergeRight',
};

export default class CellControl extends React.Component {
  menuRef = React.createRef();

  constructor(props) {
    super(props);
    this.handleRemoveTrigger = this.handleRemoveTrigger.bind(this);
    this.handleFieldMenuClick = this.handleFieldMenuClick.bind(this);

    this.handleRemoveRow = this.handleRemoveRow.bind(this);
    this.handleRemoveCol = this.handleRemoveCol.bind(this);

    this.handleRefresh = this.handleRefresh.bind(this);

    this.handleCleanWillRemovePositionX = this.handleCleanWillRemovePositionX.bind(this);
    this.handleCleanWillRemovePositionY = this.handleCleanWillRemovePositionY.bind(this);
  }

  render() {
    const {
      children,
      menuItemProps,
      component,
      currentEditField,
      willRemovePositionY,
      willRemovePositionX,
      positionY,
      positionX,
      positionRightX,
      isOverAndCanDrop,
    } = this.props;
    const cellControlClassName = [styles['cell-control']];
    const isCurrentEditField = currentEditField === component;

    const isNoEmptyCell = component.componentType !== emptyFieldType;

    let isWillRemoveCell = false;
    if (positionY === willRemovePositionY) {
      // 当前字段在将要删除的行里面
      isWillRemoveCell = true;
    } else if (willRemovePositionX >= positionX && willRemovePositionX < positionRightX) {
      // 当前字段在将要删除的列里面
      isWillRemoveCell = true;
    }

    if (isCurrentEditField) {
      cellControlClassName.push(styles['cell-control-active']);
    }

    if (!isNoEmptyCell) {
      cellControlClassName.push(styles['cell-control-placeholder']);
    }

    if (isWillRemoveCell) {
      // if(component.colspan && isNumber(component.colspan)){
      //   cellControlClassName.push(styles['cell-control-will-remove-colspan']);
      // }else{
      cellControlClassName.push(styles['cell-control-will-remove']);
      // }
    }

    if (isOverAndCanDrop) {
      cellControlClassName.push(styles['cell-control-over']);
    }

    return (
      <div className={cellControlClassName.join(' ')}>
        <div className={styles['cell-control-component']}>{children}</div>
        {isNoEmptyCell && (
          <i className={styles['cell-control-remove']}>
            <Popconfirm
              onConfirm={this.handleRemoveTrigger}
              title={intl.get('hpfm.ui.message.field.removeTitle').d('是否确认删除')}
            >
              <i className="anticon" />
            </Popconfirm>
          </i>
        )}
        <div className={styles['cell-control-menu']} ref={this.menuRef}>
          <Dropdown
            getPopupContainer={() => {
              return this.menuRef.current || document.body;
            }}
            overlay={
              <Menu onClick={this.handleFieldMenuClick}>
                <Menu.Item key={menuItemKey.appendRow} disabled={!menuItemProps.canInsertBottom}>
                  <i className="menu-icon menu-icon-append-row" />
                  {intl.get('hpfm.codeRule.model.codeRule.addRow').d('向下新增一行')}
                </Menu.Item>
                <Menu.Item key={menuItemKey.appendCol} disabled={!menuItemProps.canInsertRight}>
                  <i className="menu-icon menu-icon-append-col" />
                  {intl.get('hpfm.codeRule.model.codeRule.addColumn').d('向下新增一列')}
                </Menu.Item>
                <Menu.Divider />
                <Menu.Item key={menuItemKey.mergeRight} disabled={!menuItemProps.canMergeRight}>
                  <i className="menu-icon menu-icon-merge-right" />
                  {intl.get('hpfm.codeRule.model.codeRule.combineRight').d('向右合并')}
                </Menu.Item>
                <Menu.Item
                  key={menuItemKey.cancelMergeRight}
                  disabled={!menuItemProps.canCancelMergeRight}
                >
                  <i className="menu-icon menu-icon-cancel-merge-right" />
                  {intl.get('hpfm.codeRule.model.codeRule.cancelCombine').d('取消合并')}
                </Menu.Item>

                <Menu.Divider />

                <Menu.Item key={menuItemKey.removeRow} disabled={!menuItemProps.canRemoveRow}>
                  <i className="menu-icon menu-icon-remove-row" />
                  {intl.get('hpfm.codeRule.model.codeRule.deleteRow').d('删除行')}
                </Menu.Item>
                <Menu.Item key={menuItemKey.removeCol} disabled={!menuItemProps.canRemoveCol}>
                  <i className="menu-icon menu-icon-remove-col" />
                  {intl.get('hpfm.codeRule.model.codeRule.deleteCol').d('删除列')}
                </Menu.Item>
              </Menu>
            }
          >
            <Button style={menuBtnStyle}>
              <Icon type="down" />
            </Button>
          </Dropdown>
        </div>
      </div>
    );
  }

  handleRemoveTrigger(e) {
    e.stopPropagation();
    const { component, pComponent, fieldOptions, onRemoveField } = this.props;
    if (isFunction(onRemoveField)) {
      onRemoveField(pComponent, component, fieldOptions);
    }
  }

  handleFieldMenuClick({ key }) {
    switch (key) {
      case menuItemKey.appendRow:
        this.handleAppendRow();
        break;
      case menuItemKey.appendCol:
        this.handleAppendCol();
        break;
      case menuItemKey.removeRow:
        this.handleRemoveRowTrigger();
        break;
      case menuItemKey.removeCol:
        this.handleRemoveColTrigger();
        break;
      case menuItemKey.mergeRight:
        this.handleMergeRight();
        break;
      case menuItemKey.cancelMergeRight:
        this.handleCancelMergeRight();
        break;
      default:
        break;
    }
  }

  /**
   * 新增行
   */
  handleAppendRow() {
    const { colCount, rowIndex, pComponent } = this.props;
    const newRow = [];
    for (let index = 0; index < colCount; index += 1) {
      newRow.push(cloneDeep(emptyField));
    }
    const sliceIndex = rowIndex + 1;
    pComponent.fields = concat(
      [],
      slice(pComponent.fields, 0, sliceIndex),
      [newRow],
      slice(pComponent.fields, sliceIndex)
    );
    this.handleRefresh();
  }

  /**
   * 删除行 触发方法
   */
  handleRemoveRowTrigger() {
    const { rowCount, pComponent, positionY, rowIndex } = this.props;
    if (rowCount < 2) {
      notification.warning({
        message: intl.get('hpfm.codeRule.model.codeRule.canNotDelLastRow').d('不能删除最后一行'),
      });
      return;
    }
    if (every(pComponent.fields[rowIndex], field => field.componentType === emptyFieldType)) {
      this.handleRemoveRow();
    } else {
      this.handleUpdateWillRemovePositionY(positionY);
      Modal.confirm({
        content: intl.get('hpfm.codeRule.model.codeRule.isDelCurRow').d('是否删除当前行'),
        onOk: this.handleRemoveRow,
        onCancel: this.handleCleanWillRemovePositionY,
      });
    }
  }

  /**
   * todo 要将 site 字段放到 removeSiteFields 中
   * 删除行 实际方法
   */
  handleRemoveRow() {
    const { rowIndex, pComponent } = this.props;
    pComponent.fields = concat(
      [],
      slice(pComponent.fields, 0, rowIndex),
      slice(pComponent.fields, rowIndex + 1)
    );
    // // todo 租户级代码
    // const removeFields = pComponent.fields[rowIndex];
    // for (let index = 0; index < removeFields.length; index += 1) {
    //   const removeField = removeFields[index];
    //   if (removeField.siteFlag === 1) {
    //     removeField.visiableFlag = 0;
    //     pComponent.removeSiteFields.push(removeField);
    //   }
    // }
    this.handleCleanWillRemovePositionY();
    this.handleRefresh();
  }

  /**
   * 新增列
   */
  handleAppendCol() {
    const { pComponent, rowIndex, colIndex } = this.props;
    // 先找到当前字段的位置所占位置的最后
    const currentRow = pComponent.fields[rowIndex];
    let positionX = 0;
    for (let index = 0; index <= colIndex; index += 1) {
      const f = currentRow[index];
      if (f.colspan && isNumber(f.colspan)) {
        positionX += f.colspan;
      } else {
        positionX += 1;
      }
    }
    // 得到当前字段的位置
    forEach(pComponent.fields, (fArr, rIndex) => {
      let endPositionX = 0;
      forEach(fArr, (f, cIndex) => {
        const isColspan = f.colspan && isNumber(f.colspan);
        if (isColspan) {
          endPositionX += f.colspan;
        } else {
          endPositionX += 1;
        }
        if (endPositionX > positionX) {
          // 改变 colspan
          // eslint-disable-next-line
          f.colspan += 1;
          return false;
        } else if (endPositionX === positionX) {
          // 增加新的 emptyField
          const sliceIndex = cIndex + 1;
          pComponent.fields[rIndex] = concat(
            [],
            slice(fArr, 0, sliceIndex),
            [cloneDeep(emptyField)],
            slice(fArr, sliceIndex)
          );
          return false;
        }
      });
    });
    this.handleRefresh();
  }

  /**
   * 删除列 触发方法
   */
  handleRemoveColTrigger() {
    const { colCount, positionRightX } = this.props;
    if (colCount < 2) {
      notification.warning({
        message: intl.get('hpfm.codeRule.model.codeRule.canNotDelLastCol').d('不能删除最后一列'),
      });
      return;
    }
    this.handleUpdateWillRemovePositionX(positionRightX - 1);
    Modal.confirm({
      content: intl.get('hpfm.codeRule.model.codeRule.isDelCurCol').d('是否删除当前列'),
      onOk: this.handleRemoveCol,
      onCancel: this.handleCleanWillRemovePositionX,
    });
  }

  /**
   * todo 要将 site 字段放到 removeSiteFields 中
   * 删除列 实际方法
   */
  handleRemoveCol() {
    const { pComponent, rowIndex, colIndex } = this.props;
    // 找到要删除的位置
    const currentRow = pComponent.fields[rowIndex];
    let positionX = 0;
    for (let index = 0; index <= colIndex; index += 1) {
      const f = currentRow[index];
      if (f.colspan && isNumber(f.colspan)) {
        positionX += f.colspan;
      } else {
        positionX += 1;
      }
    }
    forEach(pComponent.fields, (fArr, rIndex) => {
      let endPositionX = 0;
      forEach(fArr, (f, cIndex) => {
        const isColspan = f.colspan && isNumber(f.colspan);
        if (isColspan) {
          endPositionX += f.colspan;
        } else {
          endPositionX += 1;
        }
        if (endPositionX >= positionX && isColspan) {
          // colspan -1
          if (f.colspan === 2) {
            // eslint-disable-next-line
            delete f.colspan;
          } else {
            // eslint-disable-next-line
            f.colspan -= 1;
          }
          return false;
        } else if (endPositionX === positionX) {
          // const removeField = fArr[cIndex];
          // // todo 租户级的代码
          // if (removeField.siteFlag === 1) {
          //   removeField.visiableFlag = 0;
          //   pComponent.removeSiteFields.push(removeField);
          // }
          pComponent.fields[rIndex] = concat([], slice(fArr, 0, cIndex), slice(fArr, cIndex + 1));
          return false;
        }
      });
    });
    this.handleCleanWillRemovePositionX();
    this.handleRefresh();
  }

  /**
   * 更新 要删除的下标
   */
  handleUpdateWillRemovePositionY(willRemovePositionY = -1) {
    const { onUpdateWillRemovePositionY } = this.props;
    if (isFunction(onUpdateWillRemovePositionY)) {
      onUpdateWillRemovePositionY(willRemovePositionY);
    }
  }

  /**
   * 更新 要删除的下标
   */
  handleUpdateWillRemovePositionX(willRemovePositionX = -1) {
    const { onUpdateWillRemovePositionX } = this.props;
    if (isFunction(onUpdateWillRemovePositionX)) {
      onUpdateWillRemovePositionX(willRemovePositionX);
    }
  }

  /**
   * 更新 要删除的下标
   */
  handleCleanWillRemovePositionY() {
    const { onUpdateWillRemovePositionY } = this.props;
    if (isFunction(onUpdateWillRemovePositionY)) {
      onUpdateWillRemovePositionY(-1);
    }
  }

  /**
   * 更新 要删除的下标
   */
  handleCleanWillRemovePositionX() {
    const { onUpdateWillRemovePositionX } = this.props;
    if (isFunction(onUpdateWillRemovePositionX)) {
      onUpdateWillRemovePositionX(-1);
    }
  }

  /**
   * 合并右侧空
   */
  handleMergeRight() {
    const { rowIndex, colIndex, pComponent } = this.props;
    const { fields = [] } = pComponent;
    const currentRow = fields[rowIndex];
    if (!(currentRow[colIndex + 1] && currentRow[colIndex + 1].componentType === emptyFieldType)) {
      notification.warning({
        message: intl
          .get('hpfm.codeRule.model.codeRule.canCombineRightFiled')
          .d('只能合并右侧空字段'),
      });
      return;
    }
    const currentField = currentRow[colIndex];

    // 重写 field 的 colspan
    if (currentField.colspan && isNumber(currentField.colspan)) {
      currentField.colspan += 1;
    } else {
      currentField.colspan = 2;
    }
    // 去除多余的 emptyField
    pComponent.fields[rowIndex] = concat(
      [],
      slice(currentRow, 0, colIndex + 1),
      slice(currentRow, colIndex + 2)
    );
    this.handleRefresh();
  }

  /**
   * 取消合并
   */
  handleCancelMergeRight() {
    const { component, pComponent, rowIndex, colIndex } = this.props;
    if (!(component.colspan && isNumber(component.colspan))) {
      notification.warning({
        message: intl.get('hpfm.codeRule.model.codeRule.canQCFiled').d('只能拆分合并后的字段'),
      });
    }

    if (component.colspan > 2) {
      component.colspan -= 1;
    } else {
      delete component.colspan;
    }
    // 加上 空字段
    const currentRow = pComponent.fields[rowIndex];
    const sliceIndex = colIndex + 1;
    pComponent.fields[rowIndex] = concat(
      [],
      slice(currentRow, 0, sliceIndex),
      [cloneDeep(emptyField)],
      slice(currentRow, sliceIndex)
    );
    this.handleRefresh();
  }

  /**
   * 更新 父组件 以刷新视图
   */
  handleRefresh() {
    const { onRefresh } = this.props;
    if (isFunction(onRefresh)) {
      onRefresh();
    }
  }
}
