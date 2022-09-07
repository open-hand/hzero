import React, { Component } from 'react';
import { DataSet, Table, Select, Button, Dropdown, IntlField } from 'choerodon-ui/pro';
import { Modal, Checkbox, Icon, Menu, Spin, Popconfirm } from 'choerodon-ui';
import { Bind, Debounce } from 'lodash-decorators';
import { isEmpty, isNil } from 'lodash';

import { HZERO_PLATFORM } from 'utils/config';
import { queryMapIdpValue } from 'services/api';
import notification from 'utils/notification';
import intl from 'utils/intl';

import {
  queryUserCustConfig,
  saveUserCustConfig,
  resetUserCustConfig,
} from '../withCustomize/customizeTool';
import styles from './style.less';

const { Option } = Select;
const { Column } = Table;
const pageSizeOption = [10, 20, 50, 100];

export default class CustModal extends Component {
  state = {
    queryLoading: false, // 查询接口loading状态标识
    saveLoading: false, // 保存接口loading标识
    modifyFlag: false, // 是否修改标识
    activeUnitCode: null,
    // 表格列
    tableCols: [],
    pageSize: 10,
    // 表格dataSet配置
    custTableDs: new DataSet({
      selection: false,
      paging: false,
    }),
    modifyConfig: {}, // 最终配置,用于保存
    dropMenuVisible: true,
    dropMenuMouseEnterFlag: false,
    gridFixedOptions: [],
  };

  componentDidMount() {
    this.fetchUserCustConfig();
    this.fetchLovData();
  }

  custColDs = new DataSet({
    autoCreate: true,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'fieldName',
        label: intl.get('hcuz.custButton.view.title.columnName').d('列名'),
        type: 'intl',
        required: true,
      },
      {
        name: 'gridWidth',
        label: intl.get('hcuz.custButton.view.title.columnWidth').d('列宽'),
        type: 'number',
      },
      {
        name: 'gridSeq',
        label: intl.get('hcuz.custButton.view.title.position').d('位置'),
        type: 'number',
      },
      {
        name: 'gridFixed',
        label: intl.get('hcuz.custButton.view.title.fixed').d('冻结'),
      },
    ],
    events: {
      update: ({ dataSet, name }) => {
        if (!name.includes('_tls')) {
          this.changeFieldConfig(dataSet.toData());
        }
      },
    },
    transport: {
      tls: ({ dataSet, name: fieldName }) => {
        // TODO: 先使用 dataSet.current 下个版本 c7n 会 把 record 传进来
        const _token = dataSet.current.get('_token');
        return {
          url: `${HZERO_PLATFORM}/v1/multi-language`,
          method: 'GET',
          params: { _token, fieldName },
          transformResponse: (data) => {
            try {
              const jsonData = JSON.parse(data);
              if (jsonData && !jsonData.faied) {
                const tlsRecord = {};
                jsonData.forEach((intlRecord) => {
                  if (!intlRecord.value.trim()) {
                    if (intlRecord.code === 'zh_CN') {
                      tlsRecord[intlRecord.code] = dataSet.current.get('fieldName');
                    } else {
                      // 不要空格，防止错操作
                      tlsRecord[intlRecord.code] = '';
                    }
                  } else {
                    tlsRecord[intlRecord.code] = intlRecord.value;
                  }
                });
                return [{ [fieldName]: tlsRecord }];
              }
            } catch (e) {
              // do nothing, use default error deal
            }
            return data;
          },
        };
      },
    },
  });

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

  // 对列排序
  @Bind()
  sortColumnsOrder(columns = [], seqField = 'gridSeq', fixedField = 'gridFixed') {
    // 按照列顺序字段seqField排序
    columns.sort((before, after) => before[seqField] - after[seqField]);
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

  // 过滤显示列
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
  initConfig(custConfig = {}) {
    const { config = {} } = custConfig;
    let { fields } = custConfig;
    let btnDisabled;
    let newPageSize = 10;
    if (!fields) {
      fields = [];
      btnDisabled = true;
    }
    if (isEmpty(config)) {
      btnDisabled = true;
    } else {
      newPageSize = config.pageSize;
    }
    const custTableDsFields = [];
    fields = this.sortColumnsOrder(fields);
    fields = this.adjustColumnsVisible(fields);
    const tableCols = fields.map((item) => {
      custTableDsFields.push({
        name: item.fieldCode,
        label: item.fieldName,
      });
      let lock = null;
      if (item.gridFixed === 'L') {
        lock = 'left';
      }
      if (item.gridFixed === 'R') {
        lock = 'right';
      }
      return {
        name: item.fieldCode,
        label: item.fieldName,
        width: item.gridWidth,
        orderSeq: item.gridSeq,
        hidden: item.visible === 0,
        lock,
      };
    });
    this.custColDs.loadData(fields.filter((item) => item.visible === 1));
    this.setState({
      btnDisabled,
      pageSize: newPageSize,
      tableCols,
      custTableDs: new DataSet({
        selection: false,
        paging: false,
        fields: custTableDsFields,
      }),
    });
  }

  @Bind()
  changeUnit(unitCode) {
    const { activeUnitCode, modifyConfig, modifyFlag = false } = this.state;
    if (activeUnitCode !== unitCode) {
      if (modifyFlag) {
        Modal.confirm({
          content: intl
            .get('hcuz.custButton.view.message.delete.confirm')
            .d('此页面有修改项尚未保存，确定要离开此页面？'),
          okText: intl.get('hzero.common.button.sure').d('确定'),
          cancelText: intl.get('hzero.common.button.cancel').d('取消'),
          onOk: () => {
            this.setState({
              activeUnitCode: unitCode,
              modifyFlag: false,
            });
            this.initConfig(modifyConfig[unitCode]);
          },
        });
      } else {
        this.setState({
          activeUnitCode: unitCode,
          modifyFlag: false,
        });
        this.initConfig(modifyConfig[unitCode]);
      }
    }
  }

  // 更改字段配置
  @Bind()
  changeFieldConfig(config) {
    const { modifyConfig, activeUnitCode } = this.state;
    const fields = modifyConfig[activeUnitCode].fields || [];
    const newFields = fields.map((fieldItem) => {
      const newItem = config.find((configItem) => configItem.fieldCode === fieldItem.fieldCode);
      if (newItem) {
        return newItem;
      }
      return fieldItem;
    });
    modifyConfig[activeUnitCode].fields = newFields;
    this.setState({
      modifyConfig,
      modifyFlag: true,
    });
    this.initConfig(modifyConfig[activeUnitCode]);
  }

  // 更改字段显示隐藏, 固定模式
  @Bind()
  changeFieldVisibleOrFixed(fieldCode, attribute, value = null) {
    const { modifyConfig, activeUnitCode } = this.state;
    const fields = modifyConfig[activeUnitCode].fields || [];
    // eslint-disable-next-line array-callback-return
    fields.find((fieldItem) => {
      if (fieldItem.fieldCode === fieldCode) {
        if (attribute === 'visible') {
          // eslint-disable-next-line no-param-reassign
          fieldItem.visible = fieldItem.visible === 0 ? 1 : 0;
        } else {
          // eslint-disable-next-line no-param-reassign
          fieldItem[attribute] = value;
        }
      }
    });
    modifyConfig[activeUnitCode].fields = fields;
    this.setState({
      modifyConfig,
      modifyFlag: true,
    });
    this.initConfig(modifyConfig[activeUnitCode]);
  }

  // 解决bug: 取消时 c7n IntlField 会默认改变index=0的record
  @Bind()
  cancleChangeIntl() {
    const { modifyConfig, activeUnitCode } = this.state;
    this.initConfig(modifyConfig[activeUnitCode]);
  }

  @Bind()
  changeIntl() {
    this.changeFieldConfig(this.custColDs.toData());
  }

  // 更改表格每页行数
  @Bind()
  changePageSize(pageSize) {
    const { activeUnitCode, modifyConfig = {} } = this.state;
    modifyConfig[activeUnitCode].config.pageSize = pageSize;
    this.setState({ modifyConfig, pageSize, modifyFlag: true });
  }

  // 渲染左侧单元
  @Bind()
  renderUnit() {
    const { activeUnitCode, modifyConfig } = this.state;
    return Object.keys(modifyConfig).map((item) => (
      <div
        className={activeUnitCode === item ? styles['cust-unit-active'] : styles['cust-unit']}
        onClick={() => this.changeUnit(item)}
      >
        <div className={styles['cust-unit-name']}>{modifyConfig[item].unitName}</div>
        <div className={styles['cust-unit-code']}>{item}</div>
      </div>
    ));
  }

  // 还原
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

  // 保存
  @Bind()
  save() {
    const { modifyConfig, activeUnitCode } = this.state;
    this.custColDs.validate().then((resp) => {
      if (resp) {
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
    });
  }

  // 取消
  @Bind()
  cancle() {
    const { handleClose = () => {} } = this.props;
    handleClose();
  }

  @Bind()
  toggleDropMenu() {
    this.setState({ dropMenuVisible: false });
  }

  @Debounce(100)
  @Bind()
  hiddenDropMenu() {
    const { dropMenuMouseEnterFlag } = this.state;
    // 防抖
    if (!dropMenuMouseEnterFlag) {
      this.setState({ dropMenuVisible: true });
    }
  }

  @Bind()
  toggleMouseEnterFlag() {
    this.setState({ dropMenuMouseEnterFlag: true });
  }

  @Bind()
  clearMouseEnterFlag() {
    this.setState({ dropMenuMouseEnterFlag: true, dropMenuVisible: true });
  }

  render() {
    const {
      dropMenuVisible,
      custTableDs,
      pageSize = 10,
      queryLoading = false,
      saveLoading = false,
      resetLoading = false,
      tableCols = [],
      gridFixedOptions = [],
      btnDisabled = false,
    } = this.state;

    const menu = (
      <Menu
        className={styles['cust-col-dropDown-menu']}
        onMouseEnter={this.toggleMouseEnterFlag}
        onMouseLeave={this.clearMouseEnterFlag}
      >
        {tableCols.map((item) => (
          <Menu.Item
            onClick={() => this.changeFieldVisibleOrFixed(item.name, 'visible')}
            className={styles['cust-col-dropDown-menu-item']}
          >
            <Checkbox checked={!item.hidden} /> {item.label}
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
              <Table dataSet={custTableDs} pagination={false} columns={tableCols} />
            </div>
            <div className={styles['cust-field']}>
              {intl.get('hcuz.custButton.view.title.field').d('字段')}
              <div className={styles['cust-col-dropDown-div']}>
                <div>
                  <span className={styles['cust-col-dropDown-div-title']}>
                    {intl.get('hcuz.custButton.view.title.pageSize').d('每页行数')}:
                  </span>
                  <Select
                    className={styles['cust-col-dropDown-div-select']}
                    value={pageSize}
                    onChange={this.changePageSize}
                  >
                    {pageSizeOption.map((item) => (
                      <Option key={item} value={item}>
                        {item}
                      </Option>
                    ))}
                  </Select>
                  <Dropdown
                    className={styles['cust-col-dropDown']}
                    overlay={menu}
                    placement="bottomRight"
                    hidden={dropMenuVisible}
                  >
                    <span className={styles['cust-col-dropDown-div-title']}>
                      {intl.get('hcuz.custButton.view.title.tableCol').d('表格字段')}:
                    </span>
                    <Icon
                      type="view_column"
                      onMouseEnter={this.toggleDropMenu}
                      onMouseLeave={this.hiddenDropMenu}
                      style={{ marginLeft: 3 }}
                    />
                  </Dropdown>
                </div>
              </div>
            </div>
            <Table className={styles['cust-col-table']} pagination={false} dataSet={this.custColDs}>
              <Column
                name="fieldName"
                resizable={false}
                editor={
                  <IntlField
                    modalProps={{
                      onCancel: this.cancleChangeIntl,
                      onOk: this.changeIntl,
                    }}
                  />
                }
              />
              <Column name="gridSeq" editor resizable={false} width={150} />
              <Column name="gridWidth" editor resizable={false} width={150} />
              <Column
                resizable={false}
                name="gridFixed"
                width={150}
                renderer={({ value, record }) => (
                  <Select
                    value={value}
                    onChange={(val) =>
                      this.changeFieldVisibleOrFixed(record.get('fieldCode'), 'gridFixed', val)
                    }
                  >
                    {gridFixedOptions.map((firxedItem) => (
                      <Option value={firxedItem.value}>{firxedItem.meaning}</Option>
                    ))}
                  </Select>
                )}
              />
            </Table>
          </div>
        </div>
        <div className={styles['modal-footer']}>
          <Button icon="cancel" onClick={this.cancle} disabled={saveLoading || resetLoading}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Popconfirm
            title={intl.get('hcuz.custButton.view.button.confirmRestore').d('是否确认还原？')}
            onConfirm={this.reset}
            okText={intl.get('hzero.common.status.yes').d('是')}
            cancelText={intl.get('hzero.common.status.no').d('否')}
          >
            <Button icon="refresh" loading={resetLoading} disabled={btnDisabled || saveLoading}>
              {intl.get('hcuz.custButton.view.button.restore').d('还原')}
            </Button>
          </Popconfirm>
          <Button
            icon="save"
            color="primary"
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
