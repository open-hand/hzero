/**
 * @since 2020-1-5
 * @author MLF <linfeng.miao@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form, Row, Col, Button, Select } from 'hzero-ui';
import intl from 'utils/intl';
import ConditionItem from './ConditionItem';

export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      relationList: [
        {
          value: 'must',
          meaning: 'must',
        },
        {
          value: 'should',
          meaning: 'should',
        },
        {
          value: 'must_not',
          meaning: 'must_not',
        },
      ],
    };
  }

  componentDidMount() {}

  render() {
    const { form, groupItem = {}, indexList = [] } = this.props;
    const { getFieldDecorator } = form;
    const { relationList } = this.state;
    const formLayout = {
      labelCol: { span: 2 },
      wrapperCol: { span: 16 },
    };
    return (
      <React.Fragment>
        <Form.Item
          {...formLayout}
          label={intl.get('hsrh.inquiryConfig.model.inquiryConfig.relation').d('关系')}
        >
          {getFieldDecorator(`${groupItem.id}-relation`, {
            initialValue: groupItem.relation,
          })(
            <Select style={{ width: '100%' }}>
              {(relationList || []).map(items => (
                <Select.Option value={items.value} key={items.value}>
                  {items.meaning}
                </Select.Option>
              ))}
            </Select>
          )}
        </Form.Item>
        {(groupItem.conditionList || []).map((item, index) => {
          const props = {
            item,
            form,
            indexList,
          };
          return (
            <React.Fragment>
              <Row>
                <Col span={21}>
                  <ConditionItem {...props} />
                </Col>
                {
                  <Col span={1} style={{ lineHeight: 3 }}>
                    <Button
                      icon="minus"
                      onClick={() => this.props.handConditions('delete', groupItem.id, item.id)}
                    />
                  </Col>
                }
                {(groupItem.conditionList || []).length - index > 1 ? null : (
                  <Col span={1} style={{ lineHeight: 3, paddingLeft: 15 }}>
                    <Button
                      icon="plus"
                      onClick={() => this.props.handConditions('add', groupItem.id)}
                    />
                  </Col>
                )}
              </Row>
            </React.Fragment>
          );
        })}
      </React.Fragment>
    );
  }
}
