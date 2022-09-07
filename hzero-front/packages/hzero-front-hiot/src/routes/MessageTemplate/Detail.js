import React, { useMemo, useState } from 'react';
import {
  Form,
  Spin,
  Select,
  IntlField,
  TextArea,
  TextField,
  Button,
  Modal,
  DataSet,
  Tooltip,
} from 'choerodon-ui/pro';
import { Icon } from 'choerodon-ui';
import { isEqual } from 'lodash';
import queryString from 'querystring';
import { Header, Content } from 'components/Page';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { detailDS } from '@/stores/MessageTemplateDS';
import { fetchMsgContent } from '@/services/messageTemplateService';
import HelpDrawer from './HelpDrawer';
import styles from './index.less';

const Drawer = (props) => {
  const {
    match: { params = {} },
  } = props;
  const { action } = params;
  const isEdit = action === 'edit';

  const detailDs = useMemo(() => new DataSet(detailDS()), []);
  const [helpVisible, setHelpVisible] = useState(false);
  const [currentSelect, setSelect] = useState('');
  const [loading, setLoading] = useState(false);
  const [initContent, setContent] = useState('');

  React.useEffect(() => {
    if (isEdit) {
      queryData();
    } else {
      detailDs.create({ tenantId: getCurrentOrganizationId(), msgContent: '' }, 0);
    }
  }, []);

  const queryData = async () => {
    try {
      const {
        location: { search },
      } = props;
      const { id } = queryString.parse(search.substring(1));
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
      props.history.push('/hiot/message-template/list');
    }
  };

  // 显示帮助模态框
  const openHelp = () => {
    setHelpVisible(true);
  };

  const onSelectChange = (value) => {
    const msgContent = detailDs.current.get('msgContent');

    if (!isEqual(msgContent, initContent)) {
      Modal.confirm({
        // title: 'Confirm',
        children: (
          <div className={styles['template-changeHelp-drawer']}>
            <Icon type="info" className={styles['template-changeHelp-drawer-icon']} />
            <p>
              {intl
                .get('hiot.messageTemplate.view.message.changeMsg')
                .d('更改类型会导致已填写的模板信息丢失，您确认更改吗？')}
            </p>
          </div>
        ),
        onOk: () => {
          changeValue(value);
        },
        onCancel: () => {
          detailDs.current.set('templateTypeCode', currentSelect);
        },
      });
    } else {
      changeValue(value);
    }
  };

  const changeValue = (value) => {
    setSelect(value);
    if (value) {
      queryMsgContent(value);
    } else {
      setContent('');
      detailDs.current.set('templateTypeCode', '');
      detailDs.current.set('msgContent', '');
    }
  };

  const queryMsgContent = (templateTypeCode) => {
    setLoading(true);
    try {
      fetchMsgContent({ templateTypeCode }).then((res) => {
        setLoading(false);
        if (res && !res.failed) {
          setContent(res.result);
          detailDs.current.set('msgContent', res.result);
        } else {
          detailDs.current.set('msgContent', '');
        }
      });
    } catch {
      setLoading(false);
    }
  };

  return (
    <>
      <Header
        title={
          isEdit
            ? intl.get('hiot.messageTemplate.view.message.editMessageTemp').d('编辑报文模板')
            : intl.get('hiot.messageTemplate.view.message.createMessageTemp').d('新建报文模板')
        }
        backPath="/hiot/message-template/list"
      >
        <Button color="primary" icon="save" onClick={handleSave}>
          {intl.get('hzero.common.button.save').d('保存')}
        </Button>
        <Button onClick={openHelp}>
          {intl.get('hiot.messageTemplate.view.button.help').d('帮助')}
        </Button>
      </Header>
      <Content>
        <Spin dataSet={detailDs}>
          <Form dataSet={detailDs} columns={3}>
            <TextField name="templateCode" disabled={isEdit} />
            <IntlField name="templateName" />
            <Select
              name="templateTypeCode"
              disabled={isEdit}
              onChange={onSelectChange}
              label={
                <React.Fragment>
                  <Tooltip
                    placement="top"
                    title={intl
                      .get('hiot.messageTemplate.view.message.helpMsg')
                      .d(
                        '下发模板用于控制参数指令下发和OTA升级任务下发升级包地址；上报模板用于设备模拟器上报时选用。'
                      )}
                  >
                    <Icon type="help_outline" />
                  </Tooltip>
                  {intl.get('hiot.messageTemplate.model.messageTemp.templateTypeCode').d('类型')}
                </React.Fragment>
              }
            />
            <TextArea name="description" colSpan={3} />
          </Form>
          <Spin spinning={loading}>
            <Form dataSet={detailDs} columns={3}>
              <TextArea name="msgContent" colSpan={3} style={{ height: 300 }} />
            </Form>
          </Spin>
        </Spin>
        {helpVisible && (
          <HelpDrawer
            onCloseDrawer={() => {
              setHelpVisible(false);
            }}
          />
        )}
      </Content>
    </>
  );
};

export default Drawer;
