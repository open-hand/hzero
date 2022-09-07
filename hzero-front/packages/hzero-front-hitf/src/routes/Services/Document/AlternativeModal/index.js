/**
 * AlternativeModal - 备选值弹窗
 * @date: 2019/5/28
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Modal } from 'hzero-ui';
import { connect } from 'dva';
import uuidv4 from 'uuid/v4';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { getEditTableData } from 'utils/utils';
import notification from 'utils/notification';
import AlternativeList from './AlternativeList';

/**
 * 备选值
 * @extends {Component} - React.Component
 * @reactProps {string} paramId - 参数ID
 * @reactProps {string} interfaceId - 接口ID
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @reactProps {!Object} services - 数据源
 * @reactProps {boolean} loading - 页面是否在加载中
 * @return React.element
 */
@connect(({ services, loading }) => ({
  services,
  loading:
    loading.effects['services/queryAlternative'] || loading.effects['services/deleteAlternative'],
  confirmLoading: loading.effects['services/saveAlternative'],
}))
export default class AlternativeModal extends Component {
  state = {
    visible: false,
  };

  /**
   * 查询操作数据
   */
  @Bind()
  fetchAlterativeData() {
    const { dispatch, paramId } = this.props;
    dispatch({
      type: 'services/queryAlternative',
      payload: paramId,
    });
  }

  /**
   * 新建参数备选值
   */
  @Bind()
  handleCreateAlternative() {
    const {
      dispatch,
      services: { alternativeData, documentData },
      interfaceId,
      paramId,
    } = this.props;
    dispatch({
      type: 'services/updateState',
      payload: {
        alternativeData: [
          {
            defaultFlag: 0,
            documentId: documentData.documentId,
            interfaceId,
            paramId,
            paramValue: '',
            paramValueId: uuidv4(),
            remark: '',
            _status: 'create',
          },
          ...alternativeData,
        ],
      },
    });
  }

  /**
   * 编辑参数备选值
   * @param {Object} record - 备选值行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEditLine(record, flag) {
    const {
      dispatch,
      services: { alternativeData = [] },
    } = this.props;
    const newList = alternativeData.map((item) =>
      item.paramValueId === record.paramValueId ? { ...item, _status: flag ? 'update' : '' } : item
    );
    dispatch({
      type: 'services/updateState',
      payload: {
        alternativeData: [...newList],
      },
    });
  }

  /**
   * 保存参数备选值
   */
  @Bind()
  handleSaveAlternative() {
    const {
      dispatch,
      services: { alternativeData },
      paramId,
    } = this.props;
    const isHaveStatus = alternativeData.some((item) => '_status' in item);
    if (!isHaveStatus) {
      this.handleCloseModal();
      return;
    }
    let alternativeList = getEditTableData(alternativeData, ['paramValueId']);
    if (Array.isArray(alternativeList) && alternativeList.length !== 0) {
      alternativeList = alternativeList.map((item) => {
        const tempItem = { ...item };
        if ('_status' in tempItem) {
          delete tempItem._status;
        }
        return tempItem;
      });
      dispatch({
        type: 'services/saveAlternative',
        payload: { alternativeList, paramId },
      }).then((res) => {
        if (res) {
          notification.success();
          this.fetchAlterativeData();
        }
      });
    }
  }

  /**
   * 清除新增行数据
   * @param {Objec} record - 待清除的数据对象
   */
  @Bind()
  handleCleanLine(record) {
    const {
      dispatch,
      services: { alternativeData },
    } = this.props;
    const newList = alternativeData.filter((item) => item.paramValueId !== record.paramValueId);
    dispatch({
      type: 'services/updateState',
      payload: {
        alternativeData: [...newList],
      },
    });
  }

  /**
   * 删除已有数据
   * @param {object} record - 当前行数据
   */
  @Bind()
  handleDelete(record) {
    const { dispatch, paramId } = this.props;
    dispatch({
      type: 'services/deleteAlternative',
      payload: { ...record, paramId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchAlterativeData();
      }
    });
  }

  /**
   * 改变默认值
   * @param {boolean} isChecked - 是否选中
   * @param {object} record - 当前行数据
   */
  @Bind()
  changeDefaultFlag(isChecked, record) {
    const {
      dispatch,
      services: { alternativeData },
    } = this.props;
    let tempDataSource = [...alternativeData];
    tempDataSource = tempDataSource.map((item) => {
      const tempItem = { ...item };
      if (isChecked) {
        tempItem.defaultFlag = item.paramValueId === record.paramValueId ? 1 : 0;
      } else {
        tempItem.defaultFlag = 0;
      }
      return tempItem;
    });
    dispatch({
      type: 'services/updateState',
      payload: {
        alternativeData: [...tempDataSource],
      },
    });
  }

  /**
   * 显示模态框
   */
  @Bind()
  handleOpenModal() {
    this.setState({
      visible: true,
    });
    this.fetchAlterativeData();
  }

  /**
   * 关闭模态框
   */
  @Bind()
  handleCloseModal() {
    const { dispatch } = this.props;
    this.setState(
      {
        visible: false,
      },
      () => {
        dispatch({
          type: 'services/updateState',
          payload: {
            alternativeData: [],
          },
        });
      }
    );
  }

  render() {
    const { visible } = this.state;
    const {
      services: { alternativeData },
      loading,
      confirmLoading,
    } = this.props;
    const listProps = {
      loading,
      dataSource: alternativeData,
      onEditLine: this.handleEditLine,
      onCleanLine: this.handleCleanLine,
      onDelete: this.handleDelete,
      onChangeDefaultFlag: this.changeDefaultFlag,
    };
    return (
      <>
        <a onClick={() => this.handleOpenModal()}>
          {intl.get('hitf.document.view.title.alternative').d('备选值')}
        </a>
        {visible && (
          <Modal
            width={700}
            visible={visible}
            destroyOnClose
            title={intl.get('hitf.document.view.title.alternative').d('备选值')}
            onCancel={this.handleCloseModal}
            onOk={this.handleSaveAlternative}
            maskClosable={false}
            confirmLoading={confirmLoading}
          >
            <div className="table-list-operator">
              <Button type="primary" onClick={this.handleCreateAlternative}>
                {intl.get('hzero.common.button.create').d('新建')}
              </Button>
            </div>
            <AlternativeList {...listProps} />
          </Modal>
        )}
      </>
    );
  }
}
