import React from 'react';
import { Form, TextField, DataSet, Select, Button } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import getLang from '@/langs/serviceLang';
import { SUBJECT } from '@/constants/constants';
import { assertionDS } from '@/stores/Services/interfaceDS';
import QuestionPopover from '@/components/QuestionPopover';

class AssertionCard extends React.Component {
  constructor(props) {
    super(props);
    this.assertionDS = new DataSet({
      ...assertionDS({
        onAddAssertionFormItem: this.addAssertionFormItem,
        onRemoveAssertionFormItem: this.removeAssertionFormItem,
        onFiledUpdate: this.handleFieldUpdate,
        onLoadAssertionForm: this.renderAssertionForm,
      }),
    });

    props.onRef(this);
    this.state = {
      formItems: [],
    };
  }

  /**
   * 渲染断言表单
   */
  @Bind()
  renderAssertionForm(dataSet = []) {
    let formItems = [];
    dataSet.forEach((record) => {
      formItems = this.renderFormItem(formItems, record);
    });
    this.setState({ formItems });
  }

  /**
   * 添加断言子表单
   */
  @Bind()
  addAssertionFormItem(record = {}) {
    let { formItems } = this.state;
    formItems = this.renderFormItem(formItems, record);
    this.setState({ formItems });
  }

  /**
   * 清空field字段
   */
  @Bind()
  handleClearField(val, record) {
    record.set('subject', val);
    if (!SUBJECT.includes(val)) {
      record.init('field', null);
    }
  }

  /**
   * 添加单行formItem
   */
  renderFormItem(items, record) {
    const { operatorList, assertionSubjects } = this.props;
    const formItems = items;
    formItems.push(
      <Select
        newLine
        required
        colSpan={5}
        label={getLang('SUBJECT')}
        value={record.get('subject')}
        onChange={(value) => record.set('subject', value)}
      >
        {assertionSubjects.map((item) => (
          <Select.Option value={item.value} key={item.value}>
            {item.meaning}
          </Select.Option>
        ))}
      </Select>
    );
    if (SUBJECT.includes(record.getState('subject')) || SUBJECT.includes(record.get('subject'))) {
      formItems.push(
        <TextField
          colSpan={6}
          label={getLang('FIELD')}
          value={record.get('field')}
          onChange={(value) => record.set('field', value)}
        />
      );
    }
    formItems.push(
      <Select
        required
        colSpan={6}
        label={getLang('CONDITION')}
        value={record.get('condition')}
        onChange={(value) => record.set('condition', value)}
      >
        {operatorList.map((item) => (
          <Select.Option value={item.value} key={item.value}>
            {item.meaning}
          </Select.Option>
        ))}
      </Select>
    );
    formItems.push(
      <TextField
        required
        colSpan={5}
        label={getLang('EXPECTATION')}
        value={record.get('expectation')}
        onChange={(value) => record.set('expectation', value)}
      />
    );
    formItems.push(
      <Button
        funcType="flat"
        icon="delete_forever"
        onClick={() => this.assertionDS.delete(record)}
      />
    );

    if (record.getState('subject') === 'JSON_BODY' || record.get('subject') === 'JSON_BODY') {
      formItems.push(
        <QuestionPopover
          message={
            <>
              {getLang('JSON_BODY_TIP')}
              <a
                href="https://help.talend.com/access/sources/content/topic?pageid=tester_json_path&amp;EnrichVersion=Cloud&amp;afs:lang=en"
                // eslint-disable-next-line react/jsx-no-target-blank
                target="_blank"
              >
                JSON Path
              </a>
            </>
          }
        />
      );
    }
    if (record.getState('subject') === 'XML_BODY' || record.get('subject') === 'XML_BODY') {
      formItems.push(
        <QuestionPopover
          message={
            <>
              {getLang('XML_BODY_TIP')}
              <a
                href="https://help.talend.com/access/sources/content/topic?pageid=tester_xpath&amp;EnrichVersion=Cloud&amp;afs:lang=en"
                // eslint-disable-next-line react/jsx-no-target-blank
                target="_blank"
              >
                XPath 1.0
              </a>
            </>
          }
        />
      );
    }
    return formItems;
  }

  /**
   * 断言主题变更时触发
   */
  @Bind()
  handleFieldUpdate(dataSet, record, name, value) {
    if (name === 'subject') {
      record.setState('subject', value);
      this.renderAssertionForm(dataSet);
    }
  }

  /**
   * 删除单行断言
   */
  @Bind()
  removeAssertionFormItem(dataSet) {
    this.renderAssertionForm(dataSet);
  }

  render() {
    const { disabledFlag, isHistory } = this.props;
    const { formItems } = this.state;
    return (
      <>
        {!disabledFlag && (
          <div style={{ marginBottom: '5px' }}>
            <Button
              key="addAssertion"
              funcType="flat"
              icon="add"
              color="primary"
              disabled={isHistory}
              onClick={() => this.assertionDS.create()}
            >
              {getLang('ADD_ASSERTION')}
            </Button>
            <Button
              key="clearAssertion"
              funcType="flat"
              icon="clear_all"
              color="default"
              disabled={isHistory}
              onClick={() => this.assertionDS.deleteAll()}
            >
              {getLang('CLEAR_ASSERTION')}
            </Button>
          </div>
        )}
        <Form
          dataSet={this.assertionDS}
          columns={24}
          labelLayout="placeholder"
          useColon={false}
          disabled={disabledFlag || isHistory}
        >
          {formItems}
        </Form>
      </>
    );
  }
}
export default AssertionCard;
