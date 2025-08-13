/**
 * MessageTabPane
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-13
 * @copyright 2019-06-13 © HAND
 */

import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Badge, Icon, Radio, Table } from 'hzero-ui';

import { tableScrollWidth } from 'utils/utils';
import { dateTimeRender } from 'utils/renderer';
import intl from 'utils/intl';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import FilterForm from './FilterForm';

function getRefFieldsValue(ref) {
  if (ref.current) {
    return ref.current.props.form.getFieldsValue();
  }
  return {};
}

export default class MessageTabPane extends Component {
  constructor(props) {
    super(props);
    this.state = {
      // prevDataSource: [], 之前的默认值
      // eslint-disable-next-line react/no-unused-state
      selectedRows: [],
      selectedRowKeys: [],
      readTypeValue: 'all',
      expand: false,
    };
    this.filterFormRef = React.createRef();
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { dataSource } = nextProps;
    const { prevDataSource } = prevState;
    if (dataSource !== prevDataSource) {
      return {
        prevDataSource: dataSource,
        selectedRows: [],
        selectedRowKeys: [],
      };
    }
    return null;
  }

  componentDidMount() {
    const { pagination = {} } = this.props;
    this.handleFetch({ page: pagination });
  }

  // 加载数据
  handleFetch(payload = {}) {
    const { fetchMessage, type } = this.props;
    const { readTypeValue } = this.state;
    const fieldsValue = getRefFieldsValue(this.filterFormRef);
    fieldsValue.fromDate =
      fieldsValue.fromDate && fieldsValue.fromDate.format(DEFAULT_DATETIME_FORMAT);
    fieldsValue.toDate = fieldsValue.toDate && fieldsValue.toDate.format(DEFAULT_DATETIME_FORMAT);
    const allPayload = { ...payload, ...fieldsValue };
    switch (readTypeValue) {
      case '0':
        allPayload.readFlag = 0;
        break;
      case '1':
        allPayload.readFlag = 1;
        break;
      case 'all':
      default:
        break;
    }
    fetchMessage(allPayload, type).then((res) => {
      if (res) {
        const { selectedRows: prevSelectedRows = [] } = this.props;
        this.setState(
          {
            // eslint-disable-next-line react/no-unused-state
            selectedRows: [],
            selectedRowKeys: [],
          },
          () => {
            // 当 因为加载数据 导致 选中数据变化时, 父组件需要强制刷新
            if (type !== 'announce') {
              if (prevSelectedRows.length !== 0) {
                const { indexForceUpdate } = this.props;
                indexForceUpdate();
              }
            }
          }
        );
      }
    });
  }

  @Bind()
  handleFormSearch() {
    const fieldsValue = getRefFieldsValue(this.filterFormRef);
    fieldsValue.fromDate =
      fieldsValue.fromDate && fieldsValue.fromDate.format(DEFAULT_DATETIME_FORMAT);
    fieldsValue.toDate = fieldsValue.toDate && fieldsValue.toDate.format(DEFAULT_DATETIME_FORMAT);
    this.handleFetch(fieldsValue);
  }

  // Table
  getColumns() {
    const { type = 'message' } = this.props;
    if (type === 'announce') {
      return [
        {
          title: intl.get('hmsg.userMessage.model.userMessage.title').d('标题内容'),
          dataIndex: 'title',
        },
        {
          title: intl.get('hmsg.userMessage.model.userMessage.creationDate').d('提交时间'),
          width: 200,
          dataIndex: 'publishedDate',
          render: dateTimeRender,
        },
        {
          title: intl.get('hmsg.common.view.type').d('类型'),
          width: 200,
          dataIndex: 'receiverTypeMeaning',
        },
      ];
    }
    return [
      {
        title: intl.get('hmsg.userMessage.model.userMessage.title').d('标题内容'),
        dataIndex: 'subject',
        render: (text, record) => {
          if (record.readFlag === 0) {
            return (
              <span>
                <Badge status="processing" />
                {text}
              </span>
            );
          } else {
            return (
              <span style={{ color: '#999' }}>
                <Badge status="default" />
                {text}
              </span>
            );
          }
        },
      },
      {
        title: intl.get('hmsg.userMessage.model.userMessage.creationDate').d('提交时间'),
        width: 200,
        dataIndex: 'creationDate',
        render: (text, record) => {
          if (record.readFlag === 0) {
            return dateTimeRender(text);
          } else {
            return <span style={{ color: '#999' }}>{dateTimeRender(text)}</span>;
          }
        },
      },
      {
        title: intl.get('hmsg.common.view.type').d('类型'),
        width: 200,
        dataIndex: 'messageTypeCode',
        render: (text, record) => this.typeCodeRender(record),
      },
    ];
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows = []) {
    this.setState(
      {
        // eslint-disable-next-line react/no-unused-state
        selectedRows,
        selectedRowKeys: selectedRows.map((r) => r.userMessageId),
      },
      () => {
        const { type, indexForceUpdate } = this.props;
        if (type !== 'announce') {
          indexForceUpdate();
        }
      }
    );
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    const params = {
      page,
      sort,
    };
    this.handleFetch(params);
  }

  @Bind()
  handleDetails(record) {
    const { onGotoDetail, type } = this.props;
    onGotoDetail(record, type);
  }

  /**
   * 全部消息 已读消息 未读消息 切换
   * @param e
   */
  @Bind()
  handleType(e) {
    this.setState(
      {
        readTypeValue: e.target.value,
      },
      () => {
        this.handleFetch();
      }
    );
  }

  @Bind()
  handleUp() {
    this.setState({
      expand: false,
    });
  }

  @Bind()
  handleDown() {
    this.setState({
      expand: true,
    });
  }

  /**
   * 处理站内消息类型
   */
  @Bind()
  typeCodeRender(record = {}) {
    // const { messageCategoryMeaning, messageSubcategoryMeaning } = record;
    const { userMessageTypeMeaning } = record;
    // const array = [messageCategoryMeaning, messageSubcategoryMeaning].filter(o => o);
    if (record.readFlag === 0) {
      // return array.join('-');
      return userMessageTypeMeaning;
    } else {
      // return <span style={{ color: '#999' }}>{array.join('-')}</span>;
      return <span style={{ color: '#999' }}>{userMessageTypeMeaning}</span>;
    }
  }

  render() {
    const { dataSource = [], pagination = false, loading = false, type = 'message' } = this.props;
    const { selectedRowKeys = [], readTypeValue, expand } = this.state;

    const columns = this.getColumns();

    const rowSelection =
      type === 'announce'
        ? null
        : {
            selectedRowKeys,
            onChange: this.handleRowSelectionChange,
          };

    return (
      <>
        {type !== 'announce' && (
          <div className="table-list-search">
            <Radio.Group buttonStyle="solid" onChange={this.handleType}>
              <Radio.Button value="all" checked={readTypeValue === 'all'}>
                {intl.get('hmsg.userMessage.view.option.allMessage').d('全部消息')}
              </Radio.Button>
              <Radio.Button value="0" checked={readTypeValue === '0'}>
                {intl.get('hmsg.userMessage.view.option.unReadMessage').d('未读消息')}
              </Radio.Button>
              <Radio.Button value="1" checked={readTypeValue === '1'}>
                {intl.get('hmsg.userMessage.view.option.readMessage').d('已读消息')}
              </Radio.Button>
            </Radio.Group>
            <a
              onClick={expand ? this.handleUp : this.handleDown}
              style={{ color: '#00CCFF', marginLeft: 8, cursor: 'pointer' }}
            >
              <span>
                {expand
                  ? intl.get(`hzero.common.button.up`).d('收起')
                  : intl.get(`hzero.common.button.expand`).d('展开')}
              </span>
              <Icon type={expand ? 'up' : 'down'} style={{ fontSize: 16 }} />
            </a>
          </div>
        )}
        {(expand || type === 'announce') && (
          <FilterForm wrappedComponentRef={this.filterFormRef} onSearch={this.handleFormSearch} />
        )}
        <Table
          bordered
          rowKey={type === 'announce' ? 'noticeId' : 'userMessageId'}
          style={{ cursor: 'pointer' }}
          loading={loading}
          onRow={(record) => ({
            onClick: () => {
              this.handleDetails(record);
            },
          })}
          rowSelection={rowSelection}
          dataSource={dataSource}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={pagination}
          onChange={this.handleTableChange}
        />
      </>
    );
  }
}
