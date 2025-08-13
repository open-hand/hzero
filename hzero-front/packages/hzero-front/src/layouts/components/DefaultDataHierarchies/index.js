/**
 * 数据层级配置
 */
import React, { PureComponent } from 'react';
import { Form, Modal, Spin, Table } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';

import { queryLov } from '../../../services/api';

import style from './index.less';

/**
 * 数据层级配置 Tenant
 *
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {!boolean} [modalVisible=false] - 是否显示选择数据层级的模态框
 * @reactProps {!array} [expandedRowKeys=[]] - 显示的数据层级值id
 * @reactProps {!array} [renderTree=[]] - 数据层级列表数据
 * @reactProps {!array} [queryParams=[]] - 值集视图配置
 * @reactProps {!string} [dataHierarchyCode] - 数据层级编码
 * @reactProps {!string} [dataHierarchyMeaning] - 数据层级值展示值
 * @returns React.element
 */
@Form.create({ fieldNameProp: null })
class DefaultDataHierarchies extends PureComponent {
  /**
   * constructor - constructor方法
   * 组件构造函数
   */
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false, // 控制模态框显示
      expandedRowKeys: [], // 可展开的行数据key集合
      renderTree: [], // 树形列表数据源
      valueList: [], // 已经配置的数据层级
      queryParams: [], // 值集视图配置
      dataHierarchyCode: '', // 配置编码
      dataHierarchyMeaning: '', // 配置显示值
      dataHierarchyId: '',
    };
  }

  /**
   * @function renderTreeData - 渲染树
   * @param {!array} collections - 查询获取的数据
   */
  @Bind()
  renderTreeData(collections = [], levelPath = {}, value = [], query = []) {
    const pathMap = levelPath;
    const valueList = value;
    const queryParams = query;
    const renderTree = collections.map((item) => {
      const temp = item;
      pathMap[temp.dataHierarchyId] = [...(pathMap[temp.parentId] || []), temp.dataHierarchyId];
      valueList.push(temp.dataHierarchyValue && temp.dataHierarchyId);
      queryLov({ viewCode: temp.valueSourceCode }).then((res) => {
        const { valueField, displayField } = res;
        queryParams.push({ [temp.dataHierarchyId]: { valueField, displayField } });
      });
      if (temp.children) {
        temp.children = [
          ...this.renderTreeData(temp.children || [], pathMap, valueList, queryParams).renderTree,
        ];
      }
      return temp;
    });
    this.setState({ valueList, queryParams });
    return {
      renderTree,
      pathMap,
      valueList,
      queryParams,
    };
  }

  /**
   * @function renderTreeData - 遍历获取父节点
   * @param {!array} record - 当前的数据
   */
  @Bind()
  handleFind(record = {}, renderTree = [], dataHierarchyValue = '', dataHierarchyId = '') {
    const { parentId } = record;
    let value = dataHierarchyValue;
    let id = dataHierarchyId;
    const traverse = renderTree.map((item) => {
      const temp = item;
      if (temp.dataHierarchyId === parentId) {
        value = temp.dataHierarchyValue;
        id = temp.dataHierarchyId;
      }
      if (temp.children) {
        temp.children = [...this.handleFind({ parentId }, temp.children || [], value, id).traverse];
      }
      return temp;
    });
    return { parentId, traverse, value, id };
  }

  /**
   * @function handleOpenModal - 显示和隐藏租户切换模态框
   * @param {boolean} flag - 显示或隐藏标识
   */
  @Bind()
  handleOpenModal(flag) {
    const { dispatch } = this.props;
    if (flag) {
      dispatch({
        type: 'user/fetchDataHierarchiesList',
        payload: { organizationId: getCurrentOrganizationId() },
      }).then((res) => {
        if (res) {
          const { renderTree, pathMap = {} } = this.renderTreeData(res);
          const expandedRowKeys = Object.keys(pathMap).map((item) => +item);
          this.setState({ renderTree, expandedRowKeys });
        }
      });
    } else {
      // 清缓存
      this.setState({
        expandedRowKeys: [],
        renderTree: [],
        valueList: [],
        dataHierarchyCode: '',
        dataHierarchyMeaning: '',
        dataHierarchyId: '',
      });
    }
    this.setState({ modalVisible: flag });
  }

  /**
   * @function handleClear - 清除层级编码
   */
  @Bind()
  handleClear(params) {
    const { dataHierarchyCode = '', dataHierarchyId = '' } = params;
    this.setState({
      dataHierarchyCode,
      dataHierarchyId,
    });
  }

  /**
   * @function handleSet - 设置层级编码
   */
  @Bind()
  handleSet(params) {
    const { dataHierarchyCode = '', dataHierarchyId } = params;
    const { queryParams } = this.state;
    queryParams.map((item) => {
      if (item[dataHierarchyId]) {
        this.setState({
          dataHierarchyCode,
          dataHierarchyId,
          dataHierarchyMeaning: item[dataHierarchyId].displayField,
        });
      }
      return 0;
    });
  }

  /**
   * @function handleChange - 切换层级配置
   */
  @Bind()
  handleChange(_, params) {
    const { dataHierarchyMeaning } = this.state;
    const meaning = dataHierarchyMeaning;
    this.setState({ dataHierarchyMeaning: params[meaning] }, () => this.handleSwitch());
  }

  /**
   * @function handleSwitch - 切换层级配置请求
   */
  @Bind()
  handleSwitch() {
    const { dispatch, form } = this.props;
    const { dataHierarchyMeaning, dataHierarchyCode, dataHierarchyId } = this.state;
    const fieldValues = form.getFieldValue(`dataHierarchyValue${dataHierarchyId}`);
    dispatch({
      type: 'user/switchDataHierarchies',
      payload: {
        dataHierarchyMeaning,
        dataHierarchyCode,
        dataHierarchyValue: fieldValues,
        organizationId: getCurrentOrganizationId(),
      },
    }).then((res) => {
      if (res) {
        form.resetFields();
        notification.success();
        this.handleOpenModal(true);
      }
    });
  }

  /**
   * 点击'+',获取当前节点的下级节点
   * @param {Boolean} isExpand 展开标记
   * @param {Object} record  当前行
   */
  @Bind()
  handleExpandRow(isExpand, record) {
    const { expandedRowKeys } = this.state;
    const rowKeys = isExpand
      ? [...expandedRowKeys, record.dataHierarchyId]
      : expandedRowKeys.filter((item) => item !== record.dataHierarchyId);
    this.setState({ expandedRowKeys: [...rowKeys] });
  }

  render() {
    const { fetchLoading } = this.props;
    const { expandedRowKeys, renderTree, queryParams } = this.state;
    const columns = [
      {
        title: intl
          .get('hzero.common.components.dataHierarchies.dataHierarchyName')
          .d('数据层级名称'),
        width: 100,
        dataIndex: 'dataHierarchyName',
      },
      {
        title: intl.get('hzero.common.components.dataHierarchies.dataHierarchyVal').d('数据层级值'),
        width: 100,
        render: (_, record) => {
          const { form } = this.props;
          const { valueList } = this.state;
          const {
            valueSourceCode,
            dataHierarchyMeaning,
            dataHierarchyValue,
            dataHierarchyId,
          } = record;
          return (
            <Form.Item className={style['data-hierarchies-td']}>
              {form.getFieldDecorator(`dataHierarchyValue${dataHierarchyId}`, {
                initialValue: dataHierarchyValue,
              })(
                <Lov
                  code={valueSourceCode}
                  textValue={dataHierarchyMeaning}
                  allowClear
                  disabled={
                    record.parentId ? !valueList.some((item) => item === record.parentId) : false
                  }
                  onClear={() => this.handleClear(record)}
                  onClick={() => this.handleSet(record, renderTree)}
                  onChange={this.handleChange}
                  queryParams={() => {
                    if (record.parentId) {
                      const { value, id } = this.handleFind(record, renderTree);
                      let code = '';
                      queryParams.map((item) => {
                        if (item[id]) {
                          code = item[id].valueField;
                        }
                        return 0;
                      });
                      return code
                        ? { [code]: value, tenantId: getCurrentOrganizationId() }
                        : { tenantId: getCurrentOrganizationId() };
                    } else {
                      return { tenantId: getCurrentOrganizationId() };
                    }
                  }}
                />
              )}
            </Form.Item>
          );
        },
      },
    ];
    return (
      <>
        <>
          <>
            <span
              style={{
                color: '#666',
              }}
              size="small"
              onClick={() => this.handleOpenModal(true)}
            >
              {intl.get('hzero.common.view.message.title.list').d('数据层级配置')}
            </span>
          </>
        </>
        <Modal
          title={intl.get('hzero.common.view.message.title.list').d('数据层级配置')}
          width="620px"
          bodyStyle={{ paddingTop: 0, height: '460px' }}
          visible={this.state.modalVisible}
          onCancel={() => this.handleOpenModal(false)}
          footer={null}
        >
          <Spin spinning={false}>
            <Table
              bordered
              className={style['data-hierarchies']}
              rowKey="dataHierarchyId"
              pagination={false}
              dataSource={renderTree}
              columns={columns}
              scroll={{ y: 360 }}
              expandedRowKeys={expandedRowKeys}
              loading={fetchLoading}
              onExpand={this.handleExpandRow}
            />
          </Spin>
        </Modal>
      </>
    );
  }
}

export default formatterCollections({ code: 'hpfm.tenantSelect' })(
  connect(({ user: { hierarchicalList = [] } = {}, loading }) => ({
    hierarchicalList,
    fetchLoading:
      loading.effects['user/fetchDataHierarchiesList'] ||
      loading.effects['user/switchDataHierarchies'],
  }))(DefaultDataHierarchies)
);
