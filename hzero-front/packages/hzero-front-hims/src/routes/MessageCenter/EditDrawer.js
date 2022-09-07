import React from 'react';
import { Lov, Form, Select, TextField } from 'choerodon-ui/pro';
import StaticTextEditor from './StaticTextEditor';

const { Option } = Select;

export default class EditDrawer extends React.Component {
  render() {
    const { editData, languageType, onHandleStaticTextEditorRef } = this.props;
    const { data } = editData;
    return (
      <Form record={editData}>
        <Lov name="categoryLov" />
        <TextField name="questionTitle" />
        <TextField name="keyWord" />
        <Select name="lang" style={{ width: '200px' }} clearButton={false}>
          {languageType.map(item => (
            <Option value={item.value} key={item.value}>
              {item.meaning}
            </Option>
          ))}
        </Select>
        <StaticTextEditor
          name="answerDesc"
          content={data.answerDesc}
          onRef={onHandleStaticTextEditorRef}
        />
      </Form>
    );
  }
}
