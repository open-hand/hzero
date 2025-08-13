import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import uuidv4 from 'uuid/v4';
import { Modal, Form, Input, Select, InputNumber, Row, Col, Tooltip, Icon } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import intl from 'utils/intl';
/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const { TextArea } = Input;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};
/**
 * 参数-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  /**
   * state初始化
   */
  state = {};

  /**
   * 组件属性定义
   */
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    anchor: 'right',
    title: '',
    visible: false,
    onOk: (e) => e,
    onCancel: (e) => e,
  };

  /**
   * 确定操作
   */
  @Bind()
  saveBtn() {
    const { form, onOk, onEditOk, itemData } = this.props;
    const { uuid = uuidv4() } = itemData;
    if (onOk) {
      form.validateFields((err, values) => {
        if (isEmpty(err)) {
          // 校验通过，进行保存操作
          if (isEmpty(itemData)) {
            onOk({ ...values, uuid });
          } else {
            onEditOk({ ...values, uuid });
          }
        }
      });
    }
  }

  /**
   * 序号唯一性校验
   * @param {!object} rule - 规则
   * @param {!string} value - 表单值
   * @param {!Function} callback
   */
  @Bind()
  checkUnique(rule, value, callback) {
    const { onCheckUnique } = this.props;
    if (!isEmpty(value)) {
      onCheckUnique(rule, value, callback);
    } else {
      callback();
    }
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      anchor,
      visible,
      paramsTitle,
      form,
      sqlType,
      itemData,
      onCancel,
      formElement = [],
      dataSourceType = [],
      dataType = [],
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const ordinalRules = [
      {
        required: true,
        message: intl.get('hzero.common.validation.notNull', {
          name: intl.get('hrpt.common.view.serialNumber').d('序号'),
        }),
      },
    ];
    const checkUniqueRule = {
      validator: this.checkUnique,
    };
    if (isEmpty(itemData)) {
      ordinalRules.push(checkUniqueRule);
    }
    const filterData = dataSourceType.find(
      (item) =>
        (item.value === itemData.dataSource || item.value === form.getFieldValue('dataSource')) &&
        item.value !== 'none'
    );
    const filterDataSource =
      sqlType === 'A' ? dataSourceType.filter((item) => item.value !== 'sql') : dataSourceType;
    const contentTool = filterData
      ? intl
          .get('hrpt.reportDataSet.view.message.filterDataMeaning', {
            filterDataMeaning: filterData.meaning,
          })
          .d(`来源值为${filterData.meaning}`)
      : intl.get('hrpt.reportDataSet.view.message.noContent').d('无内容，非必输');
    return (
      <Modal
        destroyOnClose
        title={paramsTitle}
        width={750}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.saveBtn}
        onCancel={onCancel}
      >
        <Form>
          <Row type="flex">
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.common.view.serialNumber').d('序号')}
                {...formLayout}
              >
                {getFieldDecorator('ordinal', {
                  rules: ordinalRules,
                  validateTrigger: 'onBlur',
                  initialValue: itemData.ordinal,
                })(<InputNumber min={0} style={{ width: '100%' }} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.dataType').d('数据类型')}
                {...formLayout}
              >
                {getFieldDecorator('dataType', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDataSet.model.reportDataSet.dataType')
                          .d('数据类型'),
                      }),
                    },
                  ],
                  initialValue: itemData.dataType,
                })(
                  <Select>
                    {dataType &&
                      dataType.map((item) => (
                        <Select.Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.paramsName').d('参数名')}
                {...formLayout}
              >
                {getFieldDecorator('name', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDataSet.model.reportDataSet.paramsName')
                          .d('参数名'),
                      }),
                    },
                  ],
                  initialValue: itemData.name,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.text').d('标题')}
                {...formLayout}
              >
                {getFieldDecorator('text', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hrpt.reportDataSet.model.reportDataSet.text').d('标题'),
                      }),
                    },
                  ],
                  initialValue: itemData.text,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.defaultValue').d('默认值')}
                {...formLayout}
              >
                {getFieldDecorator('defaultValue', {
                  initialValue: itemData.defaultValue,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl
                  .get('hrpt.reportDataSet.model.reportDataSet.defaultText')
                  .d('默认值显示')}
                {...formLayout}
              >
                {getFieldDecorator('defaultText', {
                  initialValue: itemData.defaultText,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.dataSource').d('来源类型')}
                {...formLayout}
              >
                {getFieldDecorator('dataSource', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDataSet.model.reportDataSet.dataSource')
                          .d('来源类型'),
                      }),
                    },
                  ],
                  initialValue: itemData.dataSource,
                })(
                  <Select
                    onChange={(value) => {
                      if (value === 'none') {
                        form.setFields({ content: { value: '', errors: null } });
                      }
                    }}
                  >
                    {dataSourceType &&
                      filterDataSource.map((item) => (
                        <Select.Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.formElement').d('表单控件')}
                {...formLayout}
              >
                {getFieldDecorator('formElement', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDataSet.model.reportDataSet.formElement')
                          .d('表单控件'),
                      }),
                    },
                  ],
                  initialValue: itemData.formElement,
                })(
                  <Select>
                    {formElement &&
                      formElement.map((item) => (
                        <Select.Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                label={
                  <>
                    <span>
                      {intl.get('hrpt.reportDataSet.model.reportDataSet.content').d('来源值')}
                    </span>
                    <Tooltip title={contentTool}>
                      <Icon type="question-circle-o" style={{ marginLeft: 5, marginTop: 5 }} />
                    </Tooltip>
                  </>
                }
                labelCol={{ span: 3 }}
                wrapperCol={{ span: 19 }}
              >
                {getFieldDecorator('content', {
                  rules: [
                    {
                      required: getFieldValue('dataSource') !== 'none',
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDataSet.model.reportDataSet.content')
                          .d('来源值'),
                      }),
                    },
                  ],
                  initialValue: itemData.content,
                })(<TextArea autosize={{ minRows: 4 }} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.width').d('宽度')}
                {...formLayout}
              >
                {getFieldDecorator('width', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hrpt.reportDataSet.model.reportDataSet.width').d('宽度'),
                      }),
                    },
                  ],
                  initialValue: itemData.width ? itemData.width : 200,
                })(<InputNumber min={0} style={{ width: '50%' }} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.height').d('高度')}
                {...formLayout}
              >
                {getFieldDecorator('height', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hrpt.reportDataSet.model.reportDataSet.height').d('高度'),
                      }),
                    },
                  ],
                  initialValue: itemData.height ? itemData.height : 28,
                })(<InputNumber min={0} style={{ width: '50%' }} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDataSet.model.reportDataSet.isRequired').d('是否必输')}
                {...formLayout}
              >
                {getFieldDecorator('isRequired', {
                  initialValue: isEmpty(itemData) ? 1 : itemData.isRequired,
                })(<Switch />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
