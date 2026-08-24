import React, { Fragment, PureComponent } from 'react';
import { Button, Checkbox, Form, Modal } from 'hzero-ui';
// import { isUndefined } from 'lodash';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
// import { operatorRender } from 'utils/renderer';
import { Content } from 'components/Page';
import OptionInput from 'components/OptionInput';
import Table from 'components/VirtualTable';
import styles from './index.less';

/**
 * 岗位信息层次结构树(Tree)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {boolean} visible - 可见性
 * @reactProps {Function} onOk - 确定操作
 * @reactProps {Function} onCancel - 取消操作
 * @reactProps {object[]} dataSource - 岗位信息
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class PositionModal extends PureComponent {
  /**
   * 组件属性定义
   */
  static propTypes = {
    visible: PropTypes.bool,
    onOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    visible: false,
    onOk: (e) => e,
    onCancel: (e) => e,
  };

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
          // 如果验证成功,则执行onSearch
          const { option = {} } = form.getFieldsValue();
          onSearch({ type: Object.keys(option)[0], name: Object.values(option)[0] });
        }
      });
    }
  }

  /**
   * 更改岗位状态
   * @param {Object} record - 岗位信息
   * @param {boolean} value - 选中标记
   */
  @Bind()
  handleChangeStatus(record, value) {
    this.props.onChange({ ...record, assignFlag: value ? 1 : 0 });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form,
      visible,
      loading = false,
      onOk,
      onCancel,
      onChange,
      onShowSubLine,
      onExpand,
      onShrink,
      employeeName,
      expandedRowKeys,
      dataSource = [],
    } = this.props;
    const { getFieldDecorator } = form;
    const queryArray = [
      {
        queryLabel: intl.get('entity.organization.name').d('组织名称'),
        queryName: 'C',
      },
      {
        queryLabel: intl.get('entity.department.name').d('部门名称'),
        queryName: 'D',
      },
      {
        queryLabel: intl.get('entity.position.name').d('岗位名称'),
        queryName: 'P',
      },
    ];
    const columns = [
      {
        title: intl.get('hpfm.employee.model.unit.name').d('组织/部门/岗位'),
        dataIndex: 'name',
        width: 300,
        render: ({ rowData: record, dataIndex }) => {
          const val = record[dataIndex];
          if (record.type === 'G' || record.type === 'C') {
            return (
              <span className={classNames(styles['hr-type-icon'])}>
                <span className={classNames(styles['hr-type-common'], styles['hr-company'])} />
                {val}
              </span>
            );
          } else if (record.type === 'D') {
            return (
              <span className={classNames(styles['hr-type-icon'])}>
                <span className={classNames(styles['hr-type-common'], styles['hr-department'])} />
                {val}
              </span>
            );
          } else {
            return (
              <span className={classNames(styles['hr-type-icon'])}>
                <span className={classNames(styles['hr-type-common'], styles['hr-position'])} />
                {val}
              </span>
            );
          }
        },
      },
      {
        title: intl.get('hpfm.employee.model.unit.code').d('编码'),
        dataIndex: 'code',
        width: 250,
        align: 'center',
      },
      {
        title: intl.get('hpfm.employee.model.unit.assignFlag').d('分配岗位'),
        dataIndex: 'assignFlag',
        width: 90,
        align: 'center',
        render: ({ rowData: record, dataIndex }) => {
          const val = record[dataIndex];
          return (
            <Checkbox
              checked={val}
              value={val}
              onChange={(e) => onChange({ ...record, assignFlag: e.target.checked ? 1 : 0 })}
              disabled={record.type !== 'P'}
            />
          );
        },
      },
    ];

    return (
      <Modal
        destroyOnClose
        visible={visible}
        onOk={onOk}
        onCancel={onCancel}
        width={720}
        okText={intl.get('hzero.common.button.save').d('保存')}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
        className={classNames(styles['hpfm-employee-position'])}
        title={
          <div style={{ display: 'flex', justifyContent: 'space-between', paddingRight: '20px' }}>
            <div>{intl.get('hpfm.employee.view.message.assign.position').d('分配岗位')}</div>
          </div>
        }
      >
        <Content>
          <Fragment>
            <p className={classNames(styles['hpfm-hr-title'])}>
              <span />
              {intl
                .get('hpfm.employee.view.message.tips', {
                  name: employeeName,
                })
                .d(`当前正在为「${employeeName}」员工，分配岗位`)}
            </p>
          </Fragment>
          <div className="table-list-search">
            <Form layout="inline">
              <Form.Item>
                {getFieldDecorator('option')(
                  <OptionInput style={{ width: 300 }} queryArray={queryArray} />
                )}
              </Form.Item>
              <Form.Item>
                <Button
                  data-code="search"
                  htmlType="submit"
                  type="primary"
                  onClick={this.handleSearch}
                  loading={loading}
                >
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Form>
          </div>
          <div style={{ textAlign: 'right', marginBottom: '16px' }}>
            <Button icon="down" onClick={onExpand}>
              {intl.get('hzero.common.button.expandAll').d('全部展开')}
            </Button>
            <Button icon="up" onClick={onShrink} style={{ marginLeft: 8 }}>
              {intl.get('hzero.common.button.collapseAll').d('全部收起')}
            </Button>
          </div>

          <Table
            isTree
            bordered
            virtualized
            data={dataSource}
            // defaultExpandAllRows
            height={400}
            minHeight={400}
            rowKey="typeWithId"
            loading={loading}
            columns={columns}
            pagination={false}
            onExpandChange={onShowSubLine}
            expandedRowKeys={expandedRowKeys}
            scroll={{ y: 400 }}
            shouldUpdateScroll={false}
          />
        </Content>
      </Modal>
    );
  }
}
