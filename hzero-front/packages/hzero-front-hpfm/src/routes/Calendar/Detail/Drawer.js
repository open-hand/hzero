import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Form, Input, Modal, Select } from 'hzero-ui';
import { DatePicker } from 'choerodon-ui';
import moment from 'moment';
import { isUndefined, startsWith } from 'lodash';
import { Bind } from 'lodash-decorators';
import TLEditor from 'components/TLEditor';
import intl from 'utils/intl';
import { DATETIME_MIN } from 'utils/constants';
/**
 * 公共假期-数据修改滑窗(抽屉)
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
    const { form, onOk, targetItem } = this.props;
    if (onOk) {
      form.validateFields((err, values) => {
        // debugger;
        if (!err) {
          // 校验通过，进行保存操作
          const { dateRange, keyDate, ...other } = values;
          const endDate = dateRange ? moment(dateRange[1]).format(DATETIME_MIN) : undefined;
          const startDate = dateRange ? moment(dateRange[0]).format(DATETIME_MIN) : undefined;
          const date = keyDate ? moment(keyDate).format(DATETIME_MIN) : undefined;
          onOk({ ...targetItem, ...other, endDate, startDate, keyDate: date });
        }
      });
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
      title,
      form,
      saveAddHolidayLoading,
      saveUpdateHolidayLoading,
      targetItem,
      onCancel,
      dateFormat,
      holidayType,
    } = this.props;
    const { getFieldDecorator, getFieldValue, setFieldsValue } = form;
    const formLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 12 },
    };
    return (
      <Modal
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.saveBtn}
        okButtonProps={{
          loading: isUndefined(targetItem.holidayId)
            ? saveAddHolidayLoading
            : saveUpdateHolidayLoading,
        }}
        onCancel={onCancel}
        destroyOnClose
      >
        <Form>
          <Form.Item
            label={intl.get('hpfm.calendar.model.calendar.holidayType').d('公休假期类型')}
            {...formLayout}
          >
            {getFieldDecorator('holidayType', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.calendar.model.calendar.holidayType').d('公休假期类型'),
                  }),
                },
              ],
              initialValue: targetItem.holidayType,
            })(
              <Select>
                {holidayType
                  .filter((item) => startsWith(item.value, 'OFFICIAL'))
                  .map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.calendar.model.calendar.holidayName').d('公休假期')}
            {...formLayout}
          >
            {getFieldDecorator('holidayName', {
              initialValue: targetItem.holidayName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.calendar.model.calendar.holidayName').d('公休假期'),
                  }),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', { max: 30 }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.calendar.model.calendar.holidayName').d('公休假期')}
                field="holidayName"
                // eslint-disable-next-line
                token={targetItem._token}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.calendar.model.calendar.dateRange').d('假期范围')}
            {...formLayout}
          >
            {getFieldDecorator('dateRange', {
              initialValue:
                !isUndefined(targetItem.startDate) && !isUndefined(targetItem.endDate)
                  ? [
                      moment(targetItem.startDate, dateFormat),
                      moment(targetItem.endDate, dateFormat),
                    ]
                  : [],
              rules: [
                {
                  required: true,
                  type: 'array',
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.calendar.model.calendar.dateRange').d('假期范围'),
                  }),
                },
              ],
            })(
              <DatePicker.RangePicker
                placeholder=""
                format={dateFormat}
                onChange={(date) => {
                  setFieldsValue({ keyDate: date[0] });
                }}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.calendar.model.calendar.keyDate').d('假期当天')}
            {...formLayout}
          >
            {getFieldDecorator('keyDate', {
              initialValue: !isUndefined(targetItem.keyDate)
                ? moment(targetItem.keyDate, dateFormat)
                : null,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.calendar.model.calendar.keyDate').d('假期当天'),
                  }),
                },
              ],
            })(
              <DatePicker
                placeholder=""
                format={dateFormat}
                style={{ width: '100%' }}
                disabledDate={(currentDate) =>
                  getFieldValue('dateRange') &&
                  (moment(getFieldValue(`dateRange`)[0]).isAfter(currentDate, 'day') ||
                    moment(getFieldValue(`dateRange`)[1]).isBefore(currentDate, 'day'))
                }
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.calendar.model.calendar.remark').d('说明')}
            {...formLayout}
          >
            {getFieldDecorator('remark', {
              initialValue: targetItem.remark,
            })(<Input.TextArea />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
