/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-08 13:50:54
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 可选项组件
 */
import React from 'react';
import { Col, DataSet, Row, TextField } from 'choerodon-ui/pro';
import { Form, Icon } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';

import intl from 'utils/intl';

import { DATA_TYPE } from '@/utils/constants';

import styles from './index.less';
import ColorPopover from './ColorPopover';

export default class EnumOptions extends React.Component {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.dataSet = new DataSet({
      fields: [
        {
          name: 'color',
          label: intl.get('hiot.common.color').d('颜色'),
        },
        {
          name: 'value',
          label: intl.get('hiot.common.value').d('值'),
        },
        {
          name: 'pointName',
          label: intl.get('hiot.common.name').d('名称'),
        },
      ],
    });
    this.state = {
      options: props.optionsSource || [
        {
          key: uuidv4(),
          color: '#07bd15',
          code: '1',
          name: '',
          select: false,
        },
        {
          key: uuidv4(),
          color: '#ff0e09',
          code: '0',
          name: '',
          select: false,
        },
      ],
    };
  }

  // 删除可选项
  @Bind()
  deleteOption(key) {
    const { options = [] } = this.state;
    this.setState({
      options: options.filter((v) => v.key !== key),
    });
  }

  // 增加可选项
  @Bind()
  addOption() {
    const { type } = this.props;
    if (type === DATA_TYPE.BOOL) {
      return;
    }
    const { options = [] } = this.state;
    const option = {
      key: uuidv4(),
      color: '#000',
      select: false, // 告警条件 是否启用
      codeStatus: '',
      codeMessage: '',
      nameStatus: '',
      nameMessage: '',
    };
    const newOptions = [...options, option];
    this.setState({
      options: newOptions,
    });
  }

  // 修改颜色
  @Bind()
  handleChange(key, optionKey, optionValue, verifyFlag = false) {
    const { optionsSource } = this.props;
    let { options } = this.state;
    options = options || optionsSource;
    const optionObj = {};
    const repeatIndex = [];
    const values = options.map((v, index) => {
      const val = v;
      if (val.key === key) {
        val[optionKey] = optionValue;
        if (verifyFlag) {
          val[`${optionKey}Status`] = 'success';
          val[`${optionKey}Message`] = '';
          if (optionValue === '') {
            val[`${optionKey}Status`] = 'error';
            val[`${optionKey}Message`] = intl
              .get('hiot.common.view.message.nameNotRepeat')
              .d('名称不能重复');
          }
        }
      }
      // 如果option中存在val[optionKey]，当前值重复，记录index.
      if ({}.hasOwnProperty.call(optionObj, val[optionKey])) {
        repeatIndex.push(optionObj[val[optionKey]]);
        repeatIndex.push(index);
      } else {
        optionObj[val[optionKey]] = index;
      }
      return val;
    });
    if (verifyFlag) {
      values.forEach((item, index) => {
        if (repeatIndex.includes(index)) {
          /* eslint-disable no-param-reassign */
          item[`${optionKey}Status`] = 'error';
          item[`${optionKey}Message`] = intl
            .get('hiot.common.view.message.valueNotRepeat')
            .d('值不能重复');
        } else if (item[optionKey] !== '') {
          item[`${optionKey}Status`] = 'success';
          item[`${optionKey}Message`] = '';
        }
      });
    }
    this.setState({
      options: values,
    });
  }

  render() {
    const { disabled = false, type } = this.props;
    const { options } = this.state;
    return (
      <Row gutter={16} type="flex" className={styles['content-wrap']}>
        <Col className={styles['enum-option-label']}>
          {intl.get('hiot.dataPointTemplate.model.dpt.selectItem').d('可选项')}
        </Col>
        <Col>
          <div
            className={styles['hiot-enum-options']}
            style={{ overflowY: 'auto', height: 280, maxHeight: 410, width: '350px' }}
          >
            <div>
              <Icon
                type="add_box"
                style={{
                  fontSize: 20,
                  marginLeft: 9,
                  display: disabled || type === DATA_TYPE.BOOL ? 'none' : 'block',
                }}
                onClick={this.addOption}
              />
            </div>
            <Row gutter={6}>
              <Col span={3} className={styles['option-col']}>
                {intl.get('hiot.common.color').d('颜色')}
              </Col>
              <Col span={3} className={styles['option-col']}>
                {intl.get('hiot.common.value').d('值')}
              </Col>
              <Col span={7} className={styles['option-col']}>
                {intl.get('hiot.common.name').d('名称')}
              </Col>
              {/* {category !== CONTROL_TYPE && (
                <Col span={7} className={styles['option-col']}>
                  {intl.get('hiot.dataPointTemplate.model.dpt.warnRuleCheck').d('告警条件')}
                </Col>
              )} */}
              <Col span={4} className={styles['option-col']} />
            </Row>
            {options.map((option) => {
              const {
                key,
                code,
                color,
                name,
                codeStatus,
                codeMessage,
                nameStatus,
                nameMessage,
                // select,
              } = option;
              return (
                <Form>
                  <Row className={styles.options} key={key} gutter={6}>
                    <Col span={3} className={styles['option-col']}>
                      {disabled ? (
                        <div
                          className={styles['options-color']}
                          style={{ backgroundColor: color, cursor: 'default' }}
                        />
                      ) : (
                        <ColorPopover
                          color={option.color}
                          handleSubmitColor={(value) => {
                            this.handleChange(key, 'color', value);
                          }}
                        >
                          <div
                            className={styles['options-color']}
                            style={{ backgroundColor: color }}
                          />
                        </ColorPopover>
                      )}
                    </Col>
                    <Col span={3} className={styles['option-col']}>
                      <Form.Item validateStatus={codeStatus} help={codeMessage}>
                        <TextField
                          value={code}
                          disabled={disabled || type === DATA_TYPE.BOOL}
                          className={styles['option-code']}
                          onChange={(value) => {
                            if (type !== DATA_TYPE.BOOL) {
                              this.handleChange(key, 'code', value, true);
                            }
                          }}
                        />
                      </Form.Item>
                    </Col>
                    <Col span={7} className={styles['option-col']}>
                      <Form.Item validateStatus={nameStatus} help={nameMessage}>
                        <TextField
                          value={name}
                          disabled={disabled}
                          className={styles['option-name']}
                          onChange={(value) => {
                            this.handleChange(key, 'name', value, true);
                          }}
                        />
                      </Form.Item>
                    </Col>
                    {/* {category !== CONTROL_TYPE && (
                      <Col
                        span={7}
                        className={styles['option-col']}
                        style={{ alignItems: 'center' }}
                      >
                        <Form.Item>
                          <CheckBox
                            defaultChecked={select}
                            disabled={disabled}
                            className={styles['option-name']}
                            onChange={value => {
                              this.handleChange(key, 'select', value, true);
                            }}
                          />
                        </Form.Item>
                      </Col>
                    )} */}
                    <Col span={4}>
                      {type !== DATA_TYPE.BOOL && (
                        <Icon
                          type="remove_circle_outline"
                          className={styles['options-delete']}
                          onClick={() => {
                            if (disabled === true) return;
                            this.deleteOption(key);
                          }}
                        />
                      )}
                    </Col>
                  </Row>
                </Form>
              );
            })}
          </div>
        </Col>
      </Row>
    );
  }
}
