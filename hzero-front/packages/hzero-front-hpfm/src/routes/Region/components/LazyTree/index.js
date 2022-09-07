/**
 * LazyTree
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-09
 * @copyright 2019 © HAND
 */
import React from 'react';
import classNames from 'classnames';
import uuid from 'uuid/v4';
import { Icon, Input, Form } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';
import { isNil, isEmpty } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';
import EditTable from 'components/EditTable';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth, getEditTableData } from 'utils/utils';
import { DEBOUNCE_TIME } from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';
import { operatorRender, enableRender } from 'utils/renderer';

import EditDrawer from '../EditDrawer';

import styles from './styles.less';

function buildNewTreeDataSource(treeDataSource = [], iterFunc) {
  return treeDataSource.map(item => {
    if (item.children) {
      const newItem = iterFunc(item);
      return {
        ...newItem,
        children: buildNewTreeDataSource(newItem.children, iterFunc),
      };
    } else {
      return iterFunc(item);
    }
  });
}

const INDENT_SIZE = 16;

export default class LazyTree extends React.Component {
  state = {
    drawerVisible: false,
    editRecord: {},
  };

  componentDidMount() {
    const { loadData } = this.props;
    loadData();
  }

  componentWillUnmount() {
    this.handleTableExpand.cancel();
  }

  // Button

  /**
   * 新建一个顶级地区
   */
  @Bind()
  handleCreateBtnClick() {
    const { updateModelState, dataSource = [], expandKeys = [] } = this.props;
    // 先获取直接下级节点，将新增的节点添加在子节点列表末位
    const regionId = uuid();
    const newItem = {
      regionId,
      indent: 0,
      enabledFlag: 1, // 新增地区 默认启用
      _status: 'create', // 新增节点的标识
    };
    updateModelState({
      treeDataSource: [newItem, ...dataSource],
      expandKeys: [...expandKeys, regionId],
    });
  }

  /**
   * 表格行内编辑的保存
   */
  @Bind()
  handleSaveBtnClick() {
    const { dataSource = [], createRecord } = this.props;
    // 处理表单效验，获取处理后的表单数据
    const params = getEditTableData(dataSource, ['children', 'regionId']);
    if (Array.isArray(params) && params.length !== 0) {
      createRecord({
        body: params,
      }).then(res => {
        if (res) {
          notification.success();
          const { loadData } = this.props;
          loadData();
        }
      });
    }
  }

  /**
   * 保存， 刷新接口是否 禁用
   */
  getBtnsDisabled() {
    const { dataSource = [] } = this.props;
    let createCount = 0;
    buildNewTreeDataSource(dataSource, item => {
      if (item._status === 'create') {
        createCount += 1;
      }
      return item;
    });
    return {
      save: createCount === 0,
      refresh: createCount !== 0,
    };
  }

  // Table

  /**
   * 清除新增的数据
   * @param record
   */
  @Bind()
  handleClearRecord(record) {
    const { updateModelState, dataSource = [], expandKeys = [] } = this.props;
    let needRemoveExpandKey = false;
    const newDataSource = isNil(record.parentRegionId)
      ? dataSource.filter(item => item.regionId !== record.regionId)
      : buildNewTreeDataSource(dataSource, item => {
          if (record.parentRegionId === item.regionId) {
            const newChildren = item.children.filter(child => child.regionId !== record.regionId);
            const newItem = { ...item };
            if (newChildren.length === 0) {
              needRemoveExpandKey = true;
              newItem.hasNextFlag = item._prevHasNextFlag;
            }
            return {
              ...newItem,
              children: newChildren,
            };
          } else {
            return item;
          }
        });

    updateModelState({
      treeDataSource: newDataSource,
      expandKeys: needRemoveExpandKey
        ? expandKeys.filter(item => item !== record.parentRegionId)
        : expandKeys,
    });
  }

  /**
   * 编辑已经保存的数据
   * @param record
   */
  @Bind()
  handleEditRecord(record) {
    this.setState({
      drawerVisible: true,
      editRecord: record,
    });
  }

  /**
   * 对保存的数据新增下级
   * @param record
   */
  @Bind()
  handleAddRecordChild(record) {
    const { updateModelState, dataSource = [], expandKeys = [] } = this.props;
    const regionId = uuid();
    const newItem = {
      regionId,
      regionCode: '',
      regionName: '',
      parentRegionId: record.regionId,
      _status: 'create', // 新增节点的标识
      indent: record.indent + 1,
    };
    let needAddExpandKey = false;
    const newDataSource = buildNewTreeDataSource(dataSource, item => {
      if (item.regionId === record.regionId) {
        if (item.hasNextFlag !== 1) {
          // 本身没有子节点
          needAddExpandKey = true;
        } else if (!expandKeys.includes(item.regionId)) {
          // 本身有子节点但是没有展开
          needAddExpandKey = true;
        }
        return {
          ...item,
          hasNextFlag: 1,
          // 存储之前的 标记, 用于 清除后 还原
          _prevHasNextFlag: isNil(item._prevHasNextFlag) ? item.hasNextFlag : item._prevHasNextFlag,
          children: item.children ? [newItem, ...item.children] : [newItem],
        };
      } else {
        return item;
      }
    });
    updateModelState({
      treeDataSource: newDataSource,
      expandKeys: needAddExpandKey ? [...expandKeys, record.regionId] : expandKeys,
    });
  }

  /**
   * 对保存的数据禁用
   * @param record
   */
  @Bind()
  handleDisabledRecord(record) {
    const { disableRecord } = this.props;
    disableRecord({
      regionId: record.regionId,
      body: {
        _token: record._token,
        regionId: record.regionId,
        objectVersionNumber: record.objectVersionNumber,
        enabledFlag: 0,
      },
    }).then(res => {
      if (res) {
        notification.success();
        const { loadData } = this.props;
        loadData();
      }
    });
  }

  /**
   * 对保存的数据启用
   * @param record
   */
  @Bind()
  handleEnableRecord(record) {
    const { enableRecord } = this.props;
    enableRecord({
      regionId: record.regionId,
      body: {
        _token: record._token,
        regionId: record.regionId,
        objectVersionNumber: record.objectVersionNumber,
        enabledFlag: 1,
      },
    }).then(res => {
      if (res) {
        notification.success();
        const { loadData } = this.props;
        loadData();
      }
    });
  }

  /**
   * 树数据展开点击
   * @param {boolean} expanded - 是否展开
   * @param {object} record - 记录
   */
  @Throttle(DEBOUNCE_TIME)
  @Bind()
  handleTableExpand(expanded, record) {
    // 如果能显示展开按钮, 那么一定有children
    if (expanded) {
      if (record.children.length === 0) {
        const { loadData } = this.props;
        loadData({ regionId: record.regionId, indent: record.indent });
      } else {
        const { updateModelState, expandKeys = [] } = this.props;
        updateModelState({
          expandKeys: [...expandKeys, record.regionId],
        });
      }
    }
    if (!expanded) {
      const { updateModelState, expandKeys = [] } = this.props;
      updateModelState({
        expandKeys: expandKeys.filter(k => k !== record.regionId),
      });
    }
  }

  /**
   * 须由 自己 手动设置 expand
   * 行内编辑只有新增
   * @return {*[]}
   */
  getColumns() {
    const { match } = this.props;
    return [
      {
        title: intl.get('hpfm.region.model.region.regionCode').d('区域代码'),
        dataIndex: 'regionCode',
        width: 150,
        render: (regionCode, record) => {
          const { expandKeys = [], loadingExpandKeys = [] } = this.props;
          const { regionId, indent = 0, _status, $form: form } = record;
          const loading = loadingExpandKeys.includes(regionId);
          const isExpand = expandKeys.includes(regionId);
          const hasExpand = record.hasNextFlag === 1;
          const expandIcon = hasExpand && (
            <Icon
              className={classNames(styles['hpfm-region-lazy-tree-expand-icon'], {
                [styles['hpfm-region-lazy-tree-expand-icon-loading']]: loading,
              })}
              type={loading ? 'loading' : isExpand ? 'minus-square-o' : 'plus-square-o'}
              onClick={loading ? undefined : () => this.handleTableExpand(!isExpand, record)}
            />
          );
          const isCreate = _status === 'create';
          const content = isCreate ? (
            <Form.Item>
              {form.getFieldDecorator('regionCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.region.regionCode').d('组织编码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', { max: 30 }),
                  },
                ],
              })(<Input trim typeCase="upper" inputChinese={false} />)}
            </Form.Item>
          ) : (
            regionCode
          );
          return (
            <span
              style={{ paddingLeft: indent * INDENT_SIZE, display: 'block' }}
              className={classNames({
                [styles['hpfm-region-lazy-tree-no-child']]: hasExpand,
              })}
            >
              {expandIcon}
              {content}
            </span>
          );
        },
      },
      {
        title: intl.get('entity.region.regionName').d('区域名称'),
        dataIndex: 'regionName',
        render: (regionName, record) => {
          const { _status, $form: form } = record;
          if (_status === 'create') {
            return (
              <Form.Item>
                {form.getFieldDecorator('regionName', {
                  initialValue: regionName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('entity.region.regionName').d('区域名称'),
                      }),
                    },
                    {
                      max: 120,
                      message: intl.get('hzero.common.validation.max', {
                        max: 120,
                      }),
                    },
                  ],
                })(
                  <TLEditor
                    label={intl.get('entity.region.regionName').d('区域名称')}
                    field="regionName"
                    token={record._token}
                  />
                )}
              </Form.Item>
            );
          } else {
            return regionName;
          }
        },
      },
      {
        title: intl.get('entity.region.quickIndex').d('快速索引'),
        dataIndex: 'quickIndex',
        width: 150,
        render: (quickIndex, record) => {
          const { _status, $form: form } = record;
          if (_status === 'create') {
            return (
              <Form.Item>
                {form.getFieldDecorator('quickIndex', {
                  initialValue: quickIndex,
                  rules: [
                    {
                      max: 30,
                      message: intl.get('hzero.common.validation.max', {
                        max: 30,
                      }),
                    },
                  ],
                })(
                  <TLEditor
                    label={intl.get('entity.region.quickIndex').d('快速索引')}
                    field="regionName"
                    token={record._token}
                  />
                )}
              </Form.Item>
            );
          } else {
            return quickIndex;
          }
        },
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: (enabledFlag, record) => {
          const { _status, $form: form } = record;
          if (_status === 'create') {
            // 状态 默认为启用
            return (
              <Form.Item>
                {form.getFieldDecorator('enabledFlag', {
                  initialValue: 1,
                  rules: [],
                })(enableRender(1))}
              </Form.Item>
            );
          } else {
            return enableRender(enabledFlag);
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        render: (text, record) => {
          const actions = [];
          if (record._status === 'create') {
            actions.push({
              key: 'clear',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.clean`,
                      type: 'button',
                      meaning: '地区定义-树形结构-清除',
                    },
                  ]}
                  onClick={() => this.handleClearRecord(record)}
                >
                  {intl.get('hzero.common.button.clean').d('清除')}
                </ButtonPermission>
              ),
            });
          } else if (record.enabledFlag === 1) {
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.add`,
                      type: 'button',
                      meaning: '地区定义-树形结构-新建下级地区',
                    },
                  ]}
                  onClick={() => {
                    // quickIndex 快速索引 不需要复制到 新增的下级地区中
                    this.handleAddRecordChild(record);
                  }}
                >
                  {intl.get('hpfm.region.button.add').d('新建下级地区')}
                </ButtonPermission>
              ),
              key: 'create',
              len: 6,
              title: intl.get('hpfm.region.button.add').d('新建下级地区'),
            });
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.disable`,
                      type: 'button',
                      meaning: '地区定义-树形结构-禁用',
                    },
                  ]}
                  onClick={() => this.handleDisabledRecord(record)}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              key: 'disable',
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            });
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '地区定义-树形结构-编辑',
                    },
                  ]}
                  onClick={() => this.handleEditRecord(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          } else {
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.enable`,
                      type: 'button',
                      meaning: '地区定义-树形结构-启用',
                    },
                  ]}
                  onClick={() => this.handleEnableRecord(record)}
                >
                  {intl.get('hzero.common.button.enable').d('启用')}
                </ButtonPermission>
              ),
              key: 'enable',
              len: 2,
              title: intl.get('hzero.common.button.enable').d('启用'),
            });
          }
          return operatorRender(actions);
        },
      },
    ];
  }

  // EditDrawer

  /**
   * 保存 - 单条组织行数据修改后保存
   * @param {Object} values 修改后的数据
   */
  @Bind()
  handleDrawerOk(values) {
    const { updateRecord } = this.props;
    updateRecord({
      regionId: values.regionId,
      body: values,
    }).then(res => {
      if (res) {
        this.setState({
          drawerVisible: false,
          editRecord: {},
        });
        const { loadData } = this.props;
        loadData();
        notification.success();
      }
    });
  }

  /**
   * 编辑侧滑框隐藏
   */
  @Bind()
  handleDrawerCancel() {
    this.setState({
      drawerVisible: false,
      editRecord: {},
    });
  }

  render() {
    const {
      queryDetail,
      dataSource = [],
      expandKeys = [],
      loadingExpandKeys = [],
      match,
      queryLoading = false,
      updateLoading = false,
      createLoading = false,
      enableLoading = false,
      disableLoading = false,
      queryDetailLoading = false,
    } = this.props;
    const { drawerVisible = false, editRecord = {} } = this.state;
    const columns = this.getColumns();
    const btnsDisabled = this.getBtnsDisabled();
    return (
      <div>
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '地区定义-树形结构-新建',
              },
            ]}
            onClick={this.handleCreateBtnClick}
            type="primary"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '地区定义-树形结构-保存',
              },
            ]}
            onClick={this.handleSaveBtnClick}
            loading={createLoading}
            disabled={btnsDisabled.save}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          pagination={false}
          rowKey="regionId"
          className={styles['hpfm-region-lazy-tree']}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          expandedRowKeys={expandKeys}
          loading={queryLoading || !isEmpty(loadingExpandKeys) || enableLoading || disableLoading}
        />
        <EditDrawer
          loading={updateLoading}
          visible={drawerVisible}
          anchor="right"
          title={intl.get('hpfm.region.view.message.edit').d('地区修改')}
          onCancel={this.handleDrawerCancel}
          onOk={this.handleDrawerOk}
          itemData={editRecord}
          queryDetail={queryDetail}
          queryDetailLoading={queryDetailLoading}
        />
      </div>
    );
  }
}
