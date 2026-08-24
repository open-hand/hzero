/**
 * 动态配置头
 * @author liang.xiong@hand-china.com
 * @date 2019-07-15
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */
import React, { PureComponent, Fragment } from 'react';
import { connect } from 'dva';
import { Table, Tag, Popconfirm } from 'hzero-ui';
import { isUndefined, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import { filterNullValueObject, tableScrollWidth } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';

import FilterForm from './FilterForm';
import Drawer from './Drawer';

@formatterCollections({ code: ['hpfm.dynamicForm', 'hpfm.prompt'] })
@connect(({ loading, header }) => ({
  header,
  fetchLoading: loading.effects['header/fetchHeaderList'],
  fetchConfigGroupLoading: loading.effects['header/fetchConfigGroupList'],
  saveLoading: loading.effects['header/updateHeader'] || loading.effects['header/createHeader'],
}))
export default class PropExtendHeader extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      formData: {},
      modalVisible: false,
    };
  }

  /**
   * render()调用后请求数据
   */
  componentDidMount() {
    const {
      header: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = _back === -1 ? pagination : {};
    this.fetchConfigGroup();
    this.handleSearch(page);
  }

  /**
   * 查询配置分类
   */
  @Bind()
  fetchConfigGroup() {
    const { dispatch } = this.props;
    return dispatch({
      type: 'header/fetchConfigGroupList',
    });
  }

  /**
   * 查询数据
   * @param {Object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'header/fetchHeaderList',
      payload: { ...fieldValues, page: isEmpty(params) ? {} : params },
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
      type: 'header/fetchHeaderById',
      payload: {
        formHeaderId: record.formHeaderId,
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
      header: { pagination = {}, headerDetail = {} },
    } = this.props;
    const { formData } = this.state;
    dispatch({
      type: formData.formHeaderId === undefined ? 'header/createHeader' : 'header/updateHeader',
      payload: {
        objectVersionNumber: headerDetail.objectVersionNumber,
        ...formData,
        ...fieldsValue,
      },
    }).then((res1) => {
      if (res1) {
        notification.success();
        this.handleSearch(pagination);
        this.hideDrawer();
      }
    });
  }

  /**
   * 删除
   */
  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'header/removeHeader',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  /**
   * 详情
   * @param {Object} record - 操作数据
   */
  @Bind()
  onEditDetail(record) {
    const { history } = this.props;
    history.push(`/hpfm/dynamic-form/detail/${record.formHeaderId}`);
  }

  /**
   * 操作区域render
   * @param {String} _ - text 文本内容
   * @param {Object} record - 操作数据
   * @returns React.element
   */
  @Bind()
  optionsRender(_, record) {
    const match = this.props;
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
                meaning: '表单配置头-编辑',
              },
            ]}
            onClick={() => this.handleUpdate(record)}
          >
            {intl.get(`hzero.common.button.edit`).d('编辑')}
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
                  meaning: '表单配置头-删除',
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
      {
        key: 'maintain',
        ele: (
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${match.path}.button.maintain`,
                type: 'button',
                meaning: '表单配置头-维护',
              },
            ]}
            style={{ marginLeft: 8 }}
            onClick={() => this.onEditDetail(record)}
          >
            {intl.get(`hpfm.dynamicForm.header.edit`).d('维护')}
          </ButtonPermission>
        ),
        len: 2,
        title: intl.get(`hpfm.dynamicForm.header.edit`).d('维护'),
      },
    ];
    return operatorRender(operators, record);
  }

  /**
   * 设置form对象
   * @param {object} ref - FilterForm子组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
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
      header: { list = [], configGroupList = [], pagination = {} },
    } = this.props;
    const { formData = {}, modalVisible = false } = this.state;
    // 表格列
    const columns = [
      {
        title: intl.get('hpfm.dynamicForm.header.configCode').d('配置编码'),
        width: 200,
        align: 'left',
        dataIndex: 'formCode',
      },
      {
        title: intl.get('hpfm.dynamicForm.header.configName').d('配置名称'),
        align: 'left',
        width: 200,
        dataIndex: 'formName',
      },
      {
        title: intl.get('hpfm.dynamicForm.header.formGroupMeaning').d('配置归类'),
        dataIndex: 'formGroupMeaning',
        width: 120,
        align: 'left',
        render: (value) => {
          const codeList = configGroupList.filter((n) => n.value === value);
          if (!isEmpty(codeList)) {
            return <Tag color={codeList[0].tag}>{codeList[0].meaning}</Tag>;
          } else {
            return value;
          }
        },
      },
      {
        title: intl.get('hpfm.dynamicForm.header.description').d('配置描述'),
        align: 'left',
        dataIndex: 'formDescription',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        align: 'left',
        dataIndex: 'operator',
        width: 140,
        fixed: 'right',
        render: this.optionsRender,
      },
    ];

    const filterProps = {
      configGroupList,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const tableProps = {
      bordered: true,
      rowKey: 'formHeaderId',
      loading: fetchLoading,
      dataSource: list,
      columns,
      pagination,
      onChange: (page) => this.handleSearch(page),
      scroll: { x: tableScrollWidth(columns) },
    };
    const drawerProps = {
      configGroupList,
      title: formData.formHeaderId
        ? intl.get('hpfm.dynamicForm.header.header.edit').d('编辑配置')
        : intl.get('hpfm.dynamicForm.header.create').d('新建配置'),
      confirmLoading: saveLoading,
      anchor: 'right',
      initData: formData,
      modalVisible,
      onCancel: this.hideDrawer,
      onOk: this.handleSave,
    };
    return (
      <Fragment>
        <Header title={intl.get('hpfm.dynamicForm.header.title').d('表单配置头')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '表单配置头-新建',
              },
            ]}
            onClick={this.showDrawer}
          >
            {intl.get(`hzero.common.button.create`).d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <Table {...tableProps} />
          <Drawer {...drawerProps} />
        </Content>
      </Fragment>
    );
  }
}
