package org.hzero.boot.platform.lov.feign;

import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.dto.LovViewDTO;
import org.hzero.boot.platform.lov.feign.fallback.LovFeignClientFallbackFactory;
import org.hzero.common.HZeroConstant;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Lov服务Feign客户端
 *
 * @author gaokuo.dai@hand-china.com 2018年6月28日上午9:35:08
 */
@FeignClient(value = HZeroService.Platform.NAME, fallbackFactory = LovFeignClientFallbackFactory.class)
public interface LovFeignClient {

    /**
     * 根据值集代码获取值集头
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @return 值集头
     */
    @GetMapping("/v1/lovs/info")
    LovDTO queryLovInfo(@RequestParam("lovCode") String lovCode, @RequestParam(name = "tenantId", required = false) Long tenantId);

    /**
     * 根据值集视图代码获取值集视图头
     *
     * @param viewCode 值集视图代码
     * @param tenantId 租户ID(全局值集时可空)
     * @return 值集视图
     */
    @GetMapping("/v1/lov-view/info")
    LovViewDTO queryLovViewInfo(@RequestParam("viewCode") String viewCode, @RequestParam(name = "tenantId", required = false) Long tenantId);

    /**
     * 根据值集代码获取SQL
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @return SQL
     */
    @GetMapping(HZeroConstant.Lov.ApiAddress.LOV_SQL_SERVICE_ADDRESS)
    String queryLovSql(@RequestParam("lovCode") String lovCode, @RequestParam(name = "tenantId", required = false) Long tenantId);

    /**
     * 根据值集代码获取反查SQL
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @return SQL
     */
    @GetMapping(HZeroConstant.Lov.ApiAddress.LOV_TRANS_SQL_SERVICE_ADDRESS)
    String queryTranslationSql(@RequestParam("lovCode") String lovCode, @RequestParam(name = "tenantId", required = false) Long tenantId);

    /**
     * 根据值集代码获取值集值
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @return 值集值
     */
    @GetMapping(HZeroConstant.Lov.ApiAddress.LOV_VALUE_SERVICE_ADDRESS)
    List<LovValueDTO> queryLovValue(@RequestParam("lovCode") String lovCode,
                                    @RequestParam(name = "tenantId", required = false) Long tenantId);

    /**
     * 根据值集代码获取值集值
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @return 值集值
     */
    @GetMapping(HZeroConstant.Lov.ApiAddress.LOV_VALUE_SERVICE_ADDRESS)
    List<LovValueDTO> queryLovValueWithLanguage(@RequestParam("lovCode") String lovCode,
                                                @RequestParam(name = "tenantId", required = false) Long tenantId,
                                                @RequestParam(name = "lang", required = false) String lang);

    // ------------------------------------------public API feign-----------------------------------------------------
    /**
     * 根据值集代码获取值集头
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @return 值集头
     */
    @GetMapping("/v1/pub/lovs/info")
    LovDTO queryLovInfo(@RequestParam("lovCode") String lovCode,
            @RequestParam(name = "tenantId", required = false) Long tenantId,
            @RequestParam(name = "lang", required = false) String lang);

    /**
     * 根据值集视图代码获取值集视图头
     *
     * @param viewCode 值集视图代码
     * @param tenantId 租户ID(全局值集时可空)
     * @param lang 语言
     * @return 值集视图
     */
    @GetMapping("/v1/pub/lov-view/info")
    LovViewDTO queryLovViewInfo(@RequestParam("viewCode") String viewCode,
            @RequestParam(name = "tenantId", required = false) Long tenantId,
            @RequestParam(name = "lang", required = false) String lang);

    /**
     * 根据值集代码获取SQL
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @param lang 语言
     * @return SQL
     */
    @GetMapping("/v1/pub/lovs/sql")
    String queryLovSql(@RequestParam("lovCode") String lovCode,
            @RequestParam(name = "tenantId", required = false) Long tenantId,
            @RequestParam(name = "lang", required = false) String lang);

    /**
     * 根据值集代码获取反查SQL
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @param lang 语言
     * @return SQL
     */
    @GetMapping("/v1/pub/lovs/translation-sql")
    String queryTranslationSql(@RequestParam("lovCode") String lovCode,
            @RequestParam(name = "tenantId", required = false) Long tenantId,
            @RequestParam(name = "lang", required = false) String lang);

    /**
     * 根据值集代码获取值集值
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集时可空)
     * @param lang 语言
     * @return 值集值
     */
    @GetMapping("/v1/pub/lovs/value")
    List<LovValueDTO> queryLovValue(@RequestParam("lovCode") String lovCode,
            @RequestParam(name = "tenantId", required = false) Long tenantId,
            @RequestParam(name = "lang", required = false) String lang);

}
