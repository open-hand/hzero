/**
 * fileAggregate - 文件汇总查询
 * @date: 2018-9-20
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { openTab } from 'utils/menuTab';

import Filter from './Filter';
import TableList from './TableList';
import FileUploadModal from './FileUploadModal';

@connect(({ fileAggregate, loading }) => ({
  fileAggregate,
  organizationId: getCurrentOrganizationId(),
  isTenant: isTenantRoleLevel(),
  loading: loading.effects['fileAggregate/queryFileList'],
}))
@formatterCollections({ code: ['hfile.fileAggregate', 'entity.tenant'] })
export default class FileAggregate extends PureComponent {
  state = {
    formValues: {}, // 查询表单中的值
    fileUploadModalVisible: false, // 文件上传 模态框是否渲染
  };

  componentDidMount() {
    this.queryFileList();
    const { dispatch } = this.props;
    const lovCodes = {
      fileTypeList: 'HFLE.CONTENT_TYPE',
      fileFormatList: 'HFLE.FILE_FORMAT',
      fileUnitList: 'HFLE.STORAGE_UNIT',
      sourceList: 'HFLE.SERVER_PROVIDER',
    };
    dispatch({
      type: 'fileAggregate/init',
      payload: {
        lovCodes,
      },
    });
  }

  /**
   * 获取文件列表
   *
   * @param {*} [params={}]
   * @memberof FileAggregate
   */
  @Bind()
  queryFileList(params = {}) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'fileAggregate/queryFileList',
      payload: { organizationId, ...params },
    });
  }

  /**
   * 获取表单的值
   *
   * @param {*} values
   * @memberof TemplateServiceMap
   */
  @Bind()
  storeFormValues(values) {
    this.setState({
      formValues: { ...values },
    });
  }

  /**
   * word 文件编辑
   */
  @Bind()
  handleRowWordEdit(record) {
    const {
      fileAggregate: { detailWordEditor = {} },
      dispatch,
    } = this.props;
    dispatch({
      type: 'fileAggregate/updateState',
      payload: {
        detailWordEditor: {
          ...detailWordEditor,
          [record.fileId]: record,
        },
      },
    });
    openTab({
      key: `/hfile/file-aggregate/word-editor/${record.fileId}`,
      title: 'hzero.common.title.wordEdit',
      closable: true,
    });
  }

  @Bind()
  handleFileUploadBtnClick() {
    this.setState({
      fileUploadModalVisible: true,
    });
  }

  @Bind()
  closeFileUploadModal() {
    this.setState({
      fileUploadModalVisible: false,
    });
  }

  render() {
    const {
      fileAggregate: {
        fileData = {},
        fileTypeList = [],
        fileFormatList = [],
        fileUnitList = [],
        sourceList = [],
      },
      loading,
      isTenant,
      organizationId,
      match,
    } = this.props;
    const { formValues = {}, fileUploadModalVisible } = this.state;
    const listProps = {
      fileData,
      formValues,
      loading,
      match,
      onSearch: this.queryFileList,
      onRowWordEdit: this.handleRowWordEdit,
    };
    const filterProps = {
      fileTypeList,
      fileFormatList,
      fileUnitList,
      sourceList,
      onSearch: this.queryFileList,
      onStoreFormValues: this.storeFormValues,
    };
    return (
      <>
        <Header title={intl.get('hfile.fileAggregate.view.message.title').d('文件汇总查询')}>
          <ButtonPermission
            icon="upload"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.upload`,
                type: 'button',
                meaning: '文件汇总查询-文件上传',
              },
            ]}
            onClick={this.handleFileUploadBtnClick}
          >
            {intl.get('hfile.fileAggregate.view.button.upload').d('文件上传')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <Filter {...filterProps} />
          </div>
          <TableList {...listProps} />
          <FileUploadModal
            visible={fileUploadModalVisible}
            onCancel={this.closeFileUploadModal}
            onOk={this.closeFileUploadModal}
            isTenant={isTenant}
            organizationId={organizationId}
          />
        </Content>
      </>
    );
  }
}
