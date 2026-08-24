package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/22
 */
public class DeptListDTO extends DefaultResultDTO {

    /**
     * errcode : 0
     * errmsg : ok
     * department : [{"id":2,"name":"广州研发中心","parentid":1,"order":10},{"id":3,"name":"邮箱产品部","parentid":2,"order":40}]
     */

    private List<DepartmentBean> department;

    public List<DepartmentBean> getDepartment() {
        return department;
    }

    public void setDepartment(List<DepartmentBean> department) {
        this.department = department;
    }

    public static class DepartmentBean {
        /**
         * id : 2
         * name : 广州研发中心
         * parentid : 1
         * order : 10
         */

        private Long id;
        private String name;
        private Long parentid;
        private int order;


        public Long getId() {
            return id;
        }

        public DepartmentBean setId(Long id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public DepartmentBean setName(String name) {
            this.name = name;
            return this;
        }

        public Long getParentid() {
            return parentid;
        }

        public DepartmentBean setParentid(Long parentid) {
            this.parentid = parentid;
            return this;
        }

        public int getOrder() {
            return order;
        }

        public DepartmentBean setOrder(int order) {
            this.order = order;
            return this;
        }
    }
}
