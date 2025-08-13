import * as React from 'react';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import { connect } from 'dva';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import { filterNullValueObject } from 'utils/utils';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';

import LanguagesTable from './LanguagesTable';
import FilterForm from './FilterForm';

/**
 * 语言控制
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} languages - 数据源
 * @reactProps {boolean} loading - 数据加载是否完成
 * @reactProps {boolean} saving - 保存操作是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
@connect(({ languages, loading }) => ({
  languages,
  loading: loading.effects['languages/fetchLanguages'],
  saving: loading.effects['languages/editLanguage'],
}))
@formatterCollections({ code: ['entity.lang'] })
export default class Languages extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  /**
   * 生命周期函数 获取render数据
   */
  componentDidMount() {
    this.handleSearchLanguage();
  }

  /**
   * 语言信息查询
   * @param {object} payload - 查询条件
   */
  @Bind()
  handleSearchLanguage(payload = {}) {
    const { dispatch } = this.props;
    const { form } = this.state;
    const filterValues = isUndefined(form) ? {} : filterNullValueObject(form.getFieldsValue());
    dispatch({
      type: 'languages/fetchLanguages',
      payload: {
        page: isEmpty(payload) ? {} : payload,
        ...filterValues,
      },
    });
  }

  /**
   *
   * @param {object} ref - FilterForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.setState({ form: ref.props.form });
  }

  /**
   *编辑行
   * @param {object} record - 语言行对象
   * @memberof languages
   */
  @Bind()
  handleEditRow(record) {
    const {
      languages: { languageList },
      dispatch,
    } = this.props;
    const newLanguageList = languageList.map(item => {
      if (item.id === record.id) {
        return { ...item, _status: 'update' };
      } else {
        if (item._status) {
          return { ...item, _status: '' };
        }
        return item;
      }
    });
    dispatch({
      type: 'languages/updateState',
      payload: { languageList: newLanguageList },
    });
  }

  /**
   *取消编辑行
   * @param {object} record - 语言行对象
   * @memberof languages
   */
  @Bind()
  handleCancelRow(record) {
    const {
      dispatch,
      languages: { languageList },
    } = this.props;
    const newLanguageList = languageList.map(item => {
      if (item.id === record.id) {
        const { _status, ...others } = item;
        return others;
      } else {
        return item;
      }
    });
    dispatch({
      type: 'languages/updateState',
      payload: { languageList: newLanguageList },
    });
  }

  /**
   * 编辑后保存数据
   * @param {object} params 保存的参数
   */
  @Bind()
  handleSaveOption(params) {
    const {
      dispatch,
      languages: { pagination = {} },
    } = this.props;
    dispatch({
      type: 'languages/editLanguage',
      payload: params,
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearchLanguage(pagination);
        dispatch({
          type: 'global/querySupportLanguage',
        });
      }
    });
  }

  render() {
    const {
      loading,
      saving,
      match,
      languages: { pagination = {}, languageList = [] },
    } = this.props;

    return (
      <React.Fragment>
        <Header title={intl.get('entity.lang.maintain').d('语言维护')} />
        <Content>
          <div className="table-list-search">
            <FilterForm onRef={this.handleBindRef} onSearch={this.handleSearchLanguage} />
          </div>
          <LanguagesTable
            loading={loading}
            saving={saving}
            match={match}
            pagination={pagination}
            dataSource={languageList}
            onEdit={this.handleEditRow}
            onCancel={this.handleCancelRow}
            onSave={this.handleSaveOption}
            onChange={this.handleSearchLanguage}
          />
        </Content>
      </React.Fragment>
    );
  }
}
