import React, { useEffect, useState } from 'react';
import { Icon, Input, Row, Col, Button } from 'hzero-ui';
import { Content, Header } from 'components/Page';
import { CLIENT_JUMP_URL } from 'utils/market-client';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import ProductItem from './components/ProductItem';
import CategoryList from './components/CategoryList';
import { fetchProductList, fetchCategoryList } from './services';
import styles from './index.less';

const { Search } = Input;
const pageSize = 10;
const BACK_TO_HOME_URL = '/market-client/home';

function ProductLit() {
  const [productList, setProductList] = useState([]);
  const [categoryList, setCategoryList] = useState([]);
  const [hasMore, setHasMore] = useState(true);
  const [productListLoading, setProductListLoading] = useState(false);
  const [loadMoreLoading, setLoadMoreLoading] = useState(false);
  const [query, setQuery] = useState({});

  useEffect(() => {
    getProducts();
    getCategoryList();
  }, []);

  // 获取产品列表
  const getProducts = (params) => {
    setProductListLoading(true);
    const newQuery = { ...query, ...params };
    fetchProductList(newQuery).then((res) => {
      setProductListLoading(false);
      if (Array.isArray(res)) {
        setProductList(res);
        setHasMore(res.length >= pageSize);
      }
    });
    setQuery(newQuery);
  };

  // 获取分类数据
  const getCategoryList = () => {
    fetchCategoryList({ useFlag: true }).then((res) => {
      setCategoryList(handleCategoryListToTree(res));
    });
  };

  // 加载更多
  const handleLoadMore = () => {
    setLoadMoreLoading(true);
    const lastItem = productList[productList.length - 1] || {};
    const {
      productId: afterId,
      score: afterScore,
      trialFlag: afterTrialFlag,
      releaseDate: afterReleaseTime,
    } = lastItem;
    const tempQuery = {
      ...query,
      afterId,
      afterScore,
      afterTrialFlag: afterTrialFlag ? 1 : 0,
    };
    if (query?.releaseTimeSort) {
      Object.assign(tempQuery, { afterReleaseTime });
    }
    fetchProductList(tempQuery).then((res) => {
      setLoadMoreLoading(false);
      if (Array.isArray(res)) {
        setHasMore(res.length >= pageSize);
        setProductList([...productList, ...res]);
      }
    });
  };

  // 因为后端返回的数据是平铺的。需要根据平铺数据构建树形结构数据
  const handleCategoryListToTree = (list) => {
    if (!Array.isArray(list)) return;

    const res = {};
    const pathRecord = [];
    let tree;
    const mergeChild = (parent, child) => {
      if (!parent) return;

      Object.keys(child).forEach((i) => {
        const edit = child[i];
        if (parent[edit.parentId] && Array.isArray(parent[edit.parentId].children)) {
          parent[edit.parentId].children.push(edit);
        }
      });
    };

    list.forEach((item) => {
      const { level, categoryId } = item;
      if (!res[level]) {
        pathRecord.push(level);
        res[level] = {
          [categoryId]: { ...item, children: [] },
        };
      } else {
        res[level][categoryId] = { ...item, children: [] };
      }
    });

    const sortPathRecord = pathRecord.sort((a, b) => b - a);
    sortPathRecord.forEach((o) => {
      if (!res[o - 1]) {
        tree = Object.keys(res[o]).map((treeItemId) => {
          return res[o][treeItemId];
        });
      } else {
        mergeChild(res[o - 1], res[o]);
      }
    });

    return tree;
  };

  return (
    <>
      <Header
        title={intl.get('hadm.marketclient.view.home.productList').d('产品列表')}
        backPath={BACK_TO_HOME_URL}
      />
      <Content>
        <Row type="flex" style={{ alignItems: 'flex-start' }}>
          <Col className={styles['market-menu-wrap']}>
            <div className={styles['menu-guide-text']}>
              {intl.get('hadm.marketclient.view.product.catalogue').d('产品目录')}
            </div>
            <CategoryList
              category={categoryList}
              categoryChange={(key, item) => getProducts({ codePath: item.codePath })}
            />
          </Col>
          <Col className={styles['market-content-wrap']}>
            <div className={styles['content-search']}>
              <Search
                size="large"
                allowClear
                placeholder={intl
                  .get('hadm.marketclient.input.search.placeholder')
                  .d('请输入关键词进行搜索')}
                enterButton={
                  <div className={styles['search-icon']}>
                    <Icon type="search" height={24} width={24} />
                  </div>
                }
                onSearch={(value) => getProducts({ keyword: value.trim() })}
              />
            </div>
            <ProductItem
              productList={productList}
              loading={productListLoading}
              jumpHost={CLIENT_JUMP_URL}
            />
            {productList.length >= pageSize && hasMore ? (
              <div className={styles['load-more']}>
                <Button onClick={handleLoadMore} loading={loadMoreLoading}>
                  {intl.get('hadm.marketclient.view.product.more').d('点击获取更多')}
                </Button>
              </div>
            ) : null}
          </Col>
        </Row>
      </Content>
    </>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient'],
})(ProductLit);
