import React from 'react';
import { Lov, Form, TextField, Select, IntlField } from 'choerodon-ui/pro';

export default class Drawer extends React.Component {
  componentDidMount() {
    const { ds, categoryId } = this.props;
    if (categoryId) {
      ds.setQueryParameter('categoryId', categoryId);
      ds.query();
    }
  }

  render() {
    const { ds, typeList, isCreate, inlineIsCreate } = this.props;
    return (
      <Form dataSet={ds}>
        <TextField name="categoryCode" disabled={!isCreate} />
        <IntlField name="categoryName" />
        <Select name="type" disabled={!isCreate}>
          {typeList.map(n => (
            <Select.Option key={n.value} value={n.value}>
              {n.meaning}
            </Select.Option>
          ))}
        </Select>
        <Lov name="parentCategoryLov" disabled={inlineIsCreate} />
      </Form>
    );
  }
}
