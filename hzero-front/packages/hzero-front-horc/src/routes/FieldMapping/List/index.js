/**
 * 字段映射
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/7/8
 * @copyright HAND ® 2020
 */
import React from 'react';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Modal } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { operatorRender, TagRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { routerRedux } from 'dva/router';
import { isUndefined, isNumber } from 'lodash';
import { getResponse } from 'utils/utils';
import FIELD_MAPPING_LANG from '@/langs/fieldMappingLang';
import { tableDS } from '@/stores/FieldMapping/FieldMappingDS';
import { TRANSFORM_STATUS, FIELD_MAPPING_TAG_STATUS } from '@/constants/constants';
import HistoryModal from './HistoryModal';

@formatterCollections({ code: ['horc.fieldMapping'] })
export default class FieldMapping extends React.Component {
  modal;

  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS());
  }

  /**
   * 映射转化执行
   */
  @Bind()
  async handleExec(record) {
    record.set('_status', 'update');
    await this.tableDS.submit();
    this.handleFetchDetail();
    const confirm = await Modal.confirm({
      children: <p>{FIELD_MAPPING_LANG.EXEC_CONFIRM}</p>,
    });
    if (confirm === 'ok') {
      const res = await this.tableDS.submit();
      if (getResponse(res)) {
        await this.tableDS.query();
      }
    }
  }

  /**
   * 跳转到新建/明细页面
   * @param {*} id
   */
  @Bind()
  handleGotoDetail(id, version, editFlag = true, clickByEdit = false) {
    const { dispatch = () => {} } = this.props;
    // eslint-disable-next-line no-nested-ternary
    const path = isUndefined(id)
      ? '/create'
      : isNumber(version)
      ? `/history/${id}/${version}`
      : `/detail/${id}`;
    if (clickByEdit && !editFlag) {
      return Modal.warning(FIELD_MAPPING_LANG.MODAL_EDIT_INFO);
    }
    dispatch(
      routerRedux.push({
        pathname: `/horc/field-mapping${path}`,
        state: { editFlag },
      })
    );
  }

  /**
   * 打开历史版本弹窗
   */
  @Bind()
  handleOpenHistoryModal(id) {
    const modalProps = {
      transformId: id,
      onGotoDetail: this.handleGotoDetail,
    };
    Modal.open({
      title: FIELD_MAPPING_LANG.VERSION_HISTORY,
      closable: true,
      movable: false,
      destroyOnClose: true,
      style: { width: 900 },
      children: <HistoryModal {...modalProps} />,
      footer: null,
    });
  }

  @Bind()
  async handleToggle(record, type) {
    record.set('_requestType', type);
    await this.tableDS.submit();
  }

  get fieldMappingColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'transformCode',
        // width: 180,
        renderer: ({ value, record }) => (
          <a
            onClick={() =>
              this.handleGotoDetail(record.get('transformId'), undefined, record.get('editFlag'))
            }
          >
            {value}
          </a>
        ),
      },
      {
        name: 'transformName',
        // width: 180,
      },
      {
        name: 'transformType',
        width: 130,
      },
      {
        name: 'versionDesc',
        width: 80,
      },
      {
        name: 'fromVersionDesc',
        width: 100,
      },
      {
        name: 'statusCode',
        width: 100,
        renderer: ({ value, record }) =>
          TagRender(value, FIELD_MAPPING_TAG_STATUS, record.get('statusCodeMeaning')),
      },
      {
        header: FIELD_MAPPING_LANG.OPERATOR,
        width: 200,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            record.get('statusCode') === TRANSFORM_STATUS.PUBLISHED
              ? {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.edit`,
                          type: 'button',
                          meaning: '字段映射列表-编辑',
                        },
                      ]}
                      onClick={() =>
                        this.handleGotoDetail(
                          record.get('transformId'),
                          undefined,
                          record.get('editFlag'),
                          true
                        )
                      }
                    >
                      {FIELD_MAPPING_LANG.EDIT}
                    </ButtonPermission>
                  ),
                  key: 'offline',
                  len: 2,
                  title: FIELD_MAPPING_LANG.OFFLINE,
                }
              : {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.release`,
                          type: 'button',
                          meaning: '字段映射列表-发布',
                        },
                      ]}
                      onClick={() => this.handleToggle(record, 'publish')}
                    >
                      {FIELD_MAPPING_LANG.RELEASE}
                    </ButtonPermission>
                  ),
                  key: 'release',
                  len: 2,
                  title: FIELD_MAPPING_LANG.RELEASE,
                },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.viewHistory`,
                      type: 'button',
                      meaning: '字段映射列表-查看历史版本',
                    },
                  ]}
                  onClick={() => this.handleOpenHistoryModal(record.get('transformId'))}
                >
                  {FIELD_MAPPING_LANG.VIEW_HISTORY}
                </ButtonPermission>
              ),
              key: 'viewHistory',
              len: 6,
              title: FIELD_MAPPING_LANG.VIEW_HISTORY,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '字段映射列表-删除',
                    },
                  ]}
                  onClick={() => this.tableDS.delete(record)}
                >
                  {FIELD_MAPPING_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: FIELD_MAPPING_LANG.DELETE,
            },
          ];
          return operatorRender(actions, record);
        },
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={FIELD_MAPPING_LANG.HEADER}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '字段映射-新建',
              },
            ]}
            icon="add"
            type="c7n-pro"
            color="primary"
            onClick={() => this.handleGotoDetail()}
          >
            {FIELD_MAPPING_LANG.CREATE}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDS} columns={this.fieldMappingColumns} />
        </Content>
      </>
    );
  }
}
