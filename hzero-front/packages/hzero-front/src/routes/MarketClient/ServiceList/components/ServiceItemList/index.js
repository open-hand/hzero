import React, { useState } from 'react';
import { Row, Col, Icon } from 'hzero-ui';
import styles from './index.less';

export default function ServiceItemList({ serviceId, serviceList, ColumnShowItem = 3 }) {
  const [isShowExpend, setIsShowExpend] = useState(false);

  // 渲染顶部服务列表
  const renderServiceItem = (i, index) => {
    const { groupId, artifactId, version } = i;
    return (
      <div
        className={styles['service-item']}
        key={i}
        style={
          index % ColumnShowItem === 0
            ? {
                marginLeft: 0,
                paddingLeft: 0,
                borderLeft: 0,
              }
            : null
        }
      >
        <p>GroupId：{groupId}</p>
        <p>ArtifactId：{artifactId}</p>
        <p>version：{version}</p>
      </div>
    );
  };

  return (
    <>
      <h3 className={styles['list-header']}>{serviceId}</h3>
      <Row type="flex">
        <Col className={styles['expend-wrap']}>
          <div className={styles['service-item-wrap']}>
            {serviceList.slice(0, ColumnShowItem).map((i, index) => renderServiceItem(i, index))}
          </div>
          {isShowExpend ? (
            <div className={styles['service-item-wrap']}>
              {serviceList.slice(ColumnShowItem).map((i, index) => renderServiceItem(i, index))}
            </div>
          ) : null}
        </Col>
        {serviceList.length > ColumnShowItem ? (
          <Col style={{ width: '84px' }} onClick={() => setIsShowExpend(!isShowExpend)}>
            {!isShowExpend ? (
              <a>
                展开其余{serviceList.length - ColumnShowItem}项 <Icon type="down" />
              </a>
            ) : (
              <a>
                收起 <Icon type="up" />
              </a>
            )}
          </Col>
        ) : null}
      </Row>
    </>
  );
}
