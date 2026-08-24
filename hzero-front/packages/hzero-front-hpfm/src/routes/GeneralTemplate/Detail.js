import React, { useMemo } from 'react';
import {
  Form,
  Spin,
  Select,
  Lov,
  TextArea,
  TextField,
  Button,
  DataSet,
  Switch,
} from 'choerodon-ui/pro';
import { Row, Col } from 'choerodon-ui';
import { Header, Content } from 'components/Page';
import { isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import { detailDS } from '../../stores/GeneralTemplateDS';

const Drawer = (props) => {
  const {
    match: { params = {} },
  } = props;
  const { action, id } = params;
  const isEdit = action !== 'create';
  // const isDetail = action === 'detail';

  const detailDs = useMemo(() => new DataSet(detailDS()), []);

  React.useEffect(() => {
    if (isEdit) {
      queryData();
    } else {
      detailDs.create({}, 0);
    }
  }, []);

  const queryData = async () => {
    try {
      detailDs.templateId = id;
      await detailDs.query();
    } catch (e) {
      //
    }
  };

  const handleSave = async () => {
    const validate = await detailDs.submit();
    if (!validate) {
      return false;
    } else {
      props.history.push('/hpfm/general-template/list');
    }
  };

  return (
    <>
      <Header
        title={
          isEdit
            ? intl.get('hpfm.generalTemplate.view.message.editGeneralTemp').d('编辑通用模板')
            : intl.get('hpfm.generalTemplate.view.message.createGeneralTemp').d('新建通用模板')
        }
        backPath="/hpfm/general-template/list"
      >
        <Button color="primary" icon="save" onClick={handleSave}>
          {intl.get('hzero.common.button.save').d('保存')}
        </Button>
      </Header>
      <Content>
        <Row>
          <Col span={22}>
            <Spin dataSet={detailDs}>
              <Form dataSet={detailDs} columns={3}>
                {!isTenantRoleLevel() && <Lov name="tenantLov" disabled={isEdit} />}
                <TextField name="templateCode" disabled={isEdit} newLine />
                <TextField name="templateName" />
                <Select name="templateCategoryCode" disabled={isEdit} />
                <Select name="lang" />
                <Switch name="enabledFlag" />
                <TextArea name="templateContent" newLine colSpan={3} style={{ height: 300 }} />
              </Form>
            </Spin>
          </Col>
        </Row>
      </Content>
    </>
  );
};

export default Drawer;
