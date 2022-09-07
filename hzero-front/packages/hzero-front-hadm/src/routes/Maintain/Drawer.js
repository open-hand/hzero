import React from 'react';
import { Modal, Button } from 'choerodon-ui/pro';
import { Icon, Card, Spin, Divider, Popconfirm } from 'choerodon-ui';
import axios from 'axios';

import notification from 'utils/notification';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_ADM } from 'utils/config';
import intl from 'utils/intl';

const Drawer = (props) => {
  const {
    service,
    maintainId,
    state,
    isStop,
    onHandleStopEnd,
    onHandleStartEnd,
    onHandleClose,
  } = props;

  const [maintainData, setMaintainData] = React.useState([]);

  const [loading, setLoading] = React.useState(false);

  const [isError, setIsError] = React.useState(false);

  const [retryLoading, setRetryLoading] = React.useState(false);

  /**
   * 根据服务列表 开启/停用运维 =》 渲染信息到页面
   */
  React.useEffect(() => {
    const length = service && service.length;
    let test = maintainData;
    service.forEach((item, index) => {
      setLoading(true);
      setIsError(true);
      const pendingData = [
        ...test,
        {
          serviceCode: item,
          status: 'pending',
        },
      ];
      setMaintainData(pendingData);
      test.push({
        serviceCode: item,
        status: 'pending',
      });
      const url =
        state === 'ACTIVE'
          ? `${
              isTenantRoleLevel()
                ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}`
                : `${HZERO_ADM}/v1`
            }/maintain-tables/disable`
          : `${
              isTenantRoleLevel()
                ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}`
                : `${HZERO_ADM}/v1`
            }/maintain-tables/enable`;
      axios({
        url,
        method: 'POST',
        params: {
          maintainId,
          serviceCode: item,
        },
      })
        .catch((err) => {
          console.info('err', err);
          notification.error({
            message: `${item}服务请求超时`,
          });
          const errorData = test.filter((r) => r.serviceCode !== item);
          const arr = [
            ...errorData,
            {
              serviceCode: item,
              status: false,
              message: `${item}服务请求超时`,
            },
          ];
          setMaintainData(arr);
          test = arr;
        })
        .then((res) => {
          if (res) {
            const filterData = test.filter((i) => i.serviceCode !== item);
            setLoading(false);
            const newData = [
              ...filterData,
              {
                serviceCode: item,
                status: res.success,
                message: res.message,
              },
            ];
            setMaintainData(newData);
            test = newData;
            if (res.success === false) {
              setIsError(true);
            }
            const flag = newData.every((it) => it.status === true); // 判断是否有异常
            if (flag) {
              setIsError(false);
            } else {
              setIsError(true);
            }
            if (length === index + 1) {
              notification.success({
                message: isStop
                  ? intl
                      .get('hadm.maintain.view.message.title.stopMaintainSuccess')
                      .d('停止运维已完成')
                  : intl
                      .get('hadm.maintain.view.message.title.maintainSuccess')
                      .d('开启运维已完成'),
              });
            }
          } else {
            setIsError(true);
          }
        });
    });
  }, []);

  /**
   * 运维失败 错误信息
   * @param {*} record
   */
  const handleError = (record) => {
    Modal.open({
      title: intl.get('hadm.maintain.view.message.title.error').d('错误信息'),
      drawer: false,
      closable: true,
      children: <pre dangerouslySetInnerHTML={{ __html: record.message.replace(/↵/g, '<br/>') }} />,
      onOk: () => true,
      onCancel: () => true,
      footer: null,
    });
  };

  /**
   * 重新运维 - 重试
   * @param {*} record
   */
  const handleRetry = (record, index) => {
    setRetryLoading(true);
    const data = maintainData;
    const url =
      state === 'ACTIVE'
        ? `${
            isTenantRoleLevel()
              ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}`
              : `${HZERO_ADM}/v1`
          }/maintain-tables/disable`
        : `${
            isTenantRoleLevel()
              ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}`
              : `${HZERO_ADM}/v1`
          }/maintain-tables/enable`;
    axios({
      url,
      method: 'POST',
      params: {
        maintainId,
        serviceCode: record.serviceCode,
      },
    })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      })
      .then((res) => {
        if (res) {
          data.splice(index, 1);
          const newData = [
            ...data,
            {
              serviceCode: record.serviceCode,
              status: res.success,
              message: res.message,
            },
          ];
          maintainData.forEach((item) => {
            if (item.success === true) {
              setIsError(false);
            } else if (item.success === false || item.status === 'pending') {
              setIsError(true);
            }
          });
          setMaintainData(newData);
          data.push({
            serviceCode: record.serviceCode,
            status: res.success,
            message: res.message,
          });
          if (res.success) {
            notification.success({
              message: intl
                .get('hadm.maintain.view.message.title.retryTrue', { name: record.serviceCode })
                .d(`“${record.serviceCode}”服务重试成功`),
            });
          } else {
            notification.error({
              message: intl
                .get('hadm.maintain.view.message.title.retryFalse', { name: record.serviceCode })
                .d(`“${record.serviceCode}”服务重试失败`),
            });
          }
        }
        setRetryLoading(false);
      });
  };

  const handleStartEnd = () => {
    setIsError(false);
    onHandleStartEnd();
  };

  const handleStopEnd = () => {
    setIsError(false);
    onHandleStopEnd();
  };

  const handleClose = () => {
    setIsError(false);
    onHandleClose();
  };

  return (
    <>
      {maintainData.map((item, index) => {
        if (item.status === true) {
          return (
            <Spin spinning={loading}>
              <Card style={{ marginBottom: 20, backgroundColor: isStop ? '#f9e8d2' : '#cff5f0' }}>
                {isStop
                  ? `${item.serviceCode}服务停止运维成功`
                  : `${item.serviceCode}服务开启运维成功`}
                ;
                <Icon type="check" style={{ color: 'green', marginLeft: 4, marginBottom: 3 }} />
              </Card>
            </Spin>
          );
        } else if (item.status === false) {
          return (
            <Spin spinning={retryLoading}>
              <Card style={{ marginBottom: 20, backgroundColor: '#f5d8d8' }}>
                {isStop
                  ? `${item.serviceCode}服务停止运维失败`
                  : `${item.serviceCode}服务开启运维失败`}
                ;
                <Icon type="error" style={{ color: '#e85656', marginLeft: 4, marginBottom: 3 }} />
                <a
                  style={{ marginLeft: 6, marginRight: 6 }}
                  onClick={() => handleRetry(item, index)}
                >
                  {intl.get('hadm.maintain.view.message.title.retry').d('重试')}
                </a>
                <a onClick={() => handleError(item)}>
                  {intl.get('hadm.maintain.view.message.title.errorMessage').d('错误信息')}
                </a>
              </Card>
            </Spin>
          );
        } else {
          return (
            <Spin>
              <Card style={{ marginBottom: 20, backgroundColor: 'rgb(159, 216, 232)' }}>
                {isStop
                  ? `${item.serviceCode}服务正在停止运维中`
                  : `${item.serviceCode}服务正在开启运维中`}
                ;
              </Card>
            </Spin>
          );
        }
      })}
      <div style={{ position: 'fixed', bottom: 0, marginBottom: '0.24rem' }}>
        <Divider style={{ width: 520, marginLeft: '-0.24rem' }} />
        {isError && isStop && (
          <Popconfirm
            placement="top"
            title={intl
              .get('hadm.maintain.view.message.confirmMessage')
              .d('是否要忽略？若失败服务无法重试成功，请重启失败服务!')}
            onConfirm={handleStopEnd}
            okText={intl.get('hzero.common.button.ok').d('确定')}
            cancelText={intl.get('hzero.common.button.cancel').d('取消')}
          >
            <Button color="primary">
              {intl.get('hadm.maintain.view.message.title.ignoreAndStop').d('忽略并结束运维')}
            </Button>
          </Popconfirm>
        )}
        {isError && !isStop && (
          <Popconfirm
            placement="top"
            title={intl
              .get('hadm.maintain.view.message.confirmMessage')
              .d('是否要忽略？若失败服务无法重试成功，请重启失败服务!')}
            onConfirm={handleStartEnd}
            okText={intl.get('hzero.common.button.ok').d('确定')}
            cancelText={intl.get('hzero.common.button.cancel').d('取消')}
          >
            <Button color="primary">
              {intl.get('hadm.maintain.view.message.title.ignore').d('忽略')}
            </Button>
          </Popconfirm>
        )}
        {!isError && (
          <Button onClick={handleClose}>{intl.get('hzero.common.button.close').d('关闭')}</Button>
        )}
      </div>
    </>
  );
};

export default Drawer;
