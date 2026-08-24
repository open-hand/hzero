import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, Input, InputNumber, Select, Row, Col } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import Switch from 'components/Switch';
import intl from 'utils/intl';
/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};
const { TextArea } = Input;
const { Option } = Select;
/**
 * 列信息-数据修改滑窗(抽屉)
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
export default class MetaColumnsDrawer extends PureComponent {
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
    const { form, onOk, onEditOk, isCreateMetaColumn } = this.props;
    if (onOk) {
      form.validateFields((err, values) => {
        if (isEmpty(err)) {
          // 校验通过，进行保存操作
          if (isCreateMetaColumn) {
            onOk(values);
          } else {
            onEditOk(values);
          }
        }
      });
    }
  }

  /**
   * 列信息-序号唯一性校验
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
      metadataTitle,
      form,
      onCancel,
      type = [],
      sortType = [],
      itemData = {},
      isCreateMetaColumn,
    } = this.props;
    const { getFieldDecorator } = form;
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
    if (isCreateMetaColumn) {
      ordinalRules.push(checkUniqueRule);
    }
    return (
      <Modal
        destroyOnClose
        title={metadataTitle}
        width={850}
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
                })(<InputNumber min={0} disabled={!isEmpty(itemData)} style={{ width: '100%' }} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl
                  .get('hrpt.reportDefinition.model.reportDefinition.dataType')
                  .d('数据类型')}
                {...formLayout}
              >
                {getFieldDecorator('dataType', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDefinition.model.reportDefinition.dataType')
                          .d('数据类型'),
                      }),
                    },
                  ],
                  initialValue: itemData.dataType,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDefinition.model.reportDefinition.name').d('列名')}
                {...formLayout}
              >
                {getFieldDecorator('name', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDefinition.model.reportDefinition.name')
                          .d('列名'),
                      }),
                    },
                  ],
                  initialValue: itemData.name,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDefinition.model.reportDefinition.text').d('标题')}
                {...formLayout}
              >
                {getFieldDecorator('text', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDefinition.model.reportDefinition.text')
                          .d('标题'),
                      }),
                    },
                  ],
                  initialValue: itemData.text,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDefinition.model.reportDefinition.type').d('列类型')}
                {...formLayout}
              >
                {getFieldDecorator('type', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDefinition.model.reportDefinition.type')
                          .d('列类型'),
                      }),
                    },
                  ],
                  initialValue: itemData.type,
                })(
                  <Select allowClear>
                    {type &&
                      type.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl
                  .get('hrpt.reportDefinition.model.reportDefinition.sortType')
                  .d('排序类型')}
                {...formLayout}
              >
                {getFieldDecorator('sortType', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDefinition.model.reportDefinition.sortType')
                          .d('排序类型'),
                      }),
                    },
                  ],
                  initialValue: itemData.sortType,
                })(
                  <Select allowClear>
                    {sortType &&
                      sortType.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl
                  .get('hrpt.reportDefinition.model.reportDefinition.linkReportCode')
                  .d('链接报表代码')}
                {...formLayout}
              >
                {getFieldDecorator('linkReportCode', {
                  rules: [
                    {
                      max: 50,
                      message: intl.get('hzero.common.validation.max', {
                        max: 50,
                      }),
                    },
                  ],
                  initialValue: itemData.linkReportCode,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl
                  .get('hrpt.reportDefinition.model.reportDefinition.linkReportParam')
                  .d('链接报表参数名')}
                {...formLayout}
              >
                {getFieldDecorator('linkReportParam', {
                  initialValue: itemData.linkReportParam,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl
                  .get('hrpt.reportDefinition.model.reportDefinition.format')
                  .d('格式掩码')}
                {...formLayout}
              >
                {getFieldDecorator('format', {
                  initialValue: itemData.format,
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                label={intl
                  .get('hrpt.reportDefinition.model.reportDefinition.expression')
                  .d('表达式')}
                labelCol={{ span: 3 }}
                wrapperCol={{ span: 19 }}
              >
                {getFieldDecorator('expression', {
                  rules: [
                    {
                      required: false,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDefinition.model.reportDefinition.expression')
                          .d('表达式'),
                      }),
                    },
                  ],
                  initialValue: itemData.expression,
                })(<TextArea autosize={{ minRows: 4 }} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDefinition.model.reportDefinition.width').d('宽度')}
                {...formLayout}
              >
                {getFieldDecorator('width', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDefinition.model.reportDefinition.width')
                          .d('宽度'),
                      }),
                    },
                  ],
                  initialValue: itemData.width || itemData.width === 0 ? itemData.width : 100,
                })(<InputNumber min={0} style={{ width: '50%' }} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDefinition.model.reportDefinition.decimals').d('精度')}
                {...formLayout}
              >
                {getFieldDecorator('decimals', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.reportDefinition.model.reportDefinition.decimals')
                          .d('精度'),
                      }),
                    },
                  ],
                  initialValue: itemData.decimals,
                })(<InputNumber min={0} style={{ width: '50%' }} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDefinition.model.reportDefinition.percent').d('百分比')}
                {...formLayout}
              >
                {getFieldDecorator('percent', {
                  initialValue: isEmpty(itemData) ? 1 : itemData.percent,
                })(<Switch />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hrpt.reportDefinition.model.reportDefinition.hidden').d('隐藏')}
                {...formLayout}
              >
                {getFieldDecorator('hidden', {
                  initialValue: isEmpty(itemData) ? 1 : itemData.hidden,
                })(<Switch />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
