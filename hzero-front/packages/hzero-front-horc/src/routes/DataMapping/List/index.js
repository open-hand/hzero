/**
 * 数据转化
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
import DATA_MAPPING_LANG from '@/langs/dataMappingLang';
import { headerTableDS } from '@/stores/DataMapping/DataMappingDS';
import { DATA_MAPPING_TAG_STATUS, DATA_MAPPING_STATUS } from '@/constants/constants';
import HistoryModal from './HistoryModal';

@formatterCollections({ code: ['horc.dataMapping'] })
export default class DataMapping extends React.Component {
  modal;

  constructor(props) {
    super(props);
    this.headerTableDS = new DataSet({
      ...headerTableDS(),
    });
  }

  /**
   * 映射转化执行
   */
  @Bind()
  async handleExec(record) {
    record.set('_status', 'update');
    await this.headerTableDS.submit();
    this.handleFetchDetail();
    const confirm = await Modal.confirm({
      children: <p>{DATA_MAPPING_LANG.EXEC_CONFIRM}</p>,
    });
    if (confirm === 'ok') {
      const res = await this.headerTableDS.submit();
      if (getResponse(res)) {
        await this.headerTableDS.query();
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
      return Modal.warning(DATA_MAPPING_LANG.MODAL_EDIT_INFO);
    }
    dispatch(
      routerRedux.push({
        pathname: `/horc/data-mapping${path}`,
        state: { editFlag },
      })
    );
  }

  @Bind()
  async handleToggle(record, type) {
    record.set('_requestType', type);
    await this.headerTableDS.submit();
    await this.headerTableDS.query();
  }

  /**
   * 打开历史版本弹窗
   */
  @Bind()
  handleOpenHistoryModal(id) {
    const modalProps = {
      castHeaderId: id,
      onGotoDetail: this.handleGotoDetail,
    };
    Modal.open({
      title: DATA_MAPPING_LANG.VERSION_HISTORY,
      closable: true,
      movable: false,
      destroyOnClose: true,
      style: { width: 900 },
      children: <HistoryModal {...modalProps} />,
      footer: null,
    });
  }

  get dataMappingColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'castCode',
        // width: 180,
        renderer: ({ value, record }) => (
          <a
            onClick={() =>
              this.handleGotoDetail(record.get('castHeaderId'), undefined, record.get('editFlag'))
            }
          >
            {value}
          </a>
        ),
      },
      {
        name: 'castName',
        // width: 180,
      },
      {
        name: 'dataType',
        width: 120,
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
          TagRender(value, DATA_MAPPING_TAG_STATUS, record.get('statusCodeMeaning')),
      },
      {
        header: DATA_MAPPING_LANG.OPERATOR,
        width: 200,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            record.get('statusCode') === DATA_MAPPING_STATUS.PUBLISHED
              ? {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.edit`,
                          type: 'button',
                          meaning: '数据映射列表-编辑',
                        },
                      ]}
                      onClick={() =>
                        this.handleGotoDetail(
                          record.get('castHeaderId'),
                          undefined,
                          record.get('editFlag'),
                          true
                        )
                      }
                    >
                      {DATA_MAPPING_LANG.EDIT}
                    </ButtonPermission>
                  ),
                  key: 'offline',
                  len: 2,
                  title: DATA_MAPPING_LANG.OFFLINE,
                }
              : {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.release`,
                          type: 'button',
                          meaning: '数据映射列表-发布',
                        },
                      ]}
                      onClick={() => this.handleToggle(record, 'publish')}
                    >
                      {DATA_MAPPING_LANG.RELEASE}
                    </ButtonPermission>
                  ),
                  key: 'release',
                  len: 2,
                  title: DATA_MAPPING_LANG.RELEASE,
                },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.viewHistory`,
                      type: 'button',
                      meaning: '数据映射列表-查看历史版本',
                    },
                  ]}
                  onClick={() => this.handleOpenHistoryModal(record.get('castHeaderId'))}
                >
                  {DATA_MAPPING_LANG.VIEW_HISTORY}
                </ButtonPermission>
              ),
              key: 'viewHistory',
              len: 6,
              title: DATA_MAPPING_LANG.VIEW_HISTORY,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '数据转化列表-删除',
                    },
                  ]}
                  onClick={() => this.headerTableDS.delete(record)}
                >
                  {DATA_MAPPING_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: DATA_MAPPING_LANG.DELETE,
            },
            // {
            //   ele: (
            //     <ButtonPermission
            //       type="text"
            //       permissionList={[
            //         {
            //           code: `${path}.button.exec`,
            //           type: 'button',
            //           meaning: '数据转化列表-执行',
            //         },
            //       ]}
            //       onClick={() => this.handleExec(record)}
            //     >
            //       {DATA_MAPPING_LANG.EXECUTE}
            //     </ButtonPermission>
            //   ),
            //   key: 'exec',
            //   len: 2,
            //   title: DATA_MAPPING_LANG.EXECUTE,
            // },
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
        <Header title={DATA_MAPPING_LANG.HEADER}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '数据转化-新建',
              },
            ]}
            icon="add"
            type="c7n-pro"
            color="primary"
            onClick={() => this.handleGotoDetail()}
          >
            {DATA_MAPPING_LANG.CREATE}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.headerTableDS} columns={this.dataMappingColumns} />
        </Content>
      </>
    );
  }
}
