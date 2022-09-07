import React from 'react';
import { Lov, Form, TextField, TextArea } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import './index.less';

export default class BaseDrawer extends React.Component {
  state = { count: [1, 1, 1, 1, 1] };

  @Bind()
  handleSelect(value) {
    const { count } = this.state;
    const arr = count.map((_, index) => (index < value ? 1 : 0));
    this.setState({ count: arr });
  }

  render() {
    const { formDs } = this.props;
    return (
      <Form dataSet={formDs}>
        <Lov name="category" />
        <TextField name="keyWord" />
        <TextField name="questionTitle" />
        <TextArea name="answerDesc" />
      </Form>
    );
  }
}
