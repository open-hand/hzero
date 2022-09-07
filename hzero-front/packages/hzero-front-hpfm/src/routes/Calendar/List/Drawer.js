import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Form, Input, Modal } from 'hzero-ui';
import { isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

/**
 * 日历定义-数据修改滑窗(抽屉)
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
    onOk: e => e,
    onCancel: e => e,
  };

  /**
   * 确定操作
   */
  @Bind()
  saveBtn() {
    const { form, onOk, targetItem } = this.props;
    if (onOk) {
      form.validateFields((err, values) => {
        if (!err) {
          // 校验通过，进行保存操作
          onOk({ ...targetItem, ...values, ...this.state });
        }
      });
    }
  }

  @Bind()
  handleChangeCountry(value, item) {
    this.setState({ countryCode: item.countryCode, countryId: item.countryId });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { anchor, visible, title, form, loading, tenantId, targetItem, onCancel } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={title}
        width={500}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        confirmLoading={loading}
        onOk={this.saveBtn}
        onCancel={onCancel}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <Form.Item
            label={intl.get('hpfm.calendar.model.calendar.calendarName').d('描述')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('calendarName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.calendar.model.calendar.calendarName').d('描述'),
                  }),
                },
              ],
              initialValue: targetItem.calendarName,
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.calendar.model.calendar.country').d('国家/地区')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('countryCode', {
              initialValue: targetItem.countryCode,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.calendar.model.calendar.country').d('国家/地区'),
                  }),
                },
              ],
            })(
              <Lov
                disabled={!isUndefined(targetItem.countryCode)}
                code="HPFM.COUNTRY"
                queryParams={{ tenantId, enabledFlag: 1 }}
                textValue={targetItem.countryName}
                onChange={this.handleChangeCountry}
              />
            )}
          </Form.Item>
          <Form.Item label={intl.get('hzero.common.status.enable').d('启用')} {...MODAL_FORM_ITEM_LAYOUT}>
            {getFieldDecorator('enabledFlag', {
              initialValue: isUndefined(targetItem.enabledFlag) ? 1 : targetItem.enabledFlag,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
