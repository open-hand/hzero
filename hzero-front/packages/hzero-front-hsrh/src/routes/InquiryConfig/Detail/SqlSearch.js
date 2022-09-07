/**
 * @since 2020-1-5
 * @author MLF <linfeng.miao@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form } from 'hzero-ui';
// import Lov from 'components/Lov';
import { CodeArea } from 'choerodon-ui/pro';
import JSONFormatter from 'choerodon-ui/pro/lib/code-area/formatters/JSONFormatter';
import { isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
// import Record from 'choerodon-ui/pro/lib/data-set/Record';

// @connect(({ searchData, loading }) => ({
//   searchData,
//   detailLoading: loading.effects['searchData/send'],
// }))
export default class SqlSearch extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  @Bind()
  indexOnChange(indexId, record) {
    const { form } = this.props;
    if (indexId) {
      if (isUndefined(form.getFieldValue('indexCode'))) {
        // 往外层form配置indexCode表单域
        form.registerField('indexCode');
      }
      form.setFieldsValue({ indexCode: record.indexCode });
    } else {
      form.setFieldsValue({ indexCode: null });
    }
  }

  render() {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    const formLayout = {
      labelCol: { span: 3 },
      wrapperCol: { span: 20 },
    };
    return (
      <React.Fragment>
        <Form>
          <Form.Item
            {...formLayout}
            label={intl.get('hsrh.inquiryConfig.model.inquiryConfig.requireUrl').d('查询')}
          >
            {getFieldDecorator('requireUrl', {})(
              <CodeArea formatter={JSONFormatter} style={{ height: 397, lineHeight: 1 }} />
            )}
          </Form.Item>
        </Form>
      </React.Fragment>
    );
  }
}
