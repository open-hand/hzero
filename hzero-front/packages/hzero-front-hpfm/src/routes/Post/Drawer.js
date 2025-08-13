import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, Input, InputNumber } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import TLEditor from 'components/TLEditor';
import intl from 'utils/intl';
import Switch from 'components/Switch';
import Lov from 'components/Lov';
/**
 * Form.Item 组件label、wrapper长度比例划分
 */

/**
 * 岗位维护-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 岗位实体
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
    onOk: e => e,
    onCancel: e => e,
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
    const { anchor, title, visible, loading, form, itemData } = this.props;
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
          <Form.Item label={intl.get('entity.position.code').d('岗位编码')} {...formLayout}>
            {getFieldDecorator('positionCode', {
              initialValue: itemData.positionCode,
            })(<Input disabled />)}
          </Form.Item>
          <Form.Item label={intl.get('entity.position.name').d('岗位名称')} {...formLayout}>
            {getFieldDecorator('positionName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('entity.position.name').d('岗位名称'),
                  }),
                },
              ],
              initialValue: itemData.positionName,
            })(
              <TLEditor
                label={intl.get('entity.position.name').d('岗位名称')}
                field="positionName"
                token={itemData._token}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.position.model.position.managementPositionName').d('上级岗位')}
            {...formLayout}
          >
            {getFieldDecorator('parentPositionId', {
              initialValue: itemData.parentPositionId,
            })(
              <Lov
                code="HPFM.PARENT_POSITION"
                textValue={itemData.parentPositionName}
                queryParams={{ positionId: itemData.positionId }}
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
            label={intl.get('hpfm.position.model.position.supervisorFlag').d('主管岗位')}
            {...formLayout}
          >
            {getFieldDecorator('supervisorFlag', {
              initialValue: itemData.supervisorFlag,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
