/**
 * ReceiveConfig - 消息接收配置
 * @date: 2018-11-21
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Form, Input, Popconfirm, Select, Tag, Button } from 'hzero-ui';
import { connect } from 'dva';
import uuidv4 from 'uuid/v4';
import { Bind } from 'lodash-decorators';
import { cloneDeep, isArray } from 'lodash';

import Lov from 'components/Lov';
import { Content, Header } from 'components/Page';
import TLEditor from 'components/TLEditor';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import { VERSION_IS_OP } from 'utils/config';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  getEditTableData,
  tableScrollWidth,
  isTenantRoleLevel,
  getCurrentOrganizationId,
} from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import { operatorRender } from 'utils/renderer';

import styles from './index.less';

const selRefs = {};
const currentTenantId = getCurrentOrganizationId();
const tenantRoleLevel = isTenantRoleLevel();

function onSelRef(key, ref) {
  if (ref) {
    selRefs[key] = ref;
  } else if (selRefs[key]) {
    delete selRefs[key];
  }
}

@connect(({ receiveConfig, loading, user }) => ({
  user,
  receiveConfig,
  searchLoading: loading.effects['receiveConfig/fetchReceiveConfig'],
  saveLoading: loading.effects['receiveConfig/saveConfig'],
}))
@formatterCollections({ code: ['hmsg.receiveConfig', 'hmsg.common'] })
export default class ReceiveConfig extends Component {
  form;

  /**
   * state初始化
   * @param {object} props 组件Props
   */
  constructor(props) {
    super(props);
    this.state = {
      expandedRowKeys: [], // 当前展开的树
      checkedTenantId: currentTenantId, // 选中的租户id
      createFlag: false,
    };
  }

  /**
   * componentDidMount 生命周期函数
   * render执行后获取页面数据
   */
  componentDidMount() {
    const { dispatch } = this.props;
    this.handleSearch();
    dispatch({
      type: 'receiveConfig/fetchMessageType',
    });
  }

  /**
   * 根据节点路径，在树形结构树中的对应节点添加或替换children属性
   * @param {Array} collections 树形结构树
   * @param {Array} cursorPath 节点路径
   * @param {Array} data  追加或替换的children数据
   * @returns {Array} 新的树形结构
   */
  findAndSetNodeProps(collections, cursorPath = [], data) {
    let newCursorList = cursorPath;
    const cursor = newCursorList[0];
    const tree = collections.map((n) => {
      const m = n;
      if (m.receiveCode === cursor) {
        if (newCursorList[1]) {
          if (!m.children) {
            m.children = [];
          }
          newCursorList = newCursorList.filter((o) => newCursorList.indexOf(o) !== 0);
          m.children = this.findAndSetNodeProps(m.children, newCursorList, data);
        } else {
          m.children = [...data];
        }
        if (m.children.length === 0) {
          const { children, ...others } = m;
          return { ...others };
        } else {
          return m;
        }
      }
      return m;
    });
    return tree;
  }

  /**
   * 根据节点路径，在树形结构树中的对应节点
   * @param {Array} collection 树形结构树
   * @param {Array} cursorList 节点路径
   * @param {String} keyName 主键名称
   * @returns {Object} 节点信息
   */
  findNode(collection, cursorList = [], keyName) {
    let newCursorList = cursorList;
    const cursor = newCursorList[0];
    for (let i = 0; i < collection.length; i++) {
      if (collection[i][keyName] === cursor) {
        if (newCursorList[1]) {
          newCursorList = newCursorList.slice(1);
          return this.findNode(collection[i].children, newCursorList, keyName);
        }
        return collection[i];
      }
    }
  }

  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'receiveConfig/fetchReceiveConfig',
      payload: params,
    });
    this.setState({
      createFlag: false,
    });
  }

  // 新增下级配置
  @Bind()
  handleAddLine(record = {}) {
    const {
      receiveConfig: { configList, pathMap = {} },
      dispatch,
    } = this.props;
    const { expandedRowKeys } = this.state;

    if (record.receiveId) {
      const newConfig = {
        receiveId: uuidv4(),
        receiveCode: '',
        receiveName: '',
        // levelNumber: record.levelNumber,
        defaultReceiveType: [],
        parentReceiveCode: record.receiveCode,
        _status: 'create', // 新增节点的标识
      };
      const newChildren = [newConfig, ...(record.children || [])];
      const newConfigList = this.findAndSetNodeProps(
        configList,
        pathMap[record.receiveCode],
        newChildren
      );

      this.setState(
        {
          expandedRowKeys: [...expandedRowKeys, record.receiveId],
        },
        () => {
          dispatch({
            type: 'receiveConfig/updateState',
            payload: { configList: newConfigList },
          });
        }
      );
    } else {
      const newConfig = {
        receiveId: uuidv4(),
        receiveCode: '',
        receiveName: '',
        defaultReceiveType: [],
        parentReceiveCode: record.receiveCode,
        _status: 'create', // 新增节点的标识
      };
      const newConfigList = [newConfig, ...configList];
      dispatch({
        type: 'receiveConfig/updateState',
        payload: { configList: newConfigList },
      });
    }
    this.setState({
      createFlag: true,
    });
  }

  // 保存
  @Bind(500)
  handleSave() {
    const { dispatch, receiveConfig: { configList = [] } = {} } = this.props;
    const { checkedTenantId } = this.state;
    const params = getEditTableData(configList, ['children', 'receiveId']);

    if (Array.isArray(params) && params.length !== 0) {
      const payloadParams = params.map((item) => ({
        ...item,
        defaultReceiveType: isArray(item.defaultReceiveType)
          ? item.defaultReceiveType.join(',')
          : item.defaultReceiveType,
      }));
      const allParams = {
        payloadParams,
        checkedTenantId,
      };
      dispatch({
        type: 'receiveConfig/saveConfig',
        payload: allParams,
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleSearch();
        }
      });
    }
  }

  // 取消
  @Bind()
  handleCancel(record) {
    const { receiveId } = record;
    const { receiveConfig: { configList = [], pathMap } = {}, dispatch } = this.props;

    let newConfigList = configList;

    if (record.parentReceiveCode) {
      const parentNode = this.findNode(
        configList,
        pathMap[record.parentReceiveCode],
        'receiveCode'
      );
      const newChildren = parentNode.children.filter((item) => item.receiveId !== receiveId);
      newConfigList = this.findAndSetNodeProps(
        configList,
        pathMap[record.parentReceiveCode],
        newChildren
      );
    } else {
      newConfigList = configList.filter((item) => item.receiveId !== receiveId);
    }

    dispatch({
      type: 'receiveConfig/updateState',
      payload: { configList: newConfigList },
    });
  }

  // 编辑树结构信息
  @Bind()
  handleEdit(record, flag) {
    const { receiveId } = record;
    const { receiveConfig: { configList = [], pathMap } = {}, dispatch } = this.props;

    let newConfigList = configList;
    if (record.parentReceiveCode) {
      const parentNode = this.findNode(
        configList,
        pathMap[record.parentReceiveCode],
        'receiveCode'
      );
      const newChildren = cloneDeep(parentNode.children);
      const index = newChildren.findIndex((item) => item.receiveId === receiveId);
      if (flag) {
        newChildren.splice(index, 1, {
          ...record,
          _status: 'update',
        });
      } else {
        const { _status, ...other } = record;
        newChildren.splice(index, 1, other);
      }
      newConfigList = this.findAndSetNodeProps(
        configList,
        pathMap[record.parentReceiveCode],
        newChildren
      );
    } else {
      const index = newConfigList.findIndex((item) => item.receiveId === receiveId);
      if (flag) {
        newConfigList.splice(index, 1, {
          ...configList[index],
          _status: 'update',
        });
      } else {
        const { _status, ...other } = configList[index];
        newConfigList.splice(index, 1, other);
      }
    }

    dispatch({
      type: 'receiveConfig/updateState',
      payload: { configList: newConfigList },
    });
  }

  // 取消新建下级
  @Bind()
  handleCleanLine(record = {}) {
    const {
      dispatch,
      receiveConfig: { renderTree = [], addData = {}, pathMap = {} },
    } = this.props;
    delete addData[record.receiveId];
    let newRenderTree = [];
    if (record.parentReceiveCode) {
      // 找到父节点的children, 更新children数组
      const parent = this.findNode(renderTree, pathMap[record.parentReceiveCode], 'receiveCode');
      const newChildren = parent.children.filter((item) => item.receiveId !== record.receiveId);
      newRenderTree = this.findAndSetNodeProps(
        renderTree,
        pathMap[record.parentReceiveCode],
        newChildren
      );
    } else {
      newRenderTree = renderTree.filter((item) => item.receiveId !== record.receiveId);
    }
    dispatch({
      type: 'receiveConfig/updateState',
      payload: {
        renderTree: newRenderTree,
        addData: {
          ...addData,
        },
      },
    });
  }

  // 删除
  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    const { checkedTenantId } = this.state;
    dispatch({
      type: 'receiveConfig/deleteConfig',
      payload: { ...record, checkedTenantId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  /**
   * 树形结构点击展开收起时的回调
   */
  @Bind()
  onExpand(expanded, record) {
    const { receiveId } = record;
    const { expandedRowKeys } = this.state;

    if (expanded) {
      this.setState({
        expandedRowKeys: [...expandedRowKeys, receiveId],
      });
    } else {
      const newExpandRowKeys = expandedRowKeys.filter((item) => item !== receiveId);
      this.setState({
        expandedRowKeys: newExpandRowKeys,
      });
    }
  }

  typeMeaningRender(tagValue, tagMeaning) {
    let mean = '';
    switch (tagValue) {
      case 'WEB':
        mean = (
          <Tag color="green" key="1">
            {tagMeaning}
          </Tag>
        );
        break;
      case 'EMAIL':
        mean = (
          <Tag color="orange" key="2">
            {tagMeaning}
          </Tag>
        );
        break;
      case 'SMS':
        mean = (
          <Tag color="blue" key="3">
            {tagMeaning}
          </Tag>
        );
        break;
      case 'WC_E':
        mean = (
          <Tag color="red" key="4">
            {tagMeaning}
          </Tag>
        );
        break;
      case 'WC_O':
        mean = (
          <Tag color="#9866ff" key="5">
            {tagMeaning}
          </Tag>
        );
        break;
      case 'DT':
        mean = (
          <Tag color="pink" key="6">
            {tagMeaning}
          </Tag>
        );
        break;
      case 'CALL':
        mean = (
          <Tag color="#00ccff" key="7">
            {tagMeaning}
          </Tag>
        );
        break;
      default:
        mean = (
          <Tag color="#dcdcdc" key="8">
            {tagMeaning}
          </Tag>
        );
        break;
    }
    return mean;
  }

  /**
   * 租户lov改变时触发
   * @param {object} record
   */
  @Bind()
  handleChangeOrg(record) {
    this.handleSearch({ tenantId: record.tenantId });
    this.setState({
      checkedTenantId: record.tenantId,
    });
  }

  /**
   * 刷新
   */
  @Bind()
  handleRefresh() {
    const { checkedTenantId } = this.state;
    const obj = !tenantRoleLevel ? { tenantId: checkedTenantId } : {};
    this.handleSearch(obj);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      receiveConfig,
      searchLoading,
      saveLoading,
      match: { path },
      user: {
        currentUser: { tenantName },
      },
    } = this.props;
    const { configList = [], messageTypeList } = receiveConfig;
    const { expandedRowKeys = [] } = this.state;
    const columns = [
      {
        title: intl.get('hmsg.receiveConfig.model.receiveConfig.receiveCode').d('接收配置编码'),
        dataIndex: 'receiveCode',
        render: (val, record) =>
          record._status === 'create' || record._status === 'update' ? (
            <Form.Item style={{ width: '250px' }}>
              {record.$form.getFieldDecorator('receiveCode', {
                initialValue: val,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hmsg.receiveConfig.model.receiveConfig.receiveCode')
                        .d('接收配置编码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={record._status === 'update'}
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hmsg.common.view.source').d('来源'),
        dataIndex: 'tenantId',
        width: 120,
        render: (_, record) => {
          const { tenantId } = record;
          // eslint-disable-next-line no-nested-ternary
          if (tenantId === undefined) {
            return <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>;
          } else {
            // eslint-disable-next-line no-nested-ternary
            return !record.editableFlag ? (
              <Tag color="blue">
                {intl.get('hmsg.receiveConfig.model.receiveConfig.sendConfig').d('发送配置')}
              </Tag>
            ) : currentTenantId.toString() === tenantId.toString() ? (
              <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
            ) : (
              <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
            );
          }
        },
      },
      {
        title: intl.get('hmsg.receiveConfig.model.receiveConfig.receiveName').d('接收配置名称'),
        dataIndex: 'receiveName',
        width: 150,
        render: (val, record) =>
          record._status === 'create' || record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('receiveName', {
                initialValue: val,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hmsg.receiveConfig.model.receiveConfig.receiveName')
                        .d('接收配置名称'),
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl
                    .get('hmsg.receiveConfig.model.receiveConfig.receiveName')
                    .d('接收配置名称')}
                  field="receiveName"
                  token={record._token}
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hmsg.receiveConfig.model.receiveConfig.receiveType').d('默认接收方式'),
        dataIndex: 'defaultReceiveType',
        width: 420,
        render: (val, record) => {
          if (record._status === 'create' || record._status === 'update') {
            return (
              <Form.Item>
                {record.$form.getFieldDecorator('defaultReceiveType', {
                  initialValue: isArray(val) ? val : val.split(','),
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hmsg.receiveConfig.model.receiveConfig.receiveType')
                          .d('默认接收方式'),
                      }),
                    },
                  ],
                })(
                  <Select
                    style={{ width: 250 }}
                    mode="multiple"
                    onFocus={() => {
                      if (selRefs[`selRef${record.receiveId}`]) {
                        selRefs[`selRef${record.receiveId}`].rcSelect.setOpenState(true);
                      }
                    }}
                    ref={(r) => {
                      onSelRef(`selRef${record.receiveId}`, r);
                    }}
                  >
                    {messageTypeList.map((item) => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            );
          } else {
            const tagValueList = record.defaultReceiveType.split(',') || [];
            const tagMeaningList = record.defaultReceiveTypeMeaning.split(',') || [];
            const tagIndexMap = tagValueList.reduce(
              (p, v, valueIndex) => ({ ...p, [v]: { oriValueIndex: valueIndex } }),
              {}
            );
            return tagValueList
              .sort()
              .map((item) =>
                this.typeMeaningRender(item, tagMeaningList[tagIndexMap[item].oriValueIndex])
              );
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 200,
        render: (val, record) => {
          const operators = [];
          if (record.levelNumber !== 0) {
            if (record._status === 'create') {
              operators.push({
                key: 'cancel',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.cancel`,
                        type: 'button',
                        meaning: '消息接收配置-取消',
                      },
                    ]}
                    style={{ cursor: 'pointer' }}
                    onClick={() => this.handleCancel(record)}
                  >
                    {intl.get('hzero.common.button.cancel').d('取消')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.cancel').d('取消'),
              });
            } else if (record._status === 'update') {
              operators.push(
                {
                  key: 'cancelEdit',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.cancelEdit`,
                          type: 'button',
                          meaning: '消息接收配置-取消编辑',
                        },
                      ]}
                      onClick={() => this.handleEdit(record, false)}
                      style={{ cursor: 'pointer' }}
                    >
                      {intl.get('hzero.common.button.cancel').d('取消')}
                    </ButtonPermission>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.cancel').d('取消'),
                },
                record.editableFlag && {
                  key: 'createChildren',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.createChildren`,
                          type: 'button',
                          meaning: '消息接收配置-新建下级配置',
                        },
                      ]}
                      onClick={() => this.handleAddLine(record)}
                    >
                      {intl.get('hmsg.receiveConfig.option.createChildren').d('新建下级配置')}
                    </ButtonPermission>
                  ),
                  len: 6,
                  title: intl.get('hmsg.receiveConfig.option.createChildren').d('新建下级配置'),
                },
                record.editableFlag && {
                  key: 'delete',
                  ele: (
                    <Popconfirm
                      placement="topRight"
                      title={intl
                        .get('hzero.common.message.confirm.delete')
                        .d('是否删除此条记录？')}
                      onConfirm={() => this.handleDelete(record)}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${path}.button.delete`,
                            type: 'button',
                            meaning: '消息接收配置-删除',
                          },
                        ]}
                      >
                        {intl.get('hzero.common.button.delete').d('删除')}
                      </ButtonPermission>
                    </Popconfirm>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.delete').d('删除'),
                }
              );
            } else if (
              tenantRoleLevel &&
              !VERSION_IS_OP &&
              currentTenantId.toString() !== record.tenantId.toString()
            ) {
              operators.push(
                record.editableFlag && {
                  key: 'edit1',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.edit`,
                          type: 'button',
                          meaning: '消息接收配置-编辑',
                        },
                      ]}
                      onClick={() => this.handleEdit(record, true)}
                      style={{ cursor: 'pointer' }}
                    >
                      {intl.get('hzero.common.button.edit').d('编辑')}
                    </ButtonPermission>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.edit').d('编辑'),
                },
                record.editableFlag && {
                  key: 'createChildren3',
                  ele: (
                    <a onClick={() => this.handleAddLine(record)}>
                      {intl.get('hmsg.receiveConfig.option.createChildren').d('新建下级配置')}
                    </a>
                  ),
                  len: 6,
                  title: intl.get('hmsg.receiveConfig.option.createChildren').d('新建下级配置'),
                }
              );
            } else {
              operators.push(
                record.editableFlag && {
                  key: 'edit',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.edit`,
                          type: 'button',
                          meaning: '消息接收配置-编辑',
                        },
                      ]}
                      onClick={() => this.handleEdit(record, true)}
                      style={{ cursor: 'pointer' }}
                    >
                      {intl.get('hzero.common.button.edit').d('编辑')}
                    </ButtonPermission>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.edit').d('编辑'),
                },
                record.editableFlag && {
                  key: 'createChildren2',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.createChildren`,
                          type: 'button',
                          meaning: '消息接收配置',
                        },
                      ]}
                      onClick={() => this.handleAddLine(record)}
                    >
                      {intl.get('hmsg.receiveConfig.option.createChildren').d('新建下级配置')}
                    </ButtonPermission>
                  ),
                  len: 6,
                  title: intl.get('hmsg.receiveConfig.option.createChildren').d('新建下级配置'),
                },
                record.editableFlag && {
                  key: 'delete1',
                  ele: (
                    <Popconfirm
                      placement="topRight"
                      title={intl
                        .get('hzero.common.message.confirm.delete')
                        .d('是否删除此条记录？')}
                      onConfirm={() => this.handleDelete(record)}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${path}.button.delete`,
                            type: 'button',
                            meaning: '消息接收配置-删除',
                          },
                        ]}
                      >
                        {intl.get('hzero.common.button.delete').d('删除')}
                      </ButtonPermission>
                    </Popconfirm>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.delete').d('删除'),
                }
              );
            }
          } else {
            operators.push(
              record.editableFlag && {
                key: 'createChildren2',
                ele: (
                  <a onClick={() => this.handleAddLine(record)}>
                    {intl.get('hmsg.receiveConfig.option.createChildren').d('新建下级配置')}
                  </a>
                ),
                len: 6,
                title: intl.get('hmsg.receiveConfig.option.createChildren').d('新建下级配置'),
              }
            );
          }
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    const editTableProps = {
      expandedRowKeys,
      columns,
      scroll: { x: tableScrollWidth(columns) },
      loading: searchLoading,
      rowKey: 'receiveId',
      pagination: false,
      bordered: true,
      dataSource: configList,
      onExpand: this.onExpand,
      indentSize: 24,
      className: styles['hmsg-hr-show'],
    };

    return (
      <>
        <Header title={intl.get('hmsg.receiveConfig.view.message.title').d('消息接收配置')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '消息接收配置-保存',
              },
            ]}
            icon="save"
            type="primary"
            onClick={this.handleSave}
            loading={saveLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <Button icon="sync" onClick={this.handleRefresh}>
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </Button>
          {!tenantRoleLevel && (
            <Lov
              className="page-head-operation"
              style={{ width: '200px' }}
              value={currentTenantId}
              allowClear={false}
              textValue={tenantName}
              code="HPFM.TENANT"
              onChange={(text, record) => {
                this.handleChangeOrg(record);
              }}
            />
          )}
        </Header>
        <Content>
          <EditTable {...editTableProps} />
        </Content>
      </>
    );
  }
}
