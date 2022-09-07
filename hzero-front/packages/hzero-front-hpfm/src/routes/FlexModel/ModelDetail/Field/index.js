/* eslint-disable eqeqeq */
import React, { Component, Fragment } from 'react';
import { Bind } from 'lodash-decorators';

import List from './List';
import FieldEditor from './FieldEditor';

const defaultRowKey = 'fieldId';

export default class Field extends Component {
  state = {
    fieldEditorVisible: false,
    fieldEditorData: {},
  };

  @Bind()
  editField(id = '') {
    const { dataSource } = this.props;
    const fieldEditorData = dataSource.find((item) => item[defaultRowKey] == id);
    this.setState({ fieldEditorVisible: true, fieldEditorData });
  }

  @Bind()
  closeFieldEditor() {
    this.setState({ fieldEditorVisible: false, fieldEditorData: {} });
  }

  @Bind()
  handleEdit() {
    const { handleEdit } = this.props;
    this.setState({ fieldEditorVisible: false });
    handleEdit();
  }

  @Bind()
  handleSelect(rowKeys = []) {
    const { handleSelectedRows } = this.props;
    handleSelectedRows(rowKeys);
  }

  render() {
    const {
      dataSource,
      queryFieldsListLoading,
      refreshFieldsLoading,
      removeFieldLoading,
      fieldTypeOptions = [],
      fieldCategoryOptions = [],
      fieldComponentOptions = [],
      dateFormatOptions = [],
      handleRemoveField,
    } = this.props;
    const { fieldEditorVisible, fieldEditorData } = this.state;

    return (
      <Fragment>
        <List
          primaryKey={this.props.primaryKey}
          rowKey={defaultRowKey}
          handleSelect={this.handleSelect}
          loading={queryFieldsListLoading || refreshFieldsLoading || removeFieldLoading}
          dataSource={dataSource}
          handleEditField={this.editField}
          handleRemoveField={handleRemoveField}
          handleEditLimitTenants={this.editLimitTenants}
          fieldTypeOptions={fieldTypeOptions}
          fieldCategoryOptions={fieldCategoryOptions}
          fieldComponentOptions={fieldComponentOptions}
        />
        <FieldEditor
          handleEdit={this.handleEdit}
          visible={fieldEditorVisible}
          data={fieldEditorData}
          handleClose={this.closeFieldEditor}
          fieldTypeOptions={fieldTypeOptions}
          fieldCategoryOptions={fieldCategoryOptions}
          fieldComponentOptions={fieldComponentOptions}
          dateFormatOptions={dateFormatOptions}
        />
      </Fragment>
    );
  }
}
