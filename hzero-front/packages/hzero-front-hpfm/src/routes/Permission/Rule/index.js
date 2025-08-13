import React, { PureComponent } from 'react';
import { connect } from 'dva';
// import { routerRedux } from 'dva/router';
import { isEmpty } from 'lodash';
import PropTypes from 'prop-types';

import { Button as ButtonPermission } from 'components/Permission';

import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import SearchForm from './SearchForm';
import List from './List';
import Editor from './Editor';
import styles from './index.less';

@connect(({ loading, permission }) => ({
  loading,
  permission,
}))
@formatterCollections({ code: 'hpfm.permission' })
export default class Role extends PureComponent {
  static propTypes = {
    dispatch: PropTypes.func,
    permission: PropTypes.object.isRequired,
  };

  static defaultProps = {
    dispatch: e => e,
  };

  state = {
    visible: false,
    editorDataSource: {},
  };

  constructor(props) {
    super(props);
    this.fetchList = this.fetchList.bind(this);
    this.openEditor = this.openEditor.bind(this);
    this.searchForm = React.createRef();
  }

  componentDidMount() {
    this.fetchList();
    this.fetchPermissionRuleTypeCode();
  }

  /**
   * fetchList - 获取列表数据
   * @param {Object} payload - 查询参数
   */
  fetchList(payload) {
    const { dispatch } = this.props;
    const form =
      this.searchForm.current &&
      this.searchForm.current.props &&
      this.searchForm.current.props.form;
    const fieldsValue = form ? form.getFieldsValue() : {};
    dispatch({
      type: 'permission/queryRuleList',
      payload: { ...payload, ...fieldsValue },
    });
  }

  /**
   * fetchPermissionRuleTypeCode - 查询规则类型值集
   */
  fetchPermissionRuleTypeCode() {
    const { dispatch } = this.props;
    return dispatch({
      type: 'permission/queryCode',
      payload: { lovCode: 'HPFM.PERMISSION_RULE_TYPE' },
    });
  }

  openEditor(editorDataSource = {}) {
    this.setState(
      {
        visible: true,
      },
      () => {
        if (!isEmpty(editorDataSource)) {
          this.setState({
            editorDataSource,
          });
        }
      }
    );
  }

  closeEditor() {
    this.setState({
      visible: false,
      editorDataSource: {},
    });
  }

  onTableChange(pagination) {
    const { current, pageSize } = pagination;
    this.fetchList({ page: current === 0 ? 0 : current - 1, size: pageSize });
  }

  delete(record) {
    const {
      dispatch = e => e,
      permission: { rule = {} },
    } = this.props;
    dispatch({ type: 'permission/deleteRule', payload: { ...record } }).then(res => {
      if (res) {
        const { list = {} } = rule;
        const { current = 1, pageSize = 10 } = list.pagination;
        this.fetchList({ page: current - 1, size: pageSize });
        notification.success();
      }
    });
  }

  handleCreate(data, success = e => e) {
    const {
      dispatch = e => e,
      permission: { rule = {} },
    } = this.props;
    return dispatch({ type: 'permission/createRule', data }).then(res => {
      if (res) {
        const { list = {} } = rule;
        const { current = 1, pageSize = 10 } = list.pagination;
        this.fetchList({ page: current - 1, size: pageSize });
        success();
      }
      return res;
    });
  }

  handleUpdate(data, success = e => e) {
    const {
      dispatch = e => e,
      permission: { rule = {} },
    } = this.props;
    return dispatch({ type: 'permission/updateRule', data }).then(res => {
      if (res) {
        const { list = {} } = rule;
        const { current = 1, pageSize = 10 } = list.pagination;
        this.fetchList({ page: current - 1, size: pageSize });
        success();
      }
      return res;
    });
  }

  render() {
    const { permission = {}, loading = {}, match } = this.props;
    const { visible, editorDataSource } = this.state;
    const { rule } = permission;
    const { effects } = loading;
    const organizationId = getCurrentOrganizationId();
    const commonPrompt = {
      ruleCode: '',
      ruleName: '',
      sqlValue: '',
      enabledFlag: '',
      description: '',
    };
    const formProps = {
      wrappedComponentRef: this.searchForm,
      onSearch: this.fetchList,
      prompt: {
        ...commonPrompt,
        search: '',
        reset: '',
      },
    };
    const listProps = {
      loading: effects['permission/queryRuleList'],
      ...rule.list,
      match,
      onChange: this.onTableChange.bind(this),
      openEditor: this.openEditor.bind(this),
      prompt: {
        ...commonPrompt,
        option: '',
      },
      handleDelete: this.delete.bind(this),
    };
    const editorProps = {
      visible,
      dataSource: editorDataSource,
      onCancel: this.closeEditor.bind(this),
      permissionRuleType: permission.code['HPFM.PERMISSION_RULE_TYPE'] || [],
      organizationId,
      prompt: {
        ...commonPrompt,
        creationTitle: '',
        editorTitle: '',
        cancel: '',
        save: '',
        create: '',
      },
      handleUpdate: this.handleUpdate.bind(this),
      handleCreate: this.handleCreate.bind(this),
      processing: effects['permission/updateRule'] || effects['permission/createRule'],
    };
    return (
      <div className={styles['hpfm-permission-rule']}>
        <SearchForm {...formProps} />
        <div className="table-operator">
          <ButtonPermission
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.ruleCreate`,
                type: 'button',
                meaning: '数据权限规则-创建屏蔽规则',
              },
            ]}
            onClick={() => this.openEditor()}
          >
            {intl.get('hpfm.permission.view.option.create').d('创建屏蔽规则')}
          </ButtonPermission>
        </div>
        <List {...listProps} />
        <Editor {...editorProps} />
      </div>
    );
  }
}
