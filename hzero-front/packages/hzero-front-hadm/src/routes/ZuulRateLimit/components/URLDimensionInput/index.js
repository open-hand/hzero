/**
 * 维度编辑组件 - 受控组件
 * 1. 对外展示是一个显示值&按钮
 * 2. 点击按钮会弹出编辑模态框编辑
 * 3. 只读时只显示 显示值
 * 4. 支持 showValue 属性, 控制 显示值显示
 * 5. 只有在模态框点击确认时 才会触发 onChange
 *
 * 注意, url 在这里是没有 host, port 等的, 只能从 pathname 开始
 * [pathname][?[search]][#[hash]]
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/22
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { Button, Checkbox, Col, Input, Modal, Row, Table } from 'hzero-ui';
import uuid from 'uuid/v4';

import EditTable from 'components/EditTable';

import intl from 'utils/intl';

import { TABLE_OPERATOR_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import { getEditTableData } from 'utils/utils';
import { refactorVariableSort, transformStringToVariable } from '../utils';

/**
 * URL schema
 * schema:[//[useInfo@]host[:port]]path[?query][#fregment]
 */

/**
 * URL 维度
 * [path[?query][#fregment]]
 * 由于变量是使用{digits}占位的, 所以常量不能包含 ? & = # { } /
 * ?    query开始符
 * &    query参数分隔符
 * =    query key,value 分隔符
 * #    hash 开始符
 * { }  变量占位符
 * /    路径分隔符
 * path:    是一连串的 常量 分隔符 变量的组合
 * query:   是 key/value 组合    key/value 都可以使用 常量, 变量
 * hash:    是字符串             不可以使用变量
 */

/**
 * 变量输入框, 变量[checkbox][[常量输入框]]     [拼接值]
 */

/**
 * url:     [拼接显示值]
 * path:    [拼接显示值]
 *  [变量输入框] [+/-](新增下级/删除本级按钮)
 * query:   [拼接显示值]
 *  key:    [变量输入框]
 *  value:  [变量输入框]
 * hash: [文本输入框]
 */

const WrapButton = ({ record, id, onClick, ...rest }) => {
  const handleWrapButtonClick = React.useCallback(() => {
    onClick(record, id);
  }, [record, id, onClick]);
  return <Button {...rest} onClick={handleWrapButtonClick} />;
};

const Span = ({ record, onClick, ...rest }) => {
  const handleClick = React.useCallback(() => {
    onClick(record);
  }, [onClick, record]);
  return <span {...rest} onClick={handleClick} />;
};

/**
 * @param type - 类型 c -> 常量, v -> 变量
 * @param value - 值
 * @param id - 唯一标志
 * @param onChange
 * @param onAdd
 * @param onDelete
 * @constructor
 */
const VariableBaseInput = ({ type, value, id, onChange, onAdd, onDelete }) => {
  const handleChangeType = React.useCallback(() => {
    let nextType = 'v';
    if (type === 'c') {
      nextType = 'v';
    } else if (type === 'v') {
      nextType = 'c';
    }
    onChange(nextType, '', id);
  }, [type, id, onChange]);
  const handleChangeValue = React.useCallback(
    event => {
      const newValue = event.target.value;
      // 常量值 不能包含 ?&#{}/
      if (!/[?&=#{}/]/.test(newValue)) {
        onChange('c', newValue, id);
      }
    },
    [id, onChange]
  );
  const handleAdd = React.useCallback(() => {
    onAdd(type, value, id);
  }, [type, value, id, onAdd]);
  const handleDelete = React.useCallback(() => {
    onDelete(type, value, id);
  }, [type, value, id, onAdd]);
  return (
    <Row type="flex">
      <Col span={5}>
        <WrapButton id={id} icon="plus" record={value} onClick={handleAdd} />
        <WrapButton id={id} icon="minus" record={value} onClick={handleDelete} />
      </Col>
      <Col span={5}>
        {intl.get('hadm.zuulRateLimit.view.cmp.url.vari').d('变量')}
        <Checkbox value={type === 'v'} onChange={handleChangeType} />
      </Col>
      <Col span={14}>
        {type === 'c' && (
          <Input style={{ with: '100%' }} value={value} onChange={handleChangeValue} />
        )}
      </Col>
    </Row>
  );
};

/**
 * @param {string} [value = ''] - 拼接好的值
 * @param id - 唯一标志符
 * @param onChange
 * @constructor
 */
const VariableInput = ({ value = '', id: vId, onChange }) => {
  /* {object[]} values - { type: 'c'|'v', value: string}; c -> 常量 value -> 常量值, v -> 变量; */
  const values = React.useMemo(() => transformStringToVariable(value), [value]);
  const handleVariableChange = React.useCallback(
    (type, ve, eId) => {
      const valueArr = [];
      let variIndex = 0;
      values.forEach(v => {
        let valueItem = v;
        if (v.id === eId) {
          valueItem = { type, value: ve, id: v.id };
        }
        if (valueItem.type === 'c') {
          valueArr.push(valueItem.value);
        } else if (valueItem.type === 'v') {
          variIndex += 1;
          valueArr.push(`{${variIndex}}`);
        }
      });
      onChange(valueArr.join(''), vId);
    },
    [values]
  );
  const handleVariableAdd = React.useCallback(
    (type, ve, eId) => {
      /**/
      const valueArr = [];
      let variIndex = 0;
      values.forEach(v => {
        if (v.type === 'c') {
          valueArr.push(v.value);
        } else if (v.type === 'v') {
          variIndex += 1;
          valueArr.push(`{${variIndex}}`);
        }
        if (v.id === eId) {
          // 新加一个变量
          if (type === 'c') {
            // 新加一个变量
            variIndex += 1;
            valueArr.push(`{${variIndex}}`);
          } else if (type === 'v') {
            // 新加一个常量
            valueArr.push(`var`);
          } else {
            // 点击新增按钮
            valueArr.push(`var`);
          }
        }
      });
      onChange(valueArr.join(''), vId);
    },
    [values]
  );
  const handleVariableDelete = React.useCallback(
    (type, ve, eId) => {
      /**/
      const valueArr = [];
      let variIndex = 0;
      values.forEach(v => {
        if (v.id === eId) {
          // 删除
          return;
        }
        if (v.type === 'c') {
          valueArr.push(v.value);
        } else if (v.type === 'v') {
          variIndex += 1;
          valueArr.push(`{${variIndex}}`);
        }
      });
      onChange(valueArr.join(''), vId);
    },
    [values]
  );
  const handleAddBtnClick = React.useCallback(() => {
    const newValues = [{ type: 'c', value: 'var', id: uuid() }, ...values];
    const valueArr = [];
    let variIndex = 0;
    newValues.forEach(v => {
      if (v.type === 'c') {
        valueArr.push(v.value);
      } else if (v.type === 'v') {
        variIndex += 1;
        valueArr.push(`{${variIndex}}`);
      }
    });
    onChange(valueArr.join(''), vId);
  }, [values]);
  return (
    <Row type="flex">
      <Col span={24}>
        <WrapButton onClick={handleAddBtnClick}>
          {intl.get('hzero.common.button.add').d('新增')}
        </WrapButton>
      </Col>
      {values.map(v => (
        <Col span={24}>
          <VariableBaseInput
            type={v.type}
            value={v.value}
            id={v.id}
            onChange={handleVariableChange}
            onAdd={handleVariableAdd}
            onDelete={handleVariableDelete}
          />
        </Col>
      ))}
    </Row>
  );
};

const HashInput = ({ value, onChange }) => {
  const handleChange = React.useCallback(
    event => {
      const newValue = event.target.value;
      // 常量值 不能包含 ?&#{}/
      if (!/[?&=#{}/]/.test(newValue)) {
        onChange(newValue);
      }
    },
    [onChange]
  );
  return <Input style={{ with: '100%' }} value={value} onChange={handleChange} />;
};

const PathTable = ({ value, onChange }) => {
  const values = React.useMemo(
    () =>
      value.split('/').map(record => ({
        value: record,
        id: uuid(),
        _status: 'update',
      })),
    [value]
  );
  const [dataSource, setDataSource] = React.useState([]);
  const handleRemovePath = React.useCallback(
    rd => {
      setDataSource(dataSource.filter(r => r.id !== rd.id));
    },
    [dataSource, setDataSource]
  );
  const handleAdd = React.useCallback(
    (record, dir) => {
      if (record && dir) {
        const newDataSource = [];
        dataSource.forEach(rd => {
          if (rd.id === record.id) {
            if (dir === 'down') {
              newDataSource.push(rd);
              newDataSource.push({ _status: 'create', id: uuid(), value: '' });
            } else {
              newDataSource.push({ _status: 'create', id: uuid(), value: '' });
              newDataSource.push(rd);
            }
          } else {
            newDataSource.push(rd);
          }
        });
        setDataSource(newDataSource);
      }
      setDataSource([...dataSource, { _status: 'create', id: uuid(), value: '' }]);
    },
    [dataSource]
  );
  const handleSave = React.useCallback(() => {
    const validateDataSource = getEditTableData(dataSource);
    if (dataSource.length === validateDataSource.length) {
      onChange(refactorVariableSort(validateDataSource.map(el => el.value).join('/')));
    }
  }, [dataSource, onChange]);
  const columns = React.useMemo(
    () => [
      {
        key: 'operator',
        width: 110,
        render(_, record) {
          const actions = [
            {
              key: 'delete',
              ele: (
                <Span onClick={handleRemovePath} record={record}>
                  {intl.get('hzero.common.button.delete').d('删除')}
                </Span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
            {
              key: 'add',
              ele: (
                <Span onClick={handleAdd} record={record}>
                  {intl.get('hzero.common.button.add').d('新增')}
                </Span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.add').d('新增'),
            },
          ];
          return operatorRender(actions);
        },
      },
      {
        // 路径
        dataIndex: 'value',
        render(v, record) {
          const { _status, $form: form } = record;
          if (_status) {
            return form.getFieldDecorator('value', {
              initialValue: v,
            })(<VariableInput />);
          } else {
            return v;
          }
        },
      },
    ],
    [handleRemovePath, handleAdd]
  );
  React.useEffect(() => {
    setDataSource(values);
  }, [values]);
  return (
    <>
      <div className={TABLE_OPERATOR_CLASSNAME}>
        <WrapButton onClick={handleSave}>
          {intl.get('hzero.common.button.save').d('保存')}
        </WrapButton>
        <WrapButton onClick={handleAdd}>{intl.get('hzero.common.button.add').d('新增')}</WrapButton>
      </div>
      <EditTable
        rowKey="id"
        bordered
        dataSource={dataSource}
        showHeader={false}
        columns={columns}
      />
    </>
  );
};

const SearchTable = ({ value, onChange }) => {
  const values = React.useMemo(
    () =>
      value.split('&').map(record => {
        const [k = '', v = ''] = record.split('=');
        return {
          key: k,
          value: v,
          id: uuid(),
          _status: 'update',
        };
      }),
    [value]
  );
  const [dataSource, setDataSource] = React.useState([]);
  const handleRemovePath = React.useCallback(
    rd => {
      setDataSource(dataSource.filter(r => r.id !== rd.id));
    },
    [dataSource, setDataSource]
  );
  const handleAdd = React.useCallback(
    (record, dir) => {
      if (record && dir) {
        const newDataSource = [];
        dataSource.forEach(rd => {
          if (rd.id === record.id) {
            if (dir === 'down') {
              newDataSource.push(rd);
              newDataSource.push({ _status: 'create', id: uuid(), key: '', value: '' });
            } else {
              newDataSource.push({ _status: 'create', id: uuid(), key: '', value: '' });
              newDataSource.push(rd);
            }
          } else {
            newDataSource.push(rd);
          }
        });
        setDataSource(newDataSource);
      }
      setDataSource([...dataSource, { _status: 'create', id: uuid(), key: '', value: '' }]);
    },
    [dataSource]
  );
  const handleSave = React.useCallback(() => {
    const validateDataSource = getEditTableData(dataSource);
    if (dataSource.length === validateDataSource.length) {
      onChange(
        refactorVariableSort(validateDataSource.map(el => `${el.key}=${el.value}`).join('&'))
      );
    }
  }, [dataSource, onChange]);
  const columns = React.useMemo(
    () => [
      {
        key: 'operator',
        width: 120,
        render(_, record) {
          const actions = [
            {
              key: 'delete',
              ele: (
                <Span onClick={handleRemovePath} record={record}>
                  {intl.get('hzero.common.button.delete').d('删除')}
                </Span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
            {
              key: 'add',
              ele: (
                <Span onClick={handleAdd} record={record}>
                  {intl.get('hzero.common.button.add').d('新增')}
                </Span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.add').d('新增'),
            },
          ];
          return operatorRender(actions);
        },
      },
      {
        // 路径
        dataIndex: 'key',
        width: 200,
        render(v, record) {
          const { _status, $form: form } = record;
          if (_status) {
            return form.getFieldDecorator('key', {
              initialValue: v,
            })(<VariableInput />);
          } else {
            return v;
          }
        },
      },
      {
        // 路径
        dataIndex: 'value',
        width: 200,
        render(v, record) {
          const { _status, $form: form } = record;
          if (_status) {
            return form.getFieldDecorator('value', {
              initialValue: v,
            })(<VariableInput />);
          } else {
            return v;
          }
        },
      },
    ],
    [handleRemovePath, handleAdd]
  );
  React.useEffect(() => {
    setDataSource(values);
  }, [values]);
  return (
    <>
      <div className={TABLE_OPERATOR_CLASSNAME}>
        <WrapButton onClick={handleSave}>
          {intl.get('hzero.common.button.save').d('保存')}
        </WrapButton>
        <WrapButton onClick={handleAdd}>{intl.get('hzero.common.button.add').d('新增')}</WrapButton>
      </div>
      <EditTable
        rowKey="id"
        bordered
        dataSource={dataSource}
        showHeader={false}
        columns={columns}
      />
    </>
  );
};

const URLDimensionEditForm = ({ value = '', dataHookRef }) => {
  const values = React.useMemo(() => {
    // path is startsWith / and endWith ?/#/eof
    // search is startsWith ? and endWith #/eof, search can contains =
    // hash is startsWith # and endWith eof
    const [, , path = '', , search = '', , hash = ''] =
      value.match(/^(\/([^?&=#{}]*))(\?([^?&#{}/]*))?(#([^?&=#{}/]*))?$/) || [];
    return {
      path,
      search,
      hash,
    };
  }, [value]);
  const [editValues, setEditValues] = React.useState(values);
  const handlePathChange = React.useCallback(
    pathValue => {
      setEditValues({
        ...editValues,
        path: pathValue,
      });
    },
    [editValues, setEditValues]
  );
  const handleSearchChange = React.useCallback(
    searchValue => {
      setEditValues({
        ...editValues,
        search: searchValue,
      });
    },
    [editValues, setEditValues]
  );
  const handleHashChange = React.useCallback(
    hashValue => {
      setEditValues({
        ...editValues,
        hash: hashValue,
      });
    },
    [editValues, setEditValues]
  );
  React.useEffect(() => {
    setEditValues(values);
  }, [values]);
  const dataSource = React.useMemo(
    () => [
      {
        type: 'path',
        value: editValues.path,
      },
      {
        type: 'search',
        value: editValues.search,
      },
      {
        type: 'hash',
        value: editValues.hash,
      },
    ],
    [editValues]
  );
  const url = React.useMemo(() => {
    const [{ value: path }, { value: search }, { value: hash }] = dataSource;
    const valueArr = ['/'];
    if (path) {
      valueArr.push(path);
    }
    if (search) {
      valueArr.push('?');
      valueArr.push(search);
    }
    if (hash) {
      valueArr.push('#');
      valueArr.push(hash);
    }
    // 返回 Promise; 数据
    return refactorVariableSort(valueArr.join(''));
  }, [dataSource]);
  const tableTitle = React.useCallback(() => url, [url]);
  React.useImperativeHandle(
    dataHookRef,
    () => ({
      getValidateValue() {
        return url;
      },
    }),
    [url]
  );

  // 使用表格做
  const columns = React.useMemo(
    () => [
      {
        dataIndex: 'type',
        width: 100,
      },
      {
        dataIndex: 'value',
        render: (_, record) => {
          switch (record.type) {
            case 'path':
              return `/${record.value}`;
            case 'search':
              return `?${record.value}`;
            case 'hash':
              return `#${record.value}`;
            default:
              return record.value;
          }
        },
      },
    ],
    []
  );
  // expandedRowRender
  const expandedRowRender = React.useCallback(
    record => {
      switch (record.type) {
        case 'path':
          return <PathTable value={record.value} onChange={handlePathChange} />;
        case 'search':
          return <SearchTable value={record.value} onChange={handleSearchChange} />;
        case 'hash':
          return <HashInput value={record.value} onChange={handleHashChange} />;
        default:
          break;
      }
    },
    [handlePathChange, handleSearchChange, handleHashChange]
  );
  return (
    <Table
      title={tableTitle}
      bordered
      showHeader={false}
      dataSource={dataSource}
      columns={columns}
      expandedRowRender={expandedRowRender}
    />
  );
};

const URLDimensionInput = ({ value, onChange, disabled }) => {
  const [editModalVisible, setEditModalVisible] = React.useState(false);
  const dataHookRef = React.useRef();
  const handleEdit = React.useCallback(
    event => {
      event.preventDefault();
      setEditModalVisible(true);
    },
    [setEditModalVisible]
  );
  const handleSaveUrl = React.useCallback(() => {
    if (dataHookRef.current) {
      const url = dataHookRef.current.getValidateValue();
      onChange(url);
      setEditModalVisible(false);
    }
  }, [onChange, dataHookRef]);
  const handleCancelEdit = React.useCallback(() => {
    setEditModalVisible(false);
  }, [setEditModalVisible]);
  return (
    <>
      {disabled ? (
        <span>{value}</span>
      ) : (
        <a onClick={handleEdit}>{value || intl.get('hzero.common.button.edit').d('编辑')}</a>
      )}
      <Modal
        title={intl.get('hadm.zuulRateLimit.view.title.urlEdit').d('URL编辑')}
        visible={editModalVisible}
        onOk={handleSaveUrl}
        onCancel={handleCancelEdit}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width={1000}
      >
        <URLDimensionEditForm value={value} dataHookRef={dataHookRef} />
      </Modal>
    </>
  );
};

export default URLDimensionInput;
