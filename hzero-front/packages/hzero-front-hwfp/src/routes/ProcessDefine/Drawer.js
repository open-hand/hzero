import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { trim, omit } from 'lodash';
import { Form, Input, Modal, Select, InputNumber, Switch } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import intl from 'utils/intl';

import styles from './style/index.less';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
/**
 * 流程定义-数据添加滑窗(抽屉)
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
    const { form, onOk } = this.props;
    if (onOk) {
      form.validateFields((err, values) => {
        if (!err) {
          const { overtime } = values;
          let params = values;
          if (!overtime) {
            params = omit(params, ['overtime', 'overtimeUnit']);
          }
          // 校验通过，进行保存操作
          const key = trim(params.key);
          onOk({ ...params, key });
        }
      });
    }
  }

  @Bind()
  checkUnique(rule, value, callback) {
    const { onCheck } = this.props;
    onCheck({ key: value }).then((res) => {
      if (res && res.failed) {
        callback(
          intl.get('hwfp.common.view.validation.code.exist').d('编码已存在，请输入其他编码')
        );
      }
      callback();
    });
  }

  @Bind()
  handleCategoryChange(value) {
    const {
      onFetchDocuments,
      form: { resetFields },
    } = this.props;
    if (value) onFetchDocuments(value);
    resetFields(['documentId']);
  }

  render() {
    const {
      anchor,
      title,
      visible,
      form,
      onCancel,
      saving,
      documents = [],
      isSiteFlag,
      tenantId,
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    return (
      <Modal
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        confirmLoading={saving}
        onOk={this.saveBtn}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        onCancel={onCancel}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
        destroyOnClose
      >
        <Form>
          <Form.Item
            label={intl.get('hwfp.common.model.process.code').d('流程编码')}
            {...formLayout}
          >
            {getFieldDecorator('key', {
              // validateFirst: true,
              // validateTrigger: 'onBlur',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.process.code').d('流程编码'),
                  }),
                },
                {
                  max: 60,
                  message: intl.get('hzero.common.validation.max', {
                    max: 60,
                  }),
                },
                {
                  pattern: /^[A-Z][A-Z0-9-_./]*$/,
                  message: intl
                    .get('hzero.common.validation.codeUpperBegin')
                    .d('全大写及数字，必须以字母开头，可包含“-”、“_”、“.”、“/”'),
                },
              ],
            })(<Input trim typeCase="upper" inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.process.name').d('流程名称')}
            {...formLayout}
          >
            {getFieldDecorator('name', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.process.name').d('流程名称'),
                  }),
                },
                {
                  max: 240,
                  message: intl.get('hzero.common.validation.max', {
                    max: 240,
                  }),
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.process.class').d('流程分类')}
            {...formLayout}
          >
            {getFieldDecorator('category', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.process.class').d('流程分类'),
                  }),
                },
              ],
            })(
              <Lov
                code="HWFP.PROCESS_CATEGORY"
                queryParams={isSiteFlag ? {} : { tenantId }}
                onChange={this.handleCategoryChange}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.documents.class').d('流程单据')}
            {...formLayout}
          >
            {getFieldDecorator('documentId', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.documents.class').d('流程单据'),
                  }),
                },
              ],
            })(
              <Select allowClear style={{ width: '100%' }} disabled={!getFieldValue('category')}>
                {documents.map((item) => (
                  <Select.Option value={item.value} key={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          {!isSiteFlag && (
            <Form.Item
              className={styles['inline-form-item']}
              label={intl.get('hwfp.processDefine.model.processDefine.timeout').d('超时时间')}
              {...formLayout}
            >
              <span>
                {getFieldDecorator('overtime', {
                  rules: [
                    {
                      required: getFieldValue('overtimeEnabled'),
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hwfp.processDefine.model.processDefine.timeout')
                          .d('超时时间'),
                      }),
                    },
                  ],
                })(<InputNumber min={0} />)}
              </span>
              <span>
                {getFieldDecorator('overtimeUnit', {
                  initialValue: 'hour',
                })(
                  <Select allowClear style={{ width: '30%' }}>
                    <Select.Option value="hour">
                      {intl.get('hzero.common.date.unit.hours').d('小时')}
                    </Select.Option>
                    <Select.Option value="day">
                      {intl.get('hzero.common.date.unit.day').d('小时')}
                    </Select.Option>
                  </Select>
                )}
              </span>
            </Form.Item>
          )}
          {!isSiteFlag && (
            <Form.Item
              label={intl
                .get('hwfp.processDefine.model.processDefine.overtimeEnabled')
                .d('超时设置启用')}
              {...formLayout}
            >
              {getFieldDecorator('overtimeEnabled', {
                initialValue: 0,
              })(<Switch checkedValue={1} unCheckedValue={0} />)}
            </Form.Item>
          )}
          <Form.Item
            label={intl.get('hwfp.common.model.common.description').d('描述')}
            {...formLayout}
          >
            {getFieldDecorator('description', {
              rules: [
                {
                  max: 240,
                  message: intl.get('hzero.common.validation.max', {
                    max: 240,
                  }),
                },
              ],
            })(<Input.TextArea autosize={{ minRows: 2, maxRows: 6 }} />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
