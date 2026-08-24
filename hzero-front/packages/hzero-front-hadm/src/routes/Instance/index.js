import React, { useState } from 'react';
import { Content, Header } from 'components/Page';
import { Icon, Tree, DataSet, Button } from 'choerodon-ui/pro';
import { runInAction } from 'mobx';
import { Input } from 'choerodon-ui';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import Detail from './Detail';

import { treeDS } from '../../stores/InstanceDS';
import './index.less';

const Instance = () => {
  const dataSet = React.useMemo(() => new DataSet(treeDS), []);

  const [code, setCode] = useState(null);

  const [inputValue, setInputValue] = useState('');

  const [isTree, setIsTree] = useState(true);

  const [isRefresh, setIsRefresh] = useState(false);

  // useEffect(() => {
  //   dataSet.addEventListener('load', selectFirst);
  //   return () => {
  //     dataSet.removeEventListener('load', selectFirst);
  //   };
  // });

  const showDetail = (record) => {
    if (record.get('service')) {
      setCode(record.get('instanceId'));
      setIsRefresh(false);
    }
  };

  // const selectFirst = () => {
  //   showDetail(dataSet.data[0]);
  // };

  const getTitle = (record) => {
    const name = record.get('instanceId').toLowerCase();
    const searchValue = inputValue.toLowerCase();
    const index = name.indexOf(searchValue);
    const beforeStr = name.substr(0, index).toLowerCase();
    const afterStr = name.substr(index + searchValue.length).toLowerCase();
    const title =
      index > -1 ? (
        <span className="tree-title" onClick={() => showDetail(record)}>
          {!record.get('service') && (
            <Icon type={record.get('expand') ? 'folder_open2' : 'folder_open'} />
          )}
          {record.get('service') && <Icon type="instance_outline" />}

          {beforeStr}
          <span style={{ color: '#f50' }}>{inputValue.toLowerCase()}</span>
          {afterStr}
        </span>
      ) : (
        <span className="tree-title" onClick={() => showDetail(record)}>
          {!record.get('service') && (
            <Icon type={record.get('expand') ? 'folder_open2' : 'folder_open'} />
          )}
          {record.get('service') && <Icon type="instance_outline" />}
          {name}
        </span>
      );
    return title;
  };

  const nodeRenderer = ({ record }) => {
    return getTitle(record);
  };

  const handleSearch = (e) => {
    setInputValue(e.target.value);
  };

  const handleExpand = () => {
    runInAction(() => {
      dataSet.forEach((record) => {
        if (!record.get('service')) {
          if (record.get('instanceId').toLowerCase().includes(inputValue.toLowerCase())) {
            record.set('expand', true);
          } else {
            record.set('expand', false);
          }
        }
      });
    });
  };

  const getExpand = () => {
    return (
      <div className="c7n-instance-tree">
        <div style={{ display: 'flex' }}>
          <Input
            className="c7n-instance-search"
            style={{ marginBottom: '.1rem', width: '1.9rem', justifyContent: 'flex-start' }}
            prefix={<Icon type="search" style={{ color: 'black' }} />}
            placeholder={intl.get('hadm.instance.view.message.title.input').d('请输入查询条件')}
            onChange={handleSearch}
            value={inputValue}
            onPressEnter={handleExpand}
          />
          <div
            role="none"
            className="hidden-button"
            onClick={() => setIsTree(false)}
            style={{ justifyContent: 'flex-end', marginTop: 5 }}
          >
            <Icon type="navigate_before" />
          </div>
        </div>
        <Tree renderer={nodeRenderer} dataSet={dataSet} defaultExpandParent />
      </div>
    );
  };

  const getUnExpand = () => {
    return (
      <div className="c7n-iam-apitest-bar">
        <div role="none" className="c7n-iam-apitest-bar-button" onClick={() => setIsTree(true)}>
          <Icon type="navigate_next" />
        </div>
        <p role="none" onClick={() => setIsTree(true)}>
          {intl.get('hadm.instance.view.message.title.side').d('实例')}
        </p>
      </div>
    );
  };

  const handleRefresh = async () => {
    await dataSet.query();
    setIsRefresh(true);
  };

  return (
    <>
      <Header title={intl.get('hadm.instance.view.message.title.instance').d('微服务实例')}>
        <Button icon="refresh" onClick={handleRefresh}>
          {intl.get('hzero.common.button.refresh').d('刷新')}
        </Button>
      </Header>
      <Content className="c7n-instance">
        {isTree ? getExpand() : getUnExpand()}
        <div className="c7n-instance-content">
          <Detail id={code} isRefresh={isRefresh} />
        </div>
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hadm.instance'] })(Instance);
