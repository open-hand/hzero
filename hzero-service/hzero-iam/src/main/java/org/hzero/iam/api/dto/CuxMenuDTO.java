package org.hzero.iam.api.dto;

import java.util.List;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/7/4
 */
public class CuxMenuDTO {
    private List<CuxMenuItemDTO> menu;

    public List<CuxMenuItemDTO> getMenu() {
        return menu;
    }

    public void setMenu(List<CuxMenuItemDTO> menu) {
        this.menu = menu;
    }
}
