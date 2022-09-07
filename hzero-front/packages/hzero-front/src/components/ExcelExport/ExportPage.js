/**
 * ExportPage - 导出界面 - 将导出界面从 index.js 中提取出来
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/9/27
 * @copyright 2019/9/27 © HAND
 */

import React, { Component } from 'react';
import {
  Col,
  Divider,
  Form,
  InputNumber,
  Row,
  Select,
  Spin,
  Tree,
  Input,
  Tooltip,
  Icon,
} from 'hzero-ui';

import intl from 'utils/intl';

import './index.less';

export default class ExportPage extends Component {
  render() {
    const {
      exportTypeList,
      exportList,
      fetchColumnLoading,
      formItemLayout,
      queryFormItem,
      form,
      checkedKeys,
      expandedKeys,
      renderQueryForm,
      renderTreeNodes,
      onExpand,
      onSelect,
      enableAsync,
      exportAsync = false,
      showAsync,
      defaultRequestMode,
    } = this.props;
    return (
      <Spin spinning={fetchColumnLoading}>
        <>
          <Form>
            <Row>
              <Col span={12}>
                <Form.Item
                  {...formItemLayout}
                  label={intl.get(`hzero.common.components.export.file`).d('自定义文件名')}
                >
                  {form.getFieldDecorator('fileName', {})(<Input />)}
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  {...formItemLayout}
                  label={intl.get(`hzero.common.components.export.type`).d('导出类型')}
                >
                  {form.getFieldDecorator('fillerType', {
                    initialValue:
                      exportTypeList.length > 0 ? exportTypeList[0].value : 'single-sheet',
                  })(
                    <Select>
                      {exportTypeList.map((item) => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </Form.Item>
              </Col>
            </Row>
            <Row>
              {showAsync && (
                <Col span={12}>
                  <Form.Item
                    {...formItemLayout}
                    label={intl.get(`hzero.common.components.export.async`).d('异步')}
                  >
                    {form.getFieldDecorator('async', {
                      initialValue: defaultRequestMode === 'ASYNC' ? 'true' : 'false',
                    })(
                      <Select disabled={!exportAsync || !enableAsync}>
                        <Select.Option value="false" key="false">
                          {intl.get('hzero.common.status.no').d('否')}
                        </Select.Option>
                        <Select.Option value="true" key="true">
                          {intl.get('hzero.common.status.yes').d('是')}
                        </Select.Option>
                      </Select>
                    )}
                  </Form.Item>
                </Col>
              )}
              <Col span={12}>
                <Form.Item
                  {...formItemLayout}
                  label={
                    <span>
                      {intl.get(`hzero.common.components.export.singleSheet`).d('单sheet最大行数')}
                      &nbsp;
                      <Tooltip
                        title={intl
                          .get('hzero.common.components.export.singleSheetTip')
                          .d(
                            '限制excel中单个sheet页的数据量，当数据量超过单sheet页最大数量时，会自动分片到下一个sheet页。'
                          )}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {form.getFieldDecorator('singleSheetMaxRow', {
                    initialValue: 1048575,
                  })(
                    <InputNumber min={1} max={1048575} precision={0} style={{ width: '138px' }} />
                  )}
                </Form.Item>
              </Col>
              {!showAsync && (
                <Col span={12}>
                  <Form.Item
                    {...formItemLayout}
                    label={
                      <span>
                        {intl.get(`hzero.common.components.export.maxSheet`).d('文件最大sheet数')}
                        &nbsp;
                        <Tooltip
                          title={intl
                            .get('hzero.common.components.export.maxSheetTip')
                            .d(
                              '限制单个excel的sheet页数量，当数据量超过一个excel时，会分片成多个excel，以压缩包形式导出。'
                            )}
                        >
                          <Icon type="question-circle-o" />
                        </Tooltip>
                      </span>
                    }
                  >
                    {form.getFieldDecorator('singleExcelMaxSheetNum', {
                      initialValue: 5,
                    })(<InputNumber min={1} precision={0} style={{ width: '138px' }} />)}
                  </Form.Item>
                </Col>
              )}
            </Row>
            <Row>
              {showAsync && (
                <Col span={12}>
                  <Form.Item
                    {...formItemLayout}
                    label={
                      <span>
                        {intl.get(`hzero.common.components.export.maxSheet`).d('文件最大sheet数')}
                        &nbsp;
                        <Tooltip
                          title={intl
                            .get('hzero.common.components.export.maxSheetTip')
                            .d(
                              '限制单个excel的sheet页数量，当数据量超过一个excel时，会分片成多个excel，以压缩包形式导出。'
                            )}
                        >
                          <Icon type="question-circle-o" />
                        </Tooltip>
                      </span>
                    }
                  >
                    {form.getFieldDecorator('singleExcelMaxSheetNum', {
                      initialValue: 5,
                    })(<InputNumber min={1} precision={0} style={{ width: '138px' }} />)}
                  </Form.Item>
                </Col>
              )}
            </Row>
          </Form>
        </>
        <Divider />
        {queryFormItem && (
          <>
            <div style={{ margin: '12px auto' }}>
              {intl.get(`hzero.common.components.export.condition`).d('设置导出条件')}
            </div>
            {renderQueryForm()}
          </>
        )}
        <div style={{ margin: '12px auto' }}>
          {intl.get(`hzero.common.components.export.columns`).d('选择要导出的列')}
        </div>
        <Tree
          checkable
          onExpand={onExpand}
          expandedKeys={expandedKeys}
          defaultExpandedKeys={expandedKeys}
          onCheck={onSelect}
          checkedKeys={checkedKeys}
        >
          {renderTreeNodes(exportList)}
        </Tree>
      </Spin>
    );
  }
}
