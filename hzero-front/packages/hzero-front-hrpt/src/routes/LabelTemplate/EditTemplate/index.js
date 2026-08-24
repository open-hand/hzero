/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/2
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { Col, DataSet, Form, Row, Button } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME, EDIT_FORM_ROW_LAYOUT } from 'utils/constants';
import { labelTemplateFormDS, labelTemplateParameterDS } from '@/stores/labelTemplateDS';

import LabelCKEditor from '../components/LabelCKEditor';

import styles from './styles.less';

const editorColLayout = {
  md: { span: 24 },
  lg: { span: 24 },
};

const EditTemplate = ({
  match: {
    params: { templateHigh, templateWidth, labelTemplateId },
  },
}) => {
  const lineDS = new DataSet(labelTemplateParameterDS());
  const headerDSConfig = labelTemplateFormDS(labelTemplateId, lineDS);
  const labelDS = new DataSet(headerDSConfig);
  const handleSave = async () => {
    const valid = await labelDS.submit();
    if (valid) {
      await labelDS.query();
    }
  };
  return (
    <>
      <Header
        title={intl.get('hrpt.labelTemplate.view.title.detailEdit').d('标签模板编辑')}
        backPath={`/hrpt/label-template/detail/${labelTemplateId}`}
      >
        <Button icon="save" onClick={handleSave}>
          {intl.get('hzero.common.button.save').d('保存')}
        </Button>
      </Header>
      <Content className={styles['label-template-detail']}>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...editorColLayout}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>{intl.get('hrpt.labelTemplate.view.title.detail.content').d('模板编辑')}</h3>
              }
            >
              <Form columns={1} dataSet={labelDS}>
                <LabelCKEditor
                  name="templateContent"
                  templateWidth={templateWidth}
                  templateHigh={templateHigh}
                />
              </Form>
            </Card>
          </Col>
        </Row>
      </Content>
    </>
  );
};

export default EditTemplate;
