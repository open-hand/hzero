/**
 * 动态配置行
 * @author zixi.xie@hand-china.com
 * @date 2019-05-30
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table, Tag, Popconfirm, Form, Row, Col, Input } from 'hzero-ui';
import { connect } from 'dva';
import { isUndefined, isEmpty } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { yesOrNoRender, enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { FORM_COL_4_LAYOUT, SEARCH_FORM_ROW_LAYOUT } from 'utils/constants';

import { getCodeMeaning, tableScrollWidth } from 'utils/utils';

import Drawer from './Drawer';

const layout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 17,
  },
};

@formatterCollections({ code: ['hpfm.dynamicForm', 'hpfm.prompt'] })
@connect(({ loading, line, header }) => ({
  line,
  header,
  fetchHeaderLoading: loading.effects['header/fetchHeaderById'],
  fetchKeyTypeLoading: loading.effects['line/fetchKeyTypeList'],
  fetchLoading: loading.effects['line/fetchLineList'],
  saveLoading: loading.effects['line/createLine'] || loading.effects['line/updateLine'],
}))
@Form.create({ fieldNameProp: null })
export default class Line extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      formData: {},
      modalVisible: false,
    };
  }

  /**
   * 页面渲染完成，加载表格数据
   */
  componentDidMount() {
    this.fetchKeyType();
    this.handleSearch();
  }

  /**
   * render()调用后请求数据
   */
  @Bind()
  handleSearch(params = {}) {
    const {
      dispatch,
      match: {
        params: { formHeaderId },
      },
    } = this.props;
    // 查询头配置
    this.fetchHeader(formHeaderId, dispatch);
    // 查询行配置
    dispatch({
      type: 'line/fetchLineList',
      payload: { formHeaderId, page: isEmpty(params) ? {} : params },
    });
  }

  /**
   * 查询配置分类
   */
  @Bind()
  fetchKeyType() {
    const { dispatch } = this.props;
    return dispatch({
      type: 'line/fetchKeyTypeList',
    });
  }

  /**
   * 查询配置头
   * @param headerId
   * @param dispatch
   * @returns {*}
   */
  @Bind()
  fetchHeader(formHeaderId, dispatch) {
    return dispatch({
      type: 'header/fetchHeaderById',
      payload: { formHeaderId },
    });
  }

  /**
   * 新建时，显示Drawer
   */
  @Bind()
  showDrawer() {
    this.setState({
      formData: {},
      modalVisible: true,
    });
  }

  /**
   * 隐藏Drawer
   */
  @Bind()
  hideDrawer() {
    this.setState({
      modalVisible: false,
    });
  }

  /**
   * 编辑时，显示Drawer
   * @param {Object} record - 操作数据
   */
  @Bind()
  handleUpdate(record) {
    const { dispatch } = this.props;
    this.setState({
      formData: record,
      modalVisible: true,
    });
    dispatch({
      type: 'line/fetchLineById',
      payload: {
        formLineId: record.formLineId,
      },
    });
  }

  /**
   * 保存
   * @param {Object} fieldsValue - 操作数据
   */
  @Bind()
  handleSave(fieldsValue) {
    const {
      dispatch,
      match: {
        params: { formHeaderId },
      },
      line: { pagination = {}, lineDetail = {} },
    } = this.props;
    const { formData } = this.state;
    dispatch({
      type: isUndefined(formData.formLineId) ? 'line/createLine' : 'line/updateLine',
      payload: {
        objectVersionNumber: lineDetail.objectVersionNumber,
        formHeaderId,
        ...formData,
        ...fieldsValue,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
        this.hideDrawer();
      }
    });
  }

  /**
   * 操作区域render
   * @param {String} _ - text 文本内容
   * @param {Object} record - 操作数据
   * @returns React.element
   */
  @Bind()
  optionsRender(_, record) {
    const { match } = this.props;
    const operators = [
      {
        key: 'edit',
        ele: (
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${match.path}.button.edit`,
                type: 'button',
                meaning: '表单配置行-编辑',
              },
            ]}
            onClick={() => this.handleUpdate(record)}
          >
            {intl.get('hzero.common.button.edit').d('编辑')}
          </ButtonPermission>
        ),
        len: 2,
        title: intl.get('hzero.common.button.edit').d('编辑'),
      },
      {
        key: 'delete',
        ele: (
          <Popconfirm
            title={intl.get('hpfm.prompt.model.message.confirm.remove').d('确认删除此条记录？')}
            onConfirm={() => {
              this.handleDelete(record);
            }}
          >
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${match.path}.button.delete`,
                  type: 'button',
                  meaning: '表单配置行-删除',
                },
              ]}
              style={{ marginLeft: 8 }}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
          </Popconfirm>
        ),
        len: 2,
        title: intl.get('hzero.common.button.delete').d('删除'),
      },
    ];
    return operatorRender(operators, record);
  }

  /**
   * 删除
   */
  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'line/removeLine',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  @Bind()
  handleBack() {
    const { dispatch } = this.props;
    dispatch({
      type: 'line/updateState',
      payload: {
        list: [],
      },
    });
    dispatch({
      type: 'header/updateState',
      payload: {
        headerDetail: {},
      },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchLoading,
      saveLoading,
      match,
      line: { list = [], pagination = {}, keyTypeList = [] },
      header: { headerDetail = {} },
      form: { getFieldDecorator },
    } = this.props;
    const { formData = {}, modalVisible = false } = this.state;
    // 表格列
    const columns = [
      {
        title: intl.get('hpfm.dynamicForm.line.itemCode').d('配置编码'),
        width: 150,
        align: 'left',
        dataIndex: 'itemCode',
      },
      {
        title: intl.get('hpfm.dynamicForm.line.itemName').d('配置名称'),
        width: 150,
        align: 'left',
        dataIndex: 'itemName',
      },
      {
        title: intl.get('hpfm.dynamicForm.line.itemTypeCode').d('配置类型'),
        width: 150,
        dataIndex: 'itemTypeCode',
        align: 'left',
        render: (value) => <Tag color="blue">{getCodeMeaning(value, keyTypeList)}</Tag>,
      },
      {
        title: intl.get('hpfm.dynamicForm.line.orderSeq').d('排序号'),
        dataIndex: 'orderSeq',
        width: 80,
        align: 'left',
      },
      {
        title: intl.get('hpfm.dynamicForm.line.defaultValue').d('默认值'),
        width: 150,
        align: 'left',
        dataIndex: 'defaultValue',
      },
      {
        title: intl.get('hpfm.dynamicForm.line.itemDescription').d('配置说明'),
        dataIndex: 'itemDescription',
      },
      {
        title: intl.get('hpfm.dynamicForm.line.valueConstraint').d('值约束'),
        align: 'left',
        dataIndex: 'valueConstraint',
      },
      {
        title: intl.get('hpfm.dynamicForm.line.valueSet').d('值集/视图编码'),
        align: 'left',
        dataIndex: 'valueSet',
      },
      {
        title: intl.get('hpfm.dynamicForm.line.requiredFlag').d('是否必输'),
        dataIndex: 'requiredFlag',
        render: yesOrNoRender,
        width: 150,
        align: 'left',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 150,
        align: 'left',
      },
      {
        title: intl.get('hpfm.dynamicForm.line.isAllowUpdate').d('是否允许更新'),
        dataIndex: 'updatableFlag',
        render: yesOrNoRender,
        width: 150,
        align: 'left',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        align: 'left',
        width: 120,
        dataIndex: 'operator',
        fixed: 'right',
        render: this.optionsRender,
      },
    ];

    const tableProps = {
      bordered: true,
      rowKey: 'lineId',
      loading: fetchLoading,
      dataSource: list,
      columns,
      pagination,
      onChange: (page) => this.handleSearch(page),
      scroll: { x: tableScrollWidth(columns) },
    };
    const drawerProps = {
      title: formData.formLineId
        ? intl.get('hpfm.dynamicForm.line.edit').d('编辑配置行')
        : intl.get('hpfm.dynamicForm.line.create').d('新建配置行'),
      confirmLoading: saveLoading,
      anchor: 'right',
      initData: formData,
      modalVisible,
      keyTypeList,
      onCancel: this.hideDrawer,
      onOk: this.handleSave,
    };
    return (
      <React.Fragment>
        <Header
          title={intl.get('hpfm.dynamicForm.line.title').d('表单配置行')}
          backPath="/hpfm/dynamic-form/list"
          onBack={this.handleBack}
        >
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '表单配置行-新建',
              },
            ]}
            onClick={this.showDrawer}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Form className="more-fields-search-form">
            <Row {...SEARCH_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...layout}
                  label={intl.get('hpfm.dynamicForm.line.formHeaderCode').d('配置头编码')}
                >
                  {getFieldDecorator('formCode', {
                    initialValue: headerDetail.formCode || '',
                  })(<Input disabled />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...layout}
                  label={intl.get('hpfm.dynamicForm.line.formHeaderName').d('配置头名称')}
                >
                  {getFieldDecorator('formName', { initialValue: headerDetail.formName || '' })(
                    <Input disabled />
                  )}
                </Form.Item>
              </Col>
            </Row>
          </Form>
          <Table {...tableProps} />
          {modalVisible && <Drawer {...drawerProps} />}
        </Content>
      </React.Fragment>
    );
  }
}
