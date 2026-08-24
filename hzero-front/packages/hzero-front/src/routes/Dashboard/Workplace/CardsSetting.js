/**
 * 工作台 卡片设置
 * 对布局中的卡片进行新增或删除操作
 * @date 2019-01-28
 * @author WY yang.wang06@hand-china.com
 * @copyright © HAND 2019
 */

import React from 'react';
import { Badge, Col, Modal, Row, Spin, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { forEach } from 'lodash';

import { DEBOUNCE_TIME } from 'utils/constants';
import intl from 'utils/intl';

import styles from './index.less';

const modalStyle = {
  minWidth: '120px',
};

/**
 * @ReactProps {!Function} onAddCard - 新增卡片
 * @ReactProps {!Function} removeCard - 删除卡片
 * @ReactProps {!Function} loadLayout - 加载布局 和 设置卡片 (在比对后发现 没有权限的卡片后调用)
 * @ReactProps {!Function} onCancel - 取消卡片设置状态
 * @ReactProps {!boolean} visible
 * @ReactProps {!object[]} catalogType - 卡片类型
 * @ReactProps {!object[]} roleCards - 当前拥有权限的卡片
 * @ReactProps {object[]} prevRoleCards - 之前拥有权限的卡片
 * @ReactProps {!object[]} layout - 布局中的卡片
 */
export default class CardsSetting extends React.Component {
  state = {
    editCards: {},
  };

  timer = {}; // 卡片 code 对应的 bounce

  componentDidUpdate(prevProps) {
    const { visible: prevVisible } = prevProps;
    const { visible, loadAssignableCards } = this.props;
    if (prevVisible === false && visible === true) {
      loadAssignableCards().then((res) => {
        if (res) {
          // 由隐藏状态变为显示状态, 计算当前 卡片设置的 state
          const { prevRoleCards, roleCards, layout, catalogType, isInitCard } = this.props;
          const editCards = {};
          catalogType.forEach((catalog) => {
            editCards[catalog.value] = {
              cards: [],
            };
          });
          roleCards.forEach((card) => {
            // 卡片可能没有分配
            const cardId = String(card.cardId);
            const layoutCard = layout.find((insertCard) => insertCard.i === cardId);
            const editCard = { ...card, ...layoutCard };
            editCard.isNew = prevRoleCards
              ? !prevRoleCards.some((prevCard) => String(prevCard.cardId) === cardId)
              : false;
            editCard.isInsert = !!layoutCard;
            editCard.isInit = isInitCard(cardId);
            if (editCards[editCard.catalogType]) {
              // 存在卡片类型中, 否则是错误的数据(或者脏数据)
              editCards[editCard.catalogType].cards.push(editCard);
            }
          });
          this.setState({
            editCards,
          });
        }
      });
    }
  }

  shouldComponentUpdate(nextProps) {
    const { visible } = nextProps;
    const { visible: prevVisible } = this.props;
    if (visible === true) {
      return true;
    }
    if (visible === false && prevVisible === true) {
      return true;
    }
    return false;
  }

  componentWillUnmount() {
    forEach(this.timer, (timer) => {
      clearTimeout(timer);
    });
  }

  render() {
    const { visible, onCancel, loading = false } = this.props;
    return (
      <Modal
        destroyOnClose
        title={
          <span className={styles['card-setting-title']}>
            {intl.get('hzero.workplace.view.title.cardsSetting').d('卡片设置')}
          </span>
        }
        style={modalStyle}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        className={[styles['card-setting'], 'hzero-dashboard-card-setting'].join(' ')}
        visible={visible}
        onCancel={onCancel}
        footer={null}
      >
        <Spin spinning={loading}>{this.renderCatalogTypeRow()}</Spin>
      </Modal>
    );
  }

  renderCatalogTypeRow() {
    const { catalogType = [] } = this.props;
    const { editCards } = this.state;
    return catalogType.map((catalog) => {
      const rowData = editCards[catalog.value];
      if (rowData && rowData.cards.length > 0) {
        return (
          <>
            <div key="title" className={styles['card-col-title']}>
              {catalog.meaning}
            </div>
            <Row
              gutter={16}
              key={catalog.value}
              className={[styles['card-row'], 'hzero-dashboard-card-row'].join(' ')}
              type="flex"
              justify="start"
            >
              {this.renderCatalogTypeCols(catalog, rowData.cards)}
            </Row>
          </>
        );
      }
      return null;
    });
  }

  renderCatalogTypeCols(catalog, cards) {
    return cards.map((card) => (
      <Col key={card.cardId} className={[styles['card-col'], 'hzero-dashboard-card-col'].join(' ')}>
        <Tag.CheckableTag
          key="is-insert"
          className={[styles['card-col-tag'], 'hzero-dashboard-card-col-tag'].join(' ')}
          onChange={(checked) => this.handleCardInsertChange(card, catalog, checked)}
          checked={card.isInsert}
          disabled={card.isInit}
        >
          {card.name}
        </Tag.CheckableTag>
        {card.isNew && <Badge key="is-new" dot />}
      </Col>
    ));
  }

  /**
   * 卡片是否选中状态改变
   */
  handleCardInsertChange(card, catalog, checked) {
    const { editCards } = this.state;
    const { cards = [] } = editCards[catalog.value];
    const newCards = cards.map((record) => {
      if (record.cardId === card.cardId) {
        return {
          ...record,
          isInsert: checked,
        };
      }
      return record;
    });
    this.setState({
      editCards: {
        ...editCards,
        [catalog.value]: {
          cards: newCards,
        },
      },
    });
    this.changeCard(card, checked);
  }

  changeCard(card, isInsert) {
    const cardId = String(card.cardId);
    if (this.timer[cardId]) {
      clearTimeout(this.timer[cardId]);
    }
    this.timer[cardId] = setTimeout(() => {
      this.realChangeCard(card, isInsert);
    }, DEBOUNCE_TIME);
  }

  @Bind()
  realChangeCard(card, isInsert) {
    const { onAddCard, onRemoveCard } = this.props;
    const cardId = String(card.cardId);
    if (isInsert) {
      onAddCard(card);
    } else {
      onRemoveCard(cardId);
    }
  }
}
