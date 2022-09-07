/*
 * @Author: your name
 * @Date: 2020-02-17 17:48:31
 * @LastEditTime: 2020-02-19 18:44:22
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \hzero-front\packages\hzero-front-hims\src\routes\BasicConfig\index.js
 */
/*
 * BasicConfig 配置管理
 * @date: 2020-02-13
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Bind } from 'lodash-decorators';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Form, Switch, DataSet, NumberField } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { initDS } from '../../stores/BasicConfigDS';
import styles from './index.less';

@formatterCollections({ code: ['hims.basicConfig'] })
export default class BasicConfig extends React.Component {
  initDs = new DataSet(initDS());

  constructor(props) {
    super(props);
    this.state = {};
  }

  /**
   * 保存
   */
  @Bind()
  async handleSave() {
    const validate = await this.initDs.submit();
    if (!validate) {
      return false;
    }
    await this.initDs.query();
  }

  render() {
    const { match } = this.props;
    return (
      <>
        <Header title={intl.get('hims.basicConfig.view.message.basicCfg').d('IM基础配置')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: 'im基础配置管理-保存',
              },
            ]}
            color="primary"
            icon="save"
            onClick={() => this.handleSave()}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className={styles['basic-config-form']}>
            <Form dataSet={this.initDs} labelWidth={180}>
              <NumberField
                name="retractTime"
                min={0}
                step={1}
                renderer={({ value }) =>
                  value ? `${value} ${intl.get('hzero.common.date.unit.minutes').d('分钟')}` : ''
                }
              />
              <NumberField
                name="maxUser"
                min={1}
                step={1}
                renderer={({ value }) =>
                  value
                    ? `${value} ${intl.get('hims.basicConfig.view.message.person').d('人')}`
                    : ''
                }
              />
              <Switch name="avoidAudit" />
              <Switch name="WeChatPush" />
              <Switch name="tokenUnique" />
            </Form>
          </div>
        </Content>
      </>
    );
  }
}
