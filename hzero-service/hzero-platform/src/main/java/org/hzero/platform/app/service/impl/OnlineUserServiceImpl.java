package org.hzero.platform.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.api.dto.OnLineUserDTO;
import org.hzero.platform.api.dto.OnlineUserCountDTO;
import org.hzero.platform.app.service.OnlineUserService;
import org.hzero.platform.domain.entity.AuditLogin;
import org.hzero.platform.domain.repository.AuditLoginRepository;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.websocket.redis.OnlineUserRedis;
import org.hzero.websocket.vo.SessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/12 13:41
 */
@Service
@SuppressWarnings("all")
public class OnlineUserServiceImpl implements OnlineUserService {

    @Autowired
    private AuditLoginRepository auditLoginRepository;

    @Override
    public Page<OnLineUserDTO> pageOnlineUser(int page, int size, Long tenantId) {
        List<OnLineUserDTO> userList = listOnlineUser(tenantId);
        // 根据accessToken去重
        userList = userList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(OnLineUserDTO::getAccessToken))), ArrayList::new));

        List<OnLineUserDTO> data;
        int start = page * size;
        if (CollectionUtils.isEmpty(userList) || userList.size() <= start) {
            data = new ArrayList<>();
        } else {
            int end = start + size;
            data = userList.subList(start, userList.size() >= end + 1 ? end : userList.size());
        }

        Long count = Long.valueOf(data.size());
        Page<OnLineUserDTO> onLineUsers = new Page<>();
        onLineUsers.setNumber(page);
        onLineUsers.setSize(size);
        onLineUsers.setTotalElements(userList.size());
        onLineUsers.setTotalPages((userList.size() + size - 1) / size);
        onLineUsers.setSize(size);
        onLineUsers.setContent(data);
        return onLineUsers;
    }

    @Override
    public List<OnLineUserDTO> listOnlineUser(Long tenantId) {
        List<SessionVO> userList;
        if (tenantId != null) {
            userList = OnlineUserRedis.getCache(tenantId);
        } else {
            userList = OnlineUserRedis.getCache();
        }
        List<OnLineUserDTO> onLineUsers = new ArrayList<>();
        if (CollectionUtils.isEmpty(userList)) {
            return onLineUsers;
        }
        // 关联登录审计，获取用户信息
        List<AuditLogin> auditLoginList = auditLoginRepository.listUserInfo(userList.stream().map(SessionVO::getAccessToken).collect(Collectors.toList()));
        // 获取当前租户的租户名称
        List<OnLineUserDTO> onLineUserList = auditLoginRepository.listUserTenant(userList.stream().map(SessionVO::getTenantId).collect(Collectors.toList()));
        Map<Long, String> tenantMap = onLineUserList.stream().collect(Collectors.toMap(OnLineUserDTO::getTenantId, OnLineUserDTO::getTenantName, (k1, k2) -> k2));
        // 组装返回结果
        for (SessionVO user : userList) {
            OnLineUserDTO onLineUserDTO = new OnLineUserDTO().setUser(user);
            onLineUserDTO.setTenantName(tenantMap.get(user.getTenantId()));
            for (AuditLogin login : auditLoginList) {
                if (Objects.equals(login.getAccessToken(), user.getAccessToken())) {
                    onLineUsers.add(onLineUserDTO.setAuditLogin(login));
                }
            }
        }
        return onLineUsers;
    }

    @Override
    public Integer countOnline() {
        List<OnLineUserDTO> userList = listOnlineUser(null);
        if (CollectionUtils.isEmpty(userList)) {
            return 0;
        }
        Set<String> usernameList = userList.stream().map(OnLineUserDTO::getLoginName).collect(Collectors.toSet());
        return usernameList.size();
    }

    @Override
    public List<OnlineUserCountDTO> countOnlineUser() {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        if (BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            tenantId = null;
        }
        List<OnLineUserDTO> onlineUserList = listOnlineUser(tenantId);
        // 根据accessToken去重
        onlineUserList = onlineUserList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(OnLineUserDTO::getAccessToken))), ArrayList::new));
        List<Integer> loginTime = new ArrayList<>(onlineUserList.size());
        onlineUserList.forEach(user -> {
            Long loginDate = user.getLoginDate().getTime();
            Integer hours = (int) (System.currentTimeMillis() - loginDate) / (1000 * 60 * 60);
            hours = (int) (System.currentTimeMillis() - loginDate) % (1000 * 60 * 60) > 0 ? hours + 1 : hours;
            loginTime.add(hours);
        });
        Map<Integer, Long> map = loginTime.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<OnlineUserCountDTO> dtos = new ArrayList<>(Constants.OnlineUser.HOURS);
        for (int i = 2; i < 25; i++) {
            OnlineUserCountDTO dto = new OnlineUserCountDTO(Integer.toString(i),
                    (map.get(i) == null ? Constants.OnlineUser.INITIAL_ONLINE : map.get(i)) +
                            (map.get(i - 1) == null ? Constants.OnlineUser.INITIAL_ONLINE : map.get(i - 1)));
            if (Objects.equals(Constants.OnlineUser.INITIAL_ONLINE, dto.getQuantity())) {
                dto.setQuantity(null);
            }
            dtos.add(dto);
            i = i + 2;
        }
        long count = loginTime.stream().filter(x -> x > 24).count();
        dtos.add(new OnlineUserCountDTO(">24", Constants.OnlineUser.INITIAL_ONLINE.equals(count) ? null : count));
        return dtos;
    }

    @Override
    public Page<OnLineUserDTO> pageWithHour(PageRequest pageRequest, String hour) {
        if (StringUtils.isBlank(hour)) {
            hour = StringUtils.EMPTY;
        }
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        if (BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            tenantId = null;
        }
        List<OnLineUserDTO> onlineUserList = listOnlineUser(tenantId);
        // 根据accessToken去重
        onlineUserList = onlineUserList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(OnLineUserDTO::getAccessToken))), ArrayList::new));
        Long dateNow = System.currentTimeMillis();
        onlineUserList.forEach(user -> {
            Long loginDate = user.getLoginDate().getTime();
            user.setDuration(dateNow - loginDate);
        });
        List<OnLineUserDTO> data = null;
        switch (hour) {
            case "2":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 2).collect(Collectors.toList());
                break;
            case "4":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 4 && item.getDuration() > 1000L * 60 * 60 * 2).collect(Collectors.toList());
                break;
            case "6":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 6 && item.getDuration() > 1000L * 60 * 60 * 4).collect(Collectors.toList());
                break;
            case "8":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 8 && item.getDuration() > 1000L * 60 * 60 * 6).collect(Collectors.toList());
                break;
            case "10":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 10 && item.getDuration() > 1000L * 60 * 60 * 8).collect(Collectors.toList());
                break;
            case "12":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 12 && item.getDuration() > 1000L * 60 * 60 * 10).collect(Collectors.toList());
                break;
            case "14":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 14 && item.getDuration() > 1000L * 60 * 60 * 12).collect(Collectors.toList());
                break;
            case "16":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 16 && item.getDuration() > 1000L * 60 * 60 * 14).collect(Collectors.toList());
                break;
            case "18":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 18 && item.getDuration() > 1000L * 60 * 60 * 16).collect(Collectors.toList());
                break;
            case "20":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 20 && item.getDuration() > 1000L * 60 * 60 * 18).collect(Collectors.toList());
                break;
            case "22":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 22 && item.getDuration() > 1000L * 60 * 60 * 20).collect(Collectors.toList());
                break;
            case "24":
                data = onlineUserList.stream().filter(item -> item.getDuration() <= 1000L * 60 * 60 * 24 && item.getDuration() > 1000L * 60 * 60 * 22).collect(Collectors.toList());
                break;
            case ">24":
                data = onlineUserList.stream().filter(item -> item.getDuration() > 1000L * 60 * 60 * 24).collect(Collectors.toList());
                break;
            default:
                data = onlineUserList;
                break;
        }
        Assert.notNull(data, BaseConstants.ErrorCode.DATA_INVALID);
        List<OnLineUserDTO> result;
        int start = pageRequest.getPage() * pageRequest.getSize();
        if (CollectionUtils.isEmpty(data) || data.size() <= start) {
            result = new ArrayList<>();
        } else {
            int end = start + pageRequest.getSize();
            result = data.subList(start, data.size() >= end + 1 ? end : data.size());
        }
        Page<OnLineUserDTO> page = new Page<>();
        page.setNumber(pageRequest.getPage());
        page.setSize(pageRequest.getSize());
        page.setTotalElements(data.size());
        page.setTotalPages((data.size() + pageRequest.getSize() - 1) / pageRequest.getSize());
        page.setNumberOfElements(result.size());
        page.setContent(result);
        return page;
    }
}