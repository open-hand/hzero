/**
 * KnowledgeCategory 知识类别
 * @date: 2019-12-5
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React from 'react';
import { Table, Modal, ModalContainer, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import { queryIdpValue } from 'hzero-front/lib/services/api';
import { initDS, drawerDS } from '../../stores/knowledgeCategoryDS';
import {
  enableKnowledgeCategories,
  disableKnowledgeCategories,
} from '../../services/knowledgeCategoryServices';
import Drawer from './Drawer';

@formatterCollections({ code: ['hims.knowledgeCategory'] })
export default class KnowledgeCategory extends React.Component {
  initDs = new DataSet(initDS());

  drawerDs = null;

  constructor(props) {
    super(props);
    this.state = {
      typeList: [], // 目录类别列表
    };
  }

  componentDidMount() {
    queryIdpValue('HIMS.KNOWLEDGE_TYPE').then((res) => {
      this.setState({
        typeList: res,
      });
    });
  }

  get columns() {
    return [
      {
        name: 'categoryCode',
        width: 200,
      },
      {
        name: 'categoryName',
        width: 200,
      },
      {
        name: 'typeMeaning',
      },
      {
        name: 'enabledFlag',
        width: 100,
        renderer: ({ value }) => enableRender(value),
        align: 'left',
      },
      {
        name: 'action',
        width: 200,
        renderer: ({ record }) => {
          const { data = {} } = record;
          const operators = [];
          if (data.type === 'dir') {
            operators.push({
              key: 'create',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${this.props.match.path}.button.create`,
                      type: 'button',
                      meaning: '知识类别-新建',
                    },
                  ]}
                >
                  <a onClick={() => this.handleUpdate(true, data, true)}>
                    {intl.get('hzero.common.button.create').d('新建')}
                  </a>
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.create').d('新建'),
            });
          }
          operators.push(
            ...[
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${this.props.match.path}.button.edit`,
                        type: 'button',
                        meaning: '知识类别-编辑',
                      },
                    ]}
                  >
                    <a onClick={() => this.handleUpdate(false, data)}>
                      {intl.get('hzero.common.button.edit').d('编辑')}
                    </a>
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'able',
                ele:
                  data.enabledFlag === 1 ? (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${this.props.match.path}.button.disable`,
                          type: 'button',
                          meaning: '知识类别-禁用',
                        },
                      ]}
                    >
                      <a onClick={() => this.handleEnable(data, false)}>
                        {intl.get('hzero.common.button.disable').d('禁用')}
                      </a>
                    </ButtonPermission>
                  ) : (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${this.props.match.path}.button.able`,
                          type: 'button',
                          meaning: '知识类别-启用',
                        },
                      ]}
                    >
                      <a onClick={() => this.handleEnable(data, true)}>
                        {intl.get('hzero.common.button.enable').d('启用')}
                      </a>
                    </ButtonPermission>
                  ),
                len: 2,
                title:
                  data.enabledFlag === 1
                    ? intl.get('hzero.common.button.disable').d('禁用')
                    : intl.get('hzero.common.button.enable').d('启用'),
              },
            ]
          );
          return operatorRender(operators);
        },
      },
    ];
  }

  /**
   * 新建/编辑知识类别
   * @param {boolean} isCreate
   * @param {object} [record={}]
   * @param {boolean} inlineIsCreate
   * @memberof KnowledgeCategory
   */
  @Bind()
  handleUpdate(isCreate, record = {}, inlineIsCreate) {
    const { typeList } = this.state;
    const { categoryId, categoryName } = record;
    this.drawerDs = new DataSet(drawerDS());
    this.drawerDs.create(
      {
        parentId: categoryId,
        parentCategoryName: categoryName,
      },
      0
    );
    const drawerProps = {
      record,
      isCreate,
      typeList,
      inlineIsCreate,
      ds: this.drawerDs,
      categoryId: isCreate ? '' : categoryId,
    };
    Modal.open({
      closable: true,
      key: 'knowledge-category',
      title: isCreate
        ? intl.get('hims.knowledgeCategory.view.message.crete').d('新建知识类别')
        : intl.get('hims.knowledgeCategory.view.message.edit').d('编辑知识类别'),
      drawer: true,
      style: {
        width: 500,
      },
      children: <Drawer {...drawerProps} />,
      onOk: this.handleOk,
    });
  }

  /**
   * 更改状态
   * @param {object} record
   * @param {string} type
   * @memberof KnowledgeCategory
   */
  @Bind()
  async handleEnable(record, enable) {
    if (enable) {
      const res = await enableKnowledgeCategories(record);
      if (res && res.failed) {
        notification.error({
          message: res.message,
        });
      }
    } else {
      const res = await disableKnowledgeCategories(record);
      if (res && res.failed) {
        notification.error({
          message: res.message,
        });
      }
    }
    this.initDs.query();
  }

  /**
   * 保存知识类别数据
   */
  @Bind()
  async handleOk() {
    const validate = await this.drawerDs.submit();
    if (!validate) {
      return false;
    }
    this.initDs.query();
  }

  render() {
    const { location, match } = this.props;
    const buttons = ['expandAll', 'collapseAll'];
    return (
      <>
        <Header title={intl.get('hims.knowledgeCategory.view.message.title').d('知识类别')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '知识类别-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleUpdate(true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            mode="tree"
            buttons={buttons}
            dataSet={this.initDs}
            highLightRow={false}
            columns={this.columns}
            queryFieldsLimit={2}
          />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
