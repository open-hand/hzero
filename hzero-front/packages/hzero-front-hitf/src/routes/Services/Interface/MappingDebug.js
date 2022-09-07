/**
 * @author HBT <wanjun.feng@hand-china.com>
 * @creationDate 2020/12/24
 * @copyright HAND ® 2020
 */

import React from 'react';
import notification from 'utils/notification';
import { Bind } from 'lodash-decorators';
import { CodeArea, Row, Col } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { mappingFlowTest } from '@/services/dataMappingService';
import getLang from '@/langs/serviceLang';

export default class MappingDebug extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      ...props,
    };
  }

  componentDidMount() {
    this.handleUpdateModalProp();
  }

  /**
   * 更新当前Modal的属性
   */
  @Bind()
  handleUpdateModalProp() {
    const { modal, path } = this.props;
    modal.update({
      footer: (okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.mapping.test.transform`,
                type: 'button',
                meaning: '字段映射-调试转化',
              },
            ]}
            type="c7n-pro"
            color="primary"
            onClick={this.handleExecute}
          >
            {getLang('DEBUG_EXECUTE')}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 执行
   */
  @Bind()
  handleExecute() {
    const {
      tenantId,
      sourceContent,
      script,
      type,
      interfaceId,
      namespace,
      serverCode,
      interfaceCode,
      level,
    } = this.state;
    if (!sourceContent) {
      return notification.error({
        message: getLang('SAVE_VALIDATE'),
      });
    }
    if (type === 'FIELD_MAPPING' && !script) {
      return notification.error({
        message: getLang('SAVE_VALIDATE'),
      });
    }
    mappingFlowTest({
      interfaceId,
      interfaceCode,
      level,
      namespace,
      serverCode,
      sourceContent,
      script,
      tenantId,
    }).then((res) => {
      if (res.failed) {
        return notification.error({
          message: res.message,
        });
      } else {
        this.setState({ targetContent: res.targetContent });
      }
    });
  }

  render() {
    const { sourceContent, script, targetContent, type } = this.state;
    return (
      <div>
        <Row gutter={5}>
          <Col span={type === 'FIELD_MAPPING' ? 8 : 12}>
            <h4>{getLang('SOURCE_DATA')}</h4>
            <CodeArea
              required
              style={{
                height: document.querySelector('body').offsetHeight - 180,
              }}
              onChange={(value) => this.setState({ sourceContent: value })}
              value={sourceContent}
            />
          </Col>
          {type === 'FIELD_MAPPING' && (
            <Col span={8}>
              <h4>{getLang('SCRIPT_DATA')}</h4>
              <CodeArea
                required
                style={{
                  height: document.querySelector('body').offsetHeight - 180,
                }}
                onChange={(value) => this.setState({ script: value })}
                value={script}
              />
            </Col>
          )}
          <Col span={type === 'FIELD_MAPPING' ? 8 : 12}>
            <h4>{getLang('RESULT_DATA')}</h4>
            <CodeArea
              style={{
                height: document.querySelector('body').offsetHeight - 180,
              }}
              value={targetContent}
            />
          </Col>
        </Row>
      </div>
    );
  }
}
