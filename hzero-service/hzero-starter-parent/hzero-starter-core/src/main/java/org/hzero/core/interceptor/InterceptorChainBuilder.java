package org.hzero.core.interceptor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.choerodon.core.exception.NotFoundException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.util.AsyncTask;
import org.hzero.core.util.CommonExecutor;

/**
 * 通用拦截器链构造器，可按顺序构建前置拦截器和后置拦截器，并可配置拦截器是否异步执行。
 * <br>
 * 配置拦截器链时，需先调用 {@link #selectChain} 选择拦截器链进行配置
 *
 * @author bojiangzhou 2020/05/28
 */
public final class InterceptorChainBuilder<T> {

    private final ConcurrentHashMap<ChainId, List<Class<? extends HandlerInterceptor<T>>>> preInterMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChainId, List<Class<? extends HandlerInterceptor<T>>>> postInterMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChainId, Boolean> preAsyncMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChainId, Boolean> postAsyncMap = new ConcurrentHashMap<>();

    private ChainId currentChainId;
    private boolean post = true;

    private final Set<ChainId> chainIds = new HashSet<>();

    private static final String PRE = "Pre-";
    private static final String POST_PRE = "Post-";

    private final List<HandlerInterceptor<T>> interceptors;

    /**
     * 创建拦截器链构造器
     *
     * @param interceptors 拦截器
     */
    public InterceptorChainBuilder(List<HandlerInterceptor<T>> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * 选择拦截器链，在调用其它方法前需先调用此方法选择拦截器链
     *
     * @param chainId 拦截器链的唯一标识
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> selectChain(ChainId chainId) {
        this.currentChainId = chainId;
        chainIds.add(chainId);
        preInterMap.putIfAbsent(this.currentChainId, new ArrayList<>(8));
        postInterMap.putIfAbsent(this.currentChainId, new ArrayList<>(8));
        preAsyncMap.putIfAbsent(this.currentChainId, Boolean.FALSE);
        postAsyncMap.putIfAbsent(this.currentChainId, Boolean.FALSE);
        return this;
    }

    /**
     * 清除当前 ChainId
     */
    protected void clearChain() {
        this.currentChainId = null;
    }

    /**
     * 配置前置拦截器
     *
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> pre() {
        this.post = false;
        return this;
    }

    /**
     * 配置后置拦截器
     *
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> post() {
        this.post = true;
        return this;
    }

    /**
     * 拦截器链异步执行，需注意的是，一旦配置了异步则无法保证拦截器的执行顺序
     *
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> async() {
        checkState();
        if (post) {
            postAsyncMap.put(this.currentChainId, Boolean.TRUE);
        } else {
            preAsyncMap.put(this.currentChainId, Boolean.TRUE);
        }
        return this;
    }

    /**
     * 拦截器链同步执行
     *
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> sync() {
        checkState();
        if (post) {
            postAsyncMap.put(this.currentChainId, Boolean.FALSE);
        } else {
            preAsyncMap.put(this.currentChainId, Boolean.FALSE);
        }
        return this;
    }

    /**
     * 在 afterInterceptor 类后添加拦截器
     *
     * @param interceptor      要添加的拦截器
     * @param afterInterceptor 拦截器 Class
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> addInterceptorAfter(Class<? extends HandlerInterceptor<T>> interceptor,
                                                          Class<? extends HandlerInterceptor<T>> afterInterceptor) {
        checkState();
        int index = checkInterceptor(afterInterceptor);
        addAt(index + 1, interceptor);

        return this;
    }

    /**
     * 在 beforeInterceptor 类前添加拦截器
     *
     * @param interceptor       要添加的拦截器
     * @param beforeInterceptor 拦截器 Class
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> addInterceptorBefore(Class<? extends HandlerInterceptor<T>> interceptor,
                                                           Class<? extends HandlerInterceptor<T>> beforeInterceptor) {
        checkState();
        int index = checkInterceptor(beforeInterceptor);
        addAt(index - 1, interceptor);

        return this;
    }

    /**
     * 在末尾添加拦截器
     *
     * @param interceptor 要添加的拦截器
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> addInterceptor(Class<? extends HandlerInterceptor<T>> interceptor) {
        checkState();
        addLast(interceptor);
        return this;
    }

    /**
     * 在 atInterceptor 位置添加拦截器，即替换 atInterceptor 拦截器
     *
     * @param interceptor   要添加的拦截器
     * @param atInterceptor 要替换的拦截器
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> addInterceptorAt(Class<? extends HandlerInterceptor<T>> interceptor,
                                                       Class<? extends HandlerInterceptor<T>> atInterceptor) {
        checkState();
        int index = checkInterceptor(atInterceptor);
        getCurrentInterceptors().remove(index);
        addAt(index, interceptor);

        return this;
    }

    /**
     * 移除 interceptor 拦截器
     *
     * @param interceptor 要移除的拦截器
     * @return InterceptorChainBuilder
     */
    public InterceptorChainBuilder<T> removeInterceptor(Class<? extends HandlerInterceptor<T>> interceptor) {
        checkState();
        int index = checkInterceptor(interceptor);
        getCurrentInterceptors().remove(index);

        return this;
    }

    private void checkState() {
        if (this.currentChainId == null) {
            throw new UnsupportedOperationException("Please call selectChain First.");
        }
    }

    private int checkInterceptor(Class<? extends HandlerInterceptor<T>> interceptor) {
        List<Class<? extends HandlerInterceptor<T>>> interceptors = getCurrentInterceptors();
        for (int i = 0; i < interceptors.size(); i++) {
            if (interceptor.isAssignableFrom(interceptors.get(i))) {
                return i;
            }
        }

        throw new NotFoundException("HandlerInterceptor not found for [" + interceptor.getName() + "]");
    }

    private void addAt(int index, Class<? extends HandlerInterceptor<T>> interceptor) {
        List<Class<? extends HandlerInterceptor<T>>> interceptors = getCurrentInterceptors();
        interceptors.add(Math.max(index, 0), interceptor);
    }

    private void addLast(Class<? extends HandlerInterceptor<T>> interceptor) {
        List<Class<? extends HandlerInterceptor<T>>> interceptors = getCurrentInterceptors();
        interceptors.add(interceptor);
    }

    private List<Class<? extends HandlerInterceptor<T>>> getCurrentInterceptors() {
        return this.post ? postInterMap.get(this.currentChainId) : preInterMap.get(this.currentChainId);
    }

    /**
     * 构建拦截器链
     *
     * @return 一组拦截器链
     */
    public List<InterceptorChain<T>> performBuild() {
        List<InterceptorChain<T>> chains = new ArrayList<>();

        for (ChainId chainId : chainIds) {
            List<HandlerInterceptor<T>> preInterceptors = filterInterceptor(preInterMap.get(chainId));
            List<HandlerInterceptor<T>> postInterceptors = filterInterceptor(postInterMap.get(chainId));

            if (preInterceptors.isEmpty() && postInterceptors.isEmpty()) {
                throw new IllegalStateException("Registered Pre-Interceptors and Post-Interceptors is empty.");
            }

            Consumer<T> preConsumer = (T t) -> {
            };
            Consumer<T> postConsumer = (T t) -> {
            };

            if (!preInterceptors.isEmpty()) {
                if (preAsyncMap.get(chainId)) {
                    preConsumer = (T obj) -> {
                        List<AsyncTask<T>> tasks = createAsyncTask(preInterceptors, obj);
                        CommonExecutor.batchExecuteAsync(tasks, PRE + chainId.id());
                    };
                } else {
                    preConsumer = (T obj) -> {
                        for (HandlerInterceptor<T> item : preInterceptors) {
                            item.interceptor(obj);
                        }
                    };
                }
            }

            if (!postInterceptors.isEmpty()) {
                if (postAsyncMap.get(chainId)) {
                    postConsumer = (T obj) -> {
                        List<AsyncTask<T>> tasks = createAsyncTask(postInterceptors, obj);
                        CommonExecutor.batchExecuteAsync(tasks, POST_PRE + chainId.id());
                    };
                } else {
                    postConsumer = (T obj) -> {
                        for (HandlerInterceptor<T> item : postInterceptors) {
                            item.interceptor(obj);
                        }
                    };
                }
            }

            chains.add(new InterceptorChain<T>(chainId, preConsumer, postConsumer));
        }

        return chains;
    }

    private List<HandlerInterceptor<T>> filterInterceptor(List<Class<? extends HandlerInterceptor<T>>> classList) {
        return classList.stream()
                .map(clazz -> {
                    for (HandlerInterceptor<T> interceptor : interceptors) {
                        if (clazz.getName().equals(interceptor.getClass().getName())) {
                            return interceptor;
                        }
                    }
                    throw new NotFoundException("HandlerInterceptor Bean not found for [" + clazz.getName() + "]");
                }).collect(Collectors.toList());
    }

    private List<AsyncTask<T>> createAsyncTask(List<HandlerInterceptor<T>> interceptors, T target) {
        CustomUserDetails self = DetailsHelper.getUserDetails();
        return interceptors.stream().map(item -> new AsyncTask<T>() {
            @Override
            public String taskName() {
                return item.getClass().getSimpleName();
            }

            @Override
            public T doExecute() {
                if (self != null) {
                    DetailsHelper.setCustomUserDetails(self);
                }
                item.interceptor(target);
                return target;
            }
        }).collect(Collectors.toList());
    }

}
