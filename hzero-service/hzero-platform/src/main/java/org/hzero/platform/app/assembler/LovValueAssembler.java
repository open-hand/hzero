package org.hzero.platform.app.assembler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.hzero.platform.api.dto.LovValueDTO;

/**
 * 值集值Assembler
 *
 * @author gaokuo.dai@hand-china.com 2018年7月16日上午10:31:03
 */
public class LovValueAssembler {
    
    /**
     * 传入一个父值集值列表和子值集值列表，返回树形结构
     * @param parents 父值集值列表
     * @param children 子值集值列表
     * @return 树形结构
     */
    public static List<LovValueDTO> generateTree(List<LovValueDTO> parents, List<LovValueDTO> children){
        if(parents == null) {
            return null;
        }
        children = ListUtils.emptyIfNull(children);
        Map<String, List<LovValueDTO>> childrenGroups = children.stream().collect(Collectors.groupingBy(LovValueDTO::getParentValue));
        parents.forEach(parent -> parent.setChildren(childrenGroups.get(parent.getValue())));
        return parents;
    }

}
