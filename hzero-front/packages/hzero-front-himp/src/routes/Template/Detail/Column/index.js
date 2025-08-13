import React from 'react';
import { Popconfirm, Button, Table } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import { enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

import ColumnDrawer from './ColumnDrawer';
import FilterForm from './FilterForm';

@connect(({ template, loading }) => ({
  template,
  fetchListLoading: loading.effects['template/fetchColumnList'],
  detailLoading: loading.effects['template/fetchColumnDetail'],
  updateLoading: loading.effects['template/updateColumn'],
  createLoading: loading.effects['template/createColumn'],
}))
export default class ColumnsTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
    };
  }

  componentDidMount() {
    this.fetchColumnList();
    this.fetchColumnTypeCode();
  }

  /**
   * fetchTemplateColumnTypeCode - 查询层级<HIMP.TEMPLATE.COLUMNTYPE>code
   * @return {Array}
   */
  @Bind()
  fetchColumnTypeCode() {
    const { dispatch } = this.props;
    return dispatch({
      type: 'template/queryCode',
      payload: { lovCode: 'HIMP.TEMPLATE.COLUMNTYPE' },
    });
  }

  @Bind()
  fetchColumnList(params = {}) {
    const {
      dispatch,
      match,
      template: { columnPagination = {} },
    } = this.props;
    const {
      params: { sheetId: targetId },
    } = match;
    dispatch({
      type: 'template/fetchColumnList',
      payload: { page: columnPagination, targetId, ...params },
    });
  }

  /**
   * handlePagination - 分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchColumnList({ page: pagination });
  }

  @Bind()
  showModal(record, flag) {
    const { dispatch } = this.props;
    if (flag) {
      dispatch({
        type: 'template/fetchColumnDetail',
        payload: record,
      });
    } else {
      dispatch({
        type: 'template/updateState',
        payload: { templateColumnDetail: {} },
      });
    }
    this.setState({ modalVisible: true });
  }

  @Bind()
  hideModal() {
    this.setState({ modalVisible: false });
  }

  @Bind()
  handleDeleteColumn(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'template/deleteColumn',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchColumnList();
      }
    });
  }

  /**
   * 查询表单
   * @param {object} params - 查询参数
   */
  @Bind()
  handleSearch(params) {
    this.fetchColumnList({ ...params, page: {} });
  }

  @Bind()
  handleSaveColumn(params) {
    const {
      dispatch,
      match,
      template: { templateColumnDetail = {} },
    } = this.props;
    const {
      params: { sheetId },
    } = match;
    const { id: columnId } = templateColumnDetail;
    dispatch({
      type: `template/${columnId !== undefined ? 'updateColumn' : 'createColumn'}`,
      payload: {
        targetId: sheetId,
        changeDataFlag: 0,
        ...templateColumnDetail,
        ...params,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchColumnList();
      }
    });
  }

  render() {
    const {
      match,
      fetchListLoading = false,
      detailLoading = false,
      updateLoading = false,
      createLoading = false,
      template: {
        code = {},
        templateColumnList = [],
        templateColumnDetail = {},
        columnPagination = {},
      },
      match: {
        params: { templateType },
      },
    } = this.props;
    const { modalVisible } = this.state;
    const {
      params: { id },
    } = match;
    const columns = [
      {
        title: intl.get(`himp.template.model.template.columnIndex`).d('列号'),
        width: 80,
        dataIndex: 'columnIndex',
      },
      {
        title: intl.get(`himp.template.model.template.columnCode`).d('列编码'),
        dataIndex: 'columnCode',
      },
      {
        title: intl.get(`himp.template.model.template.columnName`).d('列名'),
        dataIndex: 'columnName',
      },
      {
        title: intl.get(`himp.template.model.template.columnType`).d('列类型'),
        width: 100,
        dataIndex: 'columnTypeMeaning',
      },
      {
        title: intl.get(`himp.template.model.template.formatMask`).d('格式掩码'),
        width: 100,
        dataIndex: 'formatMask',
      },
      {
        title: intl.get(`himp.template.model.template.length`).d('长度'),
        width: 100,
        dataIndex: 'length',
      },
      {
        title: intl.get(`himp.template.model.template.maxValue`).d('最大值'),
        width: 100,
        dataIndex: 'maxValue',
      },
      {
        title: intl.get(`himp.template.model.template.minValue`).d('最小值'),
        width: 100,
        dataIndex: 'minValue',
      },
      {
        title: intl.get(`himp.template.model.template.validateSet`).d('验证值集'),
        width: 100,
        dataIndex: 'validateSet',
      },
      {
        title: intl.get(`himp.template.model.template.regularExpression`).d('正则式'),
        width: 100,
        dataIndex: 'regularExpression',
      },
      {
        title: intl.get(`himp.template.model.template.nullable`).d('允许为空'),
        width: 100,
        dataIndex: 'nullableFlag',
        render: yesOrNoRender,
      },
      {
        title: intl.get(`himp.template.model.template.validate`).d('数据验证'),
        width: 100,
        dataIndex: 'validateFlag',
        render: yesOrNoRender,
      },
      {
        title: intl.get(`hzero.common.status`).d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        fixed: 'right',
        width: 110,
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a
                  onClick={() => {
                    this.showModal(record, true);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get(`himp.template.view.message.title.confirmDelete`).d('确定删除？')}
                  onConfirm={() => this.handleDeleteColumn(record)}
                  style={{ textAlign: 'center' }}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <>
        <Header
          title={intl.get('himp.template.view.message.title.templateCol').d('模板列')}
          backPath={`/himp/template/detail/${id}`}
        >
          <Button onClick={() => this.showModal({}, false)} type="primary" icon="plus">
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm onSearch={this.handleSearch} />
          </div>
          <Table
            bordered
            rowKey="id"
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            loading={fetchListLoading}
            dataSource={templateColumnList}
            pagination={columnPagination}
            onChange={this.handlePagination}
          />
          <ColumnDrawer
            title={
              templateColumnDetail.id !== undefined
                ? intl.get('himp.template.view.message.title.editCol').d('编辑列')
                : intl.get('himp.template.view.message.title.createCol').d('新建列')
            }
            initLoading={detailLoading}
            loading={templateColumnDetail.id !== undefined ? updateLoading : createLoading}
            modalVisible={modalVisible}
            initData={templateColumnDetail}
            columnTypeCode={code['HIMP.TEMPLATE.COLUMNTYPE']}
            onCancel={this.hideModal}
            onOk={this.handleSaveColumn}
            templateType={templateType}
          />
        </Content>
      </>
    );
  }
}
