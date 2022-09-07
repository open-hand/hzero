package org.hzero.swagger.api.dto.swagger;

import java.util.Set;

/**
 * @author superlee
 */
public class ControllerDTO {

    private String name;
    private String description;
    private Set<PathDTO> paths;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<PathDTO> getPaths() {
        return paths;
    }

    public void setPaths(Set<PathDTO> paths) {
        this.paths = paths;
    }
}
