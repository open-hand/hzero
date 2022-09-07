/**
 * sqlExecute - SQL执行界面
 * @date: 2018-9-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Header } from 'components/Page';

import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';
import request from 'utils/request';
import notification from 'utils/notification';

import emitter from './ev';
import style from './index.less';

const FormItem = Form.Item;
const { TextArea } = Input;
const promptCode = 'hpfm.sqlExecute';

@Form.create({ fieldNameProp: null })
@connect(({ sqlExecute, loading }) => ({
  sqlExecute,
  executeSelected: loading.effects['sqlExecute/fetchExecuteResult'],
  executeAll: loading.effects['sqlExecute/fetchExecuteAllResult'],
  tenantId: getCurrentOrganizationId(),
}))
export default class ExecuteForm extends PureComponent {
  state = {
    paging: { page: 0, size: 10 }, // 分页参数
    exportPending: false,
  };

  textArea;

  componentDidMount() {
    emitter.on('treeNodeClick', (msg) => {
      this.insertValue(msg);
    });
  }

  // 组件销毁前移除事件监听
  componentWillUnmount() {
    // emitter.off('treeNodeClick');
  }

  insertValue(text) {
    const { form } = this.props;
    const { value } = this.textArea.props;
    let startPos = 0;
    let endPos = 0;
    if (this.textArea.textAreaRef) {
      startPos = this.textArea.textAreaRef.selectionStart;
      endPos = this.textArea.textAreaRef.selectionEnd;
    }
    if (value) {
      form.setFieldsValue({
        sql: value.substring(0, startPos) + text + value.substring(endPos, value.length),
      });
    } else {
      form.setFieldsValue({ sql: text });
    }
  }

  /**
   * 执行所有SQL
   */
  @Bind()
  loadAllData(payload) {
    const {
      dispatch,
      tenantId,
      sqlExecute: { serverName = '' },
    } = this.props;
    const { paging } = this.state;
    dispatch({
      type: 'sqlExecute/fetchExecuteAllResult',
      payload: { ...paging, ...payload, tenantId, serverName },
    });
  }

  /**
   * 执行选中的SQL
   */
  @Bind()
  loadData(payload) {
    const {
      dispatch,
      tenantId,
      sqlExecute: { serverName = '' },
    } = this.props;
    const { paging } = this.state;
    dispatch({
      type: 'sqlExecute/fetchExecuteResult',
      payload: { ...paging, ...payload, tenantId, serverName },
    });
  }

  /**
   * 执行全部SQL
   */
  @Bind()
  handleExecuteAll() {
    const { form, dispatch } = this.props;
    form.validateFields((err, values) => {
      const { sql } = values;
      if (!err) {
        this.loadAllData({ sql });
        dispatch({
          type: 'sqlExecute/updateState',
          payload: {
            executeSql: sql,
          },
        });
      }
    });
  }

  /**
   * 执行已选择的SQL
   */
  @Bind()
  handleExecuteSelected() {
    const { value } = this.textArea.props;
    const { dispatch } = this.props;
    // 鼠标选中的内容
    const selected =
      value &&
      value.slice(this.textArea.textAreaRef.selectionStart, this.textArea.textAreaRef.selectionEnd);

    if (selected) {
      this.loadData({ sql: selected });
      dispatch({
        type: 'sqlExecute/updateState',
        payload: {
          executeSql: selected,
        },
      });
    }
  }

  /**
   * 导出结果
   */
  @Bind()
  handleExportResult() {
    const {
      sqlExecute: { exportSql },
    } = this.props;
    const requestUrl = `${HZERO_PLATFORM}/v1/db-ide/export`;

    if (exportSql) {
      this.setState({ exportPending: true });
      request(requestUrl, {
        method: 'POST',
        body: { sql: exportSql },
        responseType: 'blob',
      })
        .then((blob) => {
          // 创建隐藏的可下载链接
          const eleLink = document.createElement('a');
          eleLink.download = intl
            .get(`${promptCode}.model.sqlExecute.sqlImportRes`)
            .d('SQL导出结果.xlsx');
          eleLink.style.display = 'none';
          eleLink.href = window.URL.createObjectURL(blob);
          // 触发点击
          document.body.appendChild(eleLink);
          eleLink.click();
          // 然后移除
          document.body.removeChild(eleLink);
          this.setState({
            exportPending: false,
          });
        })
        .catch((error) => {
          notification.error({
            description: error,
          });
          this.setState({
            exportPending: false,
          });
        });
    }
  }

  /**
   * 重置表单
   */
  @Bind()
  handleFormReset() {
    const { form, dispatch } = this.props;
    form.resetFields();
    dispatch({
      type: 'sqlExecute/updateState',
      payload: {
        executeSql: '',
      },
    });
  }

  @Bind()
  renderForm() {
    const {
      form: { getFieldDecorator },
    } = this.props;
    const { fieldName } = this.props;
    return (
      <Form>
        <Row gutter={24}>
          <Col span={24}>
            <FormItem wrapperCol={{ span: 24 }}>
              {getFieldDecorator('sql', {
                initialValue: fieldName,
              })(
                <TextArea
                  ref={(node) => {
                    this.textArea = node;
                  }}
                  rows={15}
                  placeholder={intl
                    .get(`${promptCode}.model.sqlExecute.sqlPlaceholder`)
                    .d('请输入SQL语句')}
                />
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const { executeSelected, executeAll } = this.props;
    const { exportPending } = this.state;
    return (
      <React.Fragment>
        <Header
          style={{ flex: 'none' }}
          title={intl.get(`${promptCode}.view.message.title.sqlExecute`).d('SQL执行器')}
        >
          <Button
            icon="caret-right"
            type="primary"
            style={{ marginLeft: 8 }}
            onClick={this.handleExecuteAll}
            loading={executeAll}
          >
            {intl.get(`${promptCode}.view.option.executeAll`).d('运行')}
          </Button>
          <Button
            icon="caret-right"
            type="primary"
            style={{ marginLeft: 8 }}
            onClick={this.handleExecuteSelected}
            loading={executeSelected}
          >
            {intl.get(`${promptCode}.view.option.executeSelected`).d('运行已选择的')}
          </Button>
          <Button
            icon="minus"
            type="primary"
            style={{ marginLeft: 8 }}
            onClick={() => this.handleFormReset()}
          >
            {intl.get('hzero.common.button.clean').d('清除')}
          </Button>
          <Button
            icon="export"
            type="primary"
            style={{ marginLeft: 8 }}
            onClick={this.handleExportResult}
            loading={exportPending}
          >
            {intl.get(`${promptCode}.view.option.export`).d('导出结果')}
          </Button>
        </Header>
        <div className={style['execute-form']}>{this.renderForm()}</div>
      </React.Fragment>
    );
  }
}
