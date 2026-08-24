import React from 'react';
import { Form, Input, InputNumber, Popconfirm, Switch, Icon, Tooltip } from 'hzero-ui';
import { map, isEmpty, uniqBy, indexOf } from 'lodash';
import { Bind } from 'lodash-decorators';

import EditTable from 'components/EditTable';

import { operatorRender, TagRender } from 'utils/renderer';
import intl from 'utils/intl';
import { tableScrollWidth, getCurrentLanguage } from 'utils/utils';

import MultiInfoModal from './MultiInfoModal';
import styles from './index.less';

const FormItem = Form.Item;

export default class List extends React.Component {
  state = {
    editableKey: null,
    prevDataSource: [],
    dataSource: [],
    visible: false, // modal visible
    modalData: [], // modal的数据源
    dataIndex: null, // 当前列名
    currentRecord: {}, // 当前编辑的这条记录
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const { dataSource } = nextProps;
    if (dataSource !== prevState.prevDataSource) {
      return {
        dataSource: map(dataSource, (record) => {
          const { _data, ...rest } = record;
          let rData;
          try {
            rData = JSON.parse(_data);
          } catch (e) {
            rData = {};
          }
          return {
            ...rData,
            ...rest,
          };
        }),
        prevDataSource: dataSource,
      };
    }
    return null;
  }

  /**
   * 编辑的时候，将当前记录更新为record,但是此时的record是没有$form的
   * @param {*} record
   * @param {*} editableKey
   */
  @Bind()
  edit(record, editableKey) {
    const { dataSource = [] } = this.state;
    this.setState({
      editableKey,
      currentRecord: record,
      dataSource: dataSource.map((rd) =>
        // eslint-disable-next-line no-nested-ternary
        rd._id === editableKey
          ? { ...rd, _status: 'update' }
          : rd._status
          ? { ...rd, _status: '' }
          : rd
      ),
    });
  }

  @Bind()
  cancel() {
    const { dataSource = [] } = this.state;
    this.setState({
      editableKey: null,
      dataSource: dataSource.map((rd) => (rd._status ? { ...rd, _status: '' } : rd)),
      // currentRecord: {},
      // dataIndex: null,
    });
  }

  @Bind()
  handleSave(form, _id) {
    const { save = (e) => e } = this.props;
    const { currentRecord } = this.state;
    save(form, _id, currentRecord, this.cancel);
  }

  @Bind()
  handleRemove(_id) {
    const { onRemove } = this.props;
    onRemove(_id, this.cancel);
  }

  @Bind()
  editRender(text, record) {
    const { editableKey } = this.state;
    const operators = [];
    if (editableKey === record._id && record._status) {
      const { $form: form } = record;
      operators.push({
        key: 'save',
        ele: (
          <a onClick={() => this.handleSave(form, record._id)}>
            {intl.get('hzero.common.button.save').d('保存')}
          </a>
        ),
        len: 2,
        title: intl.get('hzero.common.button.save').d('保存'),
      });
      operators.push({
        key: 'cancel',
        ele: (
          <Popconfirm
            title={intl.get(`himp.comment.view.message.title.sureToCancel`).d('确定取消编辑？')}
            onConfirm={() => this.cancel()}
          >
            <a>{intl.get('hzero.common.button.cancel').d('取消')}</a>
          </Popconfirm>
        ),
        len: 2,
        title: intl.get('hzero.common.button.cancel').d('取消'),
      });
    } else if (!record.imported) {
      operators.push({
        key: 'edit',
        ele: (
          <a onClick={() => this.edit(record, record._id)}>
            {intl.get('hzero.common.button.edit').d('编辑')}
          </a>
        ),
        len: 2,
        title: intl.get('hzero.common.button.edit').d('编辑'),
      });
    }
    return operatorRender(operators, record);
  }

  @Bind()
  getDynamicColumns() {
    const { dynamicColumns = [] } = this.props;
    const { editableKey, dataSource = [] } = this.state;
    return dynamicColumns.map((n) => ({
      title: n.title,
      dataIndex: n.dataIndex,
      width: dataSource.some((o) => o._id === editableKey) || !n.width ? 180 : n.width,
      render: (text, record) => {
        const { $form: form } = record;
        // when editableKey is equal record._id, it can edit, but, check _status to decide it can edit
        return editableKey === record._id && record._status ? (
          <FormItem style={{ margin: 0 }}>
            {form.getFieldDecorator(n.dataIndex, {
              rules: [
                {
                  required: n.required,
                  message: intl.get('hzero.common.validation.notNull', { name: n.title }),
                },
              ],
              initialValue: this.setInitValue(record[n.dataIndex]),
            })(this.getInput(n.columnType, record, n.dataIndex, n.tls))}
          </FormItem>
        ) : (
          text
        );
      },
    }));
  }

  setInitValue(value) {
    if (value === 'true' || value === 'false') {
      return value === 'true';
    } else {
      return value;
    }
  }

  @Bind()
  getInput(type, record, dataIndex, _tls) {
    switch (type) {
      case 'String':
        return <Input dbc2sbc={false} />;
      case 'Decimal':
        return <InputNumber />;
      case 'Long':
        return <InputNumber />;
      case 'Boolean':
        return <Switch checkedValue unCheckedValue={false} />;
      // 类多语言框
      case 'Multi':
        return (
          <Input
            dbc2sbc={false}
            className="multi-info-input"
            readOnly
            style={{ cursor: 'pointer' }}
            suffix={
              <Icon type="global" onClick={() => this.handleOpenModal(record, dataIndex, _tls)} />
            }
            onClick={() => this.handleOpenModal(record, dataIndex, _tls)}
          />
        );
      // case 'Date':
      //   return <Switch />;
      default:
        return <Input dbc2sbc={false} />;
    }
  }

  @Bind()
  handleOpenModal(record, dataIndex, tls = {}) {
    const { languageType = [] } = this.props;
    const { currentRecord } = this.state;
    // 当前如果是第一次打开，那么_tls就取当前record的数据，否则取state中的currentRecord
    const { _tls = {} } = currentRecord.$form ? currentRecord : record;
    let data = {};
    const dataSource = [];
    const formItemData = [];

    /**
     * 字段FormItem从info接口中取
     */
    const formI = tls[dataIndex];
    Object.keys(formI).forEach((r, ri) => {
      Object.values(formI).forEach((res, resi) => {
        if (ri === resi) {
          formItemData.push({
            lang: r,
            value: '',
          });
        }
      });
    });

    // if (record !== currentRecord || dataIndex !== stateDataIndex) {
    /**
     * 获取到当前字段JSON
     */
    Object.keys(_tls).forEach((item, index) => {
      if (item === dataIndex) {
        Object.values(_tls).forEach((i, d) => {
          if (index === d) {
            data = i;
          }
        });
      }
    });
    /**
     * 将当前字段JSON处理为数组，此时的字段含有字段和字段值
     */
    Object.keys(data).forEach((r, ri) => {
      Object.values(data).forEach((res, resi) => {
        if (ri === resi) {
          dataSource.push({
            lang: r,
            value: res,
          });
        }
      });
    });

    /**
     * 将formItem和dataSource合并到一起，防止出现没有FormItem的情况
     */
    const newData = uniqBy([...dataSource, ...formItemData], 'lang');
    /**
     * 将数据与语言值集对比，添加meaning
     */
    const newModalData = [];
    const types = languageType.map((l) => l.code);
    languageType.forEach((k) => {
      newData.forEach((j) => {
        if (k.code === j.lang) {
          newModalData.push({
            ...j,
            meaning: k.meaning,
          });
        } else if (!types.includes(j.lang)) {
          newModalData.push(j);
        }
      });
    });
    const filterModalData = uniqBy(newModalData, 'lang');
    const flag = isEmpty(currentRecord.$form);
    this.setState({
      visible: true,
      modalData: filterModalData,
      dataIndex,
      currentRecord: flag ? record : currentRecord,
    });
    // } else {
    //   this.setState({
    //     visible: true,
    //     dataIndex,
    //   });
    // }
  }

  @Bind()
  handleCancel() {
    this.setState({
      visible: false,
      modalData: [],
    });
  }

  /**
   * modal确定的时候，需要把最新的当前数据更新到currentRecord中，以防下一次打开获取到的是旧的数据
   * @param {*} data
   */
  @Bind()
  handleModalOk(data = {}) {
    const { modalData = [], currentRecord = {}, dataIndex } = this.state;
    // 获取当前语言
    const currentLanguage = getCurrentLanguage();
    const { _tls = {} } = currentRecord;
    const keys = Object.keys(data);
    const values = Object.values(data);
    const index = indexOf(keys, currentLanguage);
    const newData = [];
    // 获取到modal里的新数据
    keys.forEach((key, ki) => {
      modalData.forEach((m) => {
        if (m.lang === key) {
          newData.push({
            ...m,
            value: values[ki],
          });
        }
      });
    });
    // 给外层的Input赋值，并且需要根据当前语言，渲染不同的值
    currentRecord.$form.setFieldsValue({
      [dataIndex]: values[index],
    });
    // 转换为后端需要的格式
    const newTls = {
      ..._tls,
      [dataIndex]: {
        ..._tls[dataIndex],
        ...data,
      },
    };
    const newCurrentRecord = {
      ...currentRecord,
      [dataIndex]: values[0],
      _tls: newTls,
    };
    this.setState({
      modalData: newData,
      visible: false,
      currentRecord: newCurrentRecord,
    });
  }

  @Bind()
  onRender(text) {
    return (
      <Tooltip title={text} placement="topLeft">
        <span className={styles.span}>{text}</span>
      </Tooltip>
    );
  }

  render() {
    const { processing = {}, pagination, onChange } = this.props;
    const { dataSource = [], visible = false, modalData = [], dataIndex = null } = this.state;
    const dynamicColumns = this.getDynamicColumns();
    const beforeColumns = [
      {
        title: intl.get('himp.comment.model.comment.dataStatus').d('数据状态'),
        dataIndex: '_dataStatus',
        width: 120,
        fixed: 'left',
        render: (_dataStatus) => {
          const { importStatus = [] } = this.props;
          const statusList = [
            { status: 'NEW', color: 'blue' /* , text: 'Excel导入' */ },
            { status: 'VALID_SUCCESS', color: 'green' /* , text: '验证成功' */ },
            { status: 'VALID_FAILED', color: 'red' /* , text: '验证失败' */ },
            { status: 'IMPORT_SUCCESS', color: 'green' /* , text: '导入成功' */ },
            { status: 'IMPORT_FAILED', color: 'red' /* , text: '导入失败' */ },
            { status: 'ERROR', color: 'red' /* , text: '数据异常' */ },
          ];
          return (
            <div>
              {TagRender(
                _dataStatus,
                importStatus.map((item) => {
                  const tagItem = statusList.find((t) => t.status === item.value) || {};
                  return {
                    status: item.value,
                    text: item.meaning,
                    color: tagItem.color,
                  };
                })
              )}
            </div>
          );
        },
      },
      {
        title: intl.get('himp.comment.model.comment.message').d('信息'),
        dataIndex: '_info',
        render: this.onRender,
      },
    ];
    const afterColumns = [
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 110,
        key: '_operator',
        render: this.editRender,
        fixed: 'right',
      },
    ];
    const tableColumns = [...beforeColumns, ...dynamicColumns, ...afterColumns];
    const tableProps = {
      dataSource,
      pagination,
      onChange,
      rowKey: '_id',
      bordered: true,
      loading: processing.loading || processing.queryColumns,
      columns: tableColumns,
      scroll: {
        x: tableScrollWidth(tableColumns),
      },
    };
    const modalProps = {
      visible,
      modalData,
      dataIndex,
      onCancel: this.handleCancel,
      onOk: this.handleModalOk,
    };
    return (
      <>
        <EditTable {...tableProps} />
        <MultiInfoModal {...modalProps} />
      </>
    );
  }
}
