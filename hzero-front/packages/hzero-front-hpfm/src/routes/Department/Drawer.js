import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import { Form, Input, InputNumber, Modal } from 'hzero-ui';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import Lov from 'components/Lov';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

/**
 * 部门维护-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 部门实体
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
    anchor: 'left',
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
    const { form, onOk, itemData } = this.props;
    if (onOk) {
      form.validateFields((err, values) => {
        if (!err) {
          // 校验通过，进行保存操作
          onOk({ ...itemData, ...values });
        }
      });
    }
  }

  /**
   * 取消操作
   */
  @Bind()
  cancelBtn() {
    this.props.onCancel();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { anchor, title, visible, form, loading, itemData } = this.props;
    const { getFieldDecorator } = form;
    const formLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 12 },
    };
    return (
      <Modal
        destroyOnClose
        title={title}
        width={450}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        confirmLoading={loading}
        onOk={this.saveBtn}
        onCancel={this.cancelBtn}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <Form.Item label={intl.get('entity.department.code').d('部门编码')} {...formLayout}>
            {getFieldDecorator('unitCode', {
              initialValue: itemData.unitCode,
            })(<Input disabled />)}
          </Form.Item>
          <Form.Item label={intl.get('entity.department.name').d('部门名称')} {...formLayout}>
            {getFieldDecorator('unitName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('entity.department.name').d('部门名称'),
                  }),
                },
              ],
              initialValue: itemData.unitName,
            })(
              <TLEditor
                label={intl.get('entity.department.name').d('部门名称')}
                field="unitName"
                token={itemData._token}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.department.model.department.quickIndex').d('快速索引')}
            {...formLayout}
          >
            {getFieldDecorator('quickIndex', {
              initialValue: itemData.quickIndex,
              rules: [
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.department.model.department.quickIndex').d('快速索引')}
                field="quickIndex"
                token={itemData._token}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.department.model.department.phoneticize').d('拼音')}
            {...formLayout}
          >
            {getFieldDecorator('phoneticize', {
              initialValue: itemData.phoneticize,
              rules: [
                {
                  max: 60,
                  message: intl.get('hzero.common.validation.max', {
                    max: 60,
                  }),
                },
              ],
            })(<Input inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.department.model.unit.parentUnit').d('上级部门')}
            {...formLayout}
          >
            {getFieldDecorator('parentUnitId', {
              initialValue: itemData.parentUnitId,
            })(
              <Lov
                code="HPFM.UNIT.DEPARTMENT"
                textValue={itemData.parentUnitName}
                queryParams={{
                  enabledFlag: 1,
                  organizationId: itemData.tenantId,
                  // levelPath: itemData.levelPath,
                  unitId: itemData.unitId,
                  unitCompanyId: itemData.unitCompanyId,
                }}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.common.model.common.orderSeq').d('排序号')}
            {...formLayout}
          >
            {getFieldDecorator('orderSeq', {
              initialValue: itemData.orderSeq,
            })(<InputNumber style={{ width: '100%' }} min={1} precision={0} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.department.model.unit.enableBudgetFlag').d('是否启用预算')}
            {...formLayout}
          >
            {getFieldDecorator('enableBudgetFlag', {
              initialValue: itemData.enableBudgetFlag,
            })(<Switch />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.department.model.unit.costName').d('所属成本中心')}
            {...formLayout}
          >
            {getFieldDecorator('costCode', {
              initialValue: itemData.costCode,
            })(
              <Lov
                code="HPFM.COST_CENTER"
                textValue={itemData.costName}
                textField="costName"
                queryParams={{
                  tenantId: getCurrentOrganizationId(),
                }}
              />
            )}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
