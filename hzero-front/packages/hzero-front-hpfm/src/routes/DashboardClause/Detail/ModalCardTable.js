import React, {Component, Fragment} from 'react';
import {connect} from 'dva';
import {Button, Form, Input, Modal, Table} from 'hzero-ui';
import {Bind} from 'lodash-decorators';
import {isEmpty} from 'lodash';
import uuidv4 from 'uuid/v4';

import {Content, Header} from 'components/Page';
import intl from 'utils/intl';

import {addItemsToPagination, filterNullValueObject} from 'utils/utils';

const promptCode = 'hpfm.dashboardClause';

/**
 * 条目配置详情 Modal框
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */

@connect(({ dashboardClause, loading }) => ({
  dashboardClause,
  loading: loading.effects['dashboardClause/fetchCard'],
}))
@Form.create({ fieldNameProp: null })
export default class ModalCardTable extends Component {
  state = {
    selectedRows: [],
  };

  /* eslint-disable-next-line */
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { visible } = this.props;
    if (nextProps.visible === true && nextProps.visible !== visible) {
      this.handleSearchCard();
    }
  }

  /**
   * 查询卡片
   * @param {*} page 分页参数
   */
  @Bind()
  handleSearchCard(page = {}) {
    const {clauseId, headInfo = {}, dispatch, form, excludeCardIds = []} = this.props;
    const filterValues = form ? form.getFieldsValue() : {};
    dispatch({
      type: 'dashboardClause/fetchCard',
      payload: {
        page,
        clauseId,
        dataTenantLevel: headInfo.dataTenantLevel,
        excludeCardIds: isEmpty(excludeCardIds) ? '' : excludeCardIds.join(','),
        ...filterValues,
      },
    });
  }

  @Bind()
  handleCreateRows() {
    const {
      dispatch,
      clauseId,
      onCardModalDisplay,
      dashboardClause: { clauseDetailTableList = [], clauseDetailPagination = {} },
    } = this.props;
    const { selectedRows } = this.state;
    const dataList = selectedRows.map(item =>
      filterNullValueObject({
        id: uuidv4(),
        cardId: item.id,
        clauseId,
        code: item.code,
        name: item.name,
        isLocal: true,
      })
    );
    dispatch({
      type: 'dashboardClause/updateState',
      payload: {
        clauseDetailTableList: [...clauseDetailTableList, ...dataList],
        clauseDetailPagination: addItemsToPagination(
          dataList.length,
          clauseDetailTableList.length,
          clauseDetailPagination
        ),
      },
    });
    onCardModalDisplay();
    this.setState({ selectedRows: [] });
  }

  /**
   * 保存选中的行
   * @param {Array} _ 选中数据的key
   * @param {Array} selectedRows 行数据
   */
  @Bind()
  onSelectChange(_, selectedRows) {
    this.setState({ selectedRows });
  }

  /**
   * 重置表单
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  renderForm() {
    const {
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form layout="inline">
        <Form.Item label={intl.get(`${promptCode}.model.dashboard.cardCode`).d('卡片编码')}>
          {getFieldDecorator('code')(<Input trim inputChinese={false} />)}
        </Form.Item>
        <Form.Item label={intl.get(`${promptCode}.model.dashboard.cardName`).d('卡片名称')}>
          {getFieldDecorator('name')(<Input />)}
        </Form.Item>
        <Form.Item>
          <Button
            onClick={this.handleFormReset}
          >
            {intl.get('hzero.common.button.reset').d('重置')}
          </Button>
          <Button
            style={{marginLeft: 8}}
            htmlType="submit"
            type="primary"
            onClick={this.handleSearchCard}
          >
            {intl.get('hzero.common.button.search').d('查询')}
          </Button>
        </Form.Item>
      </Form>
    );
  }

  render() {
    const {
      visible,
      loading,
      columns,
      onCardModalDisplay,
      dashboardClause: { cardList = [], cardPagination = {} },
    } = this.props;
    const { selectedRows } = this.state;
    const rowSelection = {
      selectedRowKeys: selectedRows.map(o => o.id),
      onChange: this.onSelectChange,
    };
    return (
      <Modal
        width={1000}
        visible={visible}
        onOk={this.handleCreateRows}
        onCancel={onCardModalDisplay}
      >
        <Fragment>
          <Header title={intl.get(`${promptCode}.view.message.card`).d('分配卡片')} />
          <Content>
            <div className="table-list-search">{this.renderForm()}</div>
            <Table
              bordered
              loading={loading}
              rowKey="id"
              columns={columns}
              dataSource={cardList}
              pagination={cardPagination}
              onChange={this.handleSearchCard}
              rowSelection={rowSelection}
            />
          </Content>
        </Fragment>
      </Modal>
    );
  }
}
