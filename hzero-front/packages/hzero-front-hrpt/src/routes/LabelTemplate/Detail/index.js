/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/2
 * @copyright 2019 ® HAND
 */

import React from 'react';
import {
  Col,
  DataSet,
  Form,
  Lov,
  NumberField,
  Row,
  Select,
  Table,
  TextField,
  IntlField,
  Button,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
// import queryString from 'query-string';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ROW_LAYOUT,
  TABLE_OPERATOR_CLASSNAME,
} from 'utils/constants';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

import { labelTemplateFormDS, labelTemplateParameterDS } from '@/stores/labelTemplateDS';

import styles from './styles.less';

const headerColLayout = {
  md: { span: 24 },
  lg: { span: 24 },
};
const variableColLayout = {
  md: { span: 24 },
  lg: { span: 24 },
};

const LabelTemplateDetail = ({
  match: {
    path,
    params: { labelTemplateId },
  },
  history,
}) => {
  const isTenant = isTenantRoleLevel();
  const organizationId = getCurrentOrganizationId();
  // 标签的宽/高
  const [templateWidth, setTemplateWidth] = React.useState(0);
  const [templateHigh, setTemplateHigh] = React.useState(0);
  // dataset
  const { labelDS, parameterDS } = React.useMemo(() => {
    const lineDS = new DataSet(labelTemplateParameterDS());
    const headerDSConfig = labelTemplateFormDS(labelTemplateId, lineDS);
    if (isTenant) {
      const newFields = [];

      headerDSConfig.fields.forEach((field) => {
        switch (field.name) {
          case 'tenantLov':
            break;
          case 'tenantId':
            break;
          case 'tenantName':
            break;
          // 租户去掉 租户字段
          case 'datasetLov':
            newFields.push({
              ...field,
              cascadeMap: undefined,
              lovPara: {
                tenantId: organizationId,
              },
            });
            break;
          default:
            newFields.push(field);
            break;
        }
      });

      headerDSConfig.fields = newFields;
    }
    return {
      labelDS: new DataSet(headerDSConfig),
      parameterDS: lineDS,
    };
  }, [isTenant]);
  React.useEffect(() => {
    const listenWidthAndHigh = ({ name, value }) => {
      switch (name) {
        case 'templateWidth':
          setTemplateWidth(value);
          break;
        case 'templateHigh':
          setTemplateHigh(value);
          break;
        default:
          break;
      }
    };
    // 数据加载完成后重新设置宽高
    const listenLoad = ({ dataSet }) => {
      setTemplateWidth(dataSet.current.data.templateWidth);
      setTemplateHigh(dataSet.current.data.templateHigh);
    };
    labelDS.addEventListener('update', listenWidthAndHigh);
    labelDS.addEventListener('load', listenLoad);
    return () => {
      labelDS.removeEventListener('update', listenWidthAndHigh);
      labelDS.removeEventListener('load', listenLoad);
    };
  }, [labelDS]);
  React.useEffect(() => {
    const listenParamTypeCode = ({ name, value, record }) => {
      // 监听参数类型变化
      if (name === 'paramTypeCode') {
        switch (value) {
          case 'TEXT':
            record.set('barCodeType', undefined);
            record.set('characterEncoding', undefined);
            break;
          case 'QRCODE':
            record.set('textLength', undefined);
            record.set('maxRows', undefined);
            record.set('barCodeType', undefined);
            break;
          case 'BARCODE':
            record.set('textLength', undefined);
            record.set('maxRows', undefined);
            break;
          case 'IMG':
            record.set('textLength', undefined);
            record.set('maxRows', undefined);
            record.set('barCodeType', undefined);
            record.set('characterEncoding', undefined);
            break;
          default:
            record.set('textLength', undefined);
            record.set('maxRows', undefined);
            record.set('barCodeType', undefined);
            record.set('characterEncoding', undefined);
            break;
        }
      }
    };
    parameterDS.addEventListener('update', listenParamTypeCode);
    return () => {
      parameterDS.removeEventListener('update', listenParamTypeCode);
    };
  }, [parameterDS]);
  const handleParameterAdd = React.useCallback(() => {
    parameterDS.create();
  }, [parameterDS]);
  const handleSave = React.useCallback(async () => {
    const valid = await labelDS.validate();
    if (valid) {
      await labelDS.submit();
      await labelDS.query();
    }
  }, [labelDS]);
  const handleDelete = React.useCallback(
    async (record) => {
      await parameterDS.delete([record]);
      await labelDS.query();
    },
    [parameterDS]
  );
  const handleClear = React.useCallback(
    (record) => {
      parameterDS.delete([record]);
    },
    [parameterDS]
  );
  const handleEditTemplate = React.useCallback(() => {
    history.push({
      pathname: `/hrpt/label-template/detail/edit-template/${labelTemplateId}/${templateHigh}/${templateWidth}`,
      // state: queryString.stringify(labelDS),
    });
  });
  const parameterColumns = React.useMemo(
    () => [
      { editor: (record) => record.status === 'add', width: 120, name: 'parameterCode' },
      { editor: true, width: 200, name: 'parameterName' },
      { editor: (record) => record.status === 'add', width: 120, name: 'paramTypeCode' },
      {
        editor: (record) => record.get('paramTypeCode') === 'TEXT',
        width: 120,
        name: 'textLength',
        align: 'left',
      },
      {
        editor: (record) => record.get('paramTypeCode') === 'TEXT',
        width: 120,
        name: 'maxRows',
        align: 'left',
      },
      {
        editor: (record) => record.get('paramTypeCode') === 'BARCODE',
        width: 200,
        name: 'barCodeType',
      },
      {
        editor: (record) => ['BARCODE', 'QRCODE'].includes(record.get('paramTypeCode')),
        width: 200,
        name: 'characterEncoding',
      },
      {
        name: 'operator',
        header: intl.get('hzero.common.button.action').d('操作'),
        renderer({ record }) {
          const actions = [];
          if (record.status === 'add') {
            const clearTitle = intl.get('hzero.common.button.clear').d('清空');
            actions.push({
              key: 'clear',
              title: clearTitle,
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/clear`,
                      type: 'button',
                      meaning: '标签模板编辑-清除',
                    },
                  ]}
                  style={{ cursor: 'point' }}
                  onClick={() => {
                    handleClear(record);
                  }}
                >
                  {clearTitle}
                </ButtonPermission>
              ),
              len: 2,
            });
          } else {
            const deleteTitle = intl.get('hzero.common.button.delete').d('删除');
            actions.push({
              key: 'delete',
              title: deleteTitle,
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/delete`,
                      type: 'button',
                      meaning: '标签模板编辑-删除',
                    },
                  ]}
                  style={{ cursor: 'point' }}
                  onClick={() => {
                    handleDelete(record);
                  }}
                >
                  {deleteTitle}
                </ButtonPermission>
              ),
              len: 2,
            });
          }
          return operatorRender(actions);
        },
      },
    ],
    []
  );
  return (
    <>
      <Header
        title={intl.get('hrpt.labelTemplate.view.title.detail').d('标签模板')}
        backPath="/hrpt/label-template/list"
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/save`,
              type: 'button',
              meaning: '标签模板编辑-保存',
            },
          ]}
          icon="save"
          // className="ant-btn-primary"
          // style={{ color: '#fff' }}
          color="primary"
          onClick={handleSave}
        >
          {intl.get('hzero.common.button.save').d('保存')}
        </ButtonPermission>
        <Button icon="knowledge" onClick={handleEditTemplate}>
          {intl.get('hrpt.labelTemplate.view.title.detail.content').d('模板编辑')}
        </Button>
      </Header>
      <Content className={styles['label-template-detail']}>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...headerColLayout}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>{intl.get('hrpt.labelTemplate.view.title.detail.header').d('标签模板')}</h3>
              }
            >
              <Form columns={3} dataSet={labelDS}>
                {!isTenant && <Lov name="tenantLov" disabled />}
                <TextField name="templateCode" disabled />
                <IntlField name="templateName" />
                <Lov name="datasetLov" />
                <NumberField name="templateWidth" />
                <NumberField name="templateHigh" />
                <Select name="enabledFlag" />
              </Form>
            </Card>
          </Col>
          <Col {...variableColLayout}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>{intl.get('hrpt.labelTemplate.view.title.detail.parameter').d('标签参数')}</h3>
              }
            >
              <div className={TABLE_OPERATOR_CLASSNAME}>
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}/add`,
                      type: 'button',
                      meaning: '标签模板编辑-新增',
                    },
                  ]}
                  color="primary"
                  onClick={handleParameterAdd}
                >
                  {intl.get('hzero.common.button.add').d('新增')}
                </ButtonPermission>
              </div>
              <Table dataSet={parameterDS} columns={parameterColumns} />
            </Card>
          </Col>
          {/* <Col {...editorColLayout}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>{intl.get('hrpt.labelTemplate.view.title.detail.content').d('模板编辑')}</h3>
              }
            >
              <Form columns={1} dataSet={labelDS}>
                {templateWidth && templateHigh ? (
                  <LabelCKEditor
                    name="templateContent"
                    templateWidth={templateWidth}
                    templateHigh={templateHigh}
                  />
                ) : null}
              </Form>
            </Card>
          </Col> */}
        </Row>
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hrpt.labelTemplate'],
})(LabelTemplateDetail);
