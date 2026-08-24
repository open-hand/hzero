import React, { Component } from 'react';
import {
  Dropdown,
  Checkbox,
  Popconfirm,
  Menu,
  Spin,
  Select,
  Table,
  Button,
  Form,
  Modal,
  InputNumber,
} from 'hzero-ui';
import { Bind, Debounce } from 'lodash-decorators';
import { isEmpty, isNil } from 'lodash';

import EditTable from 'components/EditTable';
import { queryMapIdpValue } from 'services/api';
import notification from 'utils/notification';
import { getEditTableData } from 'utils/utils';
import intl from 'utils/intl';

import viewColumn from '@/assets/view_column.svg';
import TLEditor from '../TLEditor';
import {
  queryUserCustConfig,
  saveUserCustConfig,
  resetUserCustConfig,
} from '../c7n/withCustomize/customizeTool';
import styles from './style.less';

const { Option } = Select;
const FormItem = Form.Item;
const rowKey = 'fieldId';
const gridFixedMap = {
  L: 'left',
  R: 'right',
};

@Form.create({ fieldNameProp: null })
export default class CustModal extends Component {
  state = {
    queryLoading: false, // 查询接口loading状态标识
    saveLoading: false, // 保存接口loading标识
    modifyFlag: false, // 是否修改标识
    activeUnitCode: null,
    fields: [], // 所有字段
    tableCols: [], // 表格列
    modifyConfig: {}, // 最终配置,用于保存
    dropMenuVisible: false,
    dropMenuMouseEnterFlag: false,
    gridFixedOptions: [],
    tableChanging: false, // 延迟
  };

  componentDidMount() {
    this.fetchUserCustConfig();
    this.fetchLovData();
  }

  @Bind()
  fetchUserCustConfig(activeUnitCode = null) {
    this.setState({ queryLoading: true });
    const { unit = [] } = this.props;
    if (unit && unit.length > 0) {
      queryUserCustConfig({ unitCode: unit.map((item) => item.code).join(',') }).then((res) => {
        if (res) {
          // 将传入的第一个作为单元设为默认
          this.setState({
            activeUnitCode: activeUnitCode || unit[0].code,
            modifyConfig: res || {},
          });
          this.initConfig(res[unit[0].code]);
        }
        this.setState({ queryLoading: false });
      });
    } else {
      this.setState({ queryLoading: false });
    }
  }

  @Bind()
  fetchLovData() {
    queryMapIdpValue({
      gridFixedOptions: 'HPFM.CUST.GIRD.FIXED',
    }).then((res) => {
      if (res) {
        this.setState({
          gridFixedOptions: res.gridFixedOptions || [],
        });
      }
    });
  }

  @Bind()
  initConfig(custConfig) {
    const { config = {} } = custConfig;
    let { fields } = custConfig;
    let btnDisabled;
    if (!fields) {
      fields = [];
      btnDisabled = true;
    }
    if (isEmpty(config)) {
      btnDisabled = true;
    }
    fields = this.sortColumnsOrder(fields);
    fields = this.adjustColumnsVisible(fields);
    const tableCols = [];
    fields.forEach((item) => {
      const {
        fieldId,
        fieldCode,
        fieldName,
        gridSeq,
        gridFixed,
        gridWidth,
        visible,
        _tls,
        _token,
      } = item;
      if (visible === 1) {
        tableCols.push({
          visible,
          gridFixed,
          fieldCode,
          _tls,
          _token,
          id: fieldId,
          fixed: gridFixedMap[gridFixed],
          title: fieldName,
          width: gridWidth,
          order: gridSeq,
          _status: 'update',
        });
      }
    });
    this.setState({
      fields,
      tableCols,
      btnDisabled,
    });
  }

  // 对列排序
  @Bind()
  sortColumnsOrder(columns = [], seqField = 'gridSeq', fixedField = 'gridFixed') {
    // 按照列顺序字段seqField排序
    columns.sort((before, after) => {
      if (!before[seqField] && !after[seqField]) {
        return 0;
      } else if (!before[seqField]) {
        return -1;
      } else if (!after[seqField]) {
        return 1;
      } else {
        return before[seqField] - after[seqField];
      }
    });
    let orderedColumns = columns;
    // 按照列固定字段gridFixed排序, 左固定前置,右固定后置
    const leftFixedColumns = orderedColumns.filter((item) =>
      ['L', 'left'].includes(item[fixedField])
    );
    const rightFixedColumns = orderedColumns.filter((item) =>
      ['R', 'right'].includes(item[fixedField])
    );
    const centerColumns = orderedColumns.filter(
      (item) => !['L', 'left', 'R', 'right'].includes(item[fixedField])
    );
    orderedColumns = leftFixedColumns.concat(centerColumns).concat(rightFixedColumns);
    return orderedColumns;
  }

  // 处理列的显示隐藏属性
  @Bind()
  adjustColumnsVisible(columns = []) {
    const { unit = [] } = this.props;
    let { activeUnitCode } = this.state;
    if (unit && unit.length > 0) {
      // 初始时设为第一个unit
      if (!activeUnitCode) {
        activeUnitCode = unit[0].code;
      }
      const currentUnit = unit.find((item) => item.code === activeUnitCode) || {};
      const currentUnitColumns = currentUnit.columns || [];
      const newColumns = [];
      columns.forEach((item) => {
        const newItem = item;
        // -1 原代码逻辑
        if (item.visible === -1) {
          // 传了原代码的columns
          if (!isEmpty(currentUnitColumns)) {
            const targetUnitItem = currentUnitColumns.find(
              (unitColumnsItem) => unitColumnsItem.name === item.fieldCode
            );
            if (targetUnitItem) {
              newItem.visible = targetUnitItem.hidden ? 0 : 1;
            }
          } else {
            // 未传原代码的columns
            newItem.visible = 1;
          }
        }
        newColumns.push(newItem);
      });
      return newColumns;
    }
    return columns;
  }

  @Bind()
  changeFieldConfig(fieldCode, attribute, value = null) {
    const { activeUnitCode, modifyConfig = {}, tableCols = [] } = this.state;
    const tableColsData = getEditTableData(tableCols);
    if (!isEmpty(modifyConfig[activeUnitCode])) {
      const { fields = [] } = modifyConfig[activeUnitCode];
      let changeFlag = false;
      let newFields = [];
      newFields = fields.map((fieldItem) => {
        if (fieldItem.fieldCode === fieldCode) {
          const currentItem = tableColsData.find(
            (colDataItem) => colDataItem.fieldCode === fieldCode
          );
          if (currentItem) {
            if (['gridFixed', 'title'].includes(attribute)) {
              currentItem[attribute] = value;
            }
            changeFlag = true;
            /*   // 多语言点开就会调用onChange,故保持changeFlag=true
            if (attribute === 'title') {
              changeFlag = true;
             // currentItem[attribute] = value;
            } else if (['width', 'order'].includes(attribute) && !isNaN(Number(value))) {
              const oldValue = fieldItem[attribute === 'width' ? 'gridWidth' : 'gridSeq'] || null;
              const newValue = Number(value) || null;
              if (oldValue !== newValue) {
                changeFlag = true;
               // currentItem[attribute] = newValue;
              }
            } else if (currentItem[attribute] !== value) {
              changeFlag = true;
             // currentItem[attribute] = value;
            } */
            const { title, order, width, gridFixed, _tls } = currentItem;
            // eslint-disable-next-line  no-param-reassign
            fieldItem = {
              ...fieldItem,
              gridFixed,
              _tls,
              fieldName: title,
              gridSeq: order,
              gridWidth: width,
            };
          }
        }
        return fieldItem;
      });
      if (changeFlag) {
        modifyConfig[activeUnitCode].fields = newFields;
        this.setState({
          modifyConfig,
          modifyFlag: true,
        });
        this.initConfig(modifyConfig[activeUnitCode]);
        // 解决EditTable编辑框内数据遗留问题
        if (attribute !== 'title') {
          this.setState({ tableChanging: true }, () => {
            this.setState({ tableChanging: false });
          });
        }
      }
    }
  }

  @Bind()
  changeFieldVisible(fieldCode) {
    const { activeUnitCode, modifyConfig = {} } = this.state;
    if (!isEmpty(modifyConfig[activeUnitCode])) {
      const { fields = [] } = modifyConfig[activeUnitCode];
      // eslint-disable-next-line array-callback-return
      fields.find((item) => {
        if (item.fieldCode === fieldCode) {
          // eslint-disable-next-line no-param-reassign
          item.visible = item.visible === 1 ? 0 : 1;
        }
      });
      modifyConfig[activeUnitCode].fields = fields;
      this.setState({ modifyConfig, modifyFlag: true });
      this.initConfig(modifyConfig[activeUnitCode]);
    }
  }

  @Bind()
  changeUnit(unitCode) {
    const { modifyConfig, activeUnitCode, modifyFlag } = this.state;
    if (unitCode !== activeUnitCode) {
      if (modifyFlag) {
        Modal.confirm({
          content: intl
            .get('hcuz.custButton.view.message.delete.confirm')
            .d('此页面有修改项尚未保存，确定要离开此页面？'),
          okText: intl.get('hzero.common.button.sure').d('确定'),
          cancelText: intl.get('hzero.common.button.cancel').d('取消'),
          onOk: () => {
            // 解决EditTable编辑框内数据遗留问题
            this.setState(
              {
                tableChanging: true,
              },
              () => {
                this.setState({
                  tableChanging: false,
                  activeUnitCode: unitCode,
                  modifyFlag: false,
                });
                this.initConfig(modifyConfig[unitCode]);
              }
            );
          },
        });
      } else {
        // 解决EditTable编辑框内数据遗留问题
        this.setState(
          {
            tableChanging: true,
          },
          () => {
            this.setState({
              tableChanging: false,
              activeUnitCode: unitCode,
              modifyFlag: false,
            });
            this.initConfig(modifyConfig[unitCode]);
          }
        );
      }
    }
  }

  @Bind()
  validateEditTableData(editTableData) {
    let flag = true;
    if (Array.isArray(editTableData) && editTableData.length !== 0) {
      for (let i = 0; i < editTableData.length; i++) {
        if (editTableData[i].$form && editTableData[i]._status) {
          editTableData[i].$form.validateFieldsAndScroll(
            { scroll: { allowHorizontalScroll: true } },
            // eslint-disable-next-line no-loop-func
            (err) => {
              if (err) {
                flag = false;
              }
            }
          );
          if (!flag) {
            break;
          }
        }
      }
    }
    return flag;
  }

  @Bind()
  reset() {
    const { modifyConfig = {}, activeUnitCode } = this.state;
    const { config = {} } = modifyConfig[activeUnitCode];
    this.setState({ resetLoading: true });
    if (!isEmpty(config)) {
      resetUserCustConfig({
        unitId: config.unitId,
      }).then((res) => {
        if (!isNil(res)) {
          notification.success();
          this.fetchUserCustConfig(activeUnitCode);
        }
        this.setState({ resetLoading: false });
      });
    }
  }

  @Bind()
  save() {
    const { modifyConfig, activeUnitCode, tableCols } = this.state;
    const flag = this.validateEditTableData(tableCols);
    if (flag) {
      const fields = modifyConfig[activeUnitCode].fields || [];
      let newFields = [];
      if (fields.length > 0) {
        newFields = fields.map((fieldItem) => {
          if (fieldItem._tls && fieldItem._tls.fieldName && !fieldItem._tls.fieldName.zh_CN) {
            // eslint-disable-next-line no-param-reassign
            fieldItem._tls.fieldName.zh_CN = fieldItem.fieldName;
          }
          return fieldItem;
        });
      }
      modifyConfig[activeUnitCode].fields = newFields;
      const params = modifyConfig[activeUnitCode];
      this.setState({ saveLoading: true });
      saveUserCustConfig(params).then((res) => {
        if (res) {
          notification.success();
          const { handleClose = () => {} } = this.props;
          handleClose();
        }
        this.setState({ saveLoading: false });
      });
    }
  }

  @Bind()
  cancle() {
    const { handleClose = () => {} } = this.props;
    handleClose();
  }

  @Bind()
  toggleDropMenu() {
    this.setState({ dropMenuVisible: true });
  }

  @Debounce(100)
  @Bind()
  hiddenDropMenu() {
    const { dropMenuMouseEnterFlag } = this.state;
    // 防抖
    if (!dropMenuMouseEnterFlag) {
      this.setState({ dropMenuVisible: false });
    }
  }

  @Bind()
  toggleMouseEnterFlag() {
    this.setState({ dropMenuMouseEnterFlag: true });
  }

  @Bind()
  clearMouseEnterFlag() {
    this.setState({ dropMenuMouseEnterFlag: true, dropMenuVisible: false });
  }

  @Bind()
  getCustFieldsColumns() {
    const { gridFixedOptions = [] } = this.state;
    return [
      {
        title: intl.get('hcuz.custButton.view.title.columnName').d('列名'),
        dataIndex: 'title',
        render: (val, record) => (
          <FormItem>
            {record.$form.getFieldDecorator('title', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hcuz.custButton.view.title.columnName').d('列名'),
                  }),
                },
              ],
              initialValue: val,
            })(
              <TLEditor
                label={intl.get('hcuz.custButton.view.title.columnName').d('列名')}
                field="fieldName"
                data={record._tls}
                token={record._token}
                onChange={(value) => this.changeFieldConfig(record.fieldCode, 'title', value)}
              />
            )}
          </FormItem>
        ),
      },
      {
        title: intl.get('hcuz.custButton.view.title.position').d('位置'),
        dataIndex: 'order',
        width: 150,
        render: (val, record) => (
          <FormItem>
            {record.$form.getFieldDecorator('order', {
              initialValue: val,
            })(
              <InputNumber
                min={0}
                onBlur={(value) => this.changeFieldConfig(record.fieldCode, 'order', value)}
              />
            )}
          </FormItem>
        ),
      },
      {
        title: intl.get('hcuz.custButton.view.title.columnWidth').d('列宽'),
        dataIndex: 'width',
        width: 150,
        render: (val, record) => (
          <FormItem>
            {record.$form.getFieldDecorator('width', {
              initialValue: val,
            })(
              <InputNumber
                min={0}
                onBlur={(value) => this.changeFieldConfig(record.fieldCode, 'width', value)}
              />
            )}
          </FormItem>
        ),
      },
      {
        title: intl.get('hcuz.custButton.view.title.fixed').d('冻结'),
        dataIndex: 'gridFixed',
        width: 150,
        render: (val, record) => (
          <FormItem>
            {record.$form.getFieldDecorator('gridFixed', {
              initialValue: val,
            })(
              <Select
                allowClear
                onChange={(value) => this.changeFieldConfig(record.fieldCode, 'gridFixed', value)}
              >
                {gridFixedOptions.map((firxedItem) => (
                  <Option key={firxedItem.value} value={firxedItem.value}>
                    {firxedItem.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
        ),
      },
    ];
  }

  @Bind()
  renderUnit() {
    const { activeUnitCode, modifyConfig } = this.state;
    return Object.keys(modifyConfig).map((item) => (
      <div
        key={item}
        className={activeUnitCode === item ? styles['cust-unit-active'] : styles['cust-unit']}
        onClick={() => this.changeUnit(item)}
      >
        <div className={styles['cust-unit-name']}>{modifyConfig[item].unitName}</div>
        <div className={styles['cust-unit-code']}>{item}</div>
      </div>
    ));
  }

  render() {
    const {
      fields = [],
      tableCols = [],
      tableChanging = false,
      dropMenuVisible = false,
      queryLoading = false,
      saveLoading = false,
      resetLoading = false,
      btnDisabled = false,
    } = this.state;
    const menu = (
      <Menu
        className={styles['cust-col-dropDown-menu']}
        onMouseEnter={this.toggleMouseEnterFlag}
        onMouseLeave={this.clearMouseEnterFlag}
      >
        {fields.map((item) => (
          <Menu.Item
            key={item.fieldId}
            onClick={() => this.changeFieldVisible(item.fieldCode)}
            className={styles['cust-col-dropDown-menu-item']}
          >
            <Checkbox checked={item.visible === 1} /> {item.fieldName}
          </Menu.Item>
        ))}
      </Menu>
    );
    return (
      <Spin spinning={queryLoading || saveLoading || resetLoading}>
        <div className={styles['modal-content']}>
          <div className={styles['modal-content-left']}>
            <header className={styles['modal-content-left-title']}>
              {intl.get('hcuz.custButton.view.title.individuationTableUnit').d('个性化表格单元')}
            </header>
            {this.renderUnit()}
          </div>
          <div className={styles['modal-content-right']}>
            <div className={styles['cust-preview']}>
              {intl.get('hcuz.custButton.view.title.preview').d('预览效果')}
            </div>
            <div className={styles['cust-preview-table']}>
              <Table bordered rowKey={rowKey} pagination={false} columns={tableCols} />
            </div>
            <div className={styles['cust-field']}>
              {intl.get('hcuz.custButton.view.title.field').d('字段')}
              <div className={styles['cust-col-dropDown-div']}>
                <span className={styles['cust-col-dropDown-div-title']}>
                  {intl.get('hcuz.custButton.view.title.tableCol').d('表格字段')}:
                </span>
                <Dropdown overlay={menu} placement="bottomRight" visible={dropMenuVisible}>
                  <img
                    src={viewColumn}
                    alt="img"
                    onMouseEnter={this.toggleDropMenu}
                    onMouseLeave={this.hiddenDropMenu}
                  />
                </Dropdown>
              </div>
            </div>
            {!tableChanging && (
              <EditTable
                className={styles['cust-col-table']}
                pagination={false}
                dataSource={tableCols}
                columns={this.getCustFieldsColumns()}
              />
            )}
          </div>
        </div>
        <div className={styles['modal-footer']}>
          <Button icon="close" onClick={this.cancle} disabled={saveLoading || resetLoading}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Popconfirm
            title={intl.get('hcuz.custButton.view.button.confirmRestore').d('是否确认还原？')}
            onConfirm={this.reset}
            okText={intl.get('hzero.common.status.yes').d('是')}
            cancelText={intl.get('hzero.common.status.no').d('否')}
          >
            <Button
              icon="reload"
              //   onClick={this.reset}
              loading={resetLoading}
              disabled={btnDisabled || saveLoading}
            >
              {intl.get('hcuz.custButton.view.button.restore').d('还原')}
            </Button>
          </Popconfirm>
          <Button
            icon="save"
            type="primary"
            onClick={this.save}
            loading={saveLoading}
            disabled={btnDisabled || resetLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
      </Spin>
    );
  }
}
