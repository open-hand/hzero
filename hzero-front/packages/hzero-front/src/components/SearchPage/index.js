/**
 * SearchPage - 查询模板
 * @date: 2018-7-20
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
// TODO: max-classed-per-file
// eslint-disable-next-line max-classes-per-file
import React, { Component } from 'react';
import { Form, Button } from 'hzero-ui';
import { Header, Content } from 'components/Page';
import EditTable from 'components/EditTable';
import CustomTable from 'components/CustomTable';
import { isUndefined, flow } from 'lodash';
import { Bind } from 'lodash-decorators';
import { filterNullValueObject } from 'utils/utils';
import intl from 'utils/intl';
import cacheComponent from 'components/CacheComponent';
import BaseFilterForm from './FilterForm';

/**
 * 通用查询模板.
 *
 * 1. componentDidMount 方法中判断是从其他路由回退进而选择是查询还是刷新
 * 2. 子类需要在 props 中映射 list,loading
 * 3. 子类覆盖部分方法
 *
 *
 * @export
 * @class OptionInput
 * @extends {React.Component}
 */
export default class SearchPage extends Component {
  constructor(props) {
    super(props);
    const pageConfig = this.pageConfig();
    class SearchBasicForm extends BaseFilterForm {}
    const formWrapper = pageConfig.cacheKey
      ? flow(
          cacheComponent({ cacheKey: pageConfig.cacheKey }),
          Form.create({ fieldNameProp: null })
        )
      : Form.create({ fieldNameProp: null });
    this.FilterForm = formWrapper(SearchBasicForm);
    this.pageConfig = pageConfig;
    this.filterForm = {};
    this.state = {
      selectedRows: [],
    };
    this.customConstructor(props); // 初始化自定义数据
  }

  componentDidMount() {
    const {
      pageConfig: { modelName },
    } = this;

    const {
      [modelName]: searchPageData = {},
      location: { state: { _back } = {} },
    } = this.props;
    const { pagination } = searchPageData;
    this.handleSearch(isUndefined(_back) ? {} : pagination);
  }

  /**
   * 列表查询
   * @param {Object} pagination 查询参数
   */
  @Bind()
  handleSearch(pagination = {}) {
    const {
      pageConfig: {
        searchDispatch,
        searchCallback = (e) => e,
        paramsFilter = (e) => e,
        otherParams = {},
      } = {},
    } = this;
    const { dispatch = (e) => e } = this.props;
    const form = this.filterForm.props && this.filterForm.props.form;
    const params = isUndefined(form) ? {} : form.getFieldsValue();
    const filterValues = filterNullValueObject({
      ...params,
      ...paramsFilter(params),
    });
    dispatch({
      type: searchDispatch,
      payload: {
        ...otherParams,
        page: pagination,
        ...filterValues,
      },
    }).then((res) => {
      searchCallback(res);
    });
  }

  /**
   *
   * @param {object} ref - FilterForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.filterForm = ref;
  }

  @Bind()
  handleRowSelectChange(_, selectedRows) {
    this.setState({ selectedRows });
  }

  /**
   * 查询页面配置，子类覆盖
   */
  pageConfig() {
    return {};
  }

  /**
   * 自定义初始化数据
   */
  // eslint-disable-next-line
  customConstructor(props) {
    return {};
  }

  /**
   * 渲染 Header, 子类覆盖
   */
  renderHeader() {
    return <Header title={intl.get('hzero.common.components.searchPage.title').d('默认标题')} />;
  }

  /**
   * Conent 属性, 子类覆盖
   */
  contentProps() {
    return null;
  }

  /**
   * Table 属性, 子类覆盖
   */
  tableProps() {
    return {};
  }

  /**
   * 渲染查询表单, 子类覆盖
   */
  renderForm() {
    return null;
  }

  /**
   * 渲染其他组件，例如弹出框等, 子类覆盖
   */
  renderOther() {
    return null;
  }

  render() {
    const {
      pageConfig: {
        modelName, // model 名
        dataName = 'list', // model 中的列表数据变量名
        customSearch = false,
        editTable = false,
      } = {},
    } = this;
    const { selectedRows = [] } = this.state;
    const { [modelName]: searchPageData = {} } = this.props; // 根据 modelName 获取 Model 数据
    const customTableProps = this.tableProps();
    const { [dataName]: list = {}, pagination = {} } = searchPageData;
    const { rowKey = 'key' } = customTableProps;
    const columns = [];
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n[rowKey]),
      onChange: this.handleRowSelectChange,
    };
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const { FilterForm } = this;
    const tableProps = {
      bordered: true,
      rowKey: 'key',
      dataSource: list.content,
      pagination,
      rowSelection,
      columns,
      onChange: this.handleSearch,
      ...customTableProps,
    };

    return (
      <>
        {this.renderHeader()}
        <Content {...this.contentProps()}>
          <div className="table-list-search">
            <FilterForm {...filterProps}>
              {(form) =>
                customSearch ? (
                  this.renderForm(form)
                ) : (
                  <Form layout="inline">
                    {this.renderForm(form)}
                    <Form.Item>
                      <Button
                        data-code="search"
                        type="primary"
                        htmlType="submit"
                        onClick={this.filterForm.handleSearch}
                      >
                        {intl.get('hzero.common.button.search').d('查询')}
                      </Button>
                      <Button
                        data-code="reset"
                        style={{ marginLeft: 8 }}
                        onClick={this.filterForm.handleFormReset}
                      >
                        {intl.get('hzero.common.button.reset').d('重置')}
                      </Button>
                    </Form.Item>
                  </Form>
                )
              }
            </FilterForm>
          </div>
          {editTable ? <EditTable {...tableProps} /> : <CustomTable {...tableProps} />}
        </Content>
        {this.renderOther()}
      </>
    );
  }
}
