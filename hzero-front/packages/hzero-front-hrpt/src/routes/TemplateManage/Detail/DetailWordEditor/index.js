/**
 * DetailWordEditor
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-02
 * @copyright 2019-07-02 © HAND
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import OnlyOfficeEditor from 'components/OnlyOfficeEditor';
import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import { closeTab } from 'utils/menuTab';
import { BKT_RPT } from 'utils/config';

@connect(({ templateManage }) => {
  const { detailWordEditor } = templateManage;
  return {
    detailWordEditor,
  };
})
export default class DetailWordEditor extends Component {
  constructor(props) {
    super(props);
    const {
      match: {
        params: { fileId },
      },
      detailWordEditor = {},
    } = props;
    const { [fileId]: record = {} } = detailWordEditor;
    this.onlyOfficeEditorRef = React.createRef();
    this.state = {
      saving: false,
      onlyOfficeEditorConfig: {
        url: record.templateUrl,
        organizationId: record.tenantId,
        bucketName: BKT_RPT,
        bucketDirectory: record.directory,
        storageCode: record.storageCode,
        autoFocus: true, // 虽然没有起作用
        permission: {
          changeReview: true,
          comment: false,
          dealWithReview: true,
          dealWithReviewOnly: false,
          download: true,
          edit: true,
          print: true,
          review: true,
        },
      },
    };
  }

  @Bind()
  handleTriggerOnlyOfficeEditorSave() {
    if (this.onlyOfficeEditorRef.current) {
      this.setState({
        saving: true,
      });
      this.onlyOfficeEditorRef.current.triggerDocumentSave(() => {
        this.setState({
          saving: false,
        });
        const {
          match: {
            params: { fileId },
          },
        } = this.props;
        closeTab(`/hrpt/template-manage/word-editor/${fileId}`);
      });
    }
  }

  render() {
    const { saving = false, onlyOfficeEditorConfig = {} } = this.state;
    return (
      <>
        <Header>
          <Button loading={saving} onClick={this.handleTriggerOnlyOfficeEditorSave}>
            {intl.get('hrpt.templateManage.view.button.saveAndClose').d('保存并关闭')}
          </Button>
        </Header>
        <Content
          style={{
            height: '100%',
          }}
        >
          <OnlyOfficeEditor
            {...onlyOfficeEditorConfig}
            width="100%"
            height="100%"
            ref={this.onlyOfficeEditorRef}
          />
        </Content>
      </>
    );
  }
}
