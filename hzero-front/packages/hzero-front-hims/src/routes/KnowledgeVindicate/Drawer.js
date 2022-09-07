import React from 'react';
import { Form, TextField, Switch, Lov } from 'choerodon-ui/pro';
import StaticTextEditor from './StaticTextEditor';

export default class Drawer extends React.Component {
  render() {
    const { editData = {}, viewStatus, editStatus, onHandleStaticTextEditorRef } = this.props;
    const { data } = editData;
    return (
      <Form record={editData}>
        <TextField name="questionTitle" disabled={viewStatus || editStatus} />
        <TextField name="keyWord" disabled={viewStatus} />
        <Lov name="category" disabled={viewStatus} />
        <Switch name="enabledFlag" disabled={viewStatus} />
        <StaticTextEditor
          name="answerDesc"
          content={data.answerDesc}
          viewStatus={viewStatus}
          onRef={onHandleStaticTextEditorRef}
        />
      </Form>
    );
  }
}
